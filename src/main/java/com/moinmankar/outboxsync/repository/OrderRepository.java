package com.moinmankar.outboxsync.repository;

import com.moinmankar.outboxsync.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}