package com.project.shopapp.controllers.user;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.customer.rate.RatingDto;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.rate.RatingResponse;
import com.project.shopapp.services.customer.rate.RatingService;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/rates")
@AllArgsConstructor
public class RateController {
    private final RatingService ratingService;
    private final LocalizationUtils localizationUtils;

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("submit")
    public ResponseEntity<ResponseObject> submitRating(@RequestBody @Valid RatingDto rateDto,
                                                       BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(errorMessages.toString())
                    .build());
        }
        try {
            RatingResponse ratingResponse = ratingService.submitRating(rateDto);
            return ResponseEntity.status(HttpStatus.CREATED).
                    body(ResponseObject.builder()
                    .data(ratingResponse)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.RATING_SUBMIT_SUCCESSFULLY, ratingResponse.getProductId()))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }
    @GetMapping("/{productId}")
    public ResponseEntity<ResponseObject> getRatingsByProductId(@PathVariable Long productId) {
        try {
            List<RatingResponse> ratingResponses = ratingService.getRatingsByProductId(productId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(ratingResponses)
                    .message(localizationUtils.getLocalizedMessage(
                            MessageKeys.RATING_FETCH_SUCCESSFULLY, productId))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }
}

