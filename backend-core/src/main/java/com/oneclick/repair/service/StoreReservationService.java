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

@Service
@RequiredArgsConstructor
public class StoreReservationService {

    // The Service needs these two workers to talk to the database
    private final StoreReservationRepository reservationRepository;
    private final ProductRepository productRepository;

    // @Transactional means if anything crashes in this method, it rolls back any database changes so data isn't corrupted.
    @Transactional
    public StoreReservationResponse createReservation(StoreReservationRequest request) {
        
        // 1. Find the product the customer wants in the database
        // If someone sends a bad product ID, we throw an error.
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Error: Product not found!"));

        // 2. Check if it's actually in stock
        if (product.getStockQuantity() <= 0) {
            throw new RuntimeException("Sorry, this item is currently out of stock!");
        }

        // 3. Create the new Reservation Model (The data that goes into the database)
        StoreReservation newReservation = StoreReservation.builder()
                .product(product)
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .pickupTimeslot(request.getExpectedPickupTimeslot())
                .status("HELD")
                .build();

        // 4. Save it to the database
        StoreReservation savedReservation = reservationRepository.save(newReservation);

        // NOTE: In the future, right here is where we will add the code to 
        // temporarily lock the stock quantity inside your Redis cache!

        // 5. Build and return the Response DTO (The box we send back to Next.js)
        return StoreReservationResponse.builder()
                .reservationId(savedReservation.getId().toString())
                .productName(product.getName())
                .status(savedReservation.getStatus())
                .message("Reservation successful! Please visit the store during your timeslot.")
                .build();
    }
}