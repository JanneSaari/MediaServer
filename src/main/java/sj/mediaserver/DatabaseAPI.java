package sj.mediaserver;

import java.io.Console;
import java.io.File;
import java.sql.*;
import java.util.Scanner;

import sj.mediaserver.data.Song;

public class DatabaseAPI {
    
    //TODO get url from Properties file or something
    private static final String url = "jdbc:postgresql://localhost/musicdb?allowMultiQueries=true";
    private static String user = "";
    private static String password = "";
    
    private static PreparedStatement addSongStatement;
    private static PreparedStatement addFileStatement;
    private static PreparedStatement getFilePathStatement;
    private Connection connection;
    
    // this class is a singleton and should not be instantiated directly!
    private static DatabaseAPI instance = new DatabaseAPI();
    
    public static DatabaseAPI getInstance() {
        return instance;
    }

    //TODO create place to define url instead of hardcoding in top of the class
    //Connect to the database defined in properties file or something
    private DatabaseAPI(){
        try {
            askAuthInfo();
            connection = DriverManager.getConnection(url, user, password);
            addFileStatement = connection.prepareStatement("INSERT INTO file (filepath) VALUES(?)");
            getFilePathStatement = connection.prepareStatement("SELECT * FROM file");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void AddSong(Song song) {
        try {
            addSongStatement.setString(1, song.getName());
            addSongStatement.setString(2, song.getArtist());
            addSongStatement.setString(3, song.getAlbum());
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    Song GetSongData() {

        Song song = new Song();
        return song;
    }

    void AddFile(){
        try {
            addFileStatement.setString(1, "src/main/resources/audio/03 - Ju Ju Magic.mp3");
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
                filepath = resultSet.getString("filepath");
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
                File file = new File(resultSet.getString("filepath"));
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