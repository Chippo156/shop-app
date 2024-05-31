package com.project.shopapp.services.impl;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.exception.InvalidParamException;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repository.CategoryRepository;
import com.project.shopapp.repository.ProductImageRepository;
import com.project.shopapp.repository.ProductRepository;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.services.ProductService;
import com.project.shopapp.untils.MessagesKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class iProductService implements ProductService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private LocalizationUtils localizationUtils;

    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category existCategory = categoryRepository.findById(productDTO.getCategoryId()).orElseThrow(()
                -> new DataNotFoundException("Cannot find category with id: " + productDTO.getCategoryId()));
        Product product = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .thumbnail(productDTO.getThumbnail())
                .category(existCategory)
                .build();
        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Long productId) throws DataNotFoundException {
        return productRepository.findById(productId).orElseThrow(() -> new DataNotFoundException("Cannot find product with id: " + productId));
    }

    @Override
    public Page<ProductResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest) {
        //Lấy danh sách sản phẩm theo trang va limit
        return productRepository.searchProducts(categoryId, keyword, pageRequest).map(ProductResponse::fromProduct);
    }

    @Override
    public Product updateProduct(Long productId, ProductDTO productDTO) {
        Product existProduct = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        existProduct.setName(productDTO.getName());
        existProduct.setPrice(productDTO.getPrice());
        existProduct.setDescription(productDTO.getDescription());
        existProduct.setThumbnail(productDTO.getThumbnail());
        existProduct.setCategory(categoryRepository.findById(productDTO.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found")));
        return productRepository.save(existProduct);
    }

    @Override
    public void deleteProduct(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            productRepository.deleteById(productId);
        } else {
            throw new RuntimeException("Product not found");
        }
    }

    @Override
    public boolean existsByProductName(String productName) {
        return productRepository.existsByName(productName);
    }

    @Override
    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws DataNotFoundException, InvalidParamException {
        Product existProduct = productRepository.findById(productId).orElseThrow(()
                -> new DataNotFoundException("Cannot find product with id: " + productImageDTO.getProductId()));

        ProductImage newProductImage = ProductImage.builder()
                .product(existProduct)
                .image_url(productImageDTO.getImage_url())
                .build();
        //Không cho insert quas 5 anh cho 1 sp
        int size = productImageRepository.findAllByProductId(productId).size();
        if (size >= 5) {
            throw new InvalidParamException(localizationUtils.getLocalizedMessage(MessagesKeys.PRODUCT_UPLOADS_MAX_5_IMAGES));
        }
        return productImageRepository.save(newProductImage);
    }

    @Override
    public Optional<Product> getDetailProduct(Long productId) {
        return productRepository.getDetailProduct(productId);
    }

    @Override
    public List<Product> findProductByIds(List<Long> productIds) {
        try {

            return productRepository.findProductByIds(productIds);

        } catch (Exception e) {
            throw new RuntimeException("Product not found");
        }
    }
}
