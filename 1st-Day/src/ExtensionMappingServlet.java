import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

//Extension Mapping
@WebServlet(urlPatterns = "*.html")
public class ExtensionMappingServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Extension Mapping Servlet Get Request received");
        PrintWriter out = resp.getWriter();
        out.println("Extension Mapping Servlet Executed");
        out.println("");
    }
}
