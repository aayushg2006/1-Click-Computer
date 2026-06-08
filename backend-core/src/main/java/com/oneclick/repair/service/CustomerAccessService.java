package com.oneclick.repair.service;

import com.oneclick.repair.dto.CustomerVerifiedInvoiceDTO;
import com.oneclick.repair.dto.CustomerVerifiedInvoiceItemDTO;
import com.oneclick.repair.dto.CustomerVerifiedTicketDTO;
import com.oneclick.repair.dto.CustomerVerifiedTicketUpdateDTO;
import com.oneclick.repair.model.Invoice;
import com.oneclick.repair.model.InvoiceItem;
import com.oneclick.repair.model.Ticket;
import com.oneclick.repair.repository.InvoiceRepository;
import com.oneclick.repair.repository.TicketRepository;
import com.oneclick.repair.repository.TicketUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerAccessService {

    private final TicketRepository ticketRepository;
    private final TicketUpdateRepository ticketUpdateRepository;
    private final InvoiceRepository invoiceRepository;
    private final MinioStorageService minioStorageService;

    @Transactional(readOnly = true)
    public CustomerVerifiedTicketDTO getVerifiedTicket(String trackingCode) {
        Ticket ticket = ticketRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Sorry, no repair ticket found with this code."));
        return CustomerVerifiedTicketDTO.builder()
                .trackingCode(ticket.getTrackingCode())
                .customerName(ticket.getCustomerName())
                .customerPhone(ticket.getCustomerPhone())
                .ticketType(ticket.getTicketType())
                .status(ticket.getStatus())
                .deviceDetails(ticket.getDeviceDetails())
                .conditionPhotoUrls(ticket.getConditionPhotoUrls())
                .estimatedCost(ticket.getEstimatedCost())
                .visitCharge(ticket.getVisitCharge())
                .createdAt(ticket.getCreatedAt())
                .updates(ticketUpdateRepository.findByTicketIdOrderByCreatedAtAsc(ticket.getId()).stream()
                        .map(update -> CustomerVerifiedTicketUpdateDTO.builder()
                                .id(update.getId().toString())
                                .statusTitle(update.getStatusTitle())
                                .statusDescription(update.getStatusDescription())
                                .mediaUrl(update.getMediaUrl())
                                .visibleToCustomer(update.getIsVisibleToCustomer())
                                .createdAt(update.getCreatedAt())
                                .build())
                        .toList())
                .build();
    }

    @Transactional(readOnly = true)
    public List<CustomerVerifiedInvoiceDTO> listVerifiedInvoices(String trackingCode) {
        Ticket ticket = ticketRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Sorry, no repair ticket found with this code."));
        return invoiceRepository.findByTicket_TrackingCodeAndCustomer_PhoneNumberOrderByCreatedAtDesc(trackingCode, ticket.getCustomerPhone())
                .stream().map(this::toVerifiedInvoice).toList();
    }

    @Transactional(readOnly = true)
    public CustomerVerifiedInvoiceDTO getVerifiedInvoice(String trackingCode, String invoiceNumber) {
        Ticket ticket = ticketRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Sorry, no repair ticket found with this code."));
        Invoice invoice = invoiceRepository.findByInvoiceNumberAndTicket_TrackingCodeAndCustomer_PhoneNumber(invoiceNumber, trackingCode, ticket.getCustomerPhone())
                .orElseThrow(() -> new RuntimeException("Invoice not found for this verified access."));
        return toVerifiedInvoice(invoice);
    }

    private CustomerVerifiedInvoiceDTO toVerifiedInvoice(Invoice invoice) {
        return CustomerVerifiedInvoiceDTO.builder()
                .invoiceNumber(invoice.getInvoiceNumber())
                .customerName(invoice.getCustomer() != null ? invoice.getCustomer().getFullName() : null)
                .customerPhone(invoice.getCustomer() != null ? invoice.getCustomer().getPhoneNumber() : null)
                .subTotal(invoice.getSubTotal())
                .gstAmount(invoice.getGstAmount())
                .totalAmount(invoice.getTotalAmount())
                .paymentStatus(invoice.getPaymentStatus())
                .pdfFilePath(invoice.getPdfFilePath())
                .pdfUrl(invoice.getPdfFilePath() == null ? null : minioStorageService.presignedGetUrl(invoice.getPdfFilePath(), 15))
                .createdAt(invoice.getCreatedAt())
                .items(invoice.getItems().stream().map(this::toItem).toList())
                .build();
    }

    private CustomerVerifiedInvoiceItemDTO toItem(InvoiceItem item) {
        return CustomerVerifiedInvoiceItemDTO.builder()
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getUnitPrice() == null ? null : item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity() == null ? 0 : item.getQuantity())))
                .build();
    }
}
