package setup;

import java.io.Console;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.io.IOException;
import java.sql.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class DatabaseCreator {

    /*
     * Created following https://www.postgresqltutorial.com/ Check
     * https://jdbc.postgresql.org/documentation/head/index.html for more info about
     * PostgreSQL JBCD Driver
     */
    /*
     * TODO -On installation, ask user if they want to use (authenticated) database
     * server(), or create local database without need for authentication
     */
    /*
     * TODO Create database on first time setup Using PostgreSQL installer for now.
     * Try creating DB from binaries or source later
     */

    // TODO read database username and password from file or ask the user for them

    /*
     * TODO try to get ssl working at some point Check https://letsencrypt.org/ for
     * free ssl certificate, https://www.postgresql.org/docs/9.1/libpq-ssl.html for
     * more info about postgresql and ssl 
     static final String url = "jdbc:postgresql://localhost/postgres?ssl=true";
     */
    static final String hostUrl = "jdbc:postgresql://localhost/";
    static final String musicDBUrl = "jdbc:postgresql://localhost/musicdb?allowMultiQueries=true";
    static String user = "postgres";
    static String password = "";
    static boolean dbIsPostgress = true;

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(hostUrl, user, password);
    }
    
    private static void askAuthInfo() {
        Console cons;
        // char[] userChar;
        char[] passwdChar;
        // if ((cons = System.console()) != null && (userChar = cons.readPassword("[%s]", "Username:")) != null) {
        //     user = new String(user);
        //     java.util.Arrays.fill(userChar, ' ');
        // }
        if ((cons = System.console()) != null && (passwdChar = cons.readPassword("[%s]", "Password:")) != null) {
            password = new String(passwdChar);
            java.util.Arrays.fill(passwdChar, ' ');
        }
    }

    //TODO move to helper class later when its created
    public static String loadResourceToString(final String path){
        final InputStream stream =
            Thread
                .currentThread()
                .getContextClassLoader()
                .getResourceAsStream(path);
        try{
            return IOUtils.toString(stream, Charset.defaultCharset());
        } catch(final IOException e){
            throw new IllegalStateException(e);
        } finally{
            //IOUtils.closeQuietly(stream);
        }
    }

    static int SQLAction(String sql) {
        Connection conn = null;
        //TODO read about PreparedStatement
        //PreparedStatement statement = null;
        Statement statement = null;

        try {
            conn = DriverManager.getConnection(musicDBUrl, user, password);
            //statement = conn.prepareStatement(sql);
            statement = conn.createStatement();
            statement.executeUpdate(sql);

            System.out.println("Action executed successfully...");
        } catch (SQLException e) {
            System.out.println("Action execution failed!: " + System.lineSeparator() + "\"" + sql + "\"");
            e.printStackTrace();
            return -1;
        }

        return 0;
    }

    public static void CreateDatabase() {
        Connection conn = null;
        Statement statement = null;

        askAuthInfo();

        try {
            System.out.println("Connecting to server...");
            conn = DriverManager.getConnection(hostUrl, user, password);

            //TODO delete this
            //or ask user if they have premade database or something
            //DropDatabase();

            System.out.println("Creating database...");
            statement = conn.createStatement();

            File dbCreationFile = new File("src/main/resources/SQL/DatabaseCreation");
            String dbString = FileUtils.readFileToString(dbCreationFile, Charset.defaultCharset());
            //String dbString = loadResourceToString(file.getAbsolutePath());
            statement.execute(dbString);
            System.out.println("Database created successfully...");

            //If database is postgresql, get uuid_ossp extension
            if(dbIsPostgress){
                File SQLExtensionsFile = new File("src/main/resources/SQL/Extensions");
                String Extensions = FileUtils.readFileToString(SQLExtensionsFile, Charset.defaultCharset());
                SQLAction(Extensions);
            }

            File TableCreationFile = new File("src/main/resources/SQL/TableCreation");
            String TableString = FileUtils.readFileToString(TableCreationFile, Charset.defaultCharset());
            //String dbString = loadResourceToString(file.getAbsolutePath());
            SQLAction(TableString);
            System.out.println("Tables created successfully...");
            
        } catch (SQLException SQLe) {
            // Handle error for JDBC
            SQLe.printStackTrace();
        } catch (Exception e) {
            // Handle error for Class.forName
            e.printStackTrace();
        } finally {
            // finally block used to close resources
            try {
                if (statement != null)
                statement.close();
            } catch (SQLException SQLe2) {
                // TODO: handle exception
            }
            try {
                if (conn != null)
                    conn.close();
                } catch (SQLException SQLe) {
                    SQLe.printStackTrace();
                }
            }
            System.out.println("Goodbye!");
        }
    
    // static String Database = "CREATE DATABASE musicdb";
    
    // static String SongTable = 
    // """
    // CREATE TABLE song (
    //     id UUID DEFAULT gen_random_uuid(),
    //     AlbumID UUID,
    //     ArtistID UUID,
    //     name VARCHAR(255), 
    //     PRIMARY KEY ( id ))
    // """;
    
    // static String ArtistTable = 
    // """
    // CREATE TABLE artist(
    //     id UUID DEFAULT gen_random_uuid(), 
    //     name VARCHAR(255),
    //     PRIMARY KEY ( id ))
    // """; 
    
    // static String AlbumTable = 
    // """
    // CREATE TABLE album (
    //     id UUID DEFAULT gen_random_uuid(),
    //     name VARCHAR(255),
    //     ArtistID UUID,
    //     PRIMARY KEY ( id ))
    // """; 
    
    // static String PlaylistTable = 
    // """
    // CREATE TABLE playlist(
    //     id UUID DEFAULT gen_random_uuid(),
    //     name VARCHAR(255),
    //     CreatorID UUID,
    //     PRIMARY KEY ( id ))
    // """; 
    
    // static String PlaylistSongsTable = 
    // """
    // CREATE TABLE playlistsongs(
    //     id UUID DEFAULT gen_random_uuid(),
    //     playlistID UUID,
    //     songID UUID,
    //     orderNumber SMALLINT,
    //     PRIMARY KEY ( id ))
    // """; 
    
    // static String UserTable = 
    // """
    // CREATE TABLE user_account(
    //     id UUID DEFAULT gen_random_uuid(),
    //     name VARCHAR(255),
    //     PRIMARY KEY ( id ))
    // """; 
    
    // //TODO store password as hash
    // static String PasswordTable = 
    // """
    // CREATE TABLE password(
    //     id UUID DEFAULT gen_random_uuid(),
    //     pw VARCHAR(255),
    //     userID UUID,
    //     PRIMARY KEY ( id ))
    // """; 
    
    // static String FileTable = 
    // """
    // CREATE TABLE file(
    //     id UUID DEFAULT gen_random_uuid(),
    //     filepath VARCHAR(255),
    //     PRIMARY KEY ( id ))
    // """; 


    // static String Extensions = 
    // """
    // CREATE EXTENSION IF NOT EXISTS "pgcrypto";
    // """;

    // //TODO delete later
    // //Used to clear database while testing
    // static String ClearDatabase = 
    // """
    // SELECT *
    // FROM pg_stat_activity
    // WHERE datname = 'musicdb';

    // SELECT pg_terminate_backend (pid)
    // FROM pg_stat_activity
    // WHERE pg_stat_activity.datname = 'musicdb';

    // DROP DATABASE IF EXISTS musicdb
    // """;
}