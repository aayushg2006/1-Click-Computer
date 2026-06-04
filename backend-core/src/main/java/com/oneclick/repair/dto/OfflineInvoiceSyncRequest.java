package com.oneclick.repair.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OfflineInvoiceSyncRequest {
    
    // A list of all the invoices the phone saved while offline
    private List<OfflineInvoiceDTO> invoices;

    @Data
    public static class OfflineInvoiceDTO {
        // The temporary invoice number the phone generated (e.g., "INV-OFF-101")
        private String invoiceNumber;
        
        // We capture basic customer details for the bill
        private String customerName;
        private String customerPhone;
        
        private BigDecimal subTotal;
        private BigDecimal taxAmount;
        private BigDecimal grandTotal;
        
        private Boolean isPaid;
        private String paymentMethod;
        
        // The list of items inside this specific bill
        private List<OfflineInvoiceItemDTO> items;
    }

    @Data
    public static class OfflineInvoiceItemDTO {
        private String itemDescription;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }
}