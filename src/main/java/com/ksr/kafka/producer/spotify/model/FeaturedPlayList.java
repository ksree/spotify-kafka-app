package com.ksr.kafka.producer.spotify.model;

import com.wrapper.spotify.model_objects.specification.PlaylistTrack;

import java.util.List;

public class FeaturedPlayList {

    private String playListName;
    private String playListId;
    private List<PlaylistTrack> playlistTracks;

    public FeaturedPlayList(String playListName, String playListId, List<PlaylistTrack> playListTrack) {
        this.playListName = playListName;
        this.playListId = playListId;
        this.playlistTracks = playListTrack;
    }

    public String getPlayListName() {
        return playListName;
    }

    public void setPlayListName(String playListName) {
        this.playListName = playListName;
    }

    public String getPlayListId() {
        return playListId;
    }

    public void setPlayListId(String playListId) {
        this.playListId = playListId;
    }

    public List<PlaylistTrack> getPlaylistTracks() {
        return playlistTracks;
    }

    public void setPlaylistTracks(List<PlaylistTrack> playlistTracks) {
        this.playlistTracks = playlistTracks;
    }

}

