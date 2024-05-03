import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.*;

public class Complain_Management_System_App extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeComboBox;
    private JButton registerButton;

    public Complain_Management_System_App() {
        setTitle("Complain Management System - Login");
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
        gbc.insets = new Insets(20, 20, 20, 20);

        Font labelFont = new Font("Arial", Font.BOLD, 30);
        Font textFont = new Font("Arial", Font.PLAIN, 20);
        Font buttonFont = new Font("Arial", Font.BOLD, 20);

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel userTypeLabel = new JLabel("User Type:");

        usernameLabel.setFont(labelFont);
        passwordLabel.setFont(labelFont);
        userTypeLabel.setFont(labelFont);

        usernameLabel.setForeground(Color.BLACK);
        passwordLabel.setForeground(Color.BLACK);
        userTypeLabel.setForeground(Color.BLACK);

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        String[] userTypes = {"User", "Staff", "Admin"};
        userTypeComboBox = new JComboBox<>(userTypes);

        usernameField.setFont(textFont);
        passwordField.setFont(textFont);
        userTypeComboBox.setFont(textFont);

        registerButton = new JButton("Register");
        registerButton.setFont(buttonFont);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String userType = (String) userTypeComboBox.getSelectedItem();

                if (!"Admin".equals(userType) && registerUser(username, password, userType)) {
                    JOptionPane.showMessageDialog(Complain_Management_System_App.this, "Registration successful! Please login.");
                } else {
                    JOptionPane.showMessageDialog(Complain_Management_System_App.this, "Username already exists or invalid user type.");
                }
            }
        });

        JButton loginButton = new JButton("Login");

        loginButton.setFont(buttonFont);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String userType = (String) userTypeComboBox.getSelectedItem();

                if (validateLogin(username, password, userType)) {
                    JOptionPane.showMessageDialog(Complain_Management_System_App.this, "Login successful! Welcome, " + userType + " " + username + "!");

                    if ("Admin".equals(userType)) {
                        if ("Admin".equals(username) && "admin123".equals(password)) {
                            AdminControlPage adminControlPage = new AdminControlPage(username);
                            adminControlPage.setVisible(true);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(Complain_Management_System_App.this, "Invalid admin credentials.");
                        }
                    } else if ("Staff".equals(userType)) {
                        StaffControlPage staffControlPage = new StaffControlPage(username);
                        staffControlPage.setVisible(true);
                        dispose();
                    } else if ("User".equals(userType)) {
                        UserControlPage userControlPage = new UserControlPage(username);
                        userControlPage.setVisible(true);
                        dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(Complain_Management_System_App.this, "Invalid username, password, or user type");
                }
            }
        });

        userTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedUserType = (String) userTypeComboBox.getSelectedItem();
                registerButton.setVisible(!("Admin".equals(selectedUserType) || "Staff".equals(selectedUserType)));
            }
        });

        JButton logoutButton = new JButton("Exit");
        logoutButton.setFont(buttonFont);

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(Complain_Management_System_App.this,
                        "Are you sure you want to log out?", "Exit", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });

        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        panel.add(logoutButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(userTypeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(userTypeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(loginButton, gbc);

        gbc.gridx = 1;
        panel.add(registerButton, gbc);

        panel.setOpaque(false);

        add(panel, BorderLayout.CENTER);
    }

    private boolean validateLogin(String username, String password, String userType) {
        String filePath;
    
        if ("user".equalsIgnoreCase(userType)) {
            filePath = "Users/" + username + "/userdata.txt";
        } else if ("staff".equalsIgnoreCase(userType)) {
            filePath = "Staff/userdata.txt";
        } else if ("admin".equalsIgnoreCase(userType)) {
            filePath = "Admin/admindata.txt";
        } else {
            throw new IllegalArgumentException("Invalid user type");
        }
    
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean registerUser(String username, String password, String userType) {
        String userFolderPath;

        if ("user".equals(userType.toLowerCase())) {
            userFolderPath = "Users/" + username;
        } else if ("staff".equals(userType.toLowerCase())) {
            userFolderPath = "Staff";
        } else {
            throw new IllegalArgumentException("Invalid user type");
        }

        String filePath = userFolderPath + "/userdata.txt";

        File userFolder = new File(userFolderPath);
        if (!userFolder.exists()) {
            userFolder.mkdirs();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equals(username)) {
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(username + "," + password + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Complain_Management_System_App().setVisible(true);
            }
        });
    }
}
