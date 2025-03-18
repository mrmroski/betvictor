package com.recruitment.task.service.kafka;

import lombok.Builder;

import java.time.Instant;

@Builder
public record CreateTextEntityCommand(
        String mostFrequentWord,
        Long avgParagraphSize,
        Long avgParagraphProcessingTime,
        Long totalProcessingTime,
        Instant createdAt
) {
}
