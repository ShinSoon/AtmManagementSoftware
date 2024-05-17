import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class Login extends JFrame implements ActionListener {
    JLabel l1, l2;
    JTextField t1;
    JPasswordField t2;
    JButton b1, b2, b3;

    Login() {
        super("Login");
        setLocation(350, 200);
        setSize(650, 400);

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(4, 2, 10, 10));
        p.setBackground(Color.WHITE);

        l1 = new JLabel("Bank Number");
        t1 = new JTextField();
        p.add(l1);
        p.add(t1);

        l2 = new JLabel("Password");
        t2 = new JPasswordField();
        p.add(l2);
        p.add(t2);

        b1 = new JButton("Login");
        b2 = new JButton("Cancel");

        b1.setBackground(Color.GREEN);
        b1.setForeground(Color.WHITE);

        b2.setBackground(Color.BLACK);
        b2.setForeground(Color.WHITE);

        b3 = new JButton("Register");
        b3.setBackground(Color.BLUE); // You can choose a different color
        b3.setForeground(Color.WHITE);

        p.add(b1);
        p.add(b2);
        p.add(b3);

        setLayout(new BorderLayout());
        add(p, "Center");

        getContentPane().setBackground(Color.WHITE);
        b1.addActionListener(this);
        b2.addActionListener(this);
        b3.addActionListener(this);
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
        if (ae.getSource() == b1) {
            String bankNumber = t1.getText();
            char[] passwordChars = t2.getPassword();
            String password = new String(passwordChars);
            Arrays.fill(passwordChars, '0');

            try {
                // Retrieve hashed password from database
                String q1 = "SELECT Password FROM Customer WHERE BankNumber=?";
                conn c1 = new conn();
                PreparedStatement preparedStatement = c1.c.prepareStatement(q1);
                preparedStatement.setString(1, bankNumber);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String storedHashedPassword = resultSet.getString("Password");
                    String hashedPassword = hashPassword(password); // Hash entered password

                    if (hashedPassword.equals(storedHashedPassword)) {
                        JOptionPane.showMessageDialog(null, "Successfully login");
                        new Transactions(bankNumber).setVisible(true); // Assuming you have a Transactions form
                        setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid Login");
                        t2.setText("");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Bank Number not found");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            }

        } else if (ae.getSource() == b2) {
            dispose();
        } else if(ae.getSource() == b3)
        {
            new Registration().setVisible(true);
            setVisible(false);
        }
    }

    public static void main(String[] args) {
        new Login().setVisible(true);
    }
}