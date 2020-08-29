package sj.mediaserver.data;

public class Artist {

    private String name;
    private Song[] songs;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Song[] getSongs() {
        return this.songs;
    }

    public void setSongs(Song[] songs) {
        this.songs = songs;
    }

}