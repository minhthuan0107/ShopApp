package com.project.shopapp.services.admin.product;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.responses.product.ProductResponse;
import com.project.shopapp.responses.user.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ProductAdminService implements IProductAdminService{
    @Autowired
    private ProductRepository productRepository;
    @Override
    public Page<ProductResponse> getAllProducts(PageRequest pageRequest, String keyword) {
        Page<Product> productPage;
        if (keyword == null || keyword.trim().isEmpty()) {
            //Nếu keyword rỗng thì lấy danh sách
            productPage = productRepository.findAll(pageRequest);
        } else {
            //Nếu có keyword thì tìm theo keyword
            productPage = productRepository.searchProductsByKeyword(keyword, pageRequest);
        }
        return productPage.map(product -> ProductResponse.fromProduct(product));
    }
}
