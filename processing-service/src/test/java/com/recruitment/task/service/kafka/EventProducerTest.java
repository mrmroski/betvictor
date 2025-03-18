package com.recruitment.task.service.kafka;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.recruitment.task.service.ProcessedText;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private EventProducer eventProducer;

    private ProcessedTextEvent mockEvent;

    @BeforeEach
    void setUp() {
        ProcessedText processedText = ProcessedText.builder()
                .mostFrequentWord("lorem")
                .avgParagraphSize(5L)
                .avgParagraphProcessingTime(100L)
                .totalProcessingTime(300L)
                .build();
        mockEvent = new ProcessedTextEvent(processedText, Instant.now());

        eventProducer.setTopic("words.processed");
    }

    @Test
    void testSendProcessedTextEvent_sendsEventSuccessfully() {
        // When
        eventProducer.sendProcessedTextEvent(mockEvent);

        // Then
        ArgumentCaptor<ProducerRecord<String, Object>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate).send(recordCaptor.capture());

        ProducerRecord<String, Object> capturedRecord = recordCaptor.getValue();
        assertEquals("words.processed", capturedRecord.topic());
        assertEquals(mockEvent.getKey(), capturedRecord.key());
        assertEquals(mockEvent, capturedRecord.value());
    }

    @Test
    void testSendProcessedTextEvent_logsErrorOnException() {
        // Given
        Logger logger = (Logger) LoggerFactory.getLogger(EventProducer.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        logger.setLevel(Level.ERROR);

        doThrow(new RuntimeException("Kafka send error")).when(kafkaTemplate).send(any(ProducerRecord.class));

        // When
        eventProducer.sendProcessedTextEvent(mockEvent);

        // Then
        verify(kafkaTemplate).send(any(ProducerRecord.class));
        List<ILoggingEvent> logsList = listAppender.list;
        assertFalse(logsList.isEmpty());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        assertTrue(logsList.get(0).getFormattedMessage().contains("Failed to send event to topic words.processed: Kafka send error"));
    }
}