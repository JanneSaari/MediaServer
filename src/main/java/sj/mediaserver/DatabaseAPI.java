package sj.mediaserver;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jaudiotagger.audio.*;
import org.jaudiotagger.audio.exceptions.*;
import org.jaudiotagger.tag.*;

import sj.mediaserver.data.Album;
import sj.mediaserver.data.Artist;
import sj.mediaserver.data.Song;

public class DatabaseAPI {

    // TODO get url from Properties file or something
    private static final String url = "jdbc:postgresql://localhost/musicdb?allowMultiQueries=true";
    private static String user = "";
    private static String password = "";

    private Connection connection;

    // TODO remove at some point
    // Statements used for testing purposes for now. REMOVE LATER
    private static PreparedStatement addSongStatement;
    private static PreparedStatement addArtistStatement;
    private static PreparedStatement addAlbumStatement;
    private static PreparedStatement addFileStatement;
    private static PreparedStatement getFilePathStatement;
    private static PreparedStatement queryStatement;
    private static PreparedStatement albumQueryStatement;

    // this class is a singleton and should not be instantiated directly!
    private static DatabaseAPI instance = new DatabaseAPI();

    public static DatabaseAPI getInstance() {
        return instance;
    }

    // TODO create place to define url instead of hardcoding in top of the class
    // Connect to the database defined in properties file or something
    private DatabaseAPI() {
        try {
            // askAuthInfo();
            System.out.println("Creating DatabaseAPI class");
            // Drivers need to be registered or Tomcat gets angry
            DriverManager.registerDriver(new org.postgresql.Driver());
            connection = DriverManager.getConnection(url, "musicserver", "testpw");

            // Define simple PreparedStatements used for testing for now. May be deleted
            // later if changed to better system
            addSongStatement = connection.prepareStatement("""
                    INSERT INTO song (name, artist_id, album_id, file_id) VALUES(
                        ?,
                        (SELECT id from artist WHERE name = ?),
                        (SELECT id from album WHERE name = ?),
                        (SELECT id from file WHERE file_path = ?));
                    """);
            addFileStatement = connection.prepareStatement("INSERT INTO file (file_path) VALUES(?)");
            addArtistStatement = connection.prepareStatement("INSERT INTO artist (name) VALUES(?)");
            addAlbumStatement = connection.prepareStatement(
                    "INSERT INTO album (name, artist_id) VALUES(?, (SELECT id from artist WHERE name=?))");
            getFilePathStatement = connection.prepareStatement("SELECT * FROM file");
            queryStatement = connection.prepareStatement("SELECT ? FROM artist WHERE name = ?");
            albumQueryStatement = connection.prepareStatement("SELECT ? FROM album WHERE name = ?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     *  Read and add song, artist, album, etc.. to database from file
     */
    void addSongFromFile(File file) {

        try {
            // Metadata
            AudioFile audioFile;
            Tag tag;
            AudioHeader audioHeader;

            audioFile = AudioFileIO.read(file);
            tag = audioFile.getTag();
            audioHeader = audioFile.getAudioHeader();
            
            //Artist
            Artist artist = new Artist();
            artist.setName(tag.getFirst(FieldKey.ARTIST));
            System.out.println(checkIfArtistExists(artist.getName()));
            if(!checkIfArtistExists(artist.getName())){
                addArtist(artist);
            }
            else
                System.out.println("Artist: " + artist.getName() + " already exists in database!");

            //Album
            Album album = new Album();
            album.setArtist(artist);
            album.setName(tag.getFirst(FieldKey.ALBUM));
            System.out.println(checkIfAlbumExists(album.getName()));
            if(!checkIfAlbumExists(album.getName())){
                addAlbum(album);
            }
            else
                System.out.println("Album: " + album.getName() + " already exists in database!");

            //Song
            Song song = new Song();
            song.setName(tag.getFirst(FieldKey.TITLE));
            song.setAlbum(album);
            song.setArtist(artist);
            song.setSongFile(file);
            
            addFile(file);
            addSong(song);

        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException
                | InvalidAudioFrameException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
    * Adds all songs, artists, etc from folder and subfolders to database
    */
    void addAllSongRecursively(String folderpath){
        //TODO make library directories that get scanned automatically and changes get added to database
        //TODO rename to something better

        try (Stream<Path> walk = Files.walk(Paths.get(folderpath))) {

            List<String> result = walk.filter(Files::isRegularFile)
                    .map(x -> x.toString()).collect(Collectors.toList());
    
            for (String string : result) {
                File file = new File(string);
                addSongFromFile(file);
            }
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void addSong(Song song) {
        System.out.println("Trying to add song: " + song.getName());
        try {
            addSongStatement.setString(1, song.getName());
            addSongStatement.setString(2, song.getArtist().getName());
            addSongStatement.setString(3, song.getAlbum().getName());

            addSongStatement.setString(4, song.getSongFile().getAbsolutePath());

            addSongStatement.executeUpdate();
            System.out.println("Song: " + song.getArtist().getName() + "-" + song.getName() + " added successfully!");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    
    void addArtist(Artist artist) {
        System.out.println("Trying to add artist: " + artist.getName());
        try {
            addArtistStatement.setString(1, artist.getName());
            
            addArtistStatement.executeUpdate();
            System.out.println("Artist: " + artist.getName() + " added successfully!");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    
    void addAlbum(Album album) {
        System.out.println("Trying to add album: " + album.getName());
        try {
            addAlbumStatement.setString(1, album.getName());
            addAlbumStatement.setString(2, album.getArtist().getName());
            
            addAlbumStatement.executeUpdate();
            System.out.println("Album: " + album.getName() + " added successfully!");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    
    void addFile(File file) {
        System.out.println("Trying to add file: " + file.getAbsolutePath());
        try {
            addFileStatement.setString(1, file.getAbsolutePath());
            
            addFileStatement.executeUpdate();
            System.out.println("File: " + file.getAbsolutePath() + " added successfully!");
        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    
    Song getSongData() {
        //Currently just a placeholder function 
        
        Song song = new Song();
        return song;
    }

    //FIXME currently just gets first file and returns its filepath
    String getFilePath() {
        String filepath = "";
        try {
            System.out.println("Starting query.");
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM file LIMIT 1");
            while (resultSet.next()) {
                filepath = resultSet.getString("file_path");
                System.out.println("Query result: " + filepath);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return filepath;
    }
    
    //TODO create another file function getFile(Song) to get file identified from song data
    /**
     * Get file from database identified by UUID file_id
     * @param id 
     * @return File. Returns file identified by id.
     */
    File getFile(UUID id) {

        File file = null;
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("SELECT file_path FROM file WHERE id = ?");
            ps.setObject(1, id);
            //ResultSet resultSet = connection.createStatement().executeQuery("SELECT 1 FROM file WHERE id = " + uuid.toString());
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                file = new File(resultSet.getString("file_path"));
                System.out.println(file.getAbsolutePath());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return file;
    }


    /**
     * Get list of all artists in database and return it
     * @return ArrayList<Artist> list of all artists in database
     */
    ArrayList<Artist> getArtists() {
        ArrayList<Artist> artists = new ArrayList<>();
        try {
            System.out.println("Starting query.");
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT name FROM artist");
            while (resultSet.next()) {
                Artist artist = new Artist();
                artist.setName(resultSet.getString("name"));
                artists.add(artist);
                System.out.println("Query result: " + artist.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return artists;
    }


    /**
     * Get list of all albums in database and return it
     * @return ArrayList<Album> list of all albums in database
     */
    ArrayList<Album> getAlbums() {
        ArrayList<Album> albums = new ArrayList<>();
        try {
            System.out.println("Starting query.");
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT name FROM album");
            while (resultSet.next()) {
                Album album = new Album();
                album.setName(resultSet.getString("name"));
                albums.add(album);
                System.out.println("Query result: " + album.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return albums;
    }



    void addUser() {

    }

    void authenticateUser() {

    }

    void deleteSong() {

    }

    boolean checkIfArtistExists(String artist) {
        try {
            queryStatement.setInt(1, 1);
            // queryStatement.setString(2, "artist");
            // queryStatement.setString(3, "name");
            queryStatement.setString(2, artist);

            ResultSet rs = queryStatement.executeQuery();
            if(rs.next())
                return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

    boolean checkIfAlbumExists(String album) {
        try {
            albumQueryStatement.setInt(1, 1);
            // queryStatement.setString(2, "artist");
            // queryStatement.setString(3, "name");
            albumQueryStatement.setString(2, album);

            ResultSet rs = albumQueryStatement.executeQuery();
            if(rs.next())
                return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
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