package com.ksr.kafka.producer.spotify;

import com.ksr.kafka.producer.spotify.model.FeaturedPlayList;
import com.ksr.kafka.producer.spotify.runnable.PlaylistFetcherThread;
import com.ksr.kafka.producer.spotify.runnable.PlaylistProducerThread;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

public class PlaylistProducerMain {
    private Logger log = LoggerFactory.getLogger(PlaylistProducerMain.class.getSimpleName());

    // thread safe queue which blocks when full.
    private ExecutorService executor;
    private CountDownLatch latch;
    private PlaylistFetcherThread spotifyAPIClient;
    private PlaylistProducerThread playListProducer;

    public static void main(String[] args) {
        PlaylistProducerMain app = new PlaylistProducerMain();
        app.start();
    }

    private PlaylistProducerMain() {
        AppConfig appConfig = new AppConfig(ConfigFactory.load());
        latch = new CountDownLatch(2);
        executor = Executors.newFixedThreadPool(2);
        ArrayBlockingQueue<FeaturedPlayList> playListQueue = new ArrayBlockingQueue<>(appConfig.getQueueCapacity());
        spotifyAPIClient = new PlaylistFetcherThread(appConfig, playListQueue, latch);
        playListProducer = new PlaylistProducerThread(appConfig, playListQueue, latch);
    }

    private void start() {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!executor.isShutdown()) {
                log.info("Shutdown requested");
                shutdown();
            }
        }));

        log.info("Application started!");
        executor.submit(spotifyAPIClient);
        executor.submit(playListProducer);
        log.info("Stuff submit");
        try {
            log.info("Latch await");
            latch.await();
            log.info("Threads completed");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            shutdown();
            log.info("Application closed succesfully");
        }
    }

    private void shutdown() {
        if (!executor.isShutdown()) {
            log.info("Shutting down");
            executor.shutdownNow();
            try {
                if (!executor.awaitTermination(2000, TimeUnit.MILLISECONDS)) { //optional *
                    log.warn("Executor did not terminate in the specified time."); //optional *
                    List<Runnable> droppedTasks = executor.shutdownNow(); //optional **
                    log.warn("Executor was abruptly shut down. " + droppedTasks.size() + " tasks will not be executed."); //optional **
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
