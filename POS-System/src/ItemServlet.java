import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/item")
public class ItemServlet extends HttpServlet {
    List<ItemDTO> itemDTOList = new ArrayList<>();

    private Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/company",
                    "root",
                    "iJSE@123");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        itemDTOList.clear();

        resp.setContentType("application/json");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company",
                    "root",
                    "iJSE@123");

            ResultSet resultSet = connection.prepareStatement("SELECT * FROM Item").executeQuery();

            JsonArrayBuilder allItem = Json.createArrayBuilder();

            while (resultSet.next()) {
                String code = resultSet.getString(1);
                String description = resultSet.getString(2);
                int qty = Integer.parseInt(resultSet.getString(3));
                double price = Double.parseDouble(resultSet.getString(4));

                JsonObjectBuilder item = Json.createObjectBuilder();
                item.add("code", code);
                item.add("description", description);
                item.add("qty", qty);
                item.add("price", price);
                allItem.add(item.build());

                ItemDTO itemDTO = new ItemDTO(code, description, qty, price);
                itemDTOList.add(itemDTO);
            }
            resp.getWriter().write(allItem.build().toString());

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
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

            if (qty <= 0 || price <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Quantity and price must be positive values.\"}");
                return;
            }

            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Item VALUES (?, ?, ?, ?)")) {

                preparedStatement.setString(1, code);
                preparedStatement.setString(2, description);
                preparedStatement.setInt(3, qty);
                preparedStatement.setDouble(4, price);
                preparedStatement.executeUpdate();

                resp.setStatus(HttpServletResponse.SC_CREATED);
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid number format for quantity or price.\"}");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to save the item. SQL Error.\"}");
        }
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("itemCode");
        String description = req.getParameter("description");
        String qtyStr = req.getParameter("qty");
        String priceStr = req.getParameter("unitPrice");

        if (code == null || code.isEmpty() || description == null || description.isEmpty() ||
                qtyStr == null || qtyStr.isEmpty() || priceStr == null || priceStr.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid request. Missing or empty parameters.\"}");
            return;
        }

        try {
            int qty = Integer.parseInt(qtyStr);
            double price = Double.parseDouble(priceStr);

            if (qty <= 0 || price <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Quantity and price must be positive values.\"}");
                return;
            }

            try (Connection connection = getConnection()) {
                ItemDTO item = findByCode(code);
                if (item != null) {
                    PreparedStatement stmt = connection.prepareStatement(
                            "UPDATE Item SET description = ?, qtyOnHand = ?, unitPrice = ? WHERE code = ?");
                    stmt.setString(1, description);
                    stmt.setInt(2, qty);
                    stmt.setDouble(3, price);
                    stmt.setString(4, code);
                    stmt.executeUpdate();
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"Item not found.\"}");
                }
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid number format for quantity or price.\"}");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Database error occurred.\"}");
        }
    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("itemCode");

        if (code == null || code.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Item code is required\"}");
            return;
        }

        try (Connection connection = getConnection()) {
            ItemDTO item = findByCode(code);
            if (item != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "DELETE FROM Item WHERE code = ?");
                preparedStatement.setString(1, code);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write("{\"error\":\"Failed to delete the Item.\"}");
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Item not found.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to delete Item..\"}");
        }
    }

    private ItemDTO findByCode(String code) {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Item WHERE code = ?")) {

            stmt.setString(1, code);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String description = resultSet.getString(2);
                int qty = resultSet.getInt(3);
                double price = resultSet.getDouble(4);
                return new ItemDTO(code, description, qty, price);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
