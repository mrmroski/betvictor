package com.recruitment.task.service;

import com.recruitment.task.exceptions.TextProcessingFailedException;
import com.recruitment.task.service.kafka.ProcessedTextEvent;
import com.recruitment.task.service.utility.ParagraphStatistics;
import com.recruitment.task.service.utility.ParagraphUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessedTextService {

    private final LoripsumApiImpl loripsumApiImpl;
    private final ParagraphUtility paragraphUtility;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ProcessedText processText(Integer numberOfParagraphs) {

        long startTime = System.nanoTime();
        List<CompletableFuture<String>> futures = new ArrayList<>();
        AtomicBoolean isInterrupted = new AtomicBoolean(false);
        for (int i = 0; i < numberOfParagraphs; i++) {
            futures.add(loripsumApiImpl.getLoremIpsum(isInterrupted));
        }
        List<String> result = awaitCompletion(futures, isInterrupted);

        ParagraphStatistics statistics = paragraphUtility.process(result);

        long endTime = System.nanoTime();
        long durationMillis = (endTime - startTime) / 1_000_000;


        ProcessedText processedText = ProcessedText.builder()
                .avgParagraphSize(statistics.avgParagraphSize())
                .mostFrequentWord(statistics.mostFrequentWord())
                .avgParagraphProcessingTime(durationMillis / numberOfParagraphs)
                .totalProcessingTime(durationMillis)
                .build();


        applicationEventPublisher.publishEvent(new ProcessedTextEvent(processedText, Instant.now()));
        return processedText;
    }

    private List<String> awaitCompletion(List<CompletableFuture<String>> futures, AtomicBoolean isInterrupted) {
        try {
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                    .get(40000, TimeUnit.MILLISECONDS);
            return futures.stream()
                    .map(unpackFuture())
                    .toList();
        } catch (Exception exception) {
            isInterrupted.set(true);
            log.warn("Process failed, cause: ", exception);
            throw new TextProcessingFailedException(exception);
        }
    }

    private static Function<CompletableFuture<String>, String> unpackFuture() {
        return future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException exception) {
                throw new TextProcessingFailedException(exception);
            }
        };
    }



}
