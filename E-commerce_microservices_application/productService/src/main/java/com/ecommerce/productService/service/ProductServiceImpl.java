package com.ecommerce.productService.service;

import com.ecommerce.productService.entity.Product;
import com.ecommerce.productService.exception.ProductServiceCustomException;
import com.ecommerce.productService.model.ProductRequest;
import com.ecommerce.productService.model.ProductResponse;
import com.ecommerce.productService.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("adding product..");

        Product product = Product.builder().productName(productRequest.getName()).price(productRequest.getPrice())
                .quantity(productRequest.getQuantity()).build();
        this.productRepository.save(product);
        log.info("product created ");
        return product.getProductId();
    }
    @Override
    public ProductResponse getProductById(Long productId) {
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ProductServiceCustomException(" product not fount with id: " + productId, " PRODUCT_NOT_FOUND "));
        log.info(" get product ");
        ProductResponse productResponse = ProductResponse.builder()
                .id(product.getProductId())
                .productName(product.getProductName())
                .quantity(product.getQuantity())
                .price(product.getPrice())
                .build();
        return productResponse;
    }

    @Override
    public void reduceQuantity(long productId, long productQuantity) {
        log.info("Reduce quantity {} for Id {}", productQuantity, productId);
          Product product = productRepository.findById(productId)
                  .orElseThrow(()->new ProductServiceCustomException("Product not found for given id ", "PRODUCT_NOT_FOUND"));
          if (product.getQuantity() < productQuantity){
              throw new ProductServiceCustomException("Product does not have sufficient quantity", "INSUFFICIENT_QUANTITY");
          }
          product.setQuantity(product.getQuantity() - productQuantity);
          productRepository.save(product);
          log.info(" Product quantity updated successfully ");
    }
}