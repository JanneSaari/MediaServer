package sj.mediaserver.data;

import java.util.ArrayList;

public class Artist {

    private String name;
    private ArrayList<Album> albums;
    private ArrayList<Song> songs;
    
    public Artist(){}

    public Artist(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Album> getAlbums() {
        return this.albums;
    }

    public void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
    };
    
    public ArrayList<Song> getSongs() {
        return this.songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

}