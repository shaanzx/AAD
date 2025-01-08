import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.IOException;

@WebServlet(urlPatterns = "/customerJSON")
public class CustomerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            //Create first JSON Object
            JsonObjectBuilder customer1 = Json.createObjectBuilder();
            customer1.add("id", 1);
            customer1.add("name", "Shan");
            customer1.add("address", "Colombo");

            //Create second JSON Object
            JsonObjectBuilder customer2 = Json.createObjectBuilder();
            customer2.add("id", 1);
            customer2.add("name", "Shan");
            customer2.add("address", "Colombo");

            //Create JSON Array
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            arrayBuilder.add(customer1);
            arrayBuilder.add(customer2);

            //Create Another JSON Object And add to array and status
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("Customers", arrayBuilder);
            response.add("Status", HttpServletResponse.SC_OK);
            response.add("Message", "Success");

            //Build JSON Object
            JsonObject jsonObject = response.build();
            resp.setContentType("application/json");//Input header to data type
            resp.getWriter().print(jsonObject);
        } catch (Exception e) {

            //Create new JSON Object for exception
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("data","");
            response.add("Status",HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.add("Message", e.getMessage());
        }
    }
}