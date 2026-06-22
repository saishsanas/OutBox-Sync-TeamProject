package com.moinmankar.outboxsync.repository;

import com.moinmankar.outboxsync.Entity.EventStatus;
import com.moinmankar.outboxsync.Entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByStatus(EventStatus status);
}