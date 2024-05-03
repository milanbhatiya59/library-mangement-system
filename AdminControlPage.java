import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.List;
import java.io.IOException;


public class AdminControlPage extends JFrame {

    public AdminControlPage(String adminUsername) {
        setTitle("Admin Control Page");
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BufferedImage blurredImage = blurImage(new ImageIcon("C:\\Data\\Study\\OOM_Java-Project\\image.jpg").getImage(), 40);
        setContentPane(new JLabel(new ImageIcon(blurredImage)));
        setLayout(new BorderLayout());

        initializeUI(adminUsername);
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

    private void initializeUI(String adminUsername) {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        Font labelFont = new Font("Arial", Font.BOLD, 30);
        Font buttonFont = new Font("Arial", Font.BOLD, 20);

        JLabel welcomeLabel = new JLabel("Welcome, Admin " + adminUsername + "!");
        welcomeLabel.setFont(labelFont);
        welcomeLabel.setForeground(Color.BLACK);

        JButton getDetailsButton = new JButton("Get Details");
        JButton registerStaffButton = new JButton("Register New Staff");
        JButton removeEmployeeButton = new JButton("Remove Employee");

        getDetailsButton.setFont(buttonFont);
        registerStaffButton.setFont(buttonFont);
        removeEmployeeButton.setFont(buttonFont);

        getDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGetDetailsOptions();
            }
        });

        registerStaffButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openStaffRegistrationWindow();
            }
        });

        removeEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeEmployee();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(welcomeLabel, gbc);

        gbc.gridy = 1;
        panel.add(getDetailsButton, gbc);

        gbc.gridy = 2;
        panel.add(registerStaffButton, gbc);

        gbc.gridy = 3;
        panel.add(removeEmployeeButton, gbc);

        panel.setOpaque(false);

        add(panel, BorderLayout.CENTER);
    }

    private void showGetDetailsOptions() {
        Object[] options = {"Users", "Staff"};
        int choice = JOptionPane.showOptionDialog(this, "Select option:", "Get Details", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == JOptionPane.YES_OPTION) {
            showUserDetails();
        } else if (choice == JOptionPane.NO_OPTION) {
            showStaffDetails();
        }
    }

    private void showUserDetails() {
        File usersFolder = new File("Users");
        File[] userFolders = usersFolder.listFiles(File::isDirectory);

        if (userFolders != null && userFolders.length > 0) {
            StringBuilder userDetails = new StringBuilder();
            for (File userFolder : userFolders) {
                userDetails.append("Username: ").append(userFolder.getName()).append("\n");
                userDetails.append("\n");
            }

            JTextArea userTextArea = new JTextArea(userDetails.toString());
            userTextArea.setEditable(false);
            JScrollPane userScrollPane = new JScrollPane(userTextArea);

            JOptionPane.showMessageDialog(this, userScrollPane, "User Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No users found.");
        }
    }

    private void showStaffDetails() {
        File staffFolder = new File("Staff");
        File[] staffFiles = staffFolder.listFiles();

        if (staffFiles != null && staffFiles.length > 0) {
            StringBuilder staffDetails = new StringBuilder();
            for (File staffFile : staffFiles) {
                try (BufferedReader reader = new BufferedReader(new FileReader(staffFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        staffDetails.append(line).append("\n");
                    }
                    staffDetails.append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            JTextArea staffTextArea = new JTextArea(staffDetails.toString());
            staffTextArea.setEditable(false);
            JScrollPane staffScrollPane = new JScrollPane(staffTextArea);

            JOptionPane.showMessageDialog(this, staffScrollPane, "Staff Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No staff found.");
        }
    }

    private void openStaffRegistrationWindow() {
        JTextField usernameField = new JTextField();
        JTextField passwordField = new JPasswordField();

        Object[] message = {
                "Username:", usernameField,
                "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Register New Staff", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (!username.isEmpty() && !password.isEmpty()) {
                registerNewStaff(username, password);
                JOptionPane.showMessageDialog(this, "Staff registered successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Please enter valid username and password.");
            }
        }
    }

    private void registerNewStaff(String username, String password) {
        String staffFilePath = "Staff/userdata.txt";
    
        if (isUserExists(username, staffFilePath)) {
            JOptionPane.showMessageDialog(this, "Staff with the username already exists.");
            return;
        }
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(staffFilePath, true))) {
            writer.write(username + "," + password + "\n");
            JOptionPane.showMessageDialog(this, "Staff registered successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private boolean isUserExists(String username, String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    

    private void removeEmployee() {
        String usernameToRemove = JOptionPane.showInputDialog(this, "Enter the username to remove:");
        if (usernameToRemove != null && !usernameToRemove.isEmpty()) {
            if (removeUser(usernameToRemove) || removeStaff(usernameToRemove)) {
                JOptionPane.showMessageDialog(this, "Employee removed successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Employee not found.");
            }
        }
    }

    private boolean removeUser(String username) {
        File userFolder = new File("Users/" + username);
        if (userFolder.exists()) {
            if (userFolder.isDirectory()) {
                File[] files = userFolder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        file.delete();
                    }
                }
            }
            return userFolder.delete();
        }
        return false;
    }

    private boolean removeStaff(String username) {
    String staffFilePath = "Staff/userdata.txt";
    
    try {
        List<String> lines = Files.readAllLines(Paths.get(staffFilePath));
        
        List<String> updatedLines = lines.stream()
                .filter(line -> !line.startsWith(username + ","))
                .collect(Collectors.toList());
        
        Files.write(Paths.get(staffFilePath), updatedLines);
        
        return true;
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AdminControlPage("TestAdmin").setVisible(true);
            }
        });
    }
}
