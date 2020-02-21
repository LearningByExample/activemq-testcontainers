package org.learning.by.example.activemq.testcontainers.context;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.learning.by.example.activemq.testcontainers.config.ActiveMQConfig;
import org.learning.by.example.activemq.testcontainers.dto.SimpleMessage;
import org.learning.by.example.activemq.testcontainers.jms.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.List;

public class JMSContext {
    final ActiveMQConfig activeMQConfig;
    final Consumer consumer;

    public JMSContext(final ActiveMQConfig activeMQConfig, final Consumer consumer) {
        this.activeMQConfig = activeMQConfig;
        this.consumer = consumer;
    }

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory(final ActiveMQConfig activeMQConfig) {
        final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(activeMQConfig.getBrokerUrl());
        final List<String> trustedPackageList = new ArrayList<>(activeMQConnectionFactory.getTrustedPackages());
        trustedPackageList.add(SimpleMessage.class.getPackageName());
        activeMQConnectionFactory.setTrustedPackages(trustedPackageList);
        return activeMQConnectionFactory;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory(final ActiveMQConnectionFactory activeMQConnectionFactory) {
        return new CachingConnectionFactory(activeMQConnectionFactory);
    }

    @Bean
    public JmsTemplate jmsTemplate(final CachingConnectionFactory cachingConnectionFactory) {
        return new JmsTemplate(cachingConnectionFactory);
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(final CachingConnectionFactory cachingConnectionFactory) {
        final DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(cachingConnectionFactory);
        return factory;
    }
}
