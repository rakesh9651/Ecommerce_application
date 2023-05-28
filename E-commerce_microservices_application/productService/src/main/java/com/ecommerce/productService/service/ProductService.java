package com.ecommerce.productService.service;

import com.ecommerce.productService.model.ProductRequest;
import com.ecommerce.productService.model.ProductResponse;

public interface ProductService {
    long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(Long productId);

    void reduceQuantity(long productId, long productQuantity);
}
