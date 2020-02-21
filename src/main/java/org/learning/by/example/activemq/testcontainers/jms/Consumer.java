package org.learning.by.example.activemq.testcontainers.jms;

import org.learning.by.example.activemq.testcontainers.dto.SimpleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    private final String topic;
    private AtomicInteger totalMessages = new AtomicInteger(0);
    private ConcurrentLinkedQueue<SimpleMessage> messages = new ConcurrentLinkedQueue<>();

    public Consumer(final String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void resetMessages() {
        totalMessages.set(0);
        messages.clear();
    }

    public int getTotalMessages() {
        return totalMessages.get();
    }

    public Queue<SimpleMessage> getMessages() {
        return new ArrayDeque<>(messages);
    }

    @JmsListener(
            destination = "${activemq.consumer.topic}",
            concurrency = "${activemq.consumer.concurrency.lower}-${activemq.consumer.concurrency.upper}"
    )
    public void receiveMessage(final SimpleMessage message) {
        messages.add(message);
        totalMessages.incrementAndGet();
        LOGGER.debug("got message topic: '{} <== message: '{}'", topic, message);
    }
}
