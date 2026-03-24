package com.ecommerce.productservice.api;

import com.ecommerce.productservice.dto.ProductDto;
import com.ecommerce.productservice.dto.ProductResponse;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecommerce.productservice.utils.EncryptionUtil;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductApi {

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @PostMapping("/create-product")
    public ResponseEntity<Map<String, String>> createProduct(@RequestBody Map<String, String> request) {
        try {
            String decrypted = EncryptionUtil.decrypt(request.get("payload"));
            Product product = objectMapper.readValue(decrypted, Product.class);
            productService.createProduct(product);
            String successMsg = objectMapper.writeValueAsString(Collections.singletonMap("message", "Product created successfully"));
            return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("payload", EncryptionUtil.encrypt(successMsg)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            String json = objectMapper.writeValueAsString(products);
            return ResponseEntity.ok(Collections.singletonMap("payload", EncryptionUtil.encrypt(json)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> getProductById(@PathVariable Long id) {
        try {
            ProductResponse product = productService.getProductById(id);
            String json = objectMapper.writeValueAsString(product);
            return ResponseEntity.ok(Collections.singletonMap("payload", EncryptionUtil.encrypt(json)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @PutMapping("/reduce-quantity/{id}")
    public ResponseEntity<Void> reduceQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        productService.reduceStock(id, quantity);
        return ResponseEntity.ok().build();
    }
}
