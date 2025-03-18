package com.recruitment.task.service.utility;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ParagraphUtility {

    public ParagraphStatistics process(List<String> paragraphs) {
        if (paragraphs == null || paragraphs.isEmpty()) {
            throw new IllegalArgumentException("Paragraph list cannot be null or empty");
        }

        long totalWordCount = 0;
        Map<String, Long> wordFrequency = new HashMap<>();

        for (String paragraph : paragraphs) {
            String normalized = paragraph.toLowerCase()
                    .replaceAll("\\s+", " ")
                    .replaceAll("[^\\p{IsAlphabetic}\\d\\s]", "");

            String[] words = normalized.split(" ");
            totalWordCount += words.length;

            for (String word : words) {
                if (!word.isEmpty()) {
                    wordFrequency.put(word, wordFrequency.getOrDefault(word, 0L) + 1);
                }
            }
        }

        String mostFrequentWord = wordFrequency.entrySet().stream()
                .max(Map.Entry.<String, Long>comparingByValue()
                        .thenComparing(Map.Entry.comparingByKey()))
                .map(Map.Entry::getKey)
                .orElseThrow();

        long avgParagraphSize = totalWordCount / paragraphs.size();

        return new ParagraphStatistics(mostFrequentWord, avgParagraphSize);
    }
}
