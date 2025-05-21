package com.project.shopapp.services.rate;

import com.project.shopapp.dtos.RatingDto;
import com.project.shopapp.responses.rate.RatingResponse;

import java.util.List;


public interface IRatingService {
    RatingResponse submitRating (RatingDto ratingDto) throws Exception;

    List<RatingResponse> getRatingsByProductId(Long productId) throws Exception;
}
