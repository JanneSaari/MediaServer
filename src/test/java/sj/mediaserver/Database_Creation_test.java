package sj.mediaserver;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import sj.mediaserver.setup.DatabaseCreator;

public class Database_Creation_test {

    /*
     * Sandbox for testing and adding features
     */

    public static void main(String[] args) {

        DatabaseCreator.CreateDatabase();
        DatabaseAPI API = DatabaseAPI.getInstance();

        API.addAllSongRecursively("E:/muza");
    }
}