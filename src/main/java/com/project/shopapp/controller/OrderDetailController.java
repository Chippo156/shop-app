package com.project.shopapp.controller;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.responses.OrderDetailResponse;
import com.project.shopapp.services.OrderDetailService;
import com.project.shopapp.untils.MessagesKeys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order-details")
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private LocalizationUtils localizationUtils;
    @GetMapping("order/{order_id}")
    public ResponseEntity<?> getOrderDetails(@Valid @PathVariable("order_id") Long orderId
    ) {
        try
        {
           List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);
           List<OrderDetailResponse> orderDetailResponses = orderDetails.stream().map(OrderDetailResponse::fromOrderDetail).toList();
            return ResponseEntity.ok(orderDetailResponses);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createOrderDetail(@Valid @RequestBody OrderDetailDTO orderDetailDTO) {
       try
       {
           orderDetailService.createOrderDetail(orderDetailDTO);
           return ResponseEntity.ok("create successfully");
       }
       catch (Exception e)
       {
           return ResponseEntity.badRequest().body(e.getMessage());
       }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetailByID(@Valid @PathVariable("id") Long Id) throws DataNotFoundException {
        try
        {
            OrderDetail orderDetail =  orderDetailService.getOrderDetailById(Id);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail( @Valid @PathVariable Long id,
                                         @Valid @RequestBody OrderDetailDTO orderDetailDTO
    ) {
        try
        {
          OrderDetail orderDetail =  orderDetailService.updateOrderDetail(id, orderDetailDTO);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail(@Valid @PathVariable Long id) {
       try
       {
           orderDetailService.deleteOrderDetail(id);
           return ResponseEntity.ok().body(localizationUtils.getLocalizedMessage(MessagesKeys.ORDER_DETAILS_DELETE_SUCCESSFULLY,id));
       }
       catch (Exception e)
       {
           return ResponseEntity.badRequest().body(e.getMessage());
       }
    }
}
