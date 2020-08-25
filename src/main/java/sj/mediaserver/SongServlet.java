package sj.mediaserver;

import sj.mediaserver.DatabaseAPI;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.*;

public class SongServlet extends GenericServlet 
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void service(ServletRequest request, ServletResponse response)
    throws ServletException, IOException
    {
        DatabaseAPI api = DatabaseAPI.getInstance();
        String song = api.GetFilePath();
        
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        String SongResponse="""
            <html>
            <head>
            <title>Audio</title>
            </head>
            <body>

                <audio controls>
                    <source src="$String" type="audio/mpeg">
                    Your browser does not support the audio tag.
                </audio> 

            </body>
            </html> 
        """.replace("$String", song);
        writer.println(SongResponse);
    }
}