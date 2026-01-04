import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Dashboard.java
 * - Arcade-themed dashboard with GIF panels and buttons
 * - Confirmation dialog with arcade styling
 * - Hover effects that change GIF border to bright yellow
 * - Launches MatchingCardGame and SpinandWin
 * - When game is closed, Dashboard reopens automatically.
 */
public class Dashboard extends JFrame {

    private JLabel balanceLabel;
    private double balance;
    private boolean gameOpen = false;
    private ScaledGifPanel gif1, gif2, gif3;

    private static final Color ARCADE_PURPLE = new Color(153, 51, 255);
    private static final Color BUTTON_DEFAULT_COLOR = new Color(255, 165, 0);
    private static final Color BUTTON_HOVER_COLOR = new Color(255, 255, 0);
    private static final int BORDER_THICKNESS = 6;

    private static final String BACKGROUND_GIF_PATH = "src/images/final.gif";
    private static final String GIF1_PATH = "src/images/memory.gif";
    private static final String GIF2_PATH = "src/images/hangman.gif";
    private static final String GIF3_PATH = "src/images/slot.gif";
    private static final String LOGO_PATH = "src/images/timezone.png";

    private boolean reminderShown = false;

    
    private final Map<String, String> sounds = new HashMap<>();

    
    
    public Dashboard(double initialBalance) {
        this.balance = initialBalance;

        setTitle("Timezone Arcade - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                showExitConfirmation();
            }
        });
        
        // Configure sounds
        sounds.put("hover", "src/sounds/hover.wav");
        sounds.put("click", "src/sounds/click.wav");
        sounds.put("start", "src/sounds/start.wav");
        sounds.put("error", "src/sounds/error.wav");
        sounds.put("coin", "src/sounds/coin.wav");

        // Main background panel
        BackgroundPanel mainPanel = new BackgroundPanel(BACKGROUND_GIF_PATH);
        setContentPane(mainPanel);

        // --- Logo ---
        ImageIcon logoIcon = new ImageIcon(LOGO_PATH);
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        logoPanel.add(logoLabel, BorderLayout.CENTER);
        mainPanel.add(logoPanel, BorderLayout.NORTH);

        // --- Center GIF panels ---
        JPanel gifsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        gifsPanel.setOpaque(false);
        gifsPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        gif1 = new ScaledGifPanel(GIF1_PATH);
        gif2 = new ScaledGifPanel(GIF2_PATH);
        gif3 = new ScaledGifPanel(GIF3_PATH);

        gif1.setBorder(BorderFactory.createLineBorder(ARCADE_PURPLE, BORDER_THICKNESS, true));
        gif2.setBorder(BorderFactory.createLineBorder(ARCADE_PURPLE, BORDER_THICKNESS, true));
        gif3.setBorder(BorderFactory.createLineBorder(ARCADE_PURPLE, BORDER_THICKNESS, true));

        gifsPanel.add(gif1);
        gifsPanel.add(gif2);
        gifsPanel.add(gif3);

        mainPanel.add(gifsPanel, BorderLayout.CENTER);

        // --- Bottom buttons and balance ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 30, 40));

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        buttonsPanel.setOpaque(false);

        JButton game1Btn = createGameButton("MEMORY GAME", BUTTON_DEFAULT_COLOR, () -> launchGame("Memory Game", 50));
        JButton game2Btn = createGameButton("HANGMAN", BUTTON_DEFAULT_COLOR, () -> launchGame("Hangman", 40));
        JButton game3Btn = createGameButton("SPIN & WIN", BUTTON_DEFAULT_COLOR, () -> launchGame("Spin & Win", 30));

        addHoverBorderEffect(game1Btn, gif1);
        addHoverBorderEffect(game2Btn, gif2);
        addHoverBorderEffect(game3Btn, gif3);

        buttonsPanel.add(game1Btn);
        buttonsPanel.add(game2Btn);
        buttonsPanel.add(game3Btn);

        bottomPanel.add(buttonsPanel, BorderLayout.CENTER);

        // Balance label
        balanceLabel = new JLabel("Balance: ₱" + String.format("%.2f", balance));
        balanceLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 22));
        balanceLabel.setForeground(Color.YELLOW);

        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        balancePanel.setOpaque(false);
        balancePanel.add(balanceLabel);

        bottomPanel.add(balancePanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    public void updateBalance(double newBalance) {
        this.balance = newBalance;
        balanceLabel.setText("Balance: ₱" + String.format("%.2f", balance));
    }
    
    public void setGameOpen(boolean status) {
        this.gameOpen = status;
    }
    
    private void showClaimTicketsReminder(JFrame parent, int tickets) {
        JDialog reminderDialog = new JDialog(parent, "🎟️ Ticket Reminder!", true);
        reminderDialog.setSize(350, 300);
        reminderDialog.setResizable(false);
        reminderDialog.setLocationRelativeTo(parent); 
        reminderDialog.setUndecorated(true);

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

        ImageIcon logoIcon = new ImageIcon("src/images/timezone.png");
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setPreferredSize(new Dimension(350, 150));
        mainPanel.add(logoLabel, BorderLayout.NORTH);

        JLabel message = new JLabel(
            "<html><center>🎉 Don't forget to claim your " + tickets + " tickets! 🎉</center></html>"
        );
        message.setFont(new Font("Monospaced", Font.BOLD, 16));
        message.setForeground(Color.WHITE);
        message.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(message, BorderLayout.CENTER);

        JButton okBtn = new JButton("CONTINUE");
        okBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        okBtn.setBackground(Color.YELLOW);
        okBtn.setForeground(Color.BLACK);
        okBtn.setFocusPainted(false);
        okBtn.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2, true));
        okBtn.addActionListener(e -> reminderDialog.dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(okBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        reminderDialog.add(mainPanel);
        reminderDialog.setVisible(true); // Blocks until user closes
    }

    
    // ===============================
    // Create game button
    // ===============================
    private JButton createGameButton(String title, Color defaultColor, Runnable action) {
        JButton btn = new JButton(title);
        btn.setFont(new Font(Font.MONOSPACED, Font.BOLD, 22));
        btn.setForeground(Color.BLACK);
        btn.setBackground(defaultColor);
        btn.setFocusPainted(false);

        btn.addActionListener(e -> {
            playSound("click");
            animateButtonPress(btn);
            action.run();
        });

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                playSound("hover");
                btn.setBackground(BUTTON_HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(defaultColor);
            }
        });

        return btn;
    }

    // Button press animation
    private void animateButtonPress(JButton btn) {
        if (btn == null) return;
        Object lock = btn.getClientProperty("pressAnim");
        if (lock != null) return;
        btn.putClientProperty("pressAnim", true);

        Font base = btn.getFont();
        Timer t = new Timer(30, null);
        final int[] step = {0};
        t.addActionListener(evt -> {
            step[0]++;
            if (step[0] <= 3) {
                btn.setFont(base.deriveFont(base.getSize2D() + 2f));
            } else if (step[0] <= 6) {
                btn.setFont(base.deriveFont(base.getSize2D() - 1f));
            } else {
                btn.setFont(base);
                btn.putClientProperty("pressAnim", null);
                ((Timer) evt.getSource()).stop();
            }
        });
        t.start();
    }

    // Hover effect for GIF panels
    private void addHoverBorderEffect(JButton btn, ScaledGifPanel gifPanel) {
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                gifPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, BORDER_THICKNESS, true));
                playSound("hover");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                gifPanel.setBorder(BorderFactory.createLineBorder(ARCADE_PURPLE, BORDER_THICKNESS, true));
            }
        });
    }

    // Confirmation dialog
    private boolean confirmGameStart(String gameName, int cost) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(18, 2, 40));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 0, 255), 4, true),
                BorderFactory.createEmptyBorder(18, 22, 18, 22)
        ));

        JLabel title = new JLabel("⚡ Arcade Game Confirmation ⚡");
        title.setFont(new Font("Dialog", Font.BOLD, 22));
        title.setForeground(new Color(255, 255, 0));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, new Color(255, 0, 255)));
        title.setMaximumSize(new Dimension(420, 40));

        JLabel message = new JLabel(
            "<html><div style='text-align:center;'>"
            + "Playing <b style='color:#00ffff;'>" + gameName + "</b><br>"
            + "will cost <b style='color:#ffcc00;'>₱" + cost + "</b>.<br><br>"
            + "Do you want to continue?"
            + "</div></html>"
        );
        message.setFont(new Font("Dialog", Font.PLAIN, 18));
        message.setForeground(Color.WHITE);
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        message.setHorizontalAlignment(SwingConstants.CENTER);
        message.setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 6));
        message.setMaximumSize(new Dimension(420, 200));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 18, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(420, 48));

        JButton playBtn = new JButton("PLAY");
        playBtn.setFont(new Font("Dialog", Font.BOLD, 18));
        playBtn.setBackground(new Color(0, 200, 0));
        playBtn.setForeground(Color.BLACK);
        playBtn.setFocusPainted(false);
        playBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        JButton cancelBtn = new JButton("CANCEL");
        cancelBtn.setFont(new Font("Dialog", Font.BOLD, 18));
        cancelBtn.setBackground(new Color(200, 50, 50));
        cancelBtn.setForeground(Color.BLACK);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        playBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { playBtn.setBorder(new LineBorder(Color.GREEN, 4, true)); playSound("hover"); }
            @Override public void mouseExited(MouseEvent e) { playBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3)); }
        });
        cancelBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { cancelBtn.setBorder(new LineBorder(Color.RED, 4, true)); playSound("hover"); }
            @Override public void mouseExited(MouseEvent e) { cancelBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3)); }
        });

        buttonPanel.add(playBtn);
        buttonPanel.add(cancelBtn);

        panel.add(title);
        panel.add(Box.createVerticalStrut(12));
        panel.add(message);
        panel.add(Box.createVerticalStrut(14));
        panel.add(buttonPanel);

        JDialog dialog = new JDialog(this, "Confirm", true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);

        final boolean[] result = {false};

        playBtn.addActionListener(e -> {
            result[0] = true;
            playSound("click");
            playSound("coin");
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> {
            playSound("click");
            dialog.dispose();
        });

        dialog.setVisible(true);
        return result[0];
    }

    // ===============================
    // Launch game logic
    // ===============================

    private boolean launchGame(String gameName, int cost) {
        if (gameOpen) return false; // prevent multiple opens

        if (!confirmGameStart(gameName, cost)) return false;

        if (balance < cost) {
            playSound("error");
            JOptionPane.showMessageDialog(this, "Insufficient balance! Please load your card first to play this game!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        balance -= cost;
        balanceLabel.setText("Balance: ₱" + String.format("%.2f", balance));
        playSound("start");

        gameOpen = true;   // mark as open
        this.setVisible(false);

        JFrame gameWindow = null;

        if ("Memory Game".equalsIgnoreCase(gameName)) {
            gameWindow = new MatchingCardGame(balance);
        } else if ("Spin & Win".equalsIgnoreCase(gameName)) {
            gameWindow = new SpinandWin(this, balance);
        }

        if (gameWindow != null) {
            JFrame finalGameWindow = gameWindow;
//            gameWindow.addWindowListener(new WindowAdapter() {
//                @Override
//                public void windowClosed(WindowEvent e) {
//                    Dashboard.this.setVisible(true);
//                    gameOpen = false; // reset flag
//                }
//            });
            SwingUtilities.invokeLater(() -> finalGameWindow.setVisible(true));
            return true;
        }

        // For other games (placeholder)
        JOptionPane.showMessageDialog(this, "Launching " + gameName + "!", "Game Start", JOptionPane.INFORMATION_MESSAGE);
        gameOpen = false;
        return true;
    }




    // ===============================
    // Play sound
    // ===============================
    private void playSound(String key) {
        String path = sounds.get(key);
        if (path == null) return;
        new Thread(() -> {
            try {
                File f = new File(path);
                if (!f.exists()) return;
                AudioInputStream ais = AudioSystem.getAudioInputStream(f);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
            } catch (Exception ex) {}
        }).start();
    }

    // ------------------------------
    // Background and GIF panels
    // ------------------------------
    static class BackgroundPanel extends JPanel {
        private ImageIcon backgroundImageIcon;

        public BackgroundPanel(String gifPath) {
            try { backgroundImageIcon = new ImageIcon(gifPath); } catch (Exception e) { backgroundImageIcon = null; setBackground(Color.BLACK); }
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImageIcon != null)
                g.drawImage(backgroundImageIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }

    static class ScaledGifPanel extends JPanel {
        private ImageIcon gifIcon;

        public ScaledGifPanel(String gifPath) {
            try { gifIcon = new ImageIcon(gifPath); } catch (Exception e) { gifIcon = null; }
            setPreferredSize(new Dimension(350, 350));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (gifIcon != null)
                g.drawImage(gifIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }

    
    private void showExitConfirmation() {
        JDialog exitDialog = new JDialog(this, "Exit Confirmation", true);
        exitDialog.setSize(350, 300);
        exitDialog.setResizable(false);
        exitDialog.setLocationRelativeTo(this);
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

        ImageIcon logoIcon = new ImageIcon("src/images/timezone.png");
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setPreferredSize(new Dimension(350, 150));
        mainPanel.add(logoLabel, BorderLayout.NORTH);

        JLabel message = new JLabel("<html><center>Are you sure you want to exit?</center></html>");
        message.setFont(new Font("Monospaced", Font.BOLD, 16));
        message.setForeground(Color.WHITE);
        message.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(message, BorderLayout.CENTER);

        JButton yesBtn = new JButton("YES");
        yesBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        yesBtn.setBackground(Color.YELLOW);
        yesBtn.setForeground(Color.BLACK);
        yesBtn.setFocusPainted(false);
        yesBtn.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2, true));
        yesBtn.addActionListener(e -> {
            exitDialog.dispose();
            dispose(); // close dashboard
            System.exit(0);
        });

        JButton noBtn = new JButton("NO");
        noBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        noBtn.setBackground(Color.YELLOW);
        noBtn.setForeground(Color.BLACK);
        noBtn.setFocusPainted(false);
        noBtn.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2, true));
        noBtn.addActionListener(e -> exitDialog.dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(yesBtn);
        btnPanel.add(noBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        exitDialog.add(mainPanel);
        exitDialog.setVisible(true);
    }

    
    // ===============================
    // Main
    // ===============================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard(250.00));
    }
}
