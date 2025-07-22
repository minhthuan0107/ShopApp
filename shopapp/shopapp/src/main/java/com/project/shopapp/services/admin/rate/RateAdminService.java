package com.project.shopapp.services.admin.rate;

import com.project.shopapp.models.Comment;
import com.project.shopapp.models.Rate;
import com.project.shopapp.repositories.RateRepository;
import com.project.shopapp.responses.admin.rate.RateResponse;
import com.project.shopapp.responses.comment.CommentReplyResponse;
import com.project.shopapp.responses.comment.CommentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RateAdminService implements IRateAdminService {
    @Autowired
    private RateRepository rateRepository;
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
}
