package com.recruitment.task.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TextEventListener {

    private final CreateTextEntityCommandHandler commandHandler;

    @KafkaListener(id = "TextEventListener",
            topics = "words.processed",
            concurrency = "${application.kafka.concurrency:1}")
    public void listen(@Payload ProcessedTextEvent event) {
        try {
            commandHandler.handle(
                    CreateTextEntityCommand.builder()
                            .mostFrequentWord(event.processedText().mostFrequentWord())
                            .avgParagraphSize(event.processedText().avgParagraphSize())
                            .avgParagraphProcessingTime(event.processedText().avgParagraphProcessingTime())
                            .totalProcessingTime(event.processedText().totalProcessingTime())
                            .createdAt(event.createdAt())
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to process text event: {}", e.getMessage(), e);
        }
    }
}