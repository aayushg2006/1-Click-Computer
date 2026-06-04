package com.oneclick.repair.service;

import com.oneclick.repair.model.Customer;
import com.oneclick.repair.model.Invoice;
import com.oneclick.repair.repository.CustomerRepository;
import com.oneclick.repair.repository.LedgerAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WhatsAppAdminService {

    private final EvolutionWhatsAppService evolutionWhatsAppService;
    private final InvoiceDocumentService invoiceDocumentService;
    private final CustomerRepository customerRepository;
    private final LedgerAccountRepository ledgerAccountRepository;
    private final UpiLinkService upiLinkService;

    @Transactional
    public String sendInvoicePdf(String invoiceNumber) {
        Invoice invoice = invoiceDocumentService.getInvoiceEntity(invoiceNumber);
        if (invoice.getCustomer() == null || invoice.getCustomer().getPhoneNumber() == null) {
            throw new RuntimeException("Invoice customer phone number is missing");
        }

        String pdfUrl = invoiceDocumentService.buildInvoicePdfUrl(invoiceNumber);
        evolutionWhatsAppService.sendDocument(
                invoice.getCustomer().getPhoneNumber(),
                pdfUrl,
                invoiceNumber + ".pdf",
                "Your invoice from 1 Click Computer, invoice " + invoiceNumber,
                "application/pdf"
        );
        return "Invoice sent via WhatsApp";
    }

    @Transactional
    public String sendPaymentReminder(String phoneNumber, String customerName, String note) {
        String paymentLink = upiLinkService.buildPaymentLink(
                java.math.BigDecimal.ZERO,
                note != null ? note : "Payment reminder");
        String message = "Hello " + (customerName != null ? customerName : "Customer")
                + ", this is a friendly reminder from 1 Click Computer.\n\n"
                + (paymentLink != null
                ? "Please clear your pending amount using this UPI link:\n" + paymentLink
                : "Please clear your pending amount at the shop or reply to this message for a payment link.");
        evolutionWhatsAppService.sendText(phoneNumber, message);
        return "Reminder sent via WhatsApp";
    }

    @Transactional
    public String sendLedgerReminder(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        java.math.BigDecimal balance = ledgerAccountRepository.findByCustomerId(customerId)
                .map(account -> account.getCurrentBalance())
                .orElse(java.math.BigDecimal.ZERO);
        String paymentLink = upiLinkService.buildPaymentLink(balance, "Payment for 1 Click Computer");
        String message = "Hello " + customer.getFullName() + ", your current outstanding balance is INR " + balance
                + (paymentLink != null
                ? ". Please pay using this UPI link:\n" + paymentLink
                : ". Please pay at the shop or ask us for a payment link.");
        evolutionWhatsAppService.sendText(customer.getPhoneNumber(), message);
        return "Ledger reminder sent via WhatsApp";
    }
}
