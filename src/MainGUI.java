import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainGUI extends JFrame {
    private JTextField txtSubjectName;
    private JComboBox<String> cmbDifficulty;
    private JTextField txtUnits; // 🌟 New text field for dynamic unit tracking
    private JTextField txtExamDate;
    private DefaultTableModel tableModel;
    private List<Subject> subjectList = new ArrayList<>();
    private Map<LocalDate, String> lastGeneratedPlan;

    // Reflection components
    private JTextArea txtReflection;
    private JRadioButton radSatisfied;
    private JRadioButton radNotSatisfied;
    
    private List<String> archiveHistoryLog = new ArrayList<>();

    // Custom Color Palette
    private final Color COLOR_BACKGROUND = new Color(235, 242, 250); 
    private final Color COLOR_PANEL = new Color(255, 255, 255);       
    private final Color COLOR_DARK_BTN = new Color(45, 55, 72);       
    private final Color COLOR_TEXT_LIGHT = new Color(255, 255, 255);  
    private final Color COLOR_PASTEL_BLUE_CARD = new Color(224, 236, 250); 

    // Swap these strings with your direct image file paths!
    private final String IMAGE_SUCCESS_PATH = "file:D:\\6TH SEMESTER\\Acemap\\Acemap\\celeb.jpg"; 
    private final String IMAGE_SUPPORT_PATH = "file:D:\\6TH SEMESTER\\Acemap\\Acemap\\emotional.jpg"; 

    public MainGUI() {
        setTitle("AceMap: Studio Edition");
        setSize(1020, 680); // Slight width expansion to fit all input fields beautifully
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BACKGROUND);
        setLayout(new BorderLayout(15, 15));

        // --- TOP PANEL: Input Layout (5 columns to space out Name, Difficulty, Units, Date, Buttons) ---
        // --- TOP PANEL: Input Layout (Upgraded to GridBagLayout for Perfect Alignment) ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(COLOR_PANEL);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Subtle gaps between components
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // --- ROW 0: Labels ---
        gbc.gridy = 0;
        
        gbc.gridx = 0; inputPanel.add(new JLabel("Subject Name:"), gbc);
        gbc.gridx = 1; inputPanel.add(new JLabel("Difficulty Weight:"), gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("Number of Units:"), gbc);
        gbc.gridx = 3; inputPanel.add(new JLabel("Exam Date (YYYY-MM-DD):"), gbc);

        // --- ROW 1: Inputs & Action Buttons ---
        gbc.gridy = 1;

        gbc.gridx = 0; 
        txtSubjectName = new JTextField();
        inputPanel.add(txtSubjectName, gbc);

        gbc.gridx = 1; 
        cmbDifficulty = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        inputPanel.add(cmbDifficulty, gbc);

        gbc.gridx = 2; 
        txtUnits = new JTextField("5");
        inputPanel.add(txtUnits, gbc);

        gbc.gridx = 3; 
        txtExamDate = new JTextField(LocalDate.now().plusDays(15).toString());
        inputPanel.add(txtExamDate, gbc);

        // --- Action Buttons Side-by-Side ---
        JPanel btnWrapper = new JPanel(new GridLayout(1, 2, 5, 0));
        btnWrapper.setOpaque(false);
        
        JButton btnAdd = new JButton("Add Subject");
        JButton btnGenerate = new JButton("Generate Schedule");
        styleDarkButton(btnAdd);
        styleDarkButton(btnGenerate);
        
        btnWrapper.add(btnAdd);
        btnWrapper.add(btnGenerate);

        gbc.gridx = 4; // Places buttons cleanly in the 5th column
        inputPanel.add(btnWrapper, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // --- CENTER CALENDAR DISPLAY TABLE ---
        String[] columns = {"Target Date", "Daily Strategic Strategy Plan"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(COLOR_DARK_BTN);
        table.getTableHeader().setForeground(COLOR_TEXT_LIGHT);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // --- EAST PANEL: Progress Reflection Sidebar ---
        JPanel trackerPanel = new JPanel();
        trackerPanel.setLayout(new BoxLayout(trackerPanel, BoxLayout.Y_AXIS));
        trackerPanel.setBackground(COLOR_PANEL);
        trackerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        trackerPanel.setPreferredSize(new Dimension(300, 0));

        JLabel lblTrackerTitle = new JLabel("Daily Progress Check-in");
        lblTrackerTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTrackerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        trackerPanel.add(lblTrackerTitle);
        trackerPanel.add(Box.createVerticalStrut(15));

        JLabel lblQuestion = new JLabel("What did you complete today?");
        lblQuestion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblQuestion.setAlignmentX(Component.LEFT_ALIGNMENT);
        trackerPanel.add(lblQuestion);
        trackerPanel.add(Box.createVerticalStrut(5));

        txtReflection = new JTextArea(4, 20);
        txtReflection.setLineWrap(true);
        txtReflection.setWrapStyleWord(true);
        txtReflection.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane reflectionScroll = new JScrollPane(txtReflection);
        reflectionScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        trackerPanel.add(reflectionScroll);
        trackerPanel.add(Box.createVerticalStrut(15));

        radSatisfied = new JRadioButton("I am satisfied with my work \u2728"); 
        radNotSatisfied = new JRadioButton("I need more focus \u2615");       
        radSatisfied.setBackground(COLOR_PANEL);
        radNotSatisfied.setBackground(COLOR_PANEL);
        radSatisfied.setAlignmentX(Component.LEFT_ALIGNMENT);
        radNotSatisfied.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        ButtonGroup group = new ButtonGroup();
        group.add(radSatisfied);
        group.add(radNotSatisfied);
        radSatisfied.setSelected(true);

        trackerPanel.add(radSatisfied);
        trackerPanel.add(radNotSatisfied);
        trackerPanel.add(Box.createVerticalStrut(15));

        JButton btnSubmitReflection = new JButton("Submit Check-In");
        styleDarkButton(btnSubmitReflection);
        btnSubmitReflection.setAlignmentX(Component.LEFT_ALIGNMENT);
        trackerPanel.add(btnSubmitReflection);

        trackerPanel.add(Box.createVerticalStrut(15));
        
        JButton btnViewArchive = new JButton("View Reflection Archive ");
        styleDarkButton(btnViewArchive);
        btnViewArchive.setBackground(new Color(90, 105, 120)); 
        btnViewArchive.setAlignmentX(Component.LEFT_ALIGNMENT);
        trackerPanel.add(btnViewArchive);

        trackerPanel.add(Box.createVerticalStrut(20));
        JSeparator separator = new JSeparator();
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        trackerPanel.add(separator);
        trackerPanel.add(Box.createVerticalStrut(15));

        JButton btnMissedDay = new JButton("  I Missed a Day!"); 
        styleMissedButton(btnMissedDay);
        btnMissedDay.setAlignmentX(Component.LEFT_ALIGNMENT);
        trackerPanel.add(btnMissedDay);

        add(trackerPanel, BorderLayout.EAST);

        // --- BOTTOM PANEL: Actions ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(COLOR_BACKGROUND);
        
        JButton btnExport = new JButton("Export Printable HTML Schedule "); 
        styleDarkButton(btnExport);
        btnExport.setEnabled(false);
        bottomPanel.add(btnExport);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- EVENT CONTROLLERS ---
        btnAdd.addActionListener(e -> {
            String name = txtSubjectName.getText().trim();
            String diff = (String) cmbDifficulty.getSelectedItem();
            String unitsStr = txtUnits.getText().trim();
            
            if (!name.isEmpty() && !unitsStr.isEmpty()) {
                try {
                    // 🌟 Parse user's dynamic unit count configuration
                    int units = Integer.parseInt(unitsStr);
                    subjectList.add(new Subject(name, diff, units)); 
                    txtSubjectName.setText("");
                    txtUnits.setText("5"); // Reset field back to default baseline standard
                    JOptionPane.showMessageDialog(this, "Logged: " + name + " (" + units + " Units Verified)");
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(this, "Please insert a valid numerical number for units!");
                }
            }
        });

        btnGenerate.addActionListener(e -> {
            if (subjectList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please insert tracking subjects first.");
                return;
            }
            try {
                LocalDate targetExam = LocalDate.parse(txtExamDate.getText().trim());
                updateScheduleView(targetExam);
                btnExport.setEnabled(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error processing configuration. Use YYYY-MM-DD.");
            }
        });

        btnSubmitReflection.addActionListener(e -> handleReflectionSubmit());
        btnViewArchive.addActionListener(e -> displayHistoryArchiveWindow());
        btnExport.addActionListener(e -> executeHtmlExport());

        btnMissedDay.addActionListener(e -> {
            if (lastGeneratedPlan == null || lastGeneratedPlan.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Generate a plan before rescheduling!");
                return;
            }
            try {
                LocalDate targetExam = LocalDate.parse(txtExamDate.getText().trim());
                lastGeneratedPlan = Scheduleengine.generateSchedule(LocalDate.now().plusDays(1), targetExam, subjectList);
                
                tableModel.setRowCount(0);
                for (Map.Entry<LocalDate, String> entry : lastGeneratedPlan.entrySet()) {
                    tableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
                }
                JOptionPane.showMessageDialog(this, "\uD83D\uDEE1  Shift Complete! Schedule re-balanced across tomorrow without double-booking your days.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Could not adjust timeline.");
            }
        });
    }

    private void updateScheduleView(LocalDate targetExam) {
        tableModel.setRowCount(0);
        lastGeneratedPlan = Scheduleengine.generateSchedule(LocalDate.now(), targetExam, subjectList);
        for (Map.Entry<LocalDate, String> entry : lastGeneratedPlan.entrySet()) {
            tableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }
    }

    private void handleReflectionSubmit() {
        String noteText = txtReflection.getText().trim();
        if (noteText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please type a quick reflection note first!");
            return;
        }

        String statusLabel = radSatisfied.isSelected() ? "[SATISFIED \u2728]" : "[NEEDS FOCUS \u2615]";
        String consolidatedLogEntry = String.format("%s Logged on %s:\n -> \"%s\"\n\n", statusLabel, LocalDate.now().toString(), noteText);
        archiveHistoryLog.add(consolidatedLogEntry);

        if (radSatisfied.isSelected()) {
            String[] happyMessages = {
                "Phenomenal work! Momentum is built brick by brick. Keep shining! ",
                "Fantastic job today! Your future self is thanking you right now. ",
                "You crushed it! Absolute elite dedication. Take a well-earned break! "
            };
            String selectedMsg = happyMessages[new Random().nextInt(happyMessages.length)];
            showEmpowermentCard("Great Work!", selectedMsg, new Color(46, 204, 113), IMAGE_SUCCESS_PATH, true);
        } else {
            String[] supportiveMessages = {
                "That is completely okay. Progress isn't linear. Tomorrow is a fresh canvas. ",
                "Be gentle with yourself. Doing even a small portion is a victory. Rest up! ",
                "Deep breaths. A single tough day won't undo your hard work. You've got this."
            };
            String selectedMsg = supportiveMessages[new Random().nextInt(supportiveMessages.length)];
            showEmpowermentCard("Keep Your Head Up!", selectedMsg, new Color(52, 152, 219), IMAGE_SUPPORT_PATH, false);
        }
        txtReflection.setText("");
    }

    private void showEmpowermentCard(String title, String message, Color headerColor, String imgPath, boolean triggerConfetti) {
        JDialog card = new JDialog(this, title, true);
        card.setSize(480, 280);
        card.setLocationRelativeTo(this);
        card.setLayout(new BorderLayout());

        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(headerColor);
        JLabel lblHeader = new JLabel(title);
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pnlHeader.add(lblHeader);

        ConfettiPanel pnlBody = new ConfettiPanel();
        pnlBody.setBackground(COLOR_PASTEL_BLUE_CARD);
        pnlBody.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlBody.setLayout(new BorderLayout(10, 10));
        
        JLabel lblMessage = new JLabel("<html><body style='text-align: center; width: 320px;'>" + message + "</body></html>", SwingConstants.CENTER);
        lblMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblMessage.setForeground(new Color(45, 55, 72));
        pnlBody.add(lblMessage, BorderLayout.CENTER);

        try {
            ImageIcon imageAsset;
            if (imgPath.startsWith("file:/")) {
                imageAsset = new ImageIcon(new java.io.File(imgPath.substring(6)).getAbsolutePath());
            } else {
                imageAsset = new ImageIcon(new java.net.URL(imgPath));
            }
            Image scaledImg = imageAsset.getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
            JLabel lblImage = new JLabel(new ImageIcon(scaledImg));
            JPanel imgWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            imgWrapper.setOpaque(false);
            imgWrapper.add(lblImage);
            pnlBody.add(imgWrapper, BorderLayout.NORTH);
        } catch (Exception ex) {
            System.out.println("Image path could not load. Skipping asset render layer cleanly.");
        }

        card.add(pnlHeader, BorderLayout.NORTH);
        card.add(pnlBody, BorderLayout.CENTER);

        if (triggerConfetti) {
            pnlBody.startAnimationLoop();
        }

        card.setVisible(true);
    }

    private void displayHistoryArchiveWindow() {
        JDialog archiveBox = new JDialog(this, "Logged Historical Milestones Portfolio", true);
        archiveBox.setSize(500, 400);
        archiveBox.setLocationRelativeTo(this);
        archiveBox.setLayout(new BorderLayout(10, 10));

        JTextArea txtHistoryArea = new JTextArea();
        txtHistoryArea.setEditable(false);
        txtHistoryArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtHistoryArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (archiveHistoryLog.isEmpty()) {
            txtHistoryArea.setText("--- No tracker milestones logged in the active portfolio session yet. ---");
        } else {
            for (String entry : archiveHistoryLog) {
                txtHistoryArea.append(entry);
            }
        }

        JScrollPane pane = new JScrollPane(txtHistoryArea);
        archiveBox.add(new JLabel("  📚 Compiled Academic Progress Logs History Portfolio:", SwingConstants.LEFT), BorderLayout.NORTH);
        archiveBox.add(pane, BorderLayout.CENTER);
        archiveBox.setVisible(true);
    }

    private void styleDarkButton(JButton button) {
        button.setBackground(COLOR_DARK_BTN);
        button.setForeground(COLOR_TEXT_LIGHT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
    }

    private void styleMissedButton(JButton button) {
        button.setBackground(new Color(231, 76, 60)); 
        button.setForeground(COLOR_TEXT_LIGHT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    private void executeHtmlExport() {
        if (lastGeneratedPlan == null || lastGeneratedPlan.isEmpty()) return;
        File file = new File("AceMap_Schedule.html");
        try (FileWriter writer = new FileWriter(file)) {
            StringBuilder html = new StringBuilder();
            html.append("<html><head><style>")
                .append("body { font-family: 'Segoe UI', Arial, sans-serif; margin: 40px; background-color: #f8f9fa; }")
                .append("h2 { color: #2c3e50; text-align: center; }")
                .append("table { width: 100%; border-collapse: collapse; background: white; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }")
                .append("th, td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #e0e0e0; }")
                .append("th { background-color: #34495e; color: white; }")
                .append("tr:hover { background-color: #f1f2f6; }")
                .append(".lock { color: #d63031; font-weight: bold; }")
                .append(".rev { color: #0984e3; font-weight: bold; }")
                .append("</style></head><body>")
                .append("<h2>\uD83C\uDFAF My Custom AceMap Study Roadmap</h2>")
                .append("<table><tr><th>Date</th><th>Allocated Strategy</th></tr>");

            for (Map.Entry<LocalDate, String> entry : lastGeneratedPlan.entrySet()) {
                String val = entry.getValue();
                String cssClass = val.contains("LOCK") ? "class='lock'" : (val.contains("REVISION") ? "class='rev'" : "");
                html.append("<tr><td>").append(entry.getKey()).append("</td><td ").append(cssClass).append(">").append(val).append("</td></tr>");
            }
            html.append("</table></body></html>");
            writer.write(html.toString());
            JOptionPane.showMessageDialog(this, "Schedule successfully compiled to: " + file.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to write export file.");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Modern UI Initialization Failed.");
        }
        SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
    }

    private class ConfettiPanel extends JPanel {
        private List<Particle> particles = new ArrayList<>();
        private boolean running = false;

        public void startAnimationLoop() {
            Random rand = new Random();
            for (int i = 0; i < 40; i++) {
                particles.add(new Particle(
                    rand.nextInt(400),       
                    rand.nextInt(50) - 20,   
                    new Color(Color.HSBtoRGB(rand.nextFloat(), 0.6f, 0.9f)) 
                ));
            }
            running = true;

            Thread animationThread = new Thread(() -> {
                long duration = System.currentTimeMillis() + 4000; 
                while (running && System.currentTimeMillis() < duration) {
                    for (Particle p : particles) {
                        p.updatePhysics();
                    }
                    repaint(); 
                    try { Thread.sleep(25); } catch (InterruptedException e) { break; }
                }
                particles.clear();
                repaint();
            });
            animationThread.setDaemon(true);
            animationThread.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            for (Particle p : particles) {
                g2d.setColor(p.color);
                g2d.fillRect(p.x, p.y, p.size, p.size);
            }
        }
    }

    private class Particle {
        int x, y, size, speedY, speedX;
        Color color;

        Particle(int x, int y, Color color) {
            Random r = new Random();
            this.x = x;
            this.y = y;
            this.size = r.nextInt(6) + 6;
            this.speedY = r.nextInt(4) + 3; 
            this.speedX = r.nextInt(4) - 2; 
            this.color = color;
        }

        void updatePhysics() {
            y += speedY;
            x += speedX;
        }
    }
}