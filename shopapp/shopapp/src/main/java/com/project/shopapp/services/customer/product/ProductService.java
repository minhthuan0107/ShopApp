package com.project.shopapp.services.customer.product;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.repositories.RateRepository;
import com.project.shopapp.repositories.projection.PriceRangeProjection;
import com.project.shopapp.responses.customer.product.PriceRangeResponse;
import com.project.shopapp.responses.customer.product.ProductResponse;
import com.project.shopapp.ultis.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private LocalizationUtils localizationUtils;
    @Autowired
    private RateRepository rateRepository;
    @Override
    public ProductResponse getProductbyId(Long productId) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_NOT_FOUND, productId)));

        ProductResponse productResponse = ProductResponse.fromProduct(product);
        //Lấy thống kê dánh giá theo productId
        Object statObj = rateRepository.findStatByProductId(product.getId());
        if (statObj != null) {
            Object[] stat = (Object[]) statObj; // Ép về Object[] để truy cập từng cột
            Long count = (stat[0] != null) ? ((Number) stat[0]).longValue() : 0L;
            Double avg = (stat[1] != null) ? ((Number) stat[1]).doubleValue() : 0.0;

            productResponse.setTotalReviews(count);
            productResponse.setAverageRating(avg);
        } else {
            productResponse.setTotalReviews(0L);
            productResponse.setAverageRating(0.0);
        }
        return productResponse;
    }

    @Override
    public Page<ProductResponse> getAllProducts(PageRequest pageRequest) {
        //Lay danh sach san pham theo trang (page) va gioi han(limit)
        Page<Product> productPage = productRepository.findAll(pageRequest);
        //Lấy danh sách producId
        List<Long> productIds = productPage.stream()
                .map(product -> product.getId())
                .collect(Collectors.toList());
        //Lấy thống kê dánh giá theo productId
        List<Object[]> stats = rateRepository.findStatsByProductIds(productIds);

        // Tạo map: key = productId, value = Object[]{productId, count, avg}
        Map<Long, Object[]> reviewStatsMap = stats.stream().collect(Collectors.toMap(
                obj -> ((Number) obj[0]).longValue(), // key: productId
                obj -> obj            // value: Object[]{productId, count, avg}
        ));
        Page<ProductResponse> productResponses = productPage.map(product -> {
            ProductResponse response = ProductResponse.fromProduct(product);
            Object[] stat = reviewStatsMap.get(product.getId());
            if (stat != null) {
                Long count = ((Number) stat[1]).longValue();         // ép kiểu COUNT
                Double avg = ((Number) stat[2]).doubleValue();       // ép kiểu AVG

                response.setTotalReviews(count);
                response.setAverageRating(avg);
            } else {
                response.setTotalReviews(0L);
                response.setAverageRating(0.0);
            }

            return response;
        });
        return productResponses;
    }

    @Override
    public Page<ProductResponse> getProductbyCategoryId(
            Long categoryId,
            List<Long> brandIds,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            PageRequest pageRequest
    ) throws Exception {
        if (!categoryRepository.existsById(categoryId)) {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_NOT_FOUND, categoryId)
            );
        }
        if (brandIds != null && brandIds.isEmpty()) {
            brandIds = null;
        }
        Page<Product> productPage = productRepository.findByFilters(
                categoryId, brandIds, minPrice, maxPrice, pageRequest
        );
        //Lấy danh sách producId
        List<Long> productIds = productPage.stream()
                .map(product -> product.getId())
                .collect(Collectors.toList());
        //Lấy thống kê dánh giá theo productId
        List<Object[]> stats = rateRepository.findStatsByProductIds(productIds);

        // Tạo map: key = productId, value = Object[]{productId, count, avg}
        Map<Long, Object[]> reviewStatsMap = stats.stream().collect(Collectors.toMap(
                obj -> ((Number) obj[0]).longValue(), // key: productId
                obj -> obj            // value: Object[]{productId, count, avg}
        ));
        Page<ProductResponse> productResponses = productPage.map(product -> {
            ProductResponse response = ProductResponse.fromProduct(product);
            Object[] stat = reviewStatsMap.get(product.getId());
            if (stat != null) {
                Long count = ((Number) stat[1]).longValue();         // ép kiểu COUNT
                Double avg = ((Number) stat[2]).doubleValue();       // ép kiểu AVG

                response.setTotalReviews(count);
                response.setAverageRating(avg);
            } else {
                response.setTotalReviews(0L);
                response.setAverageRating(0.0);
            }

            return response;
        });
        return productResponses;
    }

    @Override
    public Page<ProductResponse> searchProductsByKeyword(String keyword,
                                                         BigDecimal minPrice,
                                                         BigDecimal maxPrice,
                                                         PageRequest pageRequest) {
        String[] words = keyword.trim().toLowerCase().split("\\s+");
        String likePattern = String.join("%", words);  // "samsung%s24"
        Page<Product> productPage = productRepository.searchByKeyword(
                likePattern, minPrice, maxPrice, pageRequest
        );
        //Lấy danh sách producId
        List<Long> productIds = productPage.stream()
                .map(product -> product.getId())
                .collect(Collectors.toList());
        //Lấy thống kê dánh giá theo productId
        List<Object[]> stats = rateRepository.findStatsByProductIds(productIds);

        // Tạo map: key = productId, value = Object[]{productId, count, avg}
        Map<Long, Object[]> reviewStatsMap = stats.stream().collect(Collectors.toMap(
                obj -> ((Number) obj[0]).longValue(), // key: productId
                obj -> obj            // value: Object[]{productId, count, avg}
        ));
        Page<ProductResponse> productResponses = productPage.map(product -> {
            ProductResponse response = ProductResponse.fromProduct(product);
            Object[] stat = reviewStatsMap.get(product.getId());
            if (stat != null) {
                Long count = ((Number) stat[1]).longValue();         // ép kiểu COUNT
                Double avg = ((Number) stat[2]).doubleValue();       // ép kiểu AVG

                response.setTotalReviews(count);
                response.setAverageRating(avg);
            } else {
                response.setTotalReviews(0L);
                response.setAverageRating(0.0);
            }

            return response;
        });
        return productResponses;
    }


    @Override
    public List<ProductResponse> getProductSuggestions(String keyword) {
        String[] words = keyword.trim().toLowerCase().split("\\s+");
        // Tạo chuỗi tìm kiếm sử dụng dấu '%' giữa các từ
        String likePattern = String.join("%", words);
        List<Product> products = productRepository.getProductSuggestions(likePattern);

        List<ProductResponse> productResponses = products.stream()
                .map(ProductResponse::fromProduct)
                .collect(Collectors.toList());
        return productResponses;
    }

    @Override
    public List<ProductResponse> getTop14BestSellingProducts() {
        // Lấy danh sách productId từ 8 sản phẩm bán chạy
        List<Product> products = productRepository.findTop14BestSellingProducts();
        //Lấy danh sách producId
        List<Long> productIds = products.stream()
                .map(product -> product.getId())
                .collect(Collectors.toList());
        //Lấy thống kê dánh giá theo productId
        List<Object[]> stats = rateRepository.findStatsByProductIds(productIds);
        // Tạo map: key = productId, value = Object[]{productId, count, avg}
        Map<Long, Object[]> reviewStatsMap = stats.stream().collect(Collectors.toMap(
                obj -> ((Number) obj[0]).longValue(), // key: productId
                obj -> obj            // value: Object[]{productId, count, avg}
        ));
        // Ghép thống kê vào từng ProductResponse
        List<ProductResponse> productResponses = products.stream().map(product -> {
            ProductResponse response = ProductResponse.fromProduct(product);
            Object[] stat = reviewStatsMap.get(product.getId());
            if (stat != null) {
                Long count = ((Number) stat[1]).longValue();         // ép kiểu COUNT
                Double avg = ((Number) stat[2]).doubleValue();       // ép kiểu AVG
                response.setTotalReviews(count);
                response.setAverageRating(avg);
            } else {
                response.setTotalReviews(0L);
                response.setAverageRating(0.0);
            }

            return response;
        }).collect(Collectors.toList());
        return productResponses;
    }
    @Override
    public List<ProductResponse> getTop14MostHighlyRatedProducts() {
        // Lấy danh sách productId từ 14 nổi bật theo đánh giá
        List<Product> products = productRepository.findTop14MostHighlyRatedProducts();
        //Lấy danh sách producId
        List<Long> productIds = products.stream()
                .map(product -> product.getId())
                .collect(Collectors.toList());
        //Lấy thống kê dánh giá theo productId
        List<Object[]> stats = rateRepository.findStatsByProductIds(productIds);
        // Tạo map: key = productId, value = Object[]{productId, count, avg}
        Map<Long, Object[]> reviewStatsMap = stats.stream().collect(Collectors.toMap(
                obj -> ((Number) obj[0]).longValue(), // key: productId
                obj -> obj            // value: Object[]{productId, count, avg}
        ));
        // Ghép thống kê vào từng ProductResponse
        List<ProductResponse> productResponses = products.stream().map(product -> {
            ProductResponse response = ProductResponse.fromProduct(product);
            Object[] stat = reviewStatsMap.get(product.getId());
            if (stat != null) {
                Long count = ((Number) stat[1]).longValue();         // ép kiểu COUNT
                Double avg = ((Number) stat[2]).doubleValue();       // ép kiểu AVG

                response.setTotalReviews(count);
                response.setAverageRating(avg);
            } else {
                response.setTotalReviews(0L);
                response.setAverageRating(0.0);
            }

            return response;
        }).collect(Collectors.toList());
        return productResponses;
    }
    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public PriceRangeResponse getPriceRange() {
        PriceRangeProjection projection = productRepository.findMinAndMaxPrice();
        return new PriceRangeResponse(projection.getMinPrice(), projection.getMaxPrice());
    }
}
