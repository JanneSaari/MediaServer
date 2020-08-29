package sj.mediaserver.data;

public class Album {
    
    String name;
    Artist artist;
    Song[] songs;

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

    public Song[] getSongs() {
        return this.songs;
    }

    public void setSongs(Song[] songs) {
        this.songs = songs;
    }

}