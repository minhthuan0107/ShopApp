package com.project.shopapp.services.admin.rate;

import com.project.shopapp.responses.admin.rate.RateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IRateAdminService {
    Page<RateResponse> getAllRates(PageRequest pageRequest, String keyword);
}
