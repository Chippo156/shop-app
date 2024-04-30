package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductImageDTO {
    @Min(value=1, message = "Product ID must be greater than 0")
    @JsonProperty("product_id")
    private Long productId;
    @Size(min=5, max=200, message = "Image URL must be between 5 and 200 characters")
    @JsonProperty("image_url")
    private String image_url;
}
