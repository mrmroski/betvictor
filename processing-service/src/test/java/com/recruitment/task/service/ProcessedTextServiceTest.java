package com.recruitment.task.service;

import com.recruitment.task.exceptions.TextProcessingFailedException;
import com.recruitment.task.service.kafka.ProcessedTextEvent;
import com.recruitment.task.service.utility.ParagraphStatistics;
import com.recruitment.task.service.utility.ParagraphUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;


import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessedTextServiceTest {

    @Mock
    private LoripsumApiImpl loripsumApiImpl;

    @Mock
    private ParagraphUtility paragraphUtility;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private ProcessedTextService processedTextService;

    private ParagraphStatistics mockStatistics;

    @BeforeEach
    void setUp() {
        mockStatistics = new ParagraphStatistics("lorem", 5L);
    }

    @Test
    void testProcessText_successfulProcessing_returnsProcessedText() {
        // Given
        int numberOfParagraphs = 2;
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("lorem ipsum");
        CompletableFuture<String> future2 = CompletableFuture.completedFuture("ipsum dolor");
        when(loripsumApiImpl.getLoremIpsum(any())).thenReturn(future1, future2);
        when(paragraphUtility.process(anyList())).thenReturn(mockStatistics);

        // When
        ProcessedText result = processedTextService.processText(numberOfParagraphs);

        // Then
        assertNotNull(result);
        assertEquals("lorem", result.mostFrequentWord());
        assertEquals(5L, result.avgParagraphSize());
        assertTrue(result.totalProcessingTime() >= 0);
        assertTrue(result.avgParagraphProcessingTime() >= 0);
        assertEquals(result.totalProcessingTime() / numberOfParagraphs, result.avgParagraphProcessingTime());

        verify(paragraphUtility).process(List.of("lorem ipsum", "ipsum dolor"));

        ArgumentCaptor<ProcessedTextEvent> eventCaptor = ArgumentCaptor.forClass(ProcessedTextEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        ProcessedTextEvent capturedEvent = eventCaptor.getValue();
        assertEquals(result, capturedEvent.processedText());
        assertNotNull(capturedEvent.createdAt());
    }

    @Test
    void testProcessText_timeoutExceeded_throwsTextProcessingFailedException() {
        // Given
        int numberOfParagraphs = 1;
        CompletableFuture<String> delayedFuture = new CompletableFuture<>();
        when(loripsumApiImpl.getLoremIpsum(any())).thenReturn(delayedFuture);

        // When & Then
        TextProcessingFailedException exception = assertThrows(TextProcessingFailedException.class,
                () -> processedTextService.processText(numberOfParagraphs));
        assertTrue(exception.getCause() instanceof java.util.concurrent.TimeoutException);

        verify(loripsumApiImpl).getLoremIpsum(any());
        verify(paragraphUtility, never()).process(anyList());
        verify(applicationEventPublisher, never()).publishEvent(any());
    }

    @Test
    void testProcessText_emptyResult_successfulProcessing() {
        // Given
        int numberOfParagraphs = 1;
        CompletableFuture<String> future = CompletableFuture.completedFuture("");
        when(loripsumApiImpl.getLoremIpsum(any())).thenReturn(future);
        when(paragraphUtility.process(List.of(""))).thenReturn(mockStatistics);

        // When
        ProcessedText result = processedTextService.processText(numberOfParagraphs);

        // Then
        assertNotNull(result);
        assertEquals("lorem", result.mostFrequentWord());
        assertEquals(5L, result.avgParagraphSize());

        verify(applicationEventPublisher).publishEvent(any(ProcessedTextEvent.class));
    }
}