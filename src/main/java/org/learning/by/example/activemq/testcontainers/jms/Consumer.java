package org.learning.by.example.activemq.testcontainers.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer implements MessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    private final String topic;
    private AtomicInteger totalMessages = new AtomicInteger(0);
    private ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();

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

    public Queue<String> getMessages(){
        return new ArrayDeque<>(messages);
    }

    @Override
    public void onMessage(final Message message) {
        try {
            if (TextMessage.class.isAssignableFrom(message.getClass())) {
                final TextMessage textMessage = (TextMessage) message;
                final String text = textMessage.getText();
                messages.add(text);
                totalMessages.incrementAndGet();
                LOGGER.info("got message topic: '{} <== message: '{}'", topic, text);
            }
        } catch (JMSException e) {
            LOGGER.error("error getting message", e);
        }
    }
}
