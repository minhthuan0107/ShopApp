package com.project.shopapp.responses.admin.rate;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data
public class RateListResponse {
    private List<RateResponse> rateResponses;
    private int totalPages;
    private long totalItems;
    private int currentPage;
}
