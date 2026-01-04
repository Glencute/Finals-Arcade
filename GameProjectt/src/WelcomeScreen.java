import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class ScaledGifPanel extends JPanel {
    private ImageIcon gifIcon;

    public ScaledGifPanel(String gifPath) {
        gifIcon = new ImageIcon(gifPath);
        setLayout(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image img = gifIcon.getImage();
        g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
    }
}

public class WelcomeScreen extends JFrame implements ActionListener {

    private JButton startButton;
    private double balance = 250.00; // initial balance ₱250
    private boolean reminderShown = false;

    private static final String GIF_PATH = "src/images/arcade.gif";
    private static final String LOGO_PATH = "src/images/timezone.png";
    private static final String CARD_BG_PATH = "src/images/card.png";

    public WelcomeScreen() {
        setTitle("Timezone Arcade");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // handle exit manually
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        // ===== LAYERED PANE =====
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, getWidth(), getHeight());
        add(layeredPane);

        // ===== BACKGROUND GIF PANEL =====
        ScaledGifPanel bgPanel = new ScaledGifPanel(GIF_PATH);
        bgPanel.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(bgPanel, Integer.valueOf(0));

        // ===== UI PANEL =====
        JPanel uiPanel = new JPanel();
        uiPanel.setOpaque(false);
        uiPanel.setLayout(new BoxLayout(uiPanel, BoxLayout.Y_AXIS));
        uiPanel.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(uiPanel, Integer.valueOf(1));

        // LOGO
        ImageIcon logo = new ImageIcon(LOGO_PATH);
        Image scaledLogo = logo.getImage().getScaledInstance(260, 140, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        uiPanel.add(Box.createVerticalStrut(20));
        uiPanel.add(logoLabel);

        // TITLE
        JLabel titleLabel = new JLabel("<< J A V A   A R C A D E >>");
        titleLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 48));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        uiPanel.add(titleLabel);

        // SWIPE BUTTON
        startButton = new JButton("SWIPE YOUR CARD TO PLAY");
        startButton.setFont(new Font(Font.MONOSPACED, Font.BOLD, 22));
        startButton.setBackground(Color.ORANGE);
        startButton.setForeground(Color.BLACK);
        startButton.setFocusPainted(false);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(this);

        uiPanel.add(Box.createVerticalGlue());
        uiPanel.add(startButton);
        uiPanel.add(Box.createVerticalStrut(50));

        // HANDLE WINDOW CLOSING
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                showExitConfirmation(WelcomeScreen.this);
            }
        });

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            // ===== SHOW CARD BALANCE POPUP =====
            JFrame cardFrame = new JFrame("Card Balance");
            cardFrame.setSize(400, 200);
            cardFrame.setLocationRelativeTo(null);
            cardFrame.setResizable(false);
            cardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel() {
                private ImageIcon bg = new ImageIcon(CARD_BG_PATH);

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            };
            panel.setLayout(new GridBagLayout());
            panel.setOpaque(false);

            JLabel amountLabel = new JLabel("₱" + String.format("%.2f", balance));
            amountLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 28));
            amountLabel.setForeground(Color.WHITE);
            panel.add(amountLabel);

            cardFrame.add(panel);
            cardFrame.setVisible(true);

            // Wait until the popup is closed, then open Dashboard
            cardFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    dispose(); // close welcome screen
                    new Dashboard(balance); // your dashboard code
                }
            });
        }
    }

    // ==============================
    // EXIT CONFIRMATION DIALOG
    // ==============================
    private void showExitConfirmation(JFrame parent) {
        JDialog exitDialog = new JDialog(parent, "Exit Game", true);
        exitDialog.setSize(350, 300);
        exitDialog.setResizable(false);
        exitDialog.setLocationRelativeTo(parent); 
        exitDialog.setUndecorated(true);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color start = new Color(255, 140, 0);
                Color end = new Color(255, 0, 0);
                GradientPaint gp = new GradientPaint(0, 0, start, getWidth(), getHeight(), end);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 4, true));

        ImageIcon logoIcon = new ImageIcon(LOGO_PATH);
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setPreferredSize(new Dimension(350, 150));
        mainPanel.add(logoLabel, BorderLayout.NORTH);

        JLabel message = new JLabel("<html><center>Are you sure you want to exit?</center></html>");
        message.setFont(new Font("Monospaced", Font.BOLD, 16));
        message.setForeground(Color.WHITE);
        message.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(message, BorderLayout.CENTER);

        // BUTTONS
        JButton yesBtn = new JButton("YES");
        yesBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        yesBtn.setBackground(Color.YELLOW);
        yesBtn.setForeground(Color.BLACK);
        yesBtn.setFocusPainted(false);
        yesBtn.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2, true));
        yesBtn.addActionListener(e -> {
            exitDialog.dispose();
            parent.dispose(); // closes WelcomeScreen → program exits
        });

        JButton noBtn = new JButton("CANCEL");
        noBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        noBtn.setBackground(Color.YELLOW);
        noBtn.setForeground(Color.BLACK);
        noBtn.setFocusPainted(false);
        noBtn.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2, true));
        noBtn.addActionListener(e -> exitDialog.dispose()); // just close dialog

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(yesBtn);
        btnPanel.add(noBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        exitDialog.add(mainPanel);
        exitDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WelcomeScreen());
    }
}
