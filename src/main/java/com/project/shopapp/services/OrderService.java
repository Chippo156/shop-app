package com.project.shopapp.services.impl;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.Order;

import java.util.List;

public interface OrderService
{
    Order createProduct(OrderDTO orderDTO) throws DataNotFoundException;
    Order getProductById(Long productId);
    Order updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException;
    void deleteOrder(Long id);
    List<Order> findByUserId(Long userId);
}
