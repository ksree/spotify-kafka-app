package com.ksr.api.client.spotify;

import com.ksr.api.client.spotify.authorization.ClientCredential;
import com.ksr.kafka.producer.spotify.model.FeaturedPlayList;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.special.FeaturedPlaylists;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
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


    public List<FeaturedPlayList> getListOfFeaturedPlaylists() {
        List<FeaturedPlayList> featuredPlayList = new ArrayList<FeaturedPlayList>();
        try {
            final FeaturedPlaylists featuredPlaylists = spotifyApi
                    .getListOfFeaturedPlaylists().build().execute();
            log.info("Message: " + featuredPlaylists.getMessage());
            Paging<PlaylistSimplified> pagingPL = featuredPlaylists.getPlaylists();
            List<PlaylistSimplified> playlists;
            HashMap<String, List<PlaylistTrack>> playListTrackMap = new HashMap<>();
            Integer count = pagingPL.getTotal();
            Integer offset = pagingPL.getOffset();
            String next = pagingPL.getNext();
            String previous = pagingPL.getPrevious();
            Integer limit = pagingPL.getLimit();
            playlists = Arrays.asList(pagingPL.getItems());
            for (PlaylistSimplified pl : playlists) {
                try {
                    featuredPlayList.add(new FeaturedPlayList(pl.getName(), pl.getId(), getAlbumsTracks(pl.getId())));
                } catch (IOException | SpotifyWebApiException | org.apache.hc.core5.http.ParseException e) {
                    log.error("No tracks found for : " + pl.getId());
                }
            }

        } catch (IOException | SpotifyWebApiException | org.apache.hc.core5.http.ParseException e) {
            log.error("Error: " + e.getMessage());
        }
        return featuredPlayList;
    }

    public List<PlaylistTrack> getAlbumsTracks(String id) throws ParseException, SpotifyWebApiException, IOException {
        final GetPlaylistsItemsRequest trackRequest = spotifyApi.getPlaylistsItems(id).build();
        Paging<PlaylistTrack> track = trackRequest.execute();
        log.info("Total number of tracks in album :" + id + " is track.getItems().length");
        return Arrays.asList(track.getItems());
    }


}
