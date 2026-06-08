package com.oneclick.repair.repository;

import com.oneclick.repair.model.OcrInvoiceJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OcrInvoiceJobRepository extends JpaRepository<OcrInvoiceJob, UUID> {
}
