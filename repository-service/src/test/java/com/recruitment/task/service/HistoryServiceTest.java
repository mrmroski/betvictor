package com.recruitment.task.service;

import com.recruitment.task.repository.TextEntity;
import com.recruitment.task.repository.TextRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {

    @Mock
    private TextRepository textRepository;

    @InjectMocks
    private HistoryService historyService;

    private TextEntity mockTextEntity;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        mockTextEntity = new TextEntity();
        mockTextEntity.setMostFrequentWord("lorem");
        mockTextEntity.setAvgParagraphSize(5L);
        mockTextEntity.setAvgParagraphProcessingTime(100L);
        mockTextEntity.setTotalProcessingTime(300L);
        mockTextEntity.setCreationDate(Instant.now());

        Field historyLengthField = HistoryService.class.getDeclaredField("historyLength");
        historyLengthField.setAccessible(true);
        historyLengthField.setInt(historyService, 10);
    }

    @Test
    void testGetProcessedTexts_returnsListOfProcessedText() {
        // Given
        List<TextEntity> textEntities = List.of(mockTextEntity);
        Page<TextEntity> page = new PageImpl<>(textEntities);
        when(textRepository.findAll(any(PageRequest.class))).thenReturn(page);

        // When
        List<ProcessedText> result = historyService.getProcessedTexts();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        ProcessedText processedText = result.get(0);
        assertEquals("lorem", processedText.mostFrequentWord());
        assertEquals(5L, processedText.avgParagraphSize());
        assertEquals(100L, processedText.avgParagraphProcessingTime());
        assertEquals(300L, processedText.totalProcessingTime());
        assertEquals(mockTextEntity.getCreationDate(), processedText.creationDate());

        ArgumentCaptor<PageRequest> pageRequestCaptor = ArgumentCaptor.forClass(PageRequest.class);
        verify(textRepository).findAll(pageRequestCaptor.capture());
        PageRequest capturedPageRequest = pageRequestCaptor.getValue();
        assertEquals(0, capturedPageRequest.getPageNumber());
        assertEquals(10, capturedPageRequest.getPageSize());
        assertEquals(Sort.by(Sort.Direction.DESC, "creationDate"), capturedPageRequest.getSort());
    }

    @Test
    void testGetProcessedTexts_emptyPage_returnsEmptyList() {
        // Given
        Page<TextEntity> emptyPage = new PageImpl<>(Collections.emptyList());
        when(textRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);

        // When
        List<ProcessedText> result = historyService.getProcessedTexts();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(textRepository).findAll(any(PageRequest.class));
    }

    @Test
    void testMapFromEntity_mapsTextEntityToProcessedText() {
        // When
        ProcessedText result = historyService.mapFromEntity(mockTextEntity);

        // Then
        assertNotNull(result);
        assertEquals("lorem", result.mostFrequentWord());
        assertEquals(5L, result.avgParagraphSize());
        assertEquals(100L, result.avgParagraphProcessingTime());
        assertEquals(300L, result.totalProcessingTime());
        assertEquals(mockTextEntity.getCreationDate(), result.creationDate());
    }
}