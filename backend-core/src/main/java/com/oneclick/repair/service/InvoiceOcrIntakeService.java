package com.oneclick.repair.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneclick.repair.dto.OcrExtractionResultDTO;
import com.oneclick.repair.dto.OcrIntakeResponse;
import com.oneclick.repair.dto.OcrInvoiceItemDTO;
import com.oneclick.repair.dto.OcrInvoiceDraftDTO;
import com.oneclick.repair.dto.OcrInvoiceDraftItemDTO;
import com.oneclick.repair.dto.OcrInvoiceDraftItemUpdateRequest;
import com.oneclick.repair.dto.OcrInvoiceDraftUpdateRequest;
import com.oneclick.repair.model.Invoice;
import com.oneclick.repair.model.InvoiceItem;
import com.oneclick.repair.model.Customer;
import com.oneclick.repair.model.OcrInvoiceDraft;
import com.oneclick.repair.model.OcrInvoiceDraftItem;
import com.oneclick.repair.model.OcrInvoiceJob;
import com.oneclick.repair.repository.CustomerRepository;
import com.oneclick.repair.repository.InvoiceItemRepository;
import com.oneclick.repair.repository.InvoiceRepository;
import com.oneclick.repair.repository.OcrInvoiceDraftRepository;
import com.oneclick.repair.repository.OcrInvoiceJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceOcrIntakeService {

    private final MinioStorageService minioStorageService;
    private final StorageKeyService storageKeyService;
    private final OcrInvoiceJobRepository ocrInvoiceJobRepository;
    private final OcrInvoiceDraftRepository ocrInvoiceDraftRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final CustomerRepository customerRepository;
    private final OllamaOcrService ollamaOcrService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public OcrIntakeResponse intake(MultipartFile file) {
        validateDocument(file);
        String documentId = UUID.randomUUID().toString();
        String objectKey = storageKeyService.invoicePdfKey(documentId, file.getOriginalFilename());
        minioStorageService.store(file, objectKey);
        OcrInvoiceJob job = ocrInvoiceJobRepository.save(OcrInvoiceJob.builder()
                .sourceFileName(file.getOriginalFilename())
                .sourceContentType(file.getContentType())
                .objectKey(objectKey)
                .status("RECEIVED")
                .build());
        return OcrIntakeResponse.builder()
                .documentId(job.getId().toString())
                .objectKey(objectKey)
                .status(job.getStatus())
                .message("Document stored successfully and queued for OCR extraction.")
                .build();
    }

    @Transactional
    public OcrExtractionResultDTO extract(UUID jobId, MultipartFile file) {
        validateDocument(file);
        OcrInvoiceJob job = ocrInvoiceJobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("OCR job not found"));
        var result = ollamaOcrService.extract(readBytes(file), file.getContentType(), file.getOriginalFilename());
        job.setStatus("EXTRACTED");
        job.setExtractedText(result.extractedText());
        job.setParsedPayload(result.rawResponse());
        ocrInvoiceJobRepository.save(job);
        OcrInvoiceDraft draft = upsertDraft(job, result.extractedText(), result.rawResponse());
        return OcrExtractionResultDTO.builder()
                .jobId(job.getId())
                .status(job.getStatus())
                .sourceFileName(job.getSourceFileName())
                .sourceContentType(job.getSourceContentType())
                .objectKey(job.getObjectKey())
                .extractedText(result.extractedText())
                .rawModelResponse(result.rawResponse())
                .vendorName(draft.getVendorName())
                .invoiceNumber(draft.getInvoiceNumber())
                .invoiceDate(draft.getInvoiceDate())
                .customerName(draft.getCustomerName())
                .customerPhone(draft.getCustomerPhone())
                .subTotal(draft.getSubTotal())
                .gstAmount(draft.getGstAmount())
                .totalAmount(draft.getTotalAmount())
                .items(draft.getItems().stream().map(this::toDto).toList())
                .build();
    }

    @Transactional(readOnly = true)
    public OcrInvoiceDraftDTO getDraft(UUID jobId) {
        return toDraftDto(findDraft(jobId));
    }

    @Transactional
    public OcrInvoiceDraftDTO updateDraft(UUID jobId, OcrInvoiceDraftUpdateRequest request) {
        OcrInvoiceDraft draft = findDraft(jobId);
        draft.setVendorName(request.getVendorName());
        draft.setInvoiceNumber(request.getInvoiceNumber());
        draft.setInvoiceDate(request.getInvoiceDate());
        draft.setCustomerName(request.getCustomerName());
        draft.setCustomerPhone(request.getCustomerPhone());
        draft.setSubTotal(defaultMoney(request.getSubTotal()));
        draft.setGstAmount(defaultMoney(request.getGstAmount()));
        draft.setTotalAmount(defaultMoney(request.getTotalAmount()));
        draft.getItems().clear();
        if (request.getItems() != null) {
            for (OcrInvoiceDraftItemUpdateRequest itemRequest : request.getItems()) {
                draft.getItems().add(OcrInvoiceDraftItem.builder()
                        .draft(draft)
                        .description(itemRequest.getDescription())
                        .quantity(itemRequest.getQuantity())
                        .unitPrice(defaultMoney(itemRequest.getUnitPrice()))
                        .lineTotal(defaultMoney(itemRequest.getLineTotal()))
                        .build());
            }
        }
        return toDraftDto(ocrInvoiceDraftRepository.save(draft));
    }

    @Transactional
    public OcrInvoiceDraftDTO approveDraft(UUID jobId) {
        OcrInvoiceDraft draft = findDraft(jobId);
        draft.setStatus("APPROVED");
        draft.setReviewedAt(OffsetDateTime.now());
        return toDraftDto(ocrInvoiceDraftRepository.save(draft));
    }

    @Transactional
    public OcrInvoiceDraftDTO rejectDraft(UUID jobId, String reason) {
        OcrInvoiceDraft draft = findDraft(jobId);
        draft.setStatus("REJECTED");
        draft.setRejectionReason(reason);
        draft.setReviewedAt(OffsetDateTime.now());
        return toDraftDto(ocrInvoiceDraftRepository.save(draft));
    }

    @Transactional
    public OcrInvoiceDraftDTO commitDraft(UUID jobId) {
        OcrInvoiceDraft draft = findDraft(jobId);
        if (!"APPROVED".equalsIgnoreCase(draft.getStatus())) {
            throw new RuntimeException("Only approved OCR drafts can be committed.");
        }
        Customer customer = null;
        if (draft.getCustomerPhone() != null && !draft.getCustomerPhone().isBlank()) {
            customer = customerRepository.findByPhoneNumber(draft.getCustomerPhone())
                    .orElseGet(() -> customerRepository.save(Customer.builder()
                            .fullName(draft.getCustomerName())
                            .phoneNumber(draft.getCustomerPhone())
                            .build()));
        }
        final Customer resolvedCustomer = customer;
        Invoice invoice = invoiceRepository.findByInvoiceNumber(draft.getInvoiceNumber())
                .orElseGet(() -> invoiceRepository.save(Invoice.builder()
                        .invoiceNumber(draft.getInvoiceNumber())
                        .customer(resolvedCustomer)
                        .subTotal(defaultMoney(draft.getSubTotal()))
                        .gstAmount(defaultMoney(draft.getGstAmount()))
                        .totalAmount(defaultMoney(draft.getTotalAmount()))
                        .paymentStatus("UNPAID")
                        .build()));
        invoice.setCustomer(customer);
        invoice.setSubTotal(defaultMoney(draft.getSubTotal()));
        invoice.setGstAmount(defaultMoney(draft.getGstAmount()));
        invoice.setTotalAmount(defaultMoney(draft.getTotalAmount()));
        invoiceItemRepository.deleteAll(invoice.getItems());
        invoice.getItems().clear();
        for (OcrInvoiceDraftItem item : draft.getItems()) {
            InvoiceItem invoiceItem = InvoiceItem.builder()
                    .invoice(invoice)
                    .description(item.getDescription())
                    .quantity(item.getQuantity() == null ? 1 : item.getQuantity())
                    .unitPrice(defaultMoney(item.getUnitPrice()))
                    .build();
            invoice.getItems().add(invoiceItem);
        }
        invoiceRepository.save(invoice);
        draft.setStatus("COMMITTED");
        draft.setCommittedAt(OffsetDateTime.now());
        return toDraftDto(ocrInvoiceDraftRepository.save(draft));
    }

    private void validateDocument(MultipartFile file) {
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase();
        if (!(contentType.contains("pdf") || contentType.contains("jpeg") || contentType.contains("jpg") || contentType.contains("png"))) {
            throw new RuntimeException("Only PDF, JPG, and PNG files are supported.");
        }
    }

    private OcrInvoiceDraft upsertDraft(OcrInvoiceJob job, String extractedText, String rawResponse) {
        OcrInvoiceDraft draft = ocrInvoiceDraftRepository.findByJobId(job.getId())
                .orElseGet(() -> OcrInvoiceDraft.builder().job(job).build());
        draft.setExtractedText(extractedText);
        draft.setRawModelResponse(rawResponse);
        MapValues values = parseValues(rawResponse, extractedText);
        draft.setVendorName(values.vendorName);
        draft.setInvoiceNumber(values.invoiceNumber);
        draft.setInvoiceDate(values.invoiceDate);
        draft.setCustomerName(values.customerName);
        draft.setCustomerPhone(values.customerPhone);
        draft.setSubTotal(values.subTotal);
        draft.setGstAmount(values.gstAmount);
        draft.setTotalAmount(values.totalAmount);
        draft.getItems().clear();
        for (OcrInvoiceItemDTO item : values.items) {
            draft.getItems().add(OcrInvoiceDraftItem.builder()
                    .draft(draft)
                    .description(item.getDescription())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .lineTotal(item.getLineTotal())
                    .build());
        }
        return ocrInvoiceDraftRepository.save(draft);
    }

    private MapValues parseValues(String rawResponse, String extractedText) {
        try {
            var root = objectMapper.readTree(rawResponse);
            String payload = root.has("response") ? root.get("response").asText() : rawResponse;
            String cleaned = payload.replaceAll("(?s)^```json\\s*|```$", "").trim();
            var node = objectMapper.readTree(cleaned);
            return new MapValues(
                    text(node, "vendorName"),
                    text(node, "invoiceNumber"),
                    text(node, "invoiceDate"),
                    text(node, "customerName"),
                    text(node, "customerPhone"),
                    money(node, "subTotal"),
                    money(node, "gstAmount"),
                    money(node, "totalAmount"),
                    items(node)
            );
        } catch (Exception e) {
            return new MapValues(null, null, null, null, null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, List.of());
        }
    }

    private List<OcrInvoiceItemDTO> items(com.fasterxml.jackson.databind.JsonNode node) {
        if (!node.has("items") || !node.get("items").isArray()) {
            return List.of();
        }
        return java.util.stream.StreamSupport.stream(node.get("items").spliterator(), false)
                .map(item -> OcrInvoiceItemDTO.builder()
                        .description(text(item, "description"))
                        .quantity(item.hasNonNull("quantity") ? item.get("quantity").asInt(1) : 1)
                        .unitPrice(money(item, "unitPrice"))
                        .lineTotal(money(item, "lineTotal"))
                        .build())
                .toList();
    }

    private String text(com.fasterxml.jackson.databind.JsonNode node, String field) {
        return node.hasNonNull(field) ? node.get(field).asText() : null;
    }

    private BigDecimal money(com.fasterxml.jackson.databind.JsonNode node, String field) {
        if (!node.hasNonNull(field)) return BigDecimal.ZERO;
        try {
            return new BigDecimal(node.get(field).asText().replaceAll("[^0-9.\\-]", ""));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private OcrInvoiceDraftDTO toDraftDto(OcrInvoiceDraft draft) {
        return OcrInvoiceDraftDTO.builder()
                .id(draft.getId())
                .jobId(draft.getJob().getId())
                .status(draft.getStatus())
                .vendorName(draft.getVendorName())
                .invoiceNumber(draft.getInvoiceNumber())
                .invoiceDate(draft.getInvoiceDate())
                .customerName(draft.getCustomerName())
                .customerPhone(draft.getCustomerPhone())
                .subTotal(draft.getSubTotal())
                .gstAmount(draft.getGstAmount())
                .totalAmount(draft.getTotalAmount())
                .extractedText(draft.getExtractedText())
                .rawModelResponse(draft.getRawModelResponse())
                .rejectionReason(draft.getRejectionReason())
                .reviewedAt(draft.getReviewedAt())
                .committedAt(draft.getCommittedAt())
                .items(draft.getItems().stream().map(this::toDraftDto).toList())
                .build();
    }

    private OcrInvoiceDraft findDraft(UUID jobId) {
        return ocrInvoiceDraftRepository.findByJobId(jobId)
                .orElseThrow(() -> new RuntimeException("OCR draft not found for this job"));
    }

    private OcrInvoiceDraftItemDTO toDraftDto(OcrInvoiceDraftItem item) {
        return OcrInvoiceDraftItemDTO.builder()
                .id(item.getId())
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .build();
    }

    private OcrInvoiceItemDTO toDto(OcrInvoiceDraftItem item) {
        return OcrInvoiceItemDTO.builder()
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .build();
    }

    private record MapValues(String vendorName, String invoiceNumber, String invoiceDate, String customerName, String customerPhone, BigDecimal subTotal, BigDecimal gstAmount, BigDecimal totalAmount, List<OcrInvoiceItemDTO> items) {}

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (Exception e) {
            throw new RuntimeException("Unable to read uploaded file", e);
        }
    }
}
