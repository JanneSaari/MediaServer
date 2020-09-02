package sj.mediaserver;

import sj.mediaserver.DatabaseAPI;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

public class SongServlet extends HttpServlet 
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    //TODO################################################################
    /*
    Think how to serve the media files outside of the webapp
    Read about Tomcat Default servlet. This may help.
    Options:
        Create temp folder within webapp that has copies of files to be served
            -con. file needs to be copied to server -> delay and extra write/read
        Just have source outside of webapp
            - give access to files outside of app!!!!
    */
    //####################################################################

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        DatabaseAPI api = DatabaseAPI.getInstance();
        String uuid = request.getParameter("id");
        File song = api.getFile(UUID.fromString(uuid));
        
        //Check for file extension and set content type accordinly
        String fileType = FilenameUtils.getExtension(song.getName());
        switch (fileType) {
            case "flac":
                response.setContentType("audio/flac");
                break;
            case "mp3":
                response.setContentType("audio/mpeg");
                break;
            default:
                break;
        }

        ServletOutputStream stream = response.getOutputStream();
        response.setHeader("Accept-Ranges", "bytes");

        byte[] audioBytes = null;
        try {
            audioBytes = FileUtils.readFileToByteArray(song);
            response.setContentLength(audioBytes.length);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(audioBytes);
            IOUtils.copy(inputStream, stream);
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > 2147483647L) return -1;
        else return (int) count;
    }

    public static long copyLarge(InputStream input, OutputStream output) throws IOException {
        byte buffer[] = new byte[4096];
        long count = 0L;
        for (int n = 0; -1 != (n = input.read(buffer));) {
            output.write(buffer, 0, n);
            count += n;
        }
    
        return count;
    }
}