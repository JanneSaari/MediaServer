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

import org.apache.commons.io.FilenameUtils;
import org.jaudiotagger.audio.*;
import org.jaudiotagger.audio.exceptions.*;
import org.jaudiotagger.tag.*;
import org.json.JSONArray;
import org.json.JSONObject;

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
                    INSERT INTO songs (name, artist_id, album_id, file_id) VALUES(
                        ?,
                        (SELECT id from artists WHERE name = ?),
                        (SELECT id from albums WHERE name = ?),
                        (SELECT id from files WHERE file_path = ?));
                    """);
            addFileStatement = connection.prepareStatement("INSERT INTO files (file_path) VALUES(?)");
            addArtistStatement = connection.prepareStatement("INSERT INTO artists (name) VALUES(?)");
            addAlbumStatement = connection.prepareStatement(
                    "INSERT INTO albums (name, artist_id) VALUES(?, (SELECT id from artists WHERE name=?))");
            getFilePathStatement = connection.prepareStatement("SELECT * FROM files");
            queryStatement = connection.prepareStatement("SELECT ? FROM artists WHERE name = ?");
            albumQueryStatement = connection.prepareStatement("SELECT ? FROM albums WHERE name = ?");
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
            artist.setName(tag.getFirst(FieldKey.ALBUM_ARTIST));
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
            System.out.println("File that failed: " + file.getAbsolutePath());
        } catch (NoSuchMethodError e) {
            //TODO stuff
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
                try {
                    File file = new File(string);

                    String ext = FilenameUtils.getExtension(file.getName());
                    if(ext.equals("mp3") || ext.equals("flac")) {
                        addSongFromFile(file);
                    }

                } catch (Exception e) {
                    //TODO: handle exception
                    e.printStackTrace();
                }
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
            System.out.println("###########################################");
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
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM files LIMIT 1");
            while (resultSet.next()) {
                filepath = resultSet.getString("file_path");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return filepath;
    }
    
    //TODO create another file function getFile(Song) to get file identified from song data
    /**
     * Get file from database identified by UUID id
     * @param id Used to identify wanted file
     * @return File. Returns file identified by id.
     */
    File getFile(UUID id) {

        File file = null;
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("SELECT file_path FROM files WHERE id = ?");
            ps.setObject(1, id);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                file = new File(resultSet.getString("file_path"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return file;
    }


    /**
     * Get file from database identified by songID. If you have fileID, use getFile(UUID id) instead
     * @param songID Used to identify wanted file. Note: this is songID, not fileID.
     * @return File identified by songID
     */
    File getFileFromSongID(UUID songID) {

        File file = null;
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("""
                SELECT file_path
                FROM files
                INNER JOIN songs
                    on songs.file_id = files.id
                    WHERE songs.id = ?
            """);
            ps.setObject(1, songID);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                file = new File(resultSet.getString("file_path"));
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
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("""
                SELECT
                    name
                FROM
                    artists
                """);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Artist artist = new Artist(resultSet.getString("name"));
                artists.add(artist);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return artists;
    }

    /**
     * Get list of all artists with their albums and songs
     * Returns list in format:
     * Artists[]
     *      artist_name,
     *      Albums[]
     *          album_name,
     *          Songs[]
     *              song_name,
     *              song_id
     * @return JSONArray list of all artists and their albums and songs in JSON form
     */
    JSONArray getLibraryList() {
        JSONArray arrayList = new JSONArray();
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("""
                SELECT
                json_build_object(
                    'artist_name', artists.name,
                    'albums', json_agg(
                        json_build_object(
                            'album_name', to_json(albums.name),
                            'album_id', to_json(albums.id),
                            'songs', (
                                SELECT json_agg(
                                    json_build_object(
                                        'song_name', songs.name,
                                        'song_id', songs.id)
                                    )
                                FROM songs
                                WHERE songs.album_id = albums.id
                            )
                        )
                    )
                )
                FROM artists
                INNER JOIN albums
                    on albums.artist_id = artists.id
                GROUP BY artists.name
                """);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                JSONObject object = new JSONObject(resultSet.getString("json_build_object"));
                arrayList.put(object);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    /**
     * Get list of songs in album
     * @param album 
     * @return JSONArray of all songs in album
     */
    JSONArray getSongList(UUID albumID) {
        JSONArray arrayList = new JSONArray();
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("""
                SELECT
                    json_build_object(
                        'song_name', songs.name,
                        'song_id', songs.id
                    )
                FROM songs
                WHERE songs.album_id = ?;
                """);
            ps.setObject(1, albumID);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                JSONObject object = new JSONObject(resultSet.getString("json_build_object"));
                arrayList.put(object);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return arrayList;
    }


    /**
     * Get a playlist
     * @param UUID, id of the playlist
     * @return JSONArray of the playlist
     */
    JSONArray getPlayList(UUID playlistID) {
        JSONArray arrayList = new JSONArray();
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("""
                SELECT 
                    json_build_object(
                        'order_number', playlistsongs.order_num,
                        'song_id', playlistsongs.song_id,
                        'song_name', songs.name
                    )
                FROM playlistsongs
                INNER JOIN songs
                    on playlistsongs.song_id = songs.id
                WHERE playlistsongs.playlist_id = ?
                ORDER BY order_num
                """);
            ps.setObject(1, playlistID);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                JSONObject object = new JSONObject(resultSet.getString("json_build_object"));
                arrayList.put(object);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    /**
     * 
     * @return
     */
    void SavePlaylist(UUID userID, JSONArray playlist) {

    }


    /**
     * Get list of all songs in database and return it
     * @return ArrayList<Song> list of all songs in database
     */
    ArrayList<Song> getSongs() {
        ArrayList<Song> songs = new ArrayList<>();
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("""
                SELECT
                    songs.name as song_name,
                    artists.name as artist_name,
                    albums.name as album_name
                FROM 
                    songs
                INNER JOIN albums
                    on songs.album_id = albums.id
                INNER JOIN artists
                    on songs.artist_id = artists.id        
                """);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Song song = new Song();
                song.setName(resultSet.getString("song_name"));
                song.setAlbum(resultSet.getString("album_name"));
                song.setArtist(resultSet.getString("artist_name"));
                
                songs.add(song);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return songs;
    }


    /**
     * Get list of all albums in database and return it
     * @return ArrayList<Album> list of all albums in database
     */
    ArrayList<Album> getAlbums() {
        ArrayList<Album> albums = new ArrayList<>();
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT name FROM albums");
            while (resultSet.next()) {
                Album album = new Album();
                album.setName(resultSet.getString("name"));
                albums.add(album);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return albums;
    }

    //TODO dont use text password
    int addUser(String userName, String password) {
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("""
                WITH data(userName, password) AS (
                    VALUES (?, crypt(?, gen_salt('bf')))
                ),
                ins1 AS (
                    INSERT INTO users(name)
                    SELECT userName FROM data
                --ON CONFLICT DO NOTHING
                RETURNING id AS user_id
                )
                INSERT INTO passwords(password_hash, user_id)
                SELECT password, ins1.user_id
                FROM data, ins1
            """);

            ps.setString(1, userName);
            ps.setString(2, password);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }


    /**
     * Check if userName already exists in database
     * @param userName Username to check
     * @return -1 if check failed for some reason, 0 if username doesn't exist, 1 if username exists already
     */
    int checkIfUserNameExists(String userName) {        
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("""
                SELECT name
                FROM users
                WHERE name = ?
            """);

            ps.setString(1, userName);

            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return 1;
            }
            else
                return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Authenticate user
     * @param userName Username to check
     * @param password Users password
     * @return 1 if user is valid. 0 if user is not valid. -1 if authentication failed for some reason
     */
    int authenticateUser(String userName, String password) {
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("""
                SELECT 
                    name,
                    passwords.password_hash as password
                FROM users
                INNER JOIN passwords
                    ON users.id = passwords.user_id
                WHERE
                name = ? AND passwords.password_hash = crypt(?, passwords.password_hash)
            """);
            ps.setString(1, userName);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

        return 0;
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