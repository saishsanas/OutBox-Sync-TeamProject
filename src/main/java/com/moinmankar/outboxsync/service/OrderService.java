package com.moinmankar.outboxsync.service;


import com.moinmankar.outboxsync.Entity.EventType;
import com.moinmankar.outboxsync.Entity.Order;
import com.moinmankar.outboxsync.Entity.OutboxEvent;
import com.moinmankar.outboxsync.repository.OrderRepository;
import com.moinmankar.outboxsync.repository.OutboxEventRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;


    public OrderService(OrderRepository orderRepository, OutboxEventRepository outboxEventRepository) {
        this.orderRepository = orderRepository;
        this.outboxEventRepository = outboxEventRepository;
    }

    @Transactional
    public void createOrder(Long userId, Long productId, Integer quantity){

        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setQuantity(quantity);

        Order savedOrder = orderRepository.save(order);

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType(EventType.ORDER_CREATED);


        String payload = "{ \"orderId\": " + savedOrder.getId() +
                ", \"userId\": " + savedOrder.getUserId() +
                ", \"productId\": " + savedOrder.getProductId() + " }";
        outboxEvent.setPayload(payload);

        outboxEventRepository.save(outboxEvent);
    }
}
