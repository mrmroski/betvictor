package com.recruitment.task.service;

import com.recruitment.task.exceptions.OperationTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoripsumApiImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private LoripsumProperties loripsumProperties;

    @InjectMocks
    private LoripsumApiImpl loripsumApi;

    private AtomicBoolean isInterrupted;

    @BeforeEach
    void setUp() {
        isInterrupted = new AtomicBoolean(false);
    }

    @Test
    void testGetLoremIpsum_successfulRequest_returnsCompletableFutureWithText() throws Exception {
        // Given
        when(loripsumProperties.getHost()).thenReturn("https://hipsum.co");
        when(loripsumProperties.getType()).thenReturn("hipster-centric");
        when(loripsumProperties.getParas()).thenReturn("1");

        String expectedResponse = "Lorem ipsum dolor sit amet";
        ResponseEntity<String> responseEntity = ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(expectedResponse);
        when(restTemplate.exchange(any(RequestEntity.class), eq(String.class))).thenReturn(responseEntity);

        // When
        CompletableFuture<String> result = loripsumApi.getLoremIpsum(isInterrupted);

        // Then
        assertNotNull(result);
        assertTrue(result.isDone());
        assertEquals(expectedResponse, result.get());

        ArgumentCaptor<RequestEntity<Void>> requestCaptor = ArgumentCaptor.forClass(RequestEntity.class);
        verify(restTemplate).exchange(requestCaptor.capture(), eq(String.class));
        RequestEntity<Void> capturedRequest = requestCaptor.getValue();
        URI expectedUri = new URI("https://hipsum.co/api/?type=hipster-centric&paras=1");
        assertEquals(expectedUri, capturedRequest.getUrl());
        assertEquals(HttpMethod.GET, capturedRequest.getMethod());
        assertTrue(capturedRequest.getHeaders().getAccept().contains(MediaType.TEXT_PLAIN));

        verify(loripsumProperties).getHost();
        verify(loripsumProperties).getType();
        verify(loripsumProperties).getParas();
    }

    @Test
    void testGetLoremIpsum_interrupted_throwsOperationTimeoutException() {
        // Given
        isInterrupted.set(true);

        // When & Then
        OperationTimeoutException exception = assertThrows(OperationTimeoutException.class,
                () -> loripsumApi.getLoremIpsum(isInterrupted));
        assertEquals("Interrupted", exception.getMessage());

        verifyNoInteractions(loripsumProperties);
    }

    @Test
    void testGetLoremIpsum_restTemplateThrowsException_throwsRuntimeException() {
        // Given
        when(loripsumProperties.getHost()).thenReturn("https://hipsum.co");
        when(loripsumProperties.getType()).thenReturn("hipster-centric");
        when(loripsumProperties.getParas()).thenReturn("1");

        when(restTemplate.exchange(any(RequestEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("API error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> loripsumApi.getLoremIpsum(isInterrupted));
        assertEquals("API error", exception.getMessage());

        verify(restTemplate).exchange(any(RequestEntity.class), eq(String.class));
        verify(loripsumProperties).getHost();
        verify(loripsumProperties).getType();
        verify(loripsumProperties).getParas();
    }
}