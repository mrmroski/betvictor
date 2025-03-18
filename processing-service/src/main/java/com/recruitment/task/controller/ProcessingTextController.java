package com.recruitment.task.controller;

import com.recruitment.task.service.ProcessedText;
import com.recruitment.task.service.ProcessedTextService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/betvictor/text")
@RequiredArgsConstructor
@Validated
public class ProcessingTextController {

    private final ProcessedTextService processedTextService;

    @GetMapping
    public ProcessedText getProcessedText(@RequestParam("p") @Min(1) Integer numberOfParagraphs) {
        return processedTextService.processText(numberOfParagraphs);
    }


}
