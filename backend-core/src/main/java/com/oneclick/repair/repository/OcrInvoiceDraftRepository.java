package com.oneclick.repair.repository;

import com.oneclick.repair.model.OcrInvoiceDraft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OcrInvoiceDraftRepository extends JpaRepository<OcrInvoiceDraft, UUID> {
    Optional<OcrInvoiceDraft> findByJobId(UUID jobId);
}
