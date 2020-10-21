package com.ksr.api.client.spotify;

import com.ksr.kafka.producer.spotify.model.FeaturedPlayList;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class SpotifyRESTClientTest {

    private SpotifyRESTClient spotifyRESTClient;

    @Before
    public void initialize() {
        spotifyRESTClient = new SpotifyRESTClient();
    }

    @Test
    public void getPlayListAPICallIsSuccessful() {
        List<FeaturedPlayList> featuredPlayLists = spotifyRESTClient.getListOfFeaturedPlaylists();
        assertTrue(featuredPlayLists.size() > 0);
    }
}

