package com.oneclick.repair.service;

import com.oneclick.repair.dto.StoreReservationRequest;
import com.oneclick.repair.dto.StoreReservationResponse;
import com.oneclick.repair.model.Product;
import com.oneclick.repair.model.StoreReservation;
import com.oneclick.repair.repository.ProductRepository;
import com.oneclick.repair.repository.StoreReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreReservationService {

    private final StoreReservationRepository reservationRepository;
    private final ProductRepository productRepository;
    private final ReservationHoldService reservationHoldService;

    @Transactional
    public StoreReservationResponse createReservation(StoreReservationRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Error: Product not found!"));

        if (product.getStockQuantity() <= 0) {
            throw new RuntimeException("Sorry, this item is currently out of stock!");
        }

        StoreReservation newReservation = StoreReservation.builder()
                .product(product)
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .pickupTimeslot(request.getExpectedPickupTimeslot())
                .status("HELD")
                .build();

        StoreReservation savedReservation = reservationRepository.save(newReservation);

        boolean held = reservationHoldService.holdProduct(
                product.getId(),
                savedReservation.getId(),
                Duration.ofMinutes(15)
        );
        if (!held) {
            throw new RuntimeException("This product is already temporarily held for another reservation.");
        }

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status != STATUS_COMMITTED) {
                        reservationHoldService.releaseHold(product.getId(), savedReservation.getId());
                    }
                }
            });
        }

        product.setCurrentStock(product.getStockQuantity() - 1);
        productRepository.save(product);

        return StoreReservationResponse.builder()
                .reservationId(savedReservation.getId().toString())
                .productName(product.getName())
                .status(savedReservation.getStatus())
                .message("Reservation successful! Please visit the store during your timeslot.")
                .build();
    }

    @Transactional(readOnly = true)
    public java.util.List<StoreReservation> listReservations() {
        return reservationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public java.util.List<StoreReservation> searchByPhone(String phone) {
        return reservationRepository.findByCustomerPhoneContainingIgnoreCaseOrderByReservedAtDesc(phone == null ? "" : phone.trim());
    }
}
