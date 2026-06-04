package com.oneclick.repair.service;

import com.oneclick.repair.dto.admin.StoreReservationAdminDTO;
import com.oneclick.repair.model.StoreReservation;
import com.oneclick.repair.repository.StoreReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationAdminService {

    private final StoreReservationRepository storeReservationRepository;

    @Transactional(readOnly = true)
    public List<StoreReservationAdminDTO> listReservations() {
        return storeReservationRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StoreReservationAdminDTO> searchByPhone(String phone) {
        return storeReservationRepository.findByCustomerPhoneContainingIgnoreCaseOrderByReservedAtDesc(phone == null ? "" : phone.trim())
                .stream()
                .map(this::toDto)
                .toList();
    }

    private StoreReservationAdminDTO toDto(StoreReservation reservation) {
        return StoreReservationAdminDTO.builder()
                .id(reservation.getId())
                .productId(reservation.getProduct() != null ? reservation.getProduct().getId() : null)
                .productName(reservation.getProduct() != null ? reservation.getProduct().getName() : null)
                .customerName(reservation.getCustomerName())
                .customerPhone(reservation.getCustomerPhone())
                .pickupTimeslot(reservation.getPickupTimeslot())
                .status(reservation.getStatus())
                .reservedAt(reservation.getReservedAt())
                .build();
    }
}
