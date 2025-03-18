package com.recruitment.task.service.utility;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParagraphUtilityTest {

    private final ParagraphUtility utility = new ParagraphUtility();

    @Test
    void testProcess_singleParagraph() {
        List<String> paragraphs = Collections.singletonList("Hello world hello");
        ParagraphStatistics result = utility.process(paragraphs);

        assertEquals("hello", result.mostFrequentWord());
        assertEquals(3, result.avgParagraphSize());
    }

    @Test
    void testProcess_multipleParagraphs() {
        List<String> paragraphs = Arrays.asList(
                "The quick brown fox",
                "The fox jumps over",
                "Fox fox fox"
        );
        ParagraphStatistics result = utility.process(paragraphs);

        assertEquals("fox", result.mostFrequentWord());
        assertEquals(3, result.avgParagraphSize());
    }

    @Test
    void testProcess_nullList_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            utility.process(null);
        });
        assertEquals("Paragraph list cannot be null or empty", exception.getMessage());
    }

    @Test
    void testProcess_emptyList_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            utility.process(Collections.emptyList());
        });
        assertEquals("Paragraph list cannot be null or empty", exception.getMessage());
    }

}