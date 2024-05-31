package com.project.shopapp.services;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.exception.InvalidParamException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ProductService{
    Product createProduct(ProductDTO productDTO) throws DataNotFoundException;
    Product getProductById(Long productId) throws DataNotFoundException;
    Page<ProductResponse> getAllProducts(String keyword,Long categoryId,PageRequest pageRequest);
    Product updateProduct(Long productId, ProductDTO productDTO);
    void deleteProduct(Long productId);

    boolean existsByProductName(String productName);
    ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws DataNotFoundException, InvalidParamException;
    Optional<Product> getDetailProduct(Long productId);
    List<Product> findProductByIds(List<Long> productIds);

}
