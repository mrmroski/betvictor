package com.recruitment.task.service;

import com.recruitment.task.repository.TextEntity;
import com.recruitment.task.repository.TextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HistoryService {

    private static final int FIRST_PAGE = 0;
    private static final String CREATION_DATE = "creationDate";

    @Value("${application.variables.history-length}")
    private int historyLength;

    private final TextRepository textRepository;

    public List<ProcessedText> getProcessedTexts() {
        Page<TextEntity> textEntitiesPage = textRepository.findAll(
                PageRequest.of(FIRST_PAGE, historyLength, Sort.by(Sort.Direction.DESC, CREATION_DATE)));
        return textEntitiesPage.stream()
                .map(this::mapFromEntity)
                .toList();
    }

    ProcessedText mapFromEntity(TextEntity textEntity) {
        return ProcessedText.builder()
                .totalProcessingTime(textEntity.getTotalProcessingTime())
                .creationDate(textEntity.getCreationDate())
                .avgParagraphSize(textEntity.getAvgParagraphSize())
                .mostFrequentWord(textEntity.getMostFrequentWord())
                .avgParagraphProcessingTime(textEntity.getAvgParagraphProcessingTime())
                .build();
    }
}