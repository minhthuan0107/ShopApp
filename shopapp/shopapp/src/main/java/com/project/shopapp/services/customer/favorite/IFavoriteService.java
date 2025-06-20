package com.project.shopapp.services.customer.favorite;

import com.project.shopapp.dtos.customer.favorite.FavoriteActionResult;
import com.project.shopapp.responses.favorite.FavoriteResponse;

import java.util.List;

public interface IFavoriteService {
    FavoriteActionResult addProductToFavorite(Long userId, Long productId) throws Exception;

    void deleteFavoriteProduct (Long userId,Long productId) throws Exception;

    void deleteAllFavoriteProducts (Long userId) throws Exception;

    List<FavoriteResponse> getFavoriteProductsByUserId (Long userId)  throws Exception;
    Long getFavoriteItemsCount (Long userId);

}
