package sj.mediaserver.data;

import java.io.File;
import java.util.UUID;

public class Song {
    
    private String name;
    private Artist artist;
    private Album album;
    private File songFile;
    private UUID id;
    
    public Song() {}

    public Song(String name) {
        this.name = name;
    }

    public Song(String name, Artist artist) {
        this.name = name;
        this.artist = artist;
    }

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
    
    public void setArtist(String artistName){
        Artist artist = new Artist();
        artist.setName(artistName);
        this.artist = artist;
    }
    
    public Album getAlbum() {
        return this.album;
    }
    
    public void setAlbum(Album album) {
        this.album = album;
    }
    
    public void setAlbum(String albumName){
        Album album = new Album();
        album.setName(albumName);
        this.album = album;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }  
}