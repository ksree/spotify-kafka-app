package com.ksr.kafka.producer.spotify.runnable;

import com.ksr.api.client.spotify.SpotifyAPIMethods;
import com.ksr.kafka.producer.spotify.AppConfig;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

public class PlaylistFetcherThread implements Runnable {
    private Logger log = LoggerFactory.getLogger(PlaylistFetcherThread.class.getSimpleName());

    private final AppConfig appConfig;
    private final ArrayBlockingQueue<PlaylistSimplified> playlistQueue;
    private final CountDownLatch latch;
    private SpotifyAPIMethods spotifyRestClient;

    public PlaylistFetcherThread(AppConfig appConfig,
                                 ArrayBlockingQueue<PlaylistSimplified> playlistQueue,
                                 CountDownLatch latch) {
        this.appConfig = appConfig;
        this.playlistQueue = playlistQueue;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            Boolean keepOnRunning = true;
            while (keepOnRunning) {
                List<PlaylistSimplified> playLists;
                try {
                    playLists = spotifyRestClient.getListOfFeaturedPlaylists_Sync();
                    log.info("Fetched " + playLists.size() + " play lists");
                    if (playLists.size() == 0) {
                        keepOnRunning = false;
                    } else {
                        // this may block if the queue is full - this is flow control
                        log.info("Queue size :" + playlistQueue.size());
                        for (PlaylistSimplified playList : playLists) {
                            playlistQueue.put(playList);
                        }
                    }
                } finally {
                    Thread.sleep(50);
                }
            }
        } catch (InterruptedException e) {
            log.warn("REST Client interrupted");
        } finally {
            this.close();
        }
    }

    private void close() {
        log.info("Closing");
        latch.countDown();
        log.info("Closed");
    }
}
