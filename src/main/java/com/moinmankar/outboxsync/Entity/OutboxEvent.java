package com.moinmankar.outboxsync.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @Column(nullable = false)
    private Integer retryCount;

    private LocalDateTime createdAt;

    private LocalDateTime lastAttemptedAt;

    @PrePersist
    public void onCreate(){
        this.createdAt= LocalDateTime.now();

        if(this.status==null){
            this.status=EventStatus.PENDING;
        }

        if(this.retryCount==null){
            this.retryCount=0;
        }
    }
}
