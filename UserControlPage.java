import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.*;

public class UserControlPage extends JFrame {
    private String username;
    private JTextArea complainTextArea;

    public UserControlPage(String username) {
        this.username = username;
        setTitle("User Control Page");
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BufferedImage blurredImage = blurImage(new ImageIcon("C:\\Data\\Study\\OOM_Java-Project\\image.jpg").getImage(), 40);
        setContentPane(new JLabel(new ImageIcon(blurredImage)));
        setLayout(new BorderLayout());

        initializeUI();
    }

    private BufferedImage blurImage(Image image, int percentage) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid image dimensions");
        }

        BufferedImage bufferedImage = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        float[] blurKernel = new float[400];
        for (int i = 0; i < 400; i++) {
            blurKernel[i] = 1.0f / 400.0f;
        }

        ConvolveOp blur = new ConvolveOp(new Kernel(20, 20, blurKernel));
        return blur.filter(bufferedImage, null);
    }

    private void initializeUI() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 20, 20, 20);

        Font labelFont = new Font("Arial", Font.BOLD, 30);
        Font textFont = new Font("Arial", Font.PLAIN, 20);
        Font buttonFont = new Font("Arial", Font.BOLD, 20);

        JLabel welcomeLabel = new JLabel("Welcome, User " + username + "!");
        welcomeLabel.setFont(labelFont);
        welcomeLabel.setForeground(Color.BLACK);

        JLabel subjectLabel = new JLabel("Subject:");
        subjectLabel.setFont(new Font("Arial", Font.BOLD, 20));
        subjectLabel.setForeground(Color.BLACK);

        JTextField subjectTextField = new JTextField();
        subjectTextField.setFont(textFont);
        subjectTextField.setColumns(20);

        complainTextArea = new JTextArea(10, 30);
        complainTextArea.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(complainTextArea);

        TitledBorder titledBorder = BorderFactory.createTitledBorder("Enter Your Complaints:");
        titledBorder.setTitleFont(new Font("Arial", Font.BOLD, 16));
        scrollPane.setBorder(titledBorder);

        JButton submitComplainButton = new JButton("Submit Complaint");
        JButton viewComplainStatusButton = new JButton("View Complaint Status");
        JButton logoutButton = new JButton("Log Out");

        submitComplainButton.setFont(buttonFont);
        viewComplainStatusButton.setFont(buttonFont);
        logoutButton.setFont(buttonFont);

        submitComplainButton.addActionListener(e -> submitComplaintAction(subjectTextField.getText(), complainTextArea.getText()));
        viewComplainStatusButton.addActionListener(e -> viewComplaintStatusAction());
        logoutButton.addActionListener(e -> logoutAction());

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(welcomeLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(subjectLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(subjectTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(scrollPane, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        panel.add(submitComplainButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 3;
        panel.add(viewComplainStatusButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.gridwidth = 0;
        panel.add(logoutButton, gbc);

        panel.setOpaque(false);

        add(panel, BorderLayout.CENTER);
    }

    private void submitComplaintAction(String subject, String complaint) {
        if (!subject.isEmpty() && !complaint.isEmpty()) {
            storeComplaint(subject, complaint);
            JOptionPane.showMessageDialog(this, "Complaint submitted successfully!");
            complainTextArea.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a subject and complaint before submitting.");
        }
    }

    private void storeComplaint(String subject, String complaint) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Users/" + username + "/complaints.txt", true))) {
            writer.write("Subject: " + subject + "\n");
            writer.write("Complaint: " + complaint + "\n");
            writer.write("Status: Unsolved\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void viewComplaintStatusAction() {
        viewUserComplaints();
    }

    private void viewUserComplaints() {
        File userFolder = new File("Users/" + username);
        File[] complaintFiles = userFolder.listFiles((dir, name) -> name.endsWith(".txt"));

        if (complaintFiles != null && complaintFiles.length > 0) {
            StringBuilder userComplaints = new StringBuilder();
            for (File file : complaintFiles) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        userComplaints.append(line).append("\n");
                    }
                    userComplaints.append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            JTextArea complaintsTextArea = new JTextArea(userComplaints.toString());
            complaintsTextArea.setEditable(false);
            JScrollPane complaintsScrollPane = new JScrollPane(complaintsTextArea);

            JOptionPane.showMessageDialog(this, complaintsScrollPane, "Your Complaints", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No complaints found for user " + username);
        }
    }

    private void logoutAction() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?", "Log Out", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            openLoginPage();
        }
    }

    private void openLoginPage() {
        Complain_Management_System_App loginPage = new Complain_Management_System_App();
        loginPage.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserControlPage("TestUser").setVisible(true));
    }
}
