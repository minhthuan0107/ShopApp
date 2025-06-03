package com.project.shopapp.services.cart;

import com.project.shopapp.commons.CartStatus;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.cartdetail.CartDetailDto;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Cart;
import com.project.shopapp.models.CartDetail;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.CartDetailRepository;
import com.project.shopapp.repositories.CartRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.cart.CartResponse;
import com.project.shopapp.responses.cartdetail.CartDetailResponse;
import com.project.shopapp.ultis.MessageKeys;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService implements ICartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private CartDetailRepository cartDetailRepository;
    public BigDecimal getTotalPrice(Long cartId) {
        return cartDetailRepository.sumTotalPriceByCartId(cartId);
    }
    public Integer getTotalQuantity(Long cartId) {
        return cartDetailRepository.sumQuantityByCartId(cartId);
    }
    @Override
    @Transactional
    public CartResponse addProductToCart(Long userId, CartDetailDto cartDetailDto) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND, userId)
                ));
        //Tìm cart theo userId nếu chưa có thì tạo mới cart
        Cart cart = cartRepository.findByUserIdAndActiveTrue(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setStatus(CartStatus.PENDING);
                    newCart.setTotalPrice(BigDecimal.ZERO);
                    newCart.setTotalQuantity(0);
                    newCart.setActive(true);
                    return cartRepository.save(newCart);
                });
        Product product = productRepository.findById(cartDetailDto.getProductId())
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(
                                MessageKeys.PRODUCT_NOT_FOUND, cartDetailDto.getProductId())
                ));
        //Kiểm tra như số lượng sản phẩm hết thì đưa ra thông báo sản phẩm đã hết
        if (product.getQuantity() < 1) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_OUT_OF_STOCK));
        }
        //Kiểm tra cartdetail có trong csdl hay chưa , nếu chưa có thì tạo mới, nếu có rồi thì set totalprice và quantity
        CartDetail cartDetail = cartDetailRepository.findByCartIdAndProductId(
                cart.getId(), product.getId()).orElseGet(() -> {
            CartDetail newCartDetail = new CartDetail();
            newCartDetail.setCart(cart);
            newCartDetail.setProduct(product);
            newCartDetail.setQuantity(0);
            newCartDetail.setUnitPrice(product.getPrice());
            newCartDetail.setTotalPrice(BigDecimal.ZERO);
            return newCartDetail;
        });
        // Kiểm tra nếu thêm 1 nữa thì vượt quá số lượng tồn kho không
        if (cartDetail.getQuantity() + 1 > product.getQuantity()) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_OUT_OF_STOCK));
        }
        // Nếu sản phẩm đã tồn tại trong giỏ, tăng quantity và cập nhật totalprice
        cartDetail.setQuantity(cartDetail.getQuantity() + 1);
        cartDetail.setTotalPrice(cartDetail.getTotalPrice().add(product.getPrice()));
        cartDetailRepository.save(cartDetail);
        // Chuyển dữ liệu sang CartDetailResponse
        List<CartDetailResponse> cartDetailResponses = cartDetailRepository.findByCartId(cart.getId())
                .stream()
                .map(CartDetailResponse::fromCartDetail)
                .collect(Collectors.toList());
        //Set lại totalQuantity và totalPrice cho Cart
        int totalQuantity = getTotalQuantity(cart.getId());
        BigDecimal totalPrice = getTotalPrice(cart.getId());
        cart.setTotalQuantity(totalQuantity);
        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);
        return CartResponse.fromCart(cart, cartDetailResponses);
    }

    @Override
    public Long getCartItemCount(Long userId) {
        return cartRepository.findByUserIdAndActiveTrue(userId)
                .map(cart -> cartDetailRepository.countCartItems(cart.getId()))
                .orElse(0L);
    }
    @Override
    @Transactional
    public void clearCart(Long userId) throws Exception {
        Cart cart = cartRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.CART_NOT_FOUND)
                ));
        cart.setStatus(CartStatus.CHECKED_OUT);
        cart.setActive(false);
        cartRepository.save(cart);
        cartDetailRepository.deleteByCartId(cart.getId());
    }
}
