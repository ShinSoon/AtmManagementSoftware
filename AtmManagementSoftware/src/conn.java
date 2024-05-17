import java.sql.*;

public class conn {
    Connection c;
    Statement s;

    public conn() {
        try {
            // 1. Load MySQL JDBC Driver (Updated for newer MySQL versions)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. Establish Connection (with your credentials)
            String url = "jdbc:mysql://localhost:3306/Bank_Management_Software_Database";
            String user = "devuser";
            String password = "shinsoon1234@#";
            c = DriverManager.getConnection(url, user, password);

            // 3. Create Statement
            s = c.createStatement();
            System.out.println("Connected to the database!");  // Optional feedback
        } catch (Exception e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
    }
}