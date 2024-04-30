package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductDTO{
    @NotBlank(message = "Product's name cannot be empty")
    @Size(min=3, max=300, message = "Product's name must be between 3 and 300 characters")
    private String name;
    @Min(value=0, message = "Price must be greater than or equal 0")
    @Max(value=10000000, message = "Price must be less than or equal 10.000.000")
    private float price;
    private String description;
    private String thumbnail;
    @JsonProperty("category_id")
    private Long categoryId;
}
