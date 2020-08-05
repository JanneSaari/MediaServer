
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
public class HelloWorldServlet extends GenericServlet 
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void service(ServletRequest request, ServletResponse response)
    throws ServletException, IOException
    {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        String helloWorldMessage="<html><body><H1>Hello World Servlet With extends Generic Servlet</H1></body></html>";
        writer.println(helloWorldMessage);
    }
}