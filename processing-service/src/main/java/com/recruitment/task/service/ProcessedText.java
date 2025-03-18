package com.recruitment.task.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ProcessedText(
        @JsonProperty("freq_word")
        String mostFrequentWord,
        @JsonProperty("avg_paragraph_size")
        Long avgParagraphSize,
        @JsonProperty("avg_paragraph_processing_time")
        Long avgParagraphProcessingTime,
        @JsonProperty("total_processing_time")
        Long totalProcessingTime
) {
}