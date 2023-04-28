package sj.mediaserver;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import org.json.JSONArray;

@SuppressWarnings("serial")
@WebServlet("/lib/*")
public class LibraryServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        DatabaseAPI API = DatabaseAPI.getInstance();
        JSONArray jsonArray = API.getLibraryList();

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.write(jsonArray.toString());
    }
}
