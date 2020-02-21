package org.learning.by.example.activemq.testcontainers.context;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.learning.by.example.activemq.testcontainers.config.ActiveMQConfig;
import org.learning.by.example.activemq.testcontainers.jms.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

@EnableJms
public class JMSConfigurer implements JmsListenerConfigurer {
    private static final String JMS_ENDPOINT_ID = "jmsEndpointId";
    private static final String CONCURRENCY_FORMAT = "%d-%d";

    final ActiveMQConfig activeMQConfig;
    final Consumer consumer;

    public JMSConfigurer(final ActiveMQConfig activeMQConfig, final Consumer consumer) {
        this.activeMQConfig = activeMQConfig;
        this.consumer = consumer;
    }

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory(final ActiveMQConfig activeMQConfig) {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(activeMQConfig.getBrokerUrl());
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
    public DestinationResolver destinationResolver() {
        return new DynamicDestinationResolver();
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(final CachingConnectionFactory cachingConnectionFactory,
                                                                          final DestinationResolver destinationResolve) {
        final DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(cachingConnectionFactory);
        factory.setDestinationResolver(destinationResolve);
        final String concurrency = String.format(CONCURRENCY_FORMAT,
                activeMQConfig.getConsumer().getConcurrency().getLower(),
                activeMQConfig.getConsumer().getConcurrency().getUpper());
        factory.setConcurrency(concurrency);
        return factory;
    }

    @Override
    public void configureJmsListeners(final JmsListenerEndpointRegistrar jmsListenerEndpointRegistrar) {
        final SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        endpoint.setId(JMS_ENDPOINT_ID);
        endpoint.setDestination(consumer.getTopic());
        endpoint.setMessageListener(consumer);
        jmsListenerEndpointRegistrar.registerEndpoint(endpoint);
        jmsListenerEndpointRegistrar.setMessageHandlerMethodFactory(new DefaultMessageHandlerMethodFactory());
    }
}
