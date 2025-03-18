package com.recruitment.task.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventProducer {

    @Value("${application.processed-text.topic:words.processed}")
    private String topic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @EventListener
    void sendProcessedTextEvent(ProcessedTextEvent event) {
        try {
            kafkaTemplate.send(new ProducerRecord<>(topic, event.getKey(), event));
        } catch (Exception exception) {
            log.error("Failed to send event to topic {}: {}", topic, exception.getMessage(), exception);
        }
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}