import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {
    List <CustomerDTO> customerDTOList = new ArrayList<>();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Company",
                    "root",
                    "iJSE@123");

            ResultSet resultSet = connection.prepareStatement("SELECT * FROM Customer").executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String name = resultSet.getString(2);
                String address = resultSet.getString(3);

                System.out.println("ID: " + id + "| Name: " + name + "| Address: " + address);
                CustomerDTO customerDTO = new CustomerDTO(id, name, address);

                customerDTOList.add(customerDTO);
            }
            StringBuilder customerJsonList = new StringBuilder("[");
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
            out.println(customerJson);


        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}