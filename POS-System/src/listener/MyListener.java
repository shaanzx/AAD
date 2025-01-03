package listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.SQLException;

@WebListener
public class MyListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        basicDataSource.setUrl("jdbc:mysql://localhost:3306/company");
        basicDataSource.setUsername("root");
        basicDataSource.setPassword("iJSE@123");
        basicDataSource.setMaxTotal(5);
        basicDataSource.setInitialSize(5);

        ServletContext servletContext = sce.getServletContext();
        servletContext.setAttribute("dataSource", basicDataSource);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        BasicDataSource dataSource = (BasicDataSource) servletContext.getAttribute("dataSource");

        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to close the data source.", e);
            }
        }
    }
}
