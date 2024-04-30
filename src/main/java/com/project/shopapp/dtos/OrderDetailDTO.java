package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderDetailDTO {
    @JsonProperty("order_id")
    @Min(value = 1, message = "Order ID must be greater than or equal 1")
    private Long orderId;

    @JsonProperty("product_id")
    @Min(value = 1, message = "Product ID must be greater than or equal 1")
    private Long productId;

    @Min(value = 0, message = "Price must be greater than or equal 0")
    private float price;

    @Min(value = 1, message = "Number of products must be greater than or equal 1")
    @JsonProperty("number_of_products")
    private int numberOfProducts;

    @Min(value = 0, message = "Total money must be greater than or equal 0")
    @JsonProperty("total_money")
    private float totalMoney;


    private String color;

}
