package com.moinmankar.outboxsync.controller;

import com.moinmankar.outboxsync.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public String createOrder() {
        orderService.createOrder(1L, 101L, 2);
        return "Order Created";
    }
}