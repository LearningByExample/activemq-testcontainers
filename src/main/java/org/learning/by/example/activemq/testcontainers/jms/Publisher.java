package org.learning.by.example.activemq.testcontainers.jms;

import org.learning.by.example.activemq.testcontainers.dto.SimpleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;


public class Publisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(Publisher.class);

    private final JmsTemplate template;
    private final String topic;

    public Publisher(final JmsTemplate template, final String topic) {
        this.template = template;
        this.topic = topic;
    }

    public void send(final SimpleMessage message) {
        LOGGER.debug("Sending message: '{}' ==> topic : '{}'", message, topic);
        template.convertAndSend(topic, message);
    }
}
