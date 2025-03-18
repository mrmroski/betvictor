package com.recruitment.task.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public interface LoripsumApiInterface {

    CompletableFuture<String> getLoremIpsum(AtomicBoolean isInterrupted);
}
