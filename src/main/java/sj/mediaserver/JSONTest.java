package sj.mediaserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.json.JSONArray;


@SuppressWarnings("serial")
public class JSONTest extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        DatabaseAPI API = DatabaseAPI.getInstance();

        UUID albumID = java.util.UUID.fromString(request.getParameter("albumid"));
        JSONArray jsonArray = API.getSongList(albumID);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.write(jsonArray.toString());
    }
}