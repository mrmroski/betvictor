package com.recruitment.task.controller;

import com.recruitment.task.service.ProcessedText;
import com.recruitment.task.service.ProcessedTextService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProcessingTextController.class)
class ProcessingTextControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProcessedTextService processedTextService;

    @Test
    void testGetProcessedText_validNumberOfParagraphs_returnsProcessedTextDTO() throws Exception {
        // Given
        int numberOfParagraphs = 3;
        when(processedTextService.processText(numberOfParagraphs)).thenReturn(new ProcessedText("hello", 5L, 100L, 300L));

        // When & Then
        mockMvc.perform(get("/betvictor/text")
                        .param("p", String.valueOf(numberOfParagraphs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.freq_word").value("hello"))
                .andExpect(jsonPath("$.avg_paragraph_size").value(5))
                .andExpect(jsonPath("$.avg_paragraph_processing_time").value(100))
                .andExpect(jsonPath("$.total_processing_time").value(300));
    }

    @Test
    void testGetProcessedText_negativeNumberOfParagraphs_returnsBadRequest() throws Exception {
        int invalidNumberOfParagraphs = -1;

        mockMvc.perform(get("/betvictor/text")
                        .param("p", String.valueOf(invalidNumberOfParagraphs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("numberOfParagraphs"))
                .andExpect(jsonPath("$.errors[0].message").value("must be greater than or equal to 1"));
    }

    @Test
    void testGetProcessedText_zeroNumberOfParagraphs_returnsBadRequest() throws Exception {
        int invalidNumberOfParagraphs = 0;

        mockMvc.perform(get("/betvictor/text")
                        .param("p", String.valueOf(invalidNumberOfParagraphs))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("numberOfParagraphs"))
                .andExpect(jsonPath("$.errors[0].message").value("must be greater than or equal to 1"));
    }

    @Test
    void testGetProcessedText_missingParameter_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/betvictor/text")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("p"))
                .andExpect(jsonPath("$.errors[0].message").value("Required request parameter 'p' is not present"));
    }
}