package org.learning.by.example.activemq.testcontainers.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("activemq")
public class ActiveMQConfig {
    private String brokerUrl;
    private Publisher publisher;
    private Consumer consumer;

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public static class Publisher {
        private String topic;
        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }
    }

    public static class Consumer {
        private String topic;
        private Concurrency concurrency;

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public Concurrency getConcurrency() {
            return concurrency;
        }

        public void setConcurrency(Concurrency concurrency) {
            this.concurrency = concurrency;
        }

        public static class Concurrency {
            private Integer lower;
            private Integer upper;

            public Integer getLower() {
                return lower;
            }

            public void setLower(Integer lower) {
                this.lower = lower;
            }

            public Integer getUpper() {
                return upper;
            }

            public void setUpper(Integer upper) {
                this.upper = upper;
            }
        }
    }
}
