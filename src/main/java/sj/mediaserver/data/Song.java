package sj.mediaserver.data;

import java.io.File;

public class Song {
    
    private String name;
    private String artist;
    private String album;
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

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

}