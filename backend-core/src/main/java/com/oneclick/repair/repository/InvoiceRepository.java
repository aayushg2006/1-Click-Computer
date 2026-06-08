package com.oneclick.repair.repository;

import com.oneclick.repair.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByCustomerId(UUID customerId);

    List<Invoice> findByInvoiceNumberContainingIgnoreCaseOrCustomer_PhoneNumberContainingIgnoreCaseOrderByCreatedAtDesc(String invoiceNumber, String customerPhoneNumber);

    Optional<Invoice> findByInvoiceNumberAndTicket_TrackingCodeAndCustomer_PhoneNumber(String invoiceNumber, String trackingCode, String phoneNumber);

    List<Invoice> findByTicket_TrackingCodeAndCustomer_PhoneNumberOrderByCreatedAtDesc(String trackingCode, String phoneNumber);
}
