package com.recruitment.task.service.kafka;

import com.recruitment.task.service.ProcessedText;

import java.time.Instant;

public record ProcessedTextEvent(
        ProcessedText processedText,
        Instant createdAt
) {
}
