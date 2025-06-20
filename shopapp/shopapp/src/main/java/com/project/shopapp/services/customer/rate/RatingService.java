package com.project.shopapp.services.customer.rate;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.customer.rate.RatingDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Comment;
import com.project.shopapp.models.Rate;
import com.project.shopapp.repositories.CommentRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.repositories.RateRepository;
import com.project.shopapp.responses.rate.RatingResponse;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RatingService implements IRatingService {
    private final LocalizationUtils localizationUtils;
    private final ProductRepository productRepository;
    private final CommentRepository commentRepository;
    private final RateRepository rateRepository;

    @Override
    @Transactional
    public RatingResponse submitRating(RatingDto ratingDto) throws Exception {
        Comment comment = commentRepository.findById(ratingDto.getCommentId())
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(
                                MessageKeys.COMMENT_NOT_FOUND, ratingDto.getCommentId())
                ));
        if (comment.getParentComment()!= null) {
            throw new RuntimeException(localizationUtils.getLocalizedMessage(
                    MessageKeys.ERROR_ONLY_PARENT_COMMENT_CAN_BE_RATED));
        }
        // Kiểm tra xem Comment đã có Rate chưa
        rateRepository.findByCommentId(ratingDto.getCommentId())
                .ifPresent(rate -> {
                    throw new RuntimeException(localizationUtils.getLocalizedMessage(
                            MessageKeys.ERROR_COMMENT_ALREADY_RATED));
                });
        // Nếu chưa từng đánh giá, tạo mới rating
        Rate rate = new Rate();
        rate.setUser(comment.getUser());
        rate.setProduct(comment.getProduct());
        rate.setRating(ratingDto.getRating());
        rate.setComment(comment);
        rateRepository.save(rate);
        return RatingResponse.fromRate(rate);
    }
    public List<RatingResponse> getRatingsByProductId(Long productId) throws Exception{
        if (!productRepository.existsById(productId)) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_NOT_FOUND, productId)
            );
        }
        List<RatingResponse> ratingResponses = rateRepository.findByProductId(productId)
                .stream()
                .map(RatingResponse::fromRate)
                .collect(Collectors.toList());
        return  ratingResponses;
    }
}
