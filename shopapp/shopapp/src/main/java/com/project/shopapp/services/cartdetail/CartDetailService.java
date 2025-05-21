package com.project.shopapp.services.cartdetail;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.cartdetail.CartDetailsUpdateDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Cart;
import com.project.shopapp.models.CartDetail;
import com.project.shopapp.repositories.CartDetailRepository;
import com.project.shopapp.repositories.CartRepository;
import com.project.shopapp.responses.cartdetail.CartDetailResponse;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartDetailService implements ICartDetailService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartDetailRepository cartDetailRepository;
    @Autowired
    private LocalizationUtils localizationUtils;

    public BigDecimal getTotalPrice(Long cartId) {
        return cartDetailRepository.sumTotalPriceByCartId(cartId);
    }

    public Integer getTotalQuantity(Long cartId) {
        return cartDetailRepository.sumQuantityByCartId(cartId);
    }

    @Override
    public List<CartDetailResponse> getCartDetailsByUserId(Long userId) throws Exception {
        Cart cart = cartRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.CART_NOT_FOUND, userId)));
        List<CartDetail> cartDetails = cartDetailRepository.findByCartId(cart.getId());
        return cartDetails.stream()
                .map(CartDetailResponse::fromCartDetail)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCartDetailById(long userId, long cartDetailId) throws Exception {
        Cart cart = cartRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.CART_NOT_FOUND, userId)));
        if (!cartDetailRepository.existsById(cartDetailId)) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.CARTDETAIL_NOT_FOUND, cartDetailId));
        }
        cartDetailRepository.deleteById(cartDetailId);
        int totalQuantity = getTotalQuantity(cart.getId());
        BigDecimal totalPrice = getTotalPrice(cart.getId());
        cart.setTotalQuantity(totalQuantity);
        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public List<CartDetailResponse> updateCartDetails(Long userId,
                                                      List<CartDetailsUpdateDto> cartDetailsUpdateDto) throws Exception {

        Cart cart = cartRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.CART_NOT_FOUND, userId)));
        // Lấy danh sách cartDetailId từ request
        List<Long> cartDetailIds = cartDetailsUpdateDto.stream()
                .map(CartDetailsUpdateDto::getCartDetailId)
                .collect(Collectors.toList());
        List<CartDetail> cartDetails = cartDetailRepository.findAllById(cartDetailIds);
        //Kiểm tra cartDetail gửi từ request có thuộc cart hay không
        Map<Long, CartDetail> cartDetailMap = cartDetails.stream()
                .filter(cd -> cd.getCart().getId().equals(cart.getId()))
                .collect(Collectors.toMap(CartDetail::getId, cd -> cd));
        // Kiểm tra nếu có cartDetailId nào không thuộc về giỏ hàng của user
        cartDetailIds.stream()
                .filter(id -> !cartDetailMap.containsKey(id))
                .findFirst()
                .ifPresent(id -> {
                    throw new RuntimeException(
                            localizationUtils.getLocalizedMessage(MessageKeys.CARTDETAIL_NOT_BELONG_TO_USER, id));
                });
        List<CartDetail> updatedCartDetails = cartDetailsUpdateDto.stream()
                .map(dto -> {
                    CartDetail cartDetail = cartDetailMap.get(dto.getCartDetailId());
                    cartDetail.setQuantity(dto.getNewQuantity());
                    cartDetail.setTotalPrice(BigDecimal.valueOf(dto.getNewQuantity()).multiply(cartDetail.getUnitPrice()));
                    return cartDetail;
                })
                .collect(Collectors.toList());
        cartDetailRepository.saveAll(updatedCartDetails);
        //Set tại total Quantity và toltalPrice vào cart
        int totalQuantity = getTotalQuantity(cart.getId());
        BigDecimal totalPrice = getTotalPrice(cart.getId());
        cart.setTotalQuantity(totalQuantity);
        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);
        return updatedCartDetails.stream()
                .map(CartDetailResponse::fromCartDetail)
                .collect(Collectors.toList());
    }
}



