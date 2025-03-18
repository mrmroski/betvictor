package com.recruitment.task.kafka;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.recruitment.task.service.ProcessedText;
import com.recruitment.task.service.kafka.CreateTextEntityCommand;
import com.recruitment.task.service.kafka.CreateTextEntityCommandHandler;
import com.recruitment.task.service.kafka.ProcessedTextEvent;
import com.recruitment.task.service.kafka.TextEventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TextEventListenerTest {

    @Mock
    private CreateTextEntityCommandHandler commandHandler;

    @InjectMocks
    private TextEventListener textEventListener;

    private ProcessedTextEvent mockEvent;
    private ProcessedText mockProcessedText;

    @BeforeEach
    void setUp() {
        mockProcessedText = new ProcessedText("lorem", 5L, 100L, 300L, Instant.now());
        mockEvent = new ProcessedTextEvent(mockProcessedText, Instant.now());
    }

    @Test
    void testListen_validEvent_callsCommandHandlerWithCorrectCommand() {
        // When
        textEventListener.listen(mockEvent);

        // Then
        ArgumentCaptor<CreateTextEntityCommand> commandCaptor = ArgumentCaptor.forClass(CreateTextEntityCommand.class);
        verify(commandHandler).handle(commandCaptor.capture());

        CreateTextEntityCommand capturedCommand = commandCaptor.getValue();
        assertEquals("lorem", capturedCommand.mostFrequentWord());
        assertEquals(5L, capturedCommand.avgParagraphSize());
        assertEquals(100L, capturedCommand.avgParagraphProcessingTime());
        assertEquals(300L, capturedCommand.totalProcessingTime());
        assertEquals(mockEvent.createdAt(), capturedCommand.createdAt());
    }

    @Test
    void testListen_nullProcessedText_logsErrorAndDoesNotCallHandler() {
        // Given
        Logger logger = (Logger) LoggerFactory.getLogger(TextEventListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        logger.setLevel(Level.ERROR);

        ProcessedTextEvent nullEvent = new ProcessedTextEvent(null, Instant.now());

        // When
        textEventListener.listen(nullEvent);

        // Then
        verify(commandHandler, never()).handle(any());

        List<ILoggingEvent> logsList = listAppender.list;
        assertFalse(logsList.isEmpty());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        assertTrue(logsList.get(0).getFormattedMessage().contains("Failed to process text event"));
    }

    @Test
    void testListen_commandHandlerThrowsException_logsErrorAndDoesNotPropagate() {
        // Given
        Logger logger = (Logger) LoggerFactory.getLogger(TextEventListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        logger.setLevel(Level.ERROR);

        doThrow(new RuntimeException("Handler error")).when(commandHandler).handle(any(CreateTextEntityCommand.class));

        // When
        textEventListener.listen(mockEvent);

        // Then
        verify(commandHandler).handle(any());

        List<ILoggingEvent> logsList = listAppender.list;
        assertFalse(logsList.isEmpty());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
        assertTrue(logsList.get(0).getFormattedMessage().contains("Failed to process text event: Handler error"));
    }
}