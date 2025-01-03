
import dto.ItemDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/item")
public class ItemServlet extends HttpServlet {

    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        if (dataSource == null) {
            throw new ServletException("DataSource is not initialized in context");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Item");
             ResultSet resultSet = stmt.executeQuery();

            JsonArrayBuilder itemsArray = Json.createArrayBuilder();
            while (resultSet.next()) {
                JsonObjectBuilder item = Json.createObjectBuilder()
                        .add("code", resultSet.getString("code"))
                        .add("description", resultSet.getString("description"))
                        .add("qty", resultSet.getInt("qtyOnHand"))
                        .add("price", resultSet.getDouble("unitPrice"));
                itemsArray.add(item);
            }
            resp.getWriter().write(itemsArray.build().toString());
            connection.close();
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to fetch items.\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("itemCode");
        String description = req.getParameter("description");
        String qtyStr = req.getParameter("qty");
        String priceStr = req.getParameter("unitPrice");

        if (code == null || description == null || qtyStr == null || priceStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid request. Missing parameters.\"}");
            return;
        }

        try {
            int qty = Integer.parseInt(qtyStr);
            double price = Double.parseDouble(priceStr);

            Connection connection = dataSource.getConnection();
            try (
                 PreparedStatement stmt = connection.prepareStatement("INSERT INTO Item VALUES (?, ?, ?, ?)")) {
                stmt.setString(1, code);
                stmt.setString(2, description);
                stmt.setInt(3, qty);
                stmt.setDouble(4, price);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write("{\"error\":\"Failed to save the item.\"}");
                }
            }
            connection.close();
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid number format.\"}");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Database error occurred.\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("itemCode");
        String description = req.getParameter("description");
        String qtyStr = req.getParameter("qty");
        String priceStr = req.getParameter("unitPrice");

        if (code == null || description == null || qtyStr == null || priceStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid request. Missing parameters.\"}");
            return;
        }

        try {
            int qty = Integer.parseInt(qtyStr);
            double price = Double.parseDouble(priceStr);

            Connection connection = dataSource.getConnection();
            try (
                 PreparedStatement stmt = connection.prepareStatement(
                         "UPDATE Item SET description = ?, qtyOnHand = ?, unitPrice = ? WHERE code = ?")) {
                stmt.setString(1, description);
                stmt.setInt(2, qty);
                stmt.setDouble(3, price);
                stmt.setString(4, code);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"Item not found.\"}");
                }
            }
            connection.close();
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid number format.\"}");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Database error occurred.\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("itemCode");

        if (code == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Item code is required.\"}");
            return;
        }

        try {Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("DELETE FROM Item WHERE code = ?");
            stmt.setString(1, code);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Item not found.\"}");
            }
            connection.close();
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Database error occurred.\"}");
        }
    }
}
