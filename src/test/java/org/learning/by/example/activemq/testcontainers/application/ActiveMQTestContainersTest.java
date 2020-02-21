package org.learning.by.example.activemq.testcontainers.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learning.by.example.activemq.testcontainers.dto.SimpleMessage;
import org.learning.by.example.activemq.testcontainers.jms.Consumer;
import org.learning.by.example.activemq.testcontainers.jms.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = {ActiveMQTestContainersTest.Initializer.class})
@ActiveProfiles("test")
class ActiveMQTestContainersTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(ActiveMQTestContainersTest.class);
    private static final String ACTIVEMQ_IMAGE = "rmohr/activemq";
    private static final int ACTIVEMQ_PORT = 61616;
    private static final String TCP_FORMAT = "tcp://%s:%d";
    private static final String BROKER_URL_FORMAT = "activemq.broker-url=%s";
    private static final int TIMEOUT_WAITING_FOR_MESSAGES = 15;

    @SuppressWarnings("rawtypes")
    @Container
    private static final GenericContainer activemq = new GenericContainer(ACTIVEMQ_IMAGE).withExposedPorts(ACTIVEMQ_PORT);

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            final String url = String.format(TCP_FORMAT, activemq.getContainerIpAddress(), activemq.getFirstMappedPort());
            LOGGER.info("ActiveMQ URL: '{}'", url);
            final String property = String.format(BROKER_URL_FORMAT, url);
            TestPropertyValues.of(property).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Test
    @DisplayName("Context load")
    void contestLoad() {
    }

    @Autowired
    Publisher publisher;

    @Autowired
    private Consumer consumer;

    @Test
    @DisplayName("When sending messages Then will receive the messages sent")
    void whenSendingMessagesThenWillReceiveTheMessagesSent() {
        final List<String> messagesText = Arrays.asList("one", "two", "three", "four", "five", "six");
        final List<SimpleMessage> messagesToSend = messagesText.stream().map(SimpleMessage::new).collect(Collectors.toList());
        final int totalMessages = messagesToSend.size();

        consumer.resetMessages();
        messagesToSend.forEach(publisher::send);

        await().atMost(TIMEOUT_WAITING_FOR_MESSAGES, SECONDS).until(() -> consumer.getTotalMessages() == totalMessages);

        final Queue<SimpleMessage> messagesReceived = consumer.getMessages();
        assertThat(messagesReceived).hasSize(totalMessages);
        assertThat(messagesReceived).containsAll(messagesToSend);
    }
}
