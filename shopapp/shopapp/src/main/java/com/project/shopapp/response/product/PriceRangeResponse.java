package com.project.shopapp.response.product;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceRangeResponse {
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
