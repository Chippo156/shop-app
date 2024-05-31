package com.project.shopapp.controller;

import com.github.javafaker.Faker;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.exception.InvalidParamException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductListResponse;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.services.ProductService;
import com.project.shopapp.untils.MessagesKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private LocalizationUtils localizationUtils;

    @PostMapping(value = "")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO,
                                           BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errors = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errors.toString());
            }
            Product newProduct = productService.createProduct(productDTO);
            return ResponseEntity.ok(productDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(localizationUtils.getLocalizedMessage(MessagesKeys.PRODUCT_CREATE_FAILED, e.getMessage()));
        }
    }

    @PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(@PathVariable Long id, @ModelAttribute("files") List<MultipartFile> files) {
        try {
            Product existProduct = productService.getProductById(id);
            files = files == null ? new ArrayList<MultipartFile>() : files;
            List<ProductImage> productImages = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file != null) {
                    if (file.getSize() == 0) {
                        continue;
                    }
                    if (file.getSize() > 10 * 1024 * 1024) // Kích thước lớn hơn 10MB
                    {
                        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(localizationUtils.getLocalizedMessage(MessagesKeys.PRODUCT_UPLOADS_FILE_LARGE));
                    }
                    String contentType = (file.getContentType());
                    System.out.println(contentType.toString());
                    if (contentType == null || !contentType.startsWith("image/")) {
                        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(localizationUtils.getLocalizedMessage(MessagesKeys.PRODUCT_UPLOADS_MUST_BE_IMAGE));
                    }

                    //lUƯU FILE và cập nhat thumbnail
                    String fileName = storeFile(file);
                    ProductImage productImage = productService.createProductImage(existProduct.getId(),
                            ProductImageDTO.builder()
                                    .image_url(fileName).build());
                    productImages.add(productImage);
                }
            }
            return ResponseEntity.ok().body(productImages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            Path path = Paths.get("uploads/", imageName);

            UrlResource resource = new UrlResource(path.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

    }

    private String storeFile(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        //Thêm UUID vao trước tên file để dam bao tên duy nhaất
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        //Đường dẫn đến thư mục muốn lưu file
        Path uploadDir = Paths.get("uploads");
        //Tạo thư mục nếu chưa tồn tại
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        //Tạo đường dẫn đến file
        Path destination = Paths.get(uploadDir.toString(), uniqueFileName);
        //sao chép file vào thư mục đích
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }

    @GetMapping("") //http://localhost:8088/api/v1/products?page=1&limit=10
    public ResponseEntity<ProductListResponse> getAllProducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "limit") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").ascending());
        Page<ProductResponse> pageProducts = productService.getAllProducts(keyword, categoryId, pageRequest);
        int totalPages = pageProducts.getTotalPages();
        List<ProductResponse> products = pageProducts.getContent();
        return ResponseEntity.ok(ProductListResponse.builder()
                .products(products)
                .totalPages(totalPages)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable(value = "id") Long id) {
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(ProductResponse.fromProduct(product));
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteProduct(@PathVariable(value = "id") Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(localizationUtils.getLocalizedMessage(MessagesKeys.PRODUCT_DELETE_SUCCESSFULLY, id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(localizationUtils.getLocalizedMessage(MessagesKeys.PRODUCT_DELETE_FAILED, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        try {
            productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(localizationUtils.getLocalizedMessage(MessagesKeys.PRODUCT_UPDATE_SUCCESSFULLY, id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(localizationUtils.getLocalizedMessage(MessagesKeys.PRODUCT_UPDATE_FAILED, e.getMessage()));
        }
    }

    @PostMapping("/fakeProduct")
    public ResponseEntity<String> generateFakeProducts() {
        Faker faker = new Faker();
        for (int i = 0; i < 100; i++) {
            String productName = faker.commerce().productName();
            if (productService.existsByProductName(productName)) {
                continue;
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price((float) faker.number().numberBetween(10, 90_000_000))
                    .description(faker.lorem().sentence())
                    .thumbnail("")
                    .categoryId((long) faker.number().numberBetween(1, 1))
                    .build();
            try {
                productService.createProduct(productDTO);

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Generate fake products successfully");
    }

    @GetMapping("/by-ids")
    public ResponseEntity<?> getProductByIds(@RequestParam("ids") String ids) {
        try {
            List<Long> productIds = Arrays.stream(ids.split(",")).map(Long::parseLong).toList();
            List<Product> products = productService.findProductByIds(productIds);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
