package com.ksr.api.client.spotify;

import com.ksr.kafka.producer.spotify.model.PlaylistApiResponse;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class SpotifyRESTClientTest {

    private SpotifyRESTClient spotifyRESTClient;

    @Before
    public void initialize() {
        spotifyRESTClient = new SpotifyRESTClient();
    }

    @Test
    public void getPlayListAPICallIsSuccessful() {
        PlaylistApiResponse playlistsApiResponse = spotifyRESTClient.getListOfFeaturedPlaylists();
        assertTrue(playlistsApiResponse.getPlayListTrackMap().size() > 0);
    }

}

