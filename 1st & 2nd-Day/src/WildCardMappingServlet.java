import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

//Wild Card Mapping
@WebServlet(urlPatterns = "/root/*")
public class WildCardMappingServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Wild Card Mapping Servlet Get Request received");
        PrintWriter out = resp.getWriter();
        out.println("Wild Card Mapping Servlet Executed");
    }
}
