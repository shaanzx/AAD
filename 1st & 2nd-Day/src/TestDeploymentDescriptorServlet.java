import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class TestDeploymentDescriptorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Test Deployment Descriptor Servlet Get Request received");
        PrintWriter out = resp.getWriter();
        out.println("<html>" +
                "<body>" +
                "<title>Test Deployment Descriptor Servlet</title>" +
                "<h1 style=\"color:blue\", align=\"center\">Test Deployment Descriptor Servlet</h1>" +
                "<p>Test Deployment Descriptor Servlet Executed</p>" +
                "</body>" +
                "</html>");
    }
}
