import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Withdraw extends JFrame implements ActionListener {
    JLabel l1, l2;
    JTextField t1;
    JButton b1, b2;
    String bankNumber;

    Withdraw(String bankNumber) {
        super("Withdraw Amount");
        this.bankNumber = bankNumber;
        setLocation(350, 200);
        setSize(650, 200);

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(3, 2, 10, 10));
        p.setBackground(Color.WHITE);

        l1 = new JLabel("Enter Amount to Withdraw:");
        t1 = new JTextField();
        p.add(l1);
        p.add(t1);

        b1 = new JButton("Withdraw");
        b2 = new JButton("Cancel");

        b1.setBackground(Color.ORANGE); // Distinguish from Deposit button
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
            String withdrawAmount = t1.getText();
            try {
                double withdrawAmountDouble = Double.parseDouble(withdrawAmount);
                if (withdrawAmountDouble <= 0) {
                    JOptionPane.showMessageDialog(null, "Withdrawal amount must be greater than zero.");
                    return;
                }

                conn c1 = new conn(); // Move connection establishment outside the ResultSet block

                // Check available balance and fetch CustomerID
                String query1 = "SELECT id, MoneyAmount FROM Customer WHERE BankNumber=?";
                PreparedStatement preparedStatement1 = c1.c.prepareStatement(query1);
                preparedStatement1.setString(1, bankNumber);
                ResultSet resultSet = preparedStatement1.executeQuery();

                if (resultSet.next()) {
                    int customerID = resultSet.getInt("id");
                    double currentBalance = resultSet.getDouble("MoneyAmount");

                    if (withdrawAmountDouble > currentBalance) {
                        JOptionPane.showMessageDialog(null, "Insufficient balance.");
                        return;
                    }

                    // Update database with withdrawal
                    String query2 = "UPDATE Customer SET MoneyAmount = MoneyAmount - ? WHERE BankNumber = ?";
                    PreparedStatement preparedStatement2 = c1.c.prepareStatement(query2);
                    preparedStatement2.setDouble(1, withdrawAmountDouble);
                    preparedStatement2.setString(2, bankNumber);
                    preparedStatement2.executeUpdate();

                    // Insert transaction record into Transactions table
                    String query3 = "INSERT INTO Transactions (CustomerID, Type, Amount) VALUES (?, ?, ?)";
                    PreparedStatement preparedStatement3 = c1.c.prepareStatement(query3);
                    preparedStatement3.setInt(1, customerID);
                    preparedStatement3.setString(2, "Withdrawal");
                    preparedStatement3.setDouble(3, withdrawAmountDouble);
                    preparedStatement3.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Withdrawal successful!");
                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(null, "Bank Number not found");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid withdrawal amount.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            }
        } else if (ae.getSource() == b2) {
            setVisible(false);
        }
    }
}