package com.ksr.kafka.producer.spotify.model;

import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;

import java.util.HashMap;
import java.util.List;

public class PlaylistApiResponse {
    private String next;
    private String previous;
    private Integer count;
    private Integer offset;
    private Integer limit;
    private List<PlaylistSimplified> playlists;
    private HashMap<String, List<PlaylistTrack>> playListTrackMap;

    public PlaylistApiResponse(Integer count, Integer offset, Integer limit, String previous, String next, List<PlaylistSimplified> playlists, HashMap<String, List<PlaylistTrack>> playListTrackMap) {
        this.count = count;
        this.offset = offset;
        this.limit = limit;
        this.previous = previous;
        this.next = next;
        this.playlists = playlists;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public List<PlaylistSimplified> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<PlaylistSimplified> playlists) {
        this.playlists = playlists;
    }

    public HashMap<String, List<PlaylistTrack>> getPlayListTrackMap() {
        return playListTrackMap;
    }

    public void setPlayListTrackMap(HashMap<String, List<PlaylistTrack>> playListTrackMap) {
        this.playListTrackMap = playListTrackMap;
    }
}
