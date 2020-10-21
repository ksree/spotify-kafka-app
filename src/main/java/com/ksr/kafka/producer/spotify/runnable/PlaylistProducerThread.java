package com.ksr.kafka.producer.spotify.runnable;

import com.ksr.kafka.producer.spotify.AppConfig;
import com.ksr.kafka.producer.spotify.model.FeaturedPlayList;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

public class PlaylistProducerThread implements Runnable {
    private Logger log = LoggerFactory.getLogger(PlaylistProducerThread.class.getSimpleName());

    private final AppConfig appConfig;
    private final KafkaProducer<Long, FeaturedPlayList> kafkaProducer;
    private final ArrayBlockingQueue<FeaturedPlayList> playlistQueue;
    private final CountDownLatch latch;
    private final String targetTopic;

    public PlaylistProducerThread(AppConfig appConfig,
                                  ArrayBlockingQueue<FeaturedPlayList> playlistQueue,
                                  CountDownLatch latch) {
        this.appConfig = appConfig;
        this.playlistQueue = playlistQueue;
        this.latch = latch;
        this.kafkaProducer = createKafkaProducer(appConfig);
        this.targetTopic = appConfig.getTopicName();

    }

    public KafkaProducer<Long, FeaturedPlayList> createKafkaProducer(AppConfig appConfig) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, appConfig.getBootstrapServers());
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        properties.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, appConfig.getSchemaRegistryUrl());

        return new KafkaProducer<>(properties);
    }

    @Override
    public void run() {
        int playlistCount = 0;
        try {
            while (latch.getCount() > 1 || playlistQueue.size() > 0) {
                FeaturedPlayList playList = playlistQueue.poll();
                if (playList == null) {
                    Thread.sleep(200);
                } else {
                    playlistCount += 1;
                    log.info("Sending playList " + playlistCount + ": " + playList);
                    kafkaProducer.send(new ProducerRecord<>(targetTopic, playList));
                    // sleeping to slow down the pace a bit
                    Thread.sleep(appConfig.getProducerFrequencyMs());
                }
            }
        } catch (InterruptedException e) {
            log.warn("Avro Producer interrupted");
        } finally {
            close();
        }
    }

    public void close() {
        log.info("Closing Producer");
        kafkaProducer.close();
        latch.countDown();
    }
}
