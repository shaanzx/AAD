import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

//Extract the mapping specification
@WebServlet(urlPatterns = "/extractMapping")
public class ExtractMappingServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Mapping Specification Servlet Get Request received");
        PrintWriter out = resp.getWriter();
        out.println("Extract Mapping Specification Servlet Executed");
    }
}
