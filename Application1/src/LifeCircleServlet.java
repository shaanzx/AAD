import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = "/lifecycle")
public class LifeCircleServlet extends HttpServlet {
    public LifeCircleServlet() {
        System.out.println("LifeCircle Constructor");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("LifeCircle doGet");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("LifeCircle Init");
    }

    @Override
    public void destroy() {
        System.out.println("LifeCircle Destroy");
    }
}
