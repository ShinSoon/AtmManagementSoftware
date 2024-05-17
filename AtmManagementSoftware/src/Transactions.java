import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Transactions extends JFrame implements ActionListener {
    JLabel l1;
    JButton b1, b2, b3, b4, b5;
    String bankNumber; // Store the logged-in user's bank number

    Transactions(String bankNumber) {
        super("Transactions");
        this.bankNumber = bankNumber;
        setLocation(350, 200);
        setSize(650, 300);

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(5, 1, 10, 10));
        p.setBackground(Color.WHITE);

        l1 = new JLabel("Welcome, " + getUserName(bankNumber)); // Display user's name
        p.add(l1);

        b1 = new JButton("Check Balance");
        b2 = new JButton("Deposit Amount");
        b3 = new JButton("Withdraw Amount");
        b4 = new JButton("Exit");

        // Set button colors
        b1.setBackground(Color.BLUE);
        b1.setForeground(Color.WHITE);
        b2.setBackground(Color.GREEN);
        b2.setForeground(Color.WHITE);
        b3.setBackground(Color.ORANGE);
        b3.setForeground(Color.WHITE);
        b4.setBackground(Color.RED);
        b4.setForeground(Color.WHITE);

        b5 = new JButton("Transaction History");
        b5.setBackground(Color.GRAY);
        b5.setForeground(Color.WHITE);

        p.add(b1);
        p.add(b2);
        p.add(b3);
        p.add(b4);
        p.add(b5);

        setLayout(new BorderLayout());
        add(p, "Center");
        getContentPane().setBackground(Color.WHITE);

        b1.addActionListener(this);
        b2.addActionListener(this);
        b3.addActionListener(this);
        b4.addActionListener(this);
        b5.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == b1) {
            // Check Balance logic (using bankNumber)
            checkBalance();
        } else if (ae.getSource() == b2) {
            // Deposit Amount logic (using bankNumber)
            new Deposit(bankNumber).setVisible(true);
        } else if (ae.getSource() == b3) {
            // Withdraw Amount logic (using bankNumber)
            new Withdraw(bankNumber).setVisible(true);
        } else if (ae.getSource() == b4) {
            // Exit logic
            System.exit(0);
        } else if (ae.getSource() == b5)
        {
            showTransactionHistory();
        }
    }

    private void checkBalance() {
        try {
            String q1 = "SELECT MoneyAmount FROM Customer WHERE BankNumber=?";
            conn c1 = new conn();
            PreparedStatement preparedStatement = c1.c.prepareStatement(q1);
            preparedStatement.setString(1, bankNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String balance = resultSet.getString("MoneyAmount");
                JOptionPane.showMessageDialog(null, "Your balance is: " + balance);
            } else {
                JOptionPane.showMessageDialog(null, "Bank Number not found");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
        }
    }

    private void showTransactionHistory() {
        try {
            // Fetch transaction history by joining Customer and Transactions tables
            String query = "SELECT Transactions.* FROM Transactions " +
                    "INNER JOIN Customer ON Transactions.CustomerID = Customer.id " +
                    "WHERE Customer.BankNumber = ?";

            conn c1 = new conn();
            PreparedStatement preparedStatement = c1.c.prepareStatement(query);
            preparedStatement.setString(1, bankNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Build a string representation of the history (same as before)
            StringBuilder history = new StringBuilder();
            while (resultSet.next()) {
                history.append("Date: ").append(resultSet.getString("Date"))
                        .append(", Type: ").append(resultSet.getString("Type"))
                        .append(", Amount: ").append(resultSet.getString("Amount"))
                        .append("\n");
            }

            // Display the history (same as before)
            JOptionPane.showMessageDialog(null, history.toString(), "Transaction History", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error retrieving transaction history: " + e.getMessage());
        }
    }


    // ... You'll need to create separate Deposit and Withdraw classes
    // ... with similar structures to handle those operations.

    private String getUserName(String bankNumber) {
        // Fetch the user's name from the database using bankNumber
        // Implement this method to retrieve the name based on the bankNumber
        return "User"; // Replace with actual name retrieved from the database
    }


}
