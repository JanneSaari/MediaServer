package sj.mediaserver;

import java.util.ArrayList;

import sj.mediaserver.data.*;
import sj.mediaserver.setup.DatabaseCreator;

public class Database_Creation_test {

    /*
     * Sandbox for testing and adding features
     */

    public static void main(String[] args) {

        //Create database. Uncomment only if you want to wipe existing database
        //DatabaseCreator.CreateDatabase();
        
        DatabaseAPI API = DatabaseAPI.getInstance();

        //Add all songs in folder and subfolders
        //API.addAllSongRecursively("E:/muza");

        //All artists test
        System.out.println("############### ALL ARTISTS #####################");
        ArrayList<Artist> test = API.getArtists();
        test.forEach((artist) -> System.out.println(artist.getName()));
        //All albums test
        System.out.println("############### ALL ALBUMS #####################");
        ArrayList<Album> test2 = API.getAlbums();
        test2.forEach((album) -> System.out.println(album.getName()));
    }
}