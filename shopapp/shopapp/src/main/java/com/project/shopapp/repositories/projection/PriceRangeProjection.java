package com.project.shopapp.repositories.projection;
import java.math.BigDecimal;

public interface PriceRangeProjection  {
    BigDecimal getMinPrice();
    BigDecimal getMaxPrice();
}
