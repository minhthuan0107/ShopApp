package com.project.shopapp.responses.product;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductListResponse {
    private List<ProductResponse> productResponses;
    private int totalPages;
    private long totalItems;
}
