package com.oneclick.repair.service;

import com.oneclick.repair.dto.admin.TicketUpdateDTO;
import com.oneclick.repair.model.Ticket;
import com.oneclick.repair.model.TicketUpdate;
import com.oneclick.repair.repository.TicketRepository;
import com.oneclick.repair.repository.TicketUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TicketMediaService {

    private final TicketRepository ticketRepository;
    private final TicketUpdateRepository ticketUpdateRepository;
    private final MinioStorageService minioStorageService;
    private final StorageKeyService storageKeyService;

    @Transactional
    public TicketUpdateDTO uploadTicketMedia(String trackingCode, MultipartFile file, String caption, Boolean visibleToCustomer) {
        Ticket ticket = ticketRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        String objectKey = storageKeyService.ticketMediaKey(trackingCode, file.getOriginalFilename());
        minioStorageService.store(file, objectKey);
        String url = minioStorageService.presignedGetUrl(objectKey, 60 * 24);

        ticket.getConditionPhotoUrls().add(url);
        ticketRepository.save(ticket);

        TicketUpdate update = TicketUpdate.builder()
                .ticket(ticket)
                .statusTitle(caption != null && !caption.isBlank() ? caption : "Hardware Condition Photo")
                .statusDescription("A new hardware condition photo has been uploaded.")
                .mediaUrl(url)
                .isVisibleToCustomer(visibleToCustomer == null || visibleToCustomer)
                .build();

        return toDto(ticketUpdateRepository.save(update));
    }

    private TicketUpdateDTO toDto(TicketUpdate update) {
        return TicketUpdateDTO.builder()
                .id(update.getId())
                .statusTitle(update.getStatusTitle())
                .statusDescription(update.getStatusDescription())
                .mediaUrl(update.getMediaUrl())
                .visibleToCustomer(update.getIsVisibleToCustomer())
                .createdAt(update.getCreatedAt())
                .build();
    }
}
