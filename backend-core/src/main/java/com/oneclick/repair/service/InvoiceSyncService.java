package com.oneclick.repair.service;

import com.oneclick.repair.dto.OfflineInvoiceSyncRequest;
import com.oneclick.repair.dto.SyncResponse;
import com.oneclick.repair.model.Customer;
import com.oneclick.repair.model.Invoice;
import com.oneclick.repair.model.InvoiceItem;
import com.oneclick.repair.repository.CustomerRepository;
import com.oneclick.repair.repository.InvoiceItemRepository;
import com.oneclick.repair.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InvoiceSyncService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public SyncResponse processOfflineSync(OfflineInvoiceSyncRequest request) {
        int syncedCount = 0;

        for (OfflineInvoiceSyncRequest.OfflineInvoiceDTO offlineInvoice : request.getInvoices()) {
            String status = (offlineInvoice.getIsPaid() != null && offlineInvoice.getIsPaid()) ? "PAID" : "UNPAID";

            Customer customer = null;
            if (offlineInvoice.getCustomerPhone() != null && !offlineInvoice.getCustomerPhone().isBlank()) {
                customer = customerRepository.findByPhoneNumber(offlineInvoice.getCustomerPhone())
                        .orElseGet(() -> customerRepository.save(Customer.builder()
                                .fullName(offlineInvoice.getCustomerName())
                                .phoneNumber(offlineInvoice.getCustomerPhone())
                                .build()));
            }

            Invoice newInvoice = Invoice.builder()
                    .customer(customer)
                    .invoiceNumber(offlineInvoice.getInvoiceNumber())
                    .subTotal(offlineInvoice.getSubTotal())
                    .gstAmount(offlineInvoice.getTaxAmount())
                    .totalAmount(offlineInvoice.getGrandTotal())
                    .paymentStatus(status)
                    .build();

            Invoice savedInvoice = invoiceRepository.save(newInvoice);

            if (offlineInvoice.getItems() != null) {
                for (OfflineInvoiceSyncRequest.OfflineInvoiceItemDTO itemDTO : offlineInvoice.getItems()) {
                    InvoiceItem item = InvoiceItem.builder()
                            .invoice(savedInvoice)
                            .description(itemDTO.getItemDescription())
                            .quantity(itemDTO.getQuantity())
                            .unitPrice(itemDTO.getUnitPrice())
                            .build();
                    invoiceItemRepository.save(item);
                }
            }

            syncedCount++;
        }

        return SyncResponse.builder()
                .message("Successfully synced all offline data to the cloud database.")
                .totalInvoicesSynced(syncedCount)
                .build();
    }
}
