package com.recruitment.task.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "processed_text")
public class TextEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String mostFrequentWord;
    private Long totalProcessingTime;
    private Instant creationDate;
    private Long avgParagraphSize;
    private Long avgParagraphProcessingTime;

}
