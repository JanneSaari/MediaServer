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

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
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
        String song = api.getFilePath();
        
        response.setContentType("audio/flac");

        ServletOutputStream stream = response.getOutputStream();
        response.setHeader("Accept-Ranges", "bytes");

        byte[] audioBytes = null;
        try {
            audioBytes = FileUtils.readFileToByteArray(new File(song));
            response.setContentLength(audioBytes.length);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(audioBytes);
            IOUtils.copy(inputStream, stream);
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }

        // try {
        //     FileInputStream stream = new FileInputStream(new File(song));
        //     int c;
        //     while ((c=stream.read()) != -1) {
        //         response.getWriter().write(c);
        //         //System.out.println("Writing response" + c);
        //     }
        //     response.getWriter().flush();
        //     System.out.println("Response writer flushed");
        // } catch (Exception e) {
        //     //TODO: handle exception
        //     e.printStackTrace();
        // }
        // PrintWriter writer = response.getWriter();
        // String SongResponse="$String".replace("$String", song);
        // writer.println(SongResponse);
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