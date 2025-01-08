import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


@WebServlet(urlPatterns = "/InBuildDBCP")
public class InBuildDBCPServlet extends HttpServlet {

    @Resource(name = "java:comp/env/jdbc/pool")

    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Connection connection = dataSource.getConnection();
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM Customer").executeQuery();

            while(resultSet.next()){
                String id = resultSet.getString(1);
                String name = resultSet.getString(2);
                String address = resultSet.getString(3);

                System.out.println(id + " " + name+" " + address);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}