package com.ksr.kafka.producer.spotify.runnable;

import com.ksr.api.client.spotify.SpotifyRESTClient;
import com.ksr.kafka.producer.spotify.AppConfig;
import com.ksr.kafka.producer.spotify.model.FeaturedPlayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

public class PlaylistFetcherThread implements Runnable {
    private Logger log = LoggerFactory.getLogger(PlaylistFetcherThread.class.getSimpleName());

    private final AppConfig appConfig;
    private final ArrayBlockingQueue<FeaturedPlayList> playlistQueue;
    private final CountDownLatch latch;
    private SpotifyRESTClient spotifyRestClient;

    public PlaylistFetcherThread(AppConfig appConfig,
                                 ArrayBlockingQueue<FeaturedPlayList> playlistQueue,
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
                List<FeaturedPlayList> featuredPlayLists;
                try {
                    featuredPlayLists = spotifyRestClient.getListOfFeaturedPlaylists();
                    log.info("Fetched " + featuredPlayLists.size() + " play lists");
                    if (featuredPlayLists.size() == 0) {
                        keepOnRunning = false;
                    } else {
                        // this may block if the queue is full - this is flow control
                        log.info("Queue size :" + featuredPlayLists.size());
                        for(FeaturedPlayList pl: featuredPlayLists)
                            playlistQueue.put(pl);
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
