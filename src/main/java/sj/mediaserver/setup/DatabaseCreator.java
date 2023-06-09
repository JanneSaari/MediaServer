package sj.mediaserver.setup;

import java.io.Console;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.io.IOException;
import java.sql.*;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class DatabaseCreator {

    /*
     * Created following https://www.postgresqltutorial.com/
     * Check https://jdbc.postgresql.org/documentation/head/index.html for more info
     * about
     * PostgreSQL JBCD Driver
     * 
     * 
     * TODO -On installation, ask user if they want to use (authenticated) database
     * server(), or create local database without need for authentication
     * 
     * 
     * TODO Create database on first time setup. Using PostgreSQL installer for now.
     * // Try creating DB from binaries or source later
     * 
     * TODO try to get ssl working at some point Check https://letsencrypt.org/ for
     * free ssl certificate, https://www.postgresql.org/docs/9.1/libpq-ssl.html for
     * more info about postgresql and ssl
     * static final String url = "jdbc:postgresql://localhost/postgres?ssl=true";
     */

    // TODO get Urls, user and passwords from separate source or ask user for them.
    // Not too big of a deal when using just locally, but if you want to use it over
    // internet, this is not good
    static final String hostUrl = "java:/comp/env/jdbc:postgresql";
    static final String musicDBUrl = "java:/comp/env/jdbc:postgresql//musicdb?allowMultiQueries=true";
    static String user = "myuser";
    static String password = "mypasswd";
    static boolean dbIsPostgress = true;

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(hostUrl, user, password);
    }

    private static void askAuthInfo() {
        Console cons;
        // char[] userChar;
        char[] passwdChar;
        // if ((cons = System.console()) != null && (userChar =
        // cons.readPassword("[%s]", "Username:")) != null) {
        // user = new String(user);
        // java.util.Arrays.fill(userChar, ' ');
        // }
        if ((cons = System.console()) != null && (passwdChar = cons.readPassword("[%s]", "Password:")) != null) {
            password = new String(passwdChar);
            java.util.Arrays.fill(passwdChar, ' ');
        }
    }

    // TODO move to helper class later when its created
    public static String loadResourceToString(final String path) {
        final InputStream stream = Thread
                .currentThread()
                .getContextClassLoader()
                .getResourceAsStream(path);
        try {
            return IOUtils.toString(stream, Charset.defaultCharset());
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        } finally {
            // IOUtils.closeQuietly(stream);
        }
    }

    static int SQLAction(String sql) {
        Connection conn = null;
        // TODO read about PreparedStatement
        // PreparedStatement statement = null;
        Statement statement = null;

        try {
            conn = DriverManager.getConnection(musicDBUrl, user, password);
            // statement = conn.prepareStatement(sql);
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
            InitialContext cxt = new InitialContext();
            if (cxt == null) {
                throw new Exception("Uh oh -- no context!");
            }

            DataSource ds = (DataSource) cxt.lookup("java:/comp/env/jdbc/postgres");

            if (ds == null) {
                throw new Exception("Data source not found!");
            }

            System.out.println("Connecting to server...");
            conn = ds.getConnection(musicDBUrl, hostUrl);
            // conn = DriverManager.getConnection(hostUrl, user, password);

            // TODO delete this
            // or ask user if they have premade database or something
            // DropDatabase();

            System.out.println("Creating database...");
            statement = conn.createStatement();

            File dbCreationFile = new File("resources/SQL/DatabaseCreation");
            String dbString = FileUtils.readFileToString(dbCreationFile, Charset.defaultCharset());
            // String dbString = loadResourceToString(file.getAbsolutePath());
            statement.execute(dbString);
            System.out.println("Database created successfully...");

            // If database is postgresql, get uuid_ossp extension
            if (dbIsPostgress) {
                File SQLExtensionsFile = new File("src/main/resources/SQL/Extensions");
                String Extensions = FileUtils.readFileToString(SQLExtensionsFile, Charset.defaultCharset());
                SQLAction(Extensions);
            }

            // Create tables for database
            File TableCreationFile = new File("src/main/resources/SQL/TableCreation");
            String TableString = FileUtils.readFileToString(TableCreationFile, Charset.defaultCharset());
            // String dbString = loadResourceToString(file.getAbsolutePath());
            SQLAction(TableString);
            System.out.println("Tables created successfully...");

            // Create MusicServer database user
            File CreateUserFile = new File("src/main/resources/SQL/AddDatabaseUser");
            String CreateUserString = FileUtils.readFileToString(CreateUserFile, Charset.defaultCharset());
            SQLAction(CreateUserString);
            System.out.println("Default user MusicServer created on database");

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
}