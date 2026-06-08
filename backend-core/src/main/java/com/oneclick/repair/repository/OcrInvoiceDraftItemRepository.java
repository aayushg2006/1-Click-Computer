package com.oneclick.repair.repository;

import com.oneclick.repair.model.OcrInvoiceDraftItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OcrInvoiceDraftItemRepository extends JpaRepository<OcrInvoiceDraftItem, UUID> {
}
