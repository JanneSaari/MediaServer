package sj.mediaserver;

import java.io.Console;
import java.io.File;
import java.sql.*;
import java.util.Scanner;

import sj.mediaserver.data.Album;
import sj.mediaserver.data.Artist;
import sj.mediaserver.data.Song;

public class DatabaseAPI {
    
    //TODO get url from Properties file or something
    private static final String url = "jdbc:postgresql://localhost/musicdb?allowMultiQueries=true";
    private static String user = "";
    private static String password = "";
    
    private Connection connection;

    //TODO remove at some point
    //Statements used for testing purposes for now. REMOVE LATER
    private static PreparedStatement addSongStatement;
    private static PreparedStatement addArtistStatement;
    private static PreparedStatement addAlbumStatement;
    private static PreparedStatement addFileStatement;
    private static PreparedStatement getFilePathStatement;
    
    // this class is a singleton and should not be instantiated directly!
    private static DatabaseAPI instance = new DatabaseAPI();  
    public static DatabaseAPI getInstance() {
        return instance;
    }

    //TODO create place to define url instead of hardcoding in top of the class
    //Connect to the database defined in properties file or something
    private DatabaseAPI(){
        try {
            //askAuthInfo();
            System.out.println("Creating DatabaseAPI class");
            //Drivers need to be registered or Tomcat gets angry
            DriverManager.registerDriver(new org.postgresql.Driver());
            connection = DriverManager.getConnection(url, "musicserver", "testpw");

            //Define simple PreparedStatements used for testing for now. May be deleted later if changed to better system
            addSongStatement = connection.prepareStatement("""
                INSERT INTO song (name, artist_id, album_id, file_id) VALUES(
                    ?, 
                    (SELECT id from artist WHERE name = ?),
                    (SELECT id from album WHERE name = ?),
                    (SELECT id from file WHERE file_path = ?));
                """);
            addFileStatement = connection.prepareStatement("INSERT INTO file (file_path) VALUES(?)");
            addArtistStatement = connection.prepareStatement("INSERT INTO artist (name) VALUES(?)");
            addAlbumStatement = connection.prepareStatement("INSERT INTO album (name, artist_id) VALUES(?, (SELECT id from artist WHERE name=?))");
            getFilePathStatement = connection.prepareStatement("SELECT * FROM file");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    void AddSong(Song song) {
        System.out.println("Trying to add song: " + song.getName());
        try {
            addSongStatement.setString(1, song.getName());
            addSongStatement.setString(2, song.getArtist().getName());
            addSongStatement.setString(3, song.getAlbum().getName());

            addSongStatement.setString(4, "E:/muza/Hollow Knight/Hidden Dreams/Christopher Larkin - Truth, Beauty and Hatred.flac");

            addSongStatement.executeUpdate();
            System.out.println("Song: " + song.getArtist().getName() + song.getName() + " added successfully!");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    
    Song GetSongData() {

        Song song = new Song();
        return song;
    }

    void AddArtist(Artist artist){
        System.out.println("Trying to add artist: " + artist.getName());
        try {
            addArtistStatement.setString(1, artist.getName());

            addArtistStatement.executeUpdate();
            System.out.println("Artist: " + artist.getName() + " added successfully!");
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
    }

    void AddAlbum(Album album){
        System.out.println("Trying to add album: " + album.getName());
        try {
            addAlbumStatement.setString(1, album.getName());
            addAlbumStatement.setString(2, album.getArtist().getName());

            addAlbumStatement.executeUpdate();
            System.out.println("Album: " + album.getName() + " added successfully!");
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
    }

    void AddFile(){
        try {
            addFileStatement.setString(1, "E:/muza/Hollow Knight/Hidden Dreams/Christopher Larkin - Truth, Beauty and Hatred.flac");

            addFileStatement.executeUpdate();
            System.out.println("File added successfully!");
        } catch (SQLException e) {
            //TODO: handle exception
            e.printStackTrace();
        }
    }

    String GetFilePath(){
        String filepath = " ";
        try {
            System.out.println("Starting query.");
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM file");
            while(resultSet.next()){
                filepath = resultSet.getString("file_path");
                System.out.println("Query result: " + filepath);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return filepath;
    }

    File GetSongFile(){
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM file LIMIT 1");
            while(resultSet.next()){
                File file = new File(resultSet.getString("file_path"));
                System.out.println(file.getAbsolutePath());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Song song = new Song();
        return song.getSongFile();
    }

    void AddUser() {

    }

    void AuthenticateUser() {

    }

    void DeleteSong() {

    }


    private static void askAuthInfo() {
        Console cons;
        // char[] userChar;
        char[] passwdChar;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your database username: ");
        user = scanner.nextLine();
        if ((cons = System.console()) != null && (passwdChar = cons.readPassword("[%s]", "Password:")) != null) {
            password = new String(passwdChar);
            java.util.Arrays.fill(passwdChar, ' ');
        }
        scanner.close();
    }
}