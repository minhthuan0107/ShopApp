package com.project.shopapp.services.cartdetail;

import com.project.shopapp.dtos.CartDetailsUpdateDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.CartDetail;
import com.project.shopapp.response.cartdetail.CartDetailResponse;

import java.util.List;

public interface ICartDetailService {
List<CartDetailResponse> getCartDetailsByUserId (Long userId) throws Exception;

void deleteCartDetailById (long userId,long cartDetailId) throws Exception;

List<CartDetailResponse> updateCartDetails (Long userId,List<CartDetailsUpdateDto> cartDetailsUpdateDto) throws Exception;
}
