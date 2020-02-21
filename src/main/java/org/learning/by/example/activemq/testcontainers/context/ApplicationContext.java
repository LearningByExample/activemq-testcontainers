package org.learning.by.example.activemq.testcontainers.context;

import org.learning.by.example.activemq.testcontainers.config.ActiveMQConfig;
import org.learning.by.example.activemq.testcontainers.jms.Consumer;
import org.learning.by.example.activemq.testcontainers.jms.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@Import(JMSConfigurer.class)
public class ApplicationContext {
    @Bean
    ActiveMQConfig activeMQConfig() {
        return new ActiveMQConfig();
    }

    @Bean
    Publisher publisher(final JmsTemplate template, final ActiveMQConfig activeMQConfig) {
        return new Publisher(template, activeMQConfig.getPublisher().getTopic());
    }

    @Bean
    Consumer consumer(final ActiveMQConfig activeMQConfig) {
        return new Consumer(activeMQConfig.getConsumer().getTopic());
    }
}
