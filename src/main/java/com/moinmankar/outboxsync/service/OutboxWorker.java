package com.moinmankar.outboxsync.service;


import com.moinmankar.outboxsync.Entity.EventStatus;
import com.moinmankar.outboxsync.Entity.OutboxEvent;
import com.moinmankar.outboxsync.repository.OutboxEventRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDateTime;
import java.util.List;

@Component
public class OutboxWorker {

    private static final Logger log = LoggerFactory.getLogger(OutboxWorker.class);

    private final OutboxEventRepository outboxEventRepository;
    private final RestTemplate restTemplate;

    private static final int MAX_RETRY = 5;

    public OutboxWorker(OutboxEventRepository outboxEventRepository, RestTemplate restTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void processOutboxEvents() {

        log.info("========== [WORKER START] ==========");

        List<OutboxEvent> events =
                outboxEventRepository.findByStatus(EventStatus.PENDING);


        log.info("[WORKER] Fetched {} pending events", events.size());

        for (OutboxEvent event : events) {

            log.info("[WORKER] Processing Event ID: {}", event.getId());


            if (event.getRetryCount() >= MAX_RETRY) {
                event.setStatus(EventStatus.FAILED);
                log.error("[FAILED] Max retries reached. Marking event as FAILED");
                outboxEventRepository.save(event);
                continue;
            }


            event.setLastAttemptedAt(LocalDateTime.now());

            log.info("[RETRY] Attempt: {}", event.getRetryCount() + 1);

            try {
                String url;

                if (event.getRetryCount() < 2) {
                    url = "https://httpbin.org/status/500"; // FAIL
                } else {
                    url = "https://httpbin.org/status/200"; // SUCCESS
                }

                restTemplate.getForObject(url, String.class);

                event.setStatus(EventStatus.SENT);
                log.info("[SENT] External API call successful");

            } catch (Exception e) {

                event.setRetryCount(event.getRetryCount() + 1);
                log.warn("[FAILED] External API failed. Retrying...", e);
            }

            outboxEventRepository.save(event);
        }

        log.info("========== [WORKER END] ==========");
    }
}