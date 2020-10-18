package com.ksr.api.client.spotify;

import com.ksr.api.client.spotify.authorization.ClientCredential;
import com.ksr.kafka.producer.spotify.model.PlaylistApiResponse;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.special.FeaturedPlaylists;
import com.wrapper.spotify.model_objects.specification.*;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SpotifyRESTClient {
    private static final Logger log = LoggerFactory.getLogger(SpotifyRESTClient.class.getSimpleName());


    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setAccessToken(ClientCredential.getInstance().getCredentials())
            .build();


    public PlaylistApiResponse getListOfFeaturedPlaylists() {
        try {
            final FeaturedPlaylists featuredPlaylists = spotifyApi
                    .getListOfFeaturedPlaylists().build().execute();
            log.info("Message: " + featuredPlaylists.getMessage());
            Paging<PlaylistSimplified> pagingPL = featuredPlaylists.getPlaylists();
            List<PlaylistSimplified> playlists;
            ArrayList<HashMap<String, Track>> playlistTracks = new ArrayList<>();
            HashMap<String, List<PlaylistTrack>> playListTrackMap = new HashMap<String, List<PlaylistTrack>>();
            Integer count = pagingPL.getTotal();
            Integer offset = pagingPL.getOffset();
            String next = pagingPL.getNext();
            String previous = pagingPL.getPrevious();
            Integer limit = pagingPL.getLimit();
            playlists = Arrays.asList(pagingPL.getItems());
            for (PlaylistSimplified pl : playlists) {
                try {
                    playListTrackMap.put(pl.getId(), getAlbumsTracks(pl.getId()));
                } catch (IOException | SpotifyWebApiException | org.apache.hc.core5.http.ParseException e) {
                    log.error("No tracks found for : " + pl.getId());
                }
            }
            return new PlaylistApiResponse(count, offset, limit, previous, next, playlists, playlistTracks);

        } catch (IOException | SpotifyWebApiException | org.apache.hc.core5.http.ParseException e) {
            log.error("Error: " + e.getMessage());
            return new PlaylistApiResponse(0, 0, 0, "", "", new ArrayList<>(), new ArrayList<>());
        }
    }

    public List<PlaylistTrack> getAlbumsTracks(String id) throws ParseException, SpotifyWebApiException, IOException {
        final GetPlaylistsItemsRequest trackRequest = spotifyApi.getPlaylistsItems(id).build();
        Paging<PlaylistTrack> track = trackRequest.execute();
        log.info("Total number of tracks in album :" +id + " is track.getItems().length");
        return Arrays.asList(track.getItems());
    }


}
