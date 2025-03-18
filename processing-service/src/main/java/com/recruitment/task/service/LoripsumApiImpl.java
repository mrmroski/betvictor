package com.recruitment.task.service;

import com.recruitment.task.exceptions.OperationTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@EnableAsync(proxyTargetClass = true)
@EnableCaching(proxyTargetClass = true)
@Slf4j
@Component
@RequiredArgsConstructor
public class LoripsumApiImpl implements LoripsumApiInterface{


    private final RestTemplate restTemplate;
    private final LoripsumProperties loripsumProperties;

    @Override
    @Async("betvictorExecutor")
    public CompletableFuture<String> getLoremIpsum(AtomicBoolean isInterrupted) {
        if (isInterrupted.get()) {
            log.warn("Operation was interrupted");
            throw new OperationTimeoutException("Interrupted");
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(loripsumProperties.getHost())
                .path("/api/")
                .queryParam("type", loripsumProperties.getType())
                .queryParam("paras", loripsumProperties.getParas())
                .build()
                .toUri();

        RequestEntity<Void> request = RequestEntity.get(uri)
                .accept(MediaType.TEXT_PLAIN)
                .build();

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        return CompletableFuture.completedFuture(response.getBody());
    }

}
