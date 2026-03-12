package com.ecommerce.productservice.service;

import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.productservice.dto.ProductResponse;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.repo.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepo productRepo; // Gọi là productRepo

    public void createProduct(Product product) {
        productRepo.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    // Trong Product Service
    @Transactional // Quan trọng: Để đảm bảo tính nhất quán dữ liệu
    public void reduceStock(Long productId, Integer quantity) {
        // 1. Tìm sản phẩm
        Product product = productRepo.findById(productId)
                .orElseThrow(() ->  new BusinessException("Sản phẩm không tìm thấy"));

        // 2. Check lại lần cuối (đề phòng 2 người cùng mua 1 lúc)
        if (product.getStockQuantity() < quantity) {
            throw new BusinessException("Sản phẩm đã hết hàng trong kho!");
        }

        // 3. Trừ kho và Lưu
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepo.save(product);
    }

    @Transactional(readOnly = true) // Chỉ đọc dữ liệu, giúp tối ưu hiệu năng
    public ProductResponse getProductById(Long id) {
        // 1. Tìm trong DB, nếu không thấy thì báo lỗi
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm có ID: " + id));

        // 2. Map từ Entity sang DTO (ProductResponse)
        return mapToProductResponse(product);
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .skuCode(product.getSkuCode())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .build();
    }
}
