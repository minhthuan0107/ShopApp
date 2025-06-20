package com.project.shopapp.services.customer.rate;

import com.project.shopapp.dtos.customer.rate.RatingDto;
import com.project.shopapp.responses.rate.RatingResponse;

import java.util.List;


public interface IRatingService {
    RatingResponse submitRating (RatingDto ratingDto) throws Exception;

    List<RatingResponse> getRatingsByProductId(Long productId) throws Exception;
}
