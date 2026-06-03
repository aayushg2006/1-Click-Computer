package com.oneclick.repair.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "pc_build_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PcBuildItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "build_configuration_id", nullable = false)
    private PcBuildConfiguration buildConfiguration;

    // Links directly to the exact product in your inventory
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity = 1;

    // We save the price *at the time of building* in case your inventory price changes later
    @Column(name = "locked_selling_price", nullable = false)
    private java.math.BigDecimal lockedSellingPrice;
}