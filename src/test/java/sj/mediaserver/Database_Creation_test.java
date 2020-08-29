package sj.mediaserver;

import java.io.File;

import sj.mediaserver.DatabaseAPI;
import sj.mediaserver.data.Album;
import sj.mediaserver.data.Artist;
import sj.mediaserver.data.Song;
import sj.mediaserver.setup.DatabaseCreator;

public class Database_Creation_test {
    
public static void main(String[] args) {

    DatabaseCreator.CreateDatabase();

    File testFile = new File("E:/muza/Hollow Knight/Hidden Dreams/Christopher Larkin - Truth, Beauty and Hatred.flac");
    //Test Artist
    Artist testArtist = new Artist();
    testArtist.setName("Christopher Larkin");
    //Test Album
    Album testAlbum = new Album();
    testAlbum.setArtist(testArtist);
    testAlbum.setName("Hidden Dreams");
    //Test Song
    Song testSong = new Song();
    testSong.setName("Truth, Beauty and Hatred");
    testSong.setAlbum(testAlbum);
    testSong.setArtist(testArtist);
    testSong.setSongFile(testFile);
    
    
    DatabaseAPI API = DatabaseAPI.getInstance();
    API.AddFile();
    String filepath = API.GetFilePath();

    API.AddArtist(testArtist);
    API.AddAlbum(testAlbum);
    API.AddSong(testSong);
}
}