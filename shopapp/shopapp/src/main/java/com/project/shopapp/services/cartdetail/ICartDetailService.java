package com.project.shopapp.services.cartdetail;

import com.project.shopapp.dtos.cartdetail.CartDetailsUpdateDto;
import com.project.shopapp.responses.cartdetail.CartDetailResponse;

import java.util.List;

public interface ICartDetailService {
List<CartDetailResponse> getCartDetailsByUserId (Long userId) throws Exception;

void deleteCartDetailById (long userId,long cartDetailId) throws Exception;

List<CartDetailResponse> updateCartDetails (Long userId,List<CartDetailsUpdateDto> cartDetailsUpdateDto) throws Exception;
}
