package org.learning.by.example.activemq.testcontainers.application;

import org.junit.jupiter.api.Test;
import org.learning.by.example.activemq.testcontainers.jms.Consumer;
import org.learning.by.example.activemq.testcontainers.jms.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Queue;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = {ActiveMQTestContainersTest.Initializer.class})
class ActiveMQTestContainersTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(ActiveMQTestContainersTest.class);
    private static final String ACTIVEMQ_IMAGE = "rmohr/activemq";
    private static final int ACTIVEMQ_PORT = 61616;
    private static final String TCP_FORMAT = "tcp://%s:%d";
    private static final String BROKER_URL_FORMAT = "activemq.broker-url=%s";
    private static final String TEST_MESSAGE = "hello world";
    private static final int TIMEOUT_WAITING_FOR_MESSAGE = 15;

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
    void contestLoad() {
    }

    @Autowired
    Publisher publisher;

    @Autowired
    private Consumer consumer;

    @Test
    void whenSendOneMessageThenWillReceiveOneMessage() {
        consumer.resetMessages();
        publisher.send(TEST_MESSAGE);
        await().atMost(TIMEOUT_WAITING_FOR_MESSAGE, SECONDS).until(() -> consumer.getTotalMessages() == 1);
        final Queue<String> messages = consumer.getMessages();
        assertThat(messages).hasSize(1);
        assertThat(messages).contains(TEST_MESSAGE);
    }
}
