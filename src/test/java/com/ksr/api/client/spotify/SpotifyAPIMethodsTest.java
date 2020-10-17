package com.ksr.api.client.spotify;

import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class SpotifyAPIMethodsTest {

    @Before
    public void setUp() throws Exception {
        System.out.println("before");
    }

    @Test
    public void apiCallIsSuccessful() {
        List<PlaylistSimplified> playlists = SpotifyAPIMethods.getListOfFeaturedPlaylists();

        assertTrue(playlists.size() > 0);
        assertTrue(playlists.iterator().next() != null);
    }

}

