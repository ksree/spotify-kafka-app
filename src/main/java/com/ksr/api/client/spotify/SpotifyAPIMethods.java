package com.ksr.api.client.spotify;

import com.ksr.api.client.spotify.authorization.ClientCredential;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.miscellaneous.PlaylistTracksInformation;
import com.wrapper.spotify.model_objects.special.FeaturedPlaylists;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpotifyAPIMethods {

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setAccessToken(ClientCredential.clientCredentials_Sync())
            .build();

    public static List<PlaylistSimplified> getListOfFeaturedPlaylists_Sync() {
        try {
            final FeaturedPlaylists featuredPlaylists = spotifyApi
                    .getListOfFeaturedPlaylists().build().execute();
            int offset = 0;
            int limit = 10;
            System.out.println("Message: " + featuredPlaylists.getMessage());
            Paging<PlaylistSimplified> pagingPL =  featuredPlaylists.getPlaylists();
            List<PlaylistSimplified> playlists = new ArrayList<>();
        do {
            playlists.addAll(Arrays.asList(pagingPL.getItems()));
            offset += limit;
        } while (offset < pagingPL.getTotal());
        return playlists;

    } catch (IOException | SpotifyWebApiException | org.apache.hc.core5.http.ParseException e) {
            System.out.println("Error: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    public static void main(String[] args) {
        List<PlaylistSimplified> playlists  = getListOfFeaturedPlaylists_Sync();
        for(PlaylistSimplified p: playlists){
            System.out.println(p.getName());
            PlaylistTracksInformation pl = p.getTracks();

        }
    }
}
