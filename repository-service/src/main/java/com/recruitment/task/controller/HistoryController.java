package com.recruitment.task.controller;

import com.recruitment.task.service.HistoryService;
import com.recruitment.task.service.ProcessedText;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/betvictor/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping
    public List<ProcessedTextDTO> getHistory() {
        List<ProcessedText> processedTexts = historyService.getProcessedTexts();

        return processedTexts.stream()
                .map(this::mapToDto)
                .toList();
    }

    private ProcessedTextDTO mapToDto(ProcessedText processedText) {
        return ProcessedTextDTO.builder()
                .freqWord(processedText.mostFrequentWord())
                .avgParagraphSize(processedText.avgParagraphSize())
                .avgParagraphProcessingTime(processedText.avgParagraphProcessingTime())
                .totalProcessingTime(processedText.totalProcessingTime())
                .build();
    }
}