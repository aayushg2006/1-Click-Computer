package com.oneclick.repair.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneclick.repair.dto.OcrExtractionResultDTO;
import com.oneclick.repair.dto.OcrIntakeResponse;
import com.oneclick.repair.dto.OcrInvoiceItemDTO;
import com.oneclick.repair.model.OcrInvoiceJob;
import com.oneclick.repair.repository.OcrInvoiceJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceOcrIntakeService {

    private final MinioStorageService minioStorageService;
    private final StorageKeyService storageKeyService;
    private final OcrInvoiceJobRepository ocrInvoiceJobRepository;
    private final OllamaOcrService ollamaOcrService;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    public OcrExtractionResultDTO extract(UUID jobId, MultipartFile file) {
        validateDocument(file);
        OcrInvoiceJob job = ocrInvoiceJobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("OCR job not found"));
        var result = ollamaOcrService.extract(readBytes(file), file.getContentType(), file.getOriginalFilename());
        job.setStatus("EXTRACTED");
        job.setExtractedText(result.extractedText());
        job.setParsedPayload(result.rawResponse());
        ocrInvoiceJobRepository.save(job);
        return OcrExtractionResultDTO.builder()
                .jobId(job.getId())
                .status(job.getStatus())
                .sourceFileName(job.getSourceFileName())
                .sourceContentType(job.getSourceContentType())
                .objectKey(job.getObjectKey())
                .extractedText(result.extractedText())
                .rawModelResponse(result.rawResponse())
                .items(List.of())
                .build();
    }

    private void validateDocument(MultipartFile file) {
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase();
        if (!(contentType.contains("pdf") || contentType.contains("jpeg") || contentType.contains("jpg") || contentType.contains("png"))) {
            throw new RuntimeException("Only PDF, JPG, and PNG files are supported.");
        }
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (Exception e) {
            throw new RuntimeException("Unable to read uploaded file", e);
        }
    }
}
