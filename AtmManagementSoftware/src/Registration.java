import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Registration extends JFrame implements ActionListener {
    JLabel l1, l2, l3, l4;
    JTextField t1,t2,t3,t4;

    JButton b1,b2;



    Registration()
    {
        super("Registration");
        setLocation(350,200);
        setSize(650,800);

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(9,2,10,10));

        p.setBackground(Color.WHITE);

        l1 = new JLabel("Name");
        t1 = new JTextField();
        p.add(l1);
        p.add(t1);

        l2 = new JLabel("BankNumber");
        t2 = new JTextField();
        p.add(l2);
        p.add(t2);


        l4 = new JLabel("Password");
        t4 = new JPasswordField();
        p.add(l4);
        p.add(t4);

        l3 = new JLabel("Money Amount");
        t3 = new JTextField();
        p.add(l3);
        p.add(t3);

        b1 = new JButton("Register");
        b2 = new JButton("Cancel Registration");

        b1.setBackground(Color.GREEN);
        b1.setForeground(Color.WHITE); // Set text color to white

        b2.setBackground(Color.BLACK);
        b2.setForeground(Color.WHITE); // Set text color to white

        p.add(b1);
        p.add(b2);

        setLayout(new BorderLayout());
        add(p, "Center");

        getContentPane().setBackground(Color.WHITE);
        b1.addActionListener(this);
        b2.addActionListener(this);





    }


    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedPasswordBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPasswordBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == b1) { // "Register" button
            String name = t1.getText();
            String bankNumber = t2.getText();
            String moneyAmount = t3.getText();
            String password = t4.getText(); // Get password as a String

            // Input Validation (Basic)
            if (name.isEmpty() || bankNumber.isEmpty() || moneyAmount.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            // Hash the password
            String hashedPassword = hashPassword(password);

            try {
                // Store in database
                String q1 = "INSERT INTO Customer (Name, BankNumber, MoneyAmount, Password) VALUES (?, ?, ?, ?)";
                conn c1 = new conn();  // Assuming you have a conn class to handle database connections
                PreparedStatement preparedStatement = c1.c.prepareStatement(q1);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, bankNumber);
                preparedStatement.setString(3, moneyAmount);
                preparedStatement.setString(4, hashedPassword); // Use hashed password
                preparedStatement.executeUpdate();

                JOptionPane.showMessageDialog(null, "New Customer Registered");
                this.setVisible(false);
                // Optionally, switch to login form:
                new Login().setVisible(true); // If you have a Login form
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            }
        } else if (ae.getSource() == b2) { // "Cancel Registration" button
            dispose(); // Close the registration form
        }
    }


}
