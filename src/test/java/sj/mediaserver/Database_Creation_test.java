package sj.mediaserver;

import java.io.File;

import sj.mediaserver.setup.DatabaseCreator;

public class Database_Creation_test {

    /*
    Sandbox for testing and adding features
    */

    public static void main(String[] args) {

        DatabaseCreator.CreateDatabase();
        DatabaseAPI API = DatabaseAPI.getInstance();
        
        File testFile = new File("E:/muza/Hollow Knight/Hidden Dreams/Christopher Larkin - White Defender.flac");
        API.addSongFromFile(testFile);
        File testFile2 = new File("E:/muza/Hollow Knight/Hidden Dreams/Christopher Larkin - Truth, Beauty and Hatred.flac");
        API.addSongFromFile(testFile2);

    }
}