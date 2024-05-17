import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Deposit extends JFrame implements ActionListener {
    JLabel l1, l2;
    JTextField t1;
    JButton b1, b2;
    String bankNumber;

    Deposit(String bankNumber) {
        super("Deposit Amount");
        this.bankNumber = bankNumber;
        setLocation(350, 200);
        setSize(650, 200);

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(3, 2, 10, 10));
        p.setBackground(Color.WHITE);

        l1 = new JLabel("Enter Amount to Deposit:");
        t1 = new JTextField();
        p.add(l1);
        p.add(t1);

        b1 = new JButton("Deposit");
        b2 = new JButton("Cancel");

        b1.setBackground(Color.GREEN);
        b1.setForeground(Color.WHITE);
        b2.setBackground(Color.BLACK);
        b2.setForeground(Color.WHITE);

        p.add(b1);
        p.add(b2);

        setLayout(new BorderLayout());
        add(p, "Center");
        getContentPane().setBackground(Color.WHITE);

        b1.addActionListener(this);
        b2.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == b1) {
            String depositAmount = t1.getText();
            try {
                double depositAmountDouble = Double.parseDouble(depositAmount);
                if (depositAmountDouble <= 0) {
                    JOptionPane.showMessageDialog(null, "Deposit amount must be greater than zero.");
                    return;
                }

                // Update database with deposit
                String q1 = "UPDATE Customer SET MoneyAmount = MoneyAmount + ? WHERE BankNumber = ?";
                conn c1 = new conn();
                PreparedStatement preparedStatement = c1.c.prepareStatement(q1);
                preparedStatement.setDouble(1, depositAmountDouble);
                preparedStatement.setString(2, bankNumber);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    // Insert transaction record into Transactions table
                    String query2 = "INSERT INTO Transactions (CustomerID, Type, Amount) VALUES (?, ?, ?)";
                    PreparedStatement preparedStatement2 = c1.c.prepareStatement(query2);

                    // Fetch CustomerID based on BankNumber
                    String query3 = "SELECT id FROM Customer WHERE BankNumber=?";
                    PreparedStatement preparedStatement3 = c1.c.prepareStatement(query3);
                    preparedStatement3.setString(1, bankNumber);
                    ResultSet resultSet = preparedStatement3.executeQuery();

                    if (resultSet.next()) {
                        int customerID = resultSet.getInt("id");
                        preparedStatement2.setInt(1, customerID);
                        preparedStatement2.setString(2, "Deposit");
                        preparedStatement2.setDouble(3, depositAmountDouble);
                        preparedStatement2.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Deposit successful!");
                        setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(null, "Bank Number not found");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Bank Number not found");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid deposit amount.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            }
        } else if (ae.getSource() == b2) {
            setVisible(false);
        }
    }
}
