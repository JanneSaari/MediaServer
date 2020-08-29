package sj.mediaserver.data;

import java.io.File;

public class Song {
    
    private String name;
    private Artist artist;
    private Album album;
    private File songFile;
    //private Length length;

    public File getSongFile() {
        return this.songFile;
    }

    public void setSongFile(File songFile) {
        this.songFile = songFile;
    };
    
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Artist getArtist() {
        return this.artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Album getAlbum() {
        return this.album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

}