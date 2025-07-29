package com.project.shopapp.services.admin.rate;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Rate;
import com.project.shopapp.repositories.RateRepository;
import com.project.shopapp.responses.admin.rate.RateResponse;
import com.project.shopapp.ultis.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class RateAdminService implements IRateAdminService {
    @Autowired
    private RateRepository rateRepository;
    @Autowired
    private LocalizationUtils localizationUtils;

    @Override
    public Page<RateResponse> getAllRates(PageRequest pageRequest, String keyword) {
        Page<Rate> ratePage;
        if (keyword == null || keyword.trim().isEmpty()) {
            ratePage = rateRepository.findAll(pageRequest);
        } else {
            ratePage = rateRepository.searchRatesByCommentUserFullName(keyword, pageRequest);
        }
        return ratePage.map(rate -> RateResponse.fromRate(rate));
    }

    @Override
    public void deleteRateById(Long rateId) throws Exception {
        if (!rateRepository.existsById(rateId)) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.RATING_NOT_FOUND,rateId));
        }
        rateRepository.deleteById(rateId);
    }
}
