package com.project.shopapp.controllers.user;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.configurations.UserDetailsImpl;
import com.project.shopapp.dtos.customer.favorite.FavoriteActionResult;
import com.project.shopapp.responses.object.ResponseObject;
import com.project.shopapp.responses.customer.favorite.FavoriteResponse;
import com.project.shopapp.services.customer.favorite.FavoriteService;
import com.project.shopapp.ultis.MessageKeys;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/favorites")
@AllArgsConstructor
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private LocalizationUtils localizationUtils;

    @PostMapping("/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseObject> addProductToFavorite(@PathVariable Long productId,
                                                               Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        try {
            FavoriteActionResult favoriteAction = favoriteService.addProductToFavorite(userId, productId);
            if (favoriteAction.isAdded()) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ResponseObject.builder()
                                .status(HttpStatus.CREATED)
                                .data(favoriteAction)
                                .message(localizationUtils.getLocalizedMessage(
                                        MessageKeys.FAVORITE_ADD_PRODUCT_SUCCESSFULLY, productId))
                                .build());
            } else {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(ResponseObject.builder()
                                .status(HttpStatus.OK)
                                .data(favoriteAction)
                                .message(localizationUtils.getLocalizedMessage(
                                        MessageKeys.FAVORITE_REMOVE_PRODUCT_SUCCESSFULLY, productId))
                                .build());
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }

    @DeleteMapping("{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseObject> deleteFavoriteProduct(Authentication authentication,
                                                                @PathVariable Long productId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        try {
            favoriteService.deleteFavoriteProduct(userId,productId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message(localizationUtils.getLocalizedMessage(
                                    MessageKeys.FAVORITE_REMOVE_PRODUCT_SUCCESSFULLY, productId))
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }
    @DeleteMapping("/remove-all")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseObject> deleteAllFavoriteProducts(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        try {
            favoriteService.deleteAllFavoriteProducts(userId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message(localizationUtils.getLocalizedMessage(
                                    MessageKeys.FAVORITE_REMOVE_ALL_PRODUCT_SUCCESSFULLY))
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping()
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseObject> getFavoriteProductsByUserId(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        try {
            List<FavoriteResponse> favoriteResponses = favoriteService.getFavoriteProductsByUserId(userId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .data(favoriteResponses)
                            .message(localizationUtils.getLocalizedMessage(
                                    MessageKeys.FAVORITE_GET_LIST_PRODUCTS_SUCCESSFULLY,userId))
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(e.getMessage())
                    .build());
        }
    }
    @GetMapping("/count")
    public ResponseEntity<ResponseObject> getFavoriteItemsCount(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        Long favotiteItems = favoriteService.getFavoriteItemsCount(userId);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(favotiteItems)
                .message(localizationUtils.getLocalizedMessage(
                        MessageKeys.FAVORITE_GET_ITEMS_SUCCESSFULLY))
                .build());
    }
}
