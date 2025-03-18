package com.recruitment.task.service.kafka;

import com.recruitment.task.repository.TextEntity;
import com.recruitment.task.repository.TextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CreateTextEntityCommandHandler {

    private final TextRepository textRepository;

    public void handle(CreateTextEntityCommand command) {
        textRepository.saveAndFlush(TextEntity.builder()
                .avgParagraphSize(command.avgParagraphSize())
                .totalProcessingTime(command.totalProcessingTime())
                .creationDate(command.createdAt())
                .mostFrequentWord(command.mostFrequentWord())
                .avgParagraphProcessingTime(command.avgParagraphProcessingTime())
                .build());
    }
}
