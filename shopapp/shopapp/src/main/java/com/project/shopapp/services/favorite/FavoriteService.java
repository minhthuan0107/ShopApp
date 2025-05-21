package com.project.shopapp.services.favorite;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.favorite.FavoriteActionResult;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Favorite;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.FavoriteRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.favorite.FavoriteResponse;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteService implements IFavoriteService {
    @Autowired
     private FavoriteRepository favoriteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private LocalizationUtils localizationUtils;


    @Override
    public FavoriteActionResult addProductToFavorite(Long userId, Long productId) throws Exception {
        User user = userRepository.findById(userId).
                orElseThrow(()-> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND)));
        Product product = productRepository.findById(productId).
                orElseThrow(()-> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_NOT_FOUND)));
        Optional<Favorite> existingFavorite = favoriteRepository.findByUserIdAndProductId(userId, productId);
        if (existingFavorite.isPresent()) { // Kiểm tra xem favorite đã được thêm vào hay chưa
            favoriteRepository.delete(existingFavorite.get());
            return new FavoriteActionResult(null, false);
        } else {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setProduct(product);
            favoriteRepository.save(favorite);
            return new FavoriteActionResult(favorite,true);
        }
    }

    @Override
    @Transactional
    public void deleteFavoriteProduct(Long favoriteId) throws Exception {
        if (!favoriteRepository.existsById(favoriteId)) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.FAVORITE_NOT_FOUND,favoriteId));
        }
        favoriteRepository.deleteById(favoriteId);
    }

    @Override
    public List<FavoriteResponse> getFavoriteProductsByUserId(Long userId) throws Exception {
        if (!userRepository.existsById(userId)) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND));
        }
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);

        List<FavoriteResponse> favoriteResponses = favorites.stream()
                .map(favorite -> FavoriteResponse.fromFavorite(favorite))
                .collect(Collectors.toList());
        return favoriteResponses;
    }
}
