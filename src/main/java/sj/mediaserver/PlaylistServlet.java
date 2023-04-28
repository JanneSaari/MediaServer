package sj.mediaserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.stream.Stream;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import org.json.JSONArray;
import org.json.JSONObject;

@SuppressWarnings("serial")
@WebServlet("/playlist/*")
public class PlaylistServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // TODO update authentication method used
        if (request.getSession().getAttribute("user") != null) {
            DatabaseAPI API = DatabaseAPI.getInstance();

            UUID playlistID = java.util.UUID.fromString(request.getParameter("playlistID"));
            JSONArray jsonArray = API.getPlayList(playlistID);

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.write(jsonArray.toString());
        } else {
            response.sendRedirect("/MediaServer/login");
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // TODO update authentication method used
        if (request.getSession().getAttribute("user") != null) {
            DatabaseAPI API = DatabaseAPI.getInstance();

            // TODO get id from request it updating old list
            UUID playlistID;
            try {
                playlistID = java.util.UUID.fromString(request.getParameter("playlistid"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            String body = null;
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = null;

            try {
                InputStream inputStream = request.getInputStream();
                if (inputStream != null) {
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    char[] charBuffer = new char[128];
                    int bytesRead = -1;
                    while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                        stringBuilder.append(charBuffer, 0, bytesRead);
                    }
                } else {
                    stringBuilder.append("");
                }
            } catch (IOException ex) {
                throw ex;
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException ex) {
                        throw ex;
                    }
                }
            }

            body = stringBuilder.toString();
            JSONArray jsonArr = new JSONArray(body);
            System.out.println(jsonArr);

        } else {
            response.sendRedirect("/MediaServer/login");
        }
    }
}