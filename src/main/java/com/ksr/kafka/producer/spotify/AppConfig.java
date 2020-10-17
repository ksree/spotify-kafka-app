package com.ksr.kafka.producer.spotify;

import com.typesafe.config.Config;

public class AppConfig {

    private final String schemaRegistryUrl;
    private final String topicName;
    private final Integer queueCapacity;
    private final Integer producerFrequencyMs;
    private final String bootstrapServers;
    private final String clientId;
    private final String clientSecret;


    public AppConfig(Config config) {
        this.bootstrapServers = config.getString("kafka.bootstrap.servers");
        this.schemaRegistryUrl = config.getString("kafka.schema.registry.url");
        this.topicName = config.getString("kafka.topic.name");
        this.queueCapacity = config.getInt("app.queue.capacity");
        this.producerFrequencyMs = config.getInt("app.producer.frequency.ms");
        this.clientId = config.getString("app.spotify.clientId");
        this.clientSecret = config.getString("app.spotify.clientSecret");
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public String getSchemaRegistryUrl() {
        return schemaRegistryUrl;
    }

    public String getTopicName() {
        return topicName;
    }

    public Integer getQueueCapacity() {
        return queueCapacity;
    }

    public Integer getProducerFrequencyMs() {
        return producerFrequencyMs;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
