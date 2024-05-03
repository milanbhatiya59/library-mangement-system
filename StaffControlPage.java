import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.*;

public class StaffControlPage extends JFrame {

    public StaffControlPage(String staffUsername) {
        setTitle("Staff Control Page");
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BufferedImage blurredImage = blurImage(new ImageIcon("C:\\Data\\Study\\OOM_Java-Project\\image.jpg").getImage(), 40);
        setContentPane(new JLabel(new ImageIcon(blurredImage)));
        setLayout(new BorderLayout());

        initializeUI(staffUsername);
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

    private void initializeUI(String staffUsername) {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        Font labelFont = new Font("Arial", Font.BOLD, 30);
        Font buttonFont = new Font("Arial", Font.BOLD, 20);

        JLabel welcomeLabel = new JLabel("Welcome, Staff " + staffUsername + "!");
        welcomeLabel.setFont(labelFont);
        welcomeLabel.setForeground(Color.BLACK);

        JButton viewComplaintsButton = new JButton("View Complaints");
        JButton logoutButton = new JButton("Logout");

        viewComplaintsButton.setFont(buttonFont);
        logoutButton.setFont(buttonFont);

        viewComplaintsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewComplaints();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Complain_Management_System_App().setVisible(true);
            }
        });

        Dimension buttonSize = new Dimension(200, 50);
        viewComplaintsButton.setPreferredSize(buttonSize);
        logoutButton.setPreferredSize(buttonSize);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(welcomeLabel, gbc);

        gbc.gridy = 1;
        panel.add(viewComplaintsButton, gbc);

        gbc.gridy = 3;
        panel.add(logoutButton, gbc);

        panel.setOpaque(false);

        add(panel, BorderLayout.CENTER);
    }

    private void viewComplaints() {
        File usersFolder = new File("Users");
        File[] userFolders = usersFolder.listFiles(File::isDirectory);

        if (userFolders != null && userFolders.length > 0) {
            StringBuilder allComplaintSubjects = new StringBuilder();
            for (File userFolder : userFolders) {
                File[] complaintFiles = userFolder.listFiles((dir, name) -> name.endsWith("complaints.txt"));

                if (complaintFiles != null && complaintFiles.length > 0) {
                    for (File file : complaintFiles) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (line.startsWith("Subject:")) {
                                    allComplaintSubjects.append(line.substring(9)).append("\n");
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            String selectedComplaintSubject = (String) JOptionPane.showInputDialog(
                    this,
                    "Select the subject of the complaint to provide a solution:",
                    "Complaints",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    allComplaintSubjects.toString().split("\n"),
                    null);

            if (selectedComplaintSubject != null && !selectedComplaintSubject.isEmpty()) {
                provideSolution(selectedComplaintSubject);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No user complaints found.");
        }
    }

    private void provideSolution(String selectedComplaint) {
        String solution = JOptionPane.showInputDialog(this, "Enter the solution for the selected complaint:");
        if (solution != null && !solution.isEmpty()) {
            markComplaintAsSolved(selectedComplaint, solution);
            JOptionPane.showMessageDialog(this, "Solution added successfully! Complaint marked as solved.");
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a valid solution.");
        }
    }

    private void markComplaintAsSolved(String selectedComplaint, String solution) {
        File usersFolder = new File("Users");
        File[] userFolders = usersFolder.listFiles(File::isDirectory);

        if (userFolders != null && userFolders.length > 0) {
            for (File userFolder : userFolders) {
                File[] complaintFiles = userFolder.listFiles((dir, name) -> name.endsWith("complaints.txt"));

                if (complaintFiles != null && complaintFiles.length > 0) {
                    for (File file : complaintFiles) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            StringBuilder updatedContent = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (line.contains(selectedComplaint)) {
                                    updatedContent.append(line).append("\n");
                                    reader.readLine();
                                    updatedContent.append("Status: Solved\n");
                                    updatedContent.append("Solution: ").append(solution).append("\n");
                                } else {
                                    updatedContent.append(line).append("\n");
                                }
                            }

                            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                                writer.write(updatedContent.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new StaffControlPage("TestStaff").setVisible(true);
            }
        });
    }
}
