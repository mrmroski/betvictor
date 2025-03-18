package com.recruitment.task.controller;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ProcessedTextDTO(
        String freqWord,
        Long avgParagraphSize,
        Long avgParagraphProcessingTime,
        Long totalProcessingTime
) {
}
