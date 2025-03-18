package com.recruitment.task.exceptions;

public class OperationTimeoutException extends RuntimeException {
    public OperationTimeoutException(String message) {
        super(message);
    }
}
