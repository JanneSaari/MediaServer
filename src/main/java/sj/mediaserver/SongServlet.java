package sj.mediaserver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        DatabaseAPI api = DatabaseAPI.getInstance();
        String uuid = request.getParameter("id");
        File songFile = api.getFileFromSongID(UUID.fromString(uuid));
        
        //Check for file extension and set content type accordinly
        String fileType = FilenameUtils.getExtension(songFile.getName());
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
            audioBytes = FileUtils.readFileToByteArray(songFile);
            response.setContentLength(audioBytes.length);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(audioBytes);
            IOUtils.copy(inputStream, stream);
        } catch (Exception e) {
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