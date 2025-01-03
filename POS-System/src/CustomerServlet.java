import dto.CustomerDTO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.sql.*;


@WebServlet(urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        ServletContext servletContext = req.getServletContext();
        BasicDataSource dataSource = (BasicDataSource) servletContext.getAttribute("dataSource");

        try {
            Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Customer");
             ResultSet resultSet = preparedStatement.executeQuery();

            JsonArrayBuilder allCustomers = Json.createArrayBuilder();

            while (resultSet.next()) {
                JsonObjectBuilder customer = Json.createObjectBuilder();
                customer.add("id", resultSet.getString(1));
                customer.add("name", resultSet.getString(2));
                customer.add("address", resultSet.getString(3));
                allCustomers.add(customer.build());
            }

            resp.getWriter().write(allCustomers.build().toString());
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to fetch customers.\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String address = req.getParameter("address");

        if (id == null || name == null || address == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid request. All fields are required.\"}");
            return;
        }

        ServletContext servletContext = req.getServletContext();
        BasicDataSource dataSource = (BasicDataSource) servletContext.getAttribute("dataSource");

        try {
            Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Customer VALUES (?, ?, ?)");

            preparedStatement.setString(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, address);
            preparedStatement.executeUpdate();

            resp.setStatus(HttpServletResponse.SC_CREATED);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to add customer.\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String address = req.getParameter("address");

        if (id == null || name == null || address == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid request. All fields are required.\"}");
            return;
        }

        ServletContext servletContext = req.getServletContext();
        BasicDataSource dataSource = (BasicDataSource) servletContext.getAttribute("dataSource");

        try {
            Connection connection = dataSource.getConnection();
            CustomerDTO customer = findById(id, connection);

            if (customer != null) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "UPDATE Customer SET name = ?, address = ? WHERE id = ?")) {
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, address);
                    preparedStatement.setString(3, id);
                    preparedStatement.executeUpdate();

                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Customer not found.\"}");
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to update customer.\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");

        if (id == null || id.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Customer ID is required.\"}");
            return;
        }

        ServletContext servletContext = req.getServletContext();
        BasicDataSource dataSource = (BasicDataSource) servletContext.getAttribute("dataSource");

        try {
            Connection connection = dataSource.getConnection();
            CustomerDTO customer = findById(id, connection);

            if (customer != null) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "DELETE FROM Customer WHERE id = ?")) {
                    preparedStatement.setString(1, id);
                    preparedStatement.executeUpdate();

                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Customer not found.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to delete customer.\"}");
        }
    }

    private CustomerDTO findById(String id, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Customer WHERE id = ?")) {
            preparedStatement.setString(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new CustomerDTO(id, resultSet.getString(2), resultSet.getString(3));
                }
            }
        }
        return null;
    }
}
