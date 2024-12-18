import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {
    List <CustomerDTO> customerDTOList = new ArrayList<>();

    private Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/Company",
                    "root",
                    "iJSE@123");
            return connection;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        customerDTOList.clear();


        resp.setContentType("application/json");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Company",
                    "root",
                    "iJSE@123");

            ResultSet resultSet = connection.prepareStatement("SELECT * FROM Customer").executeQuery();

            JsonArrayBuilder allCustomer = Json.createArrayBuilder();

            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String name = resultSet.getString(2);
                String address = resultSet.getString(3);

                JsonObjectBuilder customer = Json.createObjectBuilder();
                customer.add("id", id);
                customer.add("name", name);
                customer.add("address", address);
                allCustomer.add(customer.build());

                CustomerDTO customerDTO = new CustomerDTO(id, name, address);
                customerDTOList.add(customerDTO);
            }
            resp.getWriter().write(allCustomer.build().toString());

     /*       StringBuilder customerJsonList = new StringBuilder("[");
            for (CustomerDTO customerDTO : customerDTOList) {

                String customerJson = String.format("{\"id\":\"%s\", \"name\":\"%s\", \"address\":\"%s\"}",
                        customerDTO.getId(),
                        customerDTO.getName(),
                        customerDTO.getAddress());

                customerJsonList.append(customerJson);

                if (customerDTOList.indexOf(customerDTO) != customerDTOList.size()-1) {
                    customerJsonList.append(",");
                }
            }
            customerJsonList.append("]");
            String customerJson = customerJsonList.toString();
            PrintWriter out = resp.getWriter();
            out.println(customerJson);*/


        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String address = req.getParameter("address");

        if(id == null || name == null || address == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\" : \"Invalid request\"}");
            return;
        }
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Customer VALUES(?, ?, ?)");
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, address);
            preparedStatement.executeUpdate();
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}