package sj.mediaserver;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.GenericServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/hello/*")
public class HelloWorldServlet extends GenericServlet {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void service(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        String helloWorldMessage = "<html><body><H1>Hello World Servlet With extends Generic Servlet</H1></body></html>";
        writer.println(helloWorldMessage);
    }
}