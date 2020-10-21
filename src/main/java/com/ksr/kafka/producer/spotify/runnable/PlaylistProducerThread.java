package com.ksr.kafka.producer.spotify.runnable;

import com.ksr.kafka.producer.spotify.AppConfig;
import com.ksr.kafka.producer.spotify.model.FeaturedPlayList;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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
                                  CountDownLatch latch) throws IOException {
        this.appConfig = appConfig;
        this.playlistQueue = playlistQueue;
        this.latch = latch;
        this.kafkaProducer = createKafkaProducer(appConfig);
        this.targetTopic = appConfig.getTopicName();

    }

    public KafkaProducer<Long, FeaturedPlayList> createKafkaProducer(AppConfig appConfig) throws IOException {
        final Properties props = loadConfig(appConfig.kafkaConfigFile);
       /* Properties properties = new Properties();
            properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, appConfig.getBootstrapServers());
            properties.put(ProducerConfig.ACKS_CONFIG, "all");
            properties.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
            properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
            properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
            properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
           // properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
            properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            properties.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, appConfig.getSchemaRegistryUrl());
        */
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<>(props);
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
                    System.out.println("Sending playList " + playlistCount + ": " + playList);
                    log.info("Sending playList " + playlistCount + ": " + playList);
                    kafkaProducer.send(new ProducerRecord(targetTopic, "test playlist"), new Callback() {
                        @Override
                        public void onCompletion(RecordMetadata m, Exception e) {
                            if (e != null) {
                                e.printStackTrace();
                            } else {
                                System.out.printf("Produced record to topic %s partition [%d] @ offset %d%n", m.topic(), m.partition(), m.offset());
                            }
                        }
                    });
                    kafkaProducer.flush();

                    System.out.printf("playlistCount playlists were produced to topic %s%n", targetTopic);
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

    public static Properties loadConfig(String configFile) throws IOException {
        if (!Files.exists(Paths.get(configFile))) {
            throw new IOException(configFile + " not found.");
        }
        final Properties cfg = new Properties();
        try (InputStream inputStream = new FileInputStream(configFile)) {
            cfg.load(inputStream);
        }
        return cfg;
    }

}
