package com.recruitment.task.controller;

import com.recruitment.task.service.HistoryService;
import com.recruitment.task.service.ProcessedText;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HistoryController.class)
class HistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HistoryController historyController;

    @MockBean
    private HistoryService historyService;

    private ProcessedText mockProcessedText;

    @BeforeEach
    void setUp() {
        mockProcessedText = new ProcessedText("lorem", 5L, 100L, 300L, Instant.now());
    }

    @Test
    void testGetHistory_returnsListOfProcessedTextDTO() throws Exception {
        // Given
        List<ProcessedText> processedTexts = List.of(mockProcessedText);
        when(historyService.getProcessedTexts()).thenReturn(processedTexts);

        // When & Then
        mockMvc.perform(get("/betvictor/history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].freq_word").value("lorem"))
                .andExpect(jsonPath("$[0].avg_paragraph_size").value(5))
                .andExpect(jsonPath("$[0].avg_paragraph_processing_time").value(100))
                .andExpect(jsonPath("$[0].total_processing_time").value(300));
    }

    @Test
    void testGetHistory_emptyList_returnsEmptyArray() throws Exception {
        // Given
        when(historyService.getProcessedTexts()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/betvictor/history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetHistory_serviceThrowsException_returnsInternalServerError() throws Exception {
        // Given
        when(historyService.getProcessedTexts()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/betvictor/history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}