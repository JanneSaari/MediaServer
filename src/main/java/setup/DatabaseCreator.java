package setup;

import java.io.Console;
import java.sql.*;

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
    static final String postgresUrl = "jdbc:postgresql://localhost/postgres?allowMultiQueries=true";
    static final String musicDBUrl = "jdbc:postgresql://localhost/musicdb?allowMultiQueries=true";
    static String user = "postgres";
    static String password = "";
    static boolean dbIsPostgress = true;

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(postgresUrl, user, password);
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

    static int SQLAction(String sql) {
        Connection conn = null;
        Statement statement = null;

        try {
            conn = DriverManager.getConnection(musicDBUrl, user, password);
            statement = conn.createStatement();
            statement.execute(sql);

            System.out.println("Action executed successfully...");
        } catch (SQLException e) {
            System.out.println("Action execution failed!: " + System.lineSeparator() + "\"" + sql + "\"");
            e.printStackTrace();
            return -1;
        }

        return 0;
    }

    static int DropDatabase() {
        Connection conn = null;
        Statement statement = null;

        try {
            conn = DriverManager.getConnection(postgresUrl, user, password);
            statement = conn.createStatement();
            statement.execute(ClearDatabase);

            System.out.println("Database dropped successfully...");
        } catch (SQLException e) {
            System.out.println("Database dropping failed!: " + System.lineSeparator() + "\"" + ClearDatabase + "\"");
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
            conn = DriverManager.getConnection(postgresUrl, user, password);

            //TODO delete this
            //or ask user if they have premade database or something
            DropDatabase();

            System.out.println("Creating database...");
            statement = conn.createStatement();

            statement.executeUpdate(Database);
            System.out.println("Database created successfully...");

            //If database is postgresql, get uuid_ossp extension
            if(dbIsPostgress)
                SQLAction(Extensions);

            SQLAction(ArtistTable);
            SQLAction(AlbumTable);
            SQLAction(PlaylistTable);
            SQLAction(PlaylistSongsTable);
            SQLAction(UserTable);
            SQLAction(PasswordTable);
            SQLAction(FileTable);
            SQLAction(SongTable);
            
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

    static String Database = "CREATE DATABASE musicdb";

    
    static String ArtistTable = 
    """
    CREATE TABLE artist(
        id UUID not NULL, 
        name VARCHAR(255),
        PRIMARY KEY ( id ))
    """; 
    
    static String AlbumTable = 
    """
    CREATE TABLE album (
        id UUID not NULL,
        name VARCHAR(255),
        PRIMARY KEY ( id ))
    """; 
    
    static String PlaylistTable = 
    """
    CREATE TABLE playlist(
        id UUID not NULL,
        name VARCHAR(255),
        PRIMARY KEY ( id ))
    """; 
    
    static String PlaylistSongsTable = 
    """
    CREATE TABLE playlistsongs(
        id UUID not NULL,
        name VARCHAR(255),
        PRIMARY KEY ( id ))
    """; 
    
    static String UserTable = 
    """
    CREATE TABLE user_account(
        id UUID not NULL,
        name VARCHAR(255),
        PRIMARY KEY ( id ))
    """; 
    
    static String PasswordTable = 
    """
    CREATE TABLE password(
        id UUID not NULL,
        name VARCHAR(255),
        PRIMARY KEY ( id ))
    """; 
    
    static String FileTable = 
    """
    CREATE TABLE file(
        id UUID not NULL,
        name VARCHAR(255),
        PRIMARY KEY ( id ))
    """; 

    static String SongTable = 
    """
    CREATE TABLE song (
        id UUID DEFAULT gen_random_uuid(),
        AlbumID UUID,
        ArtistID UUID,
        name VARCHAR(255), 
        PRIMARY KEY ( id ),
        CONSTRAINT fk_ArtistID
            FOREIGN KEY(ArtistID)
                REFERENCES Artist(id))
    """;

    static String Extensions = 
    """
    CREATE EXTENSION IF NOT EXISTS "pgcrypto";
    """;

    //TODO delete later
    //Used to clear database while testing
    static String ClearDatabase = 
    """
    SELECT *
    FROM pg_stat_activity
    WHERE datname = 'musicdb';

    SELECT pg_terminate_backend (pid)
    FROM pg_stat_activity
    WHERE pg_stat_activity.datname = 'musicdb';

    DROP DATABASE IF EXISTS musicdb
    """;
}