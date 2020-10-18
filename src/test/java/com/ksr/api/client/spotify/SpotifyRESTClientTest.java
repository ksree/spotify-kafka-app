package com.ksr.api.client.spotify;

import com.ksr.kafka.producer.spotify.model.PlaylistApiResponse;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class SpotifyRESTClientTest {

    private SpotifyRESTClient spotifyRESTClient;
    @Before
    public void initialize(){
        spotifyRESTClient = new SpotifyRESTClient();
    }

    @Test
    public void getPlayListAPICallIsSuccessful() {
        PlaylistApiResponse playlistsApiResponse = spotifyRESTClient.getListOfFeaturedPlaylists();
        System.out.println(playlistsApiResponse.getPlaylists().size());
        System.out.println(playlistsApiResponse.getPlaylistTracks().size());
        System.out.println(playlistsApiResponse.getPlaylists().get(1));
        System.out.println(playlistsApiResponse.getPlaylistTracks().get(1));

        assertTrue(playlistsApiResponse.getPlaylists().size() > 0);
        assertTrue(playlistsApiResponse.getPlaylistTracks().size() > 0);

    }

}

