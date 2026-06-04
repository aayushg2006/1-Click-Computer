package com.oneclick.repair.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductTest {

    @Test
    void getStockQuantityReturnsZeroWhenNull() {
        Product product = Product.builder().build();
        assertEquals(0, product.getStockQuantity());
    }
}
