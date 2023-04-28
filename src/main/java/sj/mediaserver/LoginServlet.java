package sj.mediaserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import sj.mediaserver.DatabaseAPI;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/login/*", name = "login")
public class LoginServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // in real life you would probably use JSP to render the login
        response.getOutputStream().println("<head><meta charset=\"utf-8\"> </head>");
        response.getOutputStream().println("<h1>Login</h1>");
        response.getOutputStream().println("<hr/>");
        response.getOutputStream().println("<form action=\"/MediaServer/login\" method=\"POST\">");
        response.getOutputStream().println("<span>Username: </span>");
        response.getOutputStream().println("<input type=\"text\" name=\"username\">");
        response.getOutputStream().println("<span>Password: </span>");
        response.getOutputStream().println("<input type=\"password\" name=\"password\">");
        response.getOutputStream().println("<input type=\"submit\" value=\"Submit\">");
        response.getOutputStream().println("</form>");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // String authHeader = request.getHeader("authorization");
        // String encodedAuth = authHeader.substring(authHeader.indexOf(' ' + 1));
        // String decodedAuth = new String(Base64.getDecoder().decode(encodedAuth));

        // String username = decodedAuth.substring(0, decodedAuth.indexOf(' '));
        // String password = decodedAuth.substring(decodedAuth.indexOf(':') + 1);

        // System.out.println("username: " + username + ", password: " + password);

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        DatabaseAPI API = DatabaseAPI.getInstance();

        // TODO make session cookies more secure

        // TODO use HSTS do prevent any communication over HTTP and only accept HTTPS
        // https://cheatsheetseries.owasp.org/cheatsheets/HTTP_Strict_Transport_Security_Cheat_Sheet.html

        // TODO!!!!
        // Read about authorization header
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Authorization
        // !!!!!!!!

        // TODO create sanitize check
        if (API.authenticateUser(username, password) == 1) {
            request.getSession().setAttribute("user", username);
            // TODO change redirect target
            response.sendRedirect("/MediaServer/listTest.html");
        } else {
            response.getOutputStream().println("<h1>Wrong username or password.</h1>");
        }
    }
}