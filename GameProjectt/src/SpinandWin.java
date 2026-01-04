import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SpinandWin extends JFrame {

    private static final long serialVersionUID = 1L;

    
    private boolean reminderShown = false;
    
    private JPanel contentPane;
    private JPanel reelContainer;
    private JLabel reel1, reel2, reel3;
    private JButton spinButton;
    private JLabel leverLabel;
    private JLabel alertLabel;
    private ImageIcon[] symbols;
    private Random random = new Random();
    private Dashboard dashboard;  
    private double balance;

    public SpinandWin(Dashboard dashboard, double balance) {
        this.dashboard = dashboard;
        this.balance = balance;

        setTitle("Arcade Slot Machine");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 900, 550);
        setResizable(false);

        loadSymbols();

        ImageIcon leverIcon = new ImageIcon("src/images/lever.gif");
        ImageIcon alertIcon = new ImageIcon("src/images/alert.gif");

        contentPane = new JPanel() {
            private ImageIcon bg = new ImageIcon("src/images/bg.gif");
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        contentPane.setLayout(null);
        setContentPane(contentPane);

        reelContainer = new JPanel(null);
        reelContainer.setBackground(new Color(0, 0, 0, 180));
        reelContainer.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 6, true));
        int containerWidth = 700;
        int containerHeight = 180;
        reelContainer.setBounds(100, 150, containerWidth, containerHeight);
        contentPane.add(reelContainer);

        alertLabel = new JLabel(alertIcon);
        alertLabel.setBounds(
                reelContainer.getX() + (reelContainer.getWidth() - 150) / 2,
                reelContainer.getY() - 140,
                150, 150
        );
        contentPane.add(alertLabel);

        int reelWidth = 200;
        int reelHeight = 140;
        int spacing = (containerWidth - 3 * reelWidth) / 4;

        reel1 = createReelLabel(spacing, reelWidth, reelHeight);
        reel2 = createReelLabel(spacing * 2 + reelWidth, reelWidth, reelHeight);
        reel3 = createReelLabel(spacing * 3 + reelWidth * 2, reelWidth, reelHeight);
        reelContainer.add(reel1);
        reelContainer.add(reel2);
        reelContainer.add(reel3);

        leverLabel = new JLabel(leverIcon);
        leverLabel.setBounds(reelContainer.getX() + reelContainer.getWidth() - 20,
                reelContainer.getY() - 125, 134, 330);
        contentPane.add(leverLabel);

        spinButton = new JButton("SPIN");
        spinButton.setFont(new Font("Monospaced", Font.BOLD, 28));
        spinButton.setBackground(new Color(255, 140, 0));
        spinButton.setForeground(Color.WHITE);
        spinButton.setFocusPainted(false);
        spinButton.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 4, true));
        spinButton.setBounds(360, 400, 180, 60);
        contentPane.add(spinButton);

        spinButton.addActionListener(e -> spinWithLever());
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (dashboard != null) {
                    dashboard.updateBalance(balance);
                    dashboard.setGameOpen(false);
                    dashboard.setVisible(true);
                    
                    // Show the claim tickets reminder
                    showClaimTicketsReminder(dashboard);
                }
            }
        });

    }

    private void loadSymbols() {
        String[] files = {
                "com1.png", "com1.png", "com1.png",
                "com2.png", "com2.png",
                "com3.png", "com4.png", "com5.png", "com6.png",
                "com7.png", "com8.png", "com9.png"
        };
        symbols = new ImageIcon[files.length];
        for (int i = 0; i < files.length; i++) {
            ImageIcon icon = new ImageIcon("src/images/" + files[i]);
            Image scaled = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
            symbols[i] = new ImageIcon(scaled);
        }
    }

    private JLabel createReelLabel(int x, int w, int h) {
        JLabel label = new JLabel(symbols[0]);
        label.setOpaque(true);
        label.setBackground(new Color(0, 0, 0, 200));
        label.setBorder(BorderFactory.createLineBorder(Color.CYAN, 4, true));
        label.setBounds(x, 20, w, h);
        return label;
    }

    private void spinWithLever() {
        spinButton.setEnabled(false);

        boolean forceWin = random.nextBoolean(); // 50/50 chance

        Timer reelTimer = new Timer(100, null);
        final int[] count = {0};

        reelTimer.addActionListener(e -> {
            count[0]++;
            if (count[0] > 15) {
                if (forceWin) {
                    ImageIcon win = symbols[random.nextInt(symbols.length)];
                    reel1.setIcon(win);
                    reel2.setIcon(win);
                    reel3.setIcon(win);
                    showJackpotDialog();
                } else {
                    reel1.setIcon(symbols[random.nextInt(symbols.length)]);
                    reel2.setIcon(symbols[random.nextInt(symbols.length)]);
                    reel3.setIcon(symbols[random.nextInt(symbols.length)]);
                    showTryAgainDialog();
                }
                reelTimer.stop();
                spinButton.setEnabled(true);
            } else {
                reel1.setIcon(symbols[random.nextInt(symbols.length)]);
                reel2.setIcon(symbols[random.nextInt(symbols.length)]);
                reel3.setIcon(symbols[random.nextInt(symbols.length)]);
            }
        });
        reelTimer.start();
    }

    private void showTryAgainDialog() {
        JFrame tryFrame = new JFrame("Try Again!");
        tryFrame.setSize(300, 250);
        tryFrame.setResizable(false);
        tryFrame.setLocationRelativeTo(this);

        JLabel gifLabel = new JLabel(new ImageIcon("src/images/try.gif"));
        gifLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tryFrame.add(gifLabel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton yesBtn = new JButton("Yes");
        JButton noBtn = new JButton("No");

        yesBtn.addActionListener(e -> {
            tryFrame.dispose();
            spinWithLever(); // immediately spin again
        });

        noBtn.addActionListener(e -> {
            tryFrame.dispose();
            this.dispose(); // close game, return to dashboard
        });

        btnPanel.add(yesBtn);
        btnPanel.add(noBtn);
        tryFrame.add(btnPanel, BorderLayout.SOUTH);

        tryFrame.setVisible(true);
    }

    private void showJackpotDialog() {
        JFrame jackpotFrame = new JFrame("JACKPOT!");
        jackpotFrame.setSize(400, 350);
        jackpotFrame.setResizable(false);
        jackpotFrame.setLocationRelativeTo(this);
        jackpotFrame.setLayout(new BorderLayout());

        JLabel gifLabel = new JLabel(new ImageIcon("src/images/jackpot.gif"));
        gifLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jackpotFrame.add(gifLabel, BorderLayout.CENTER);

        JLabel msgLabel = new JLabel("<html><center>All 3 Objects Matched!<br>Congratulations!<br>Returning to dashboard...</center></html>");
        msgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        msgLabel.setFont(new Font("Arial", Font.BOLD, 16));
        jackpotFrame.add(msgLabel, BorderLayout.SOUTH);

        jackpotFrame.setVisible(true);

        new Timer(3000, e -> {
            jackpotFrame.dispose();
            this.dispose(); // close SpinandWin, return to dashboard
        }).start();
    }
    
    private void showClaimTicketsReminder(JFrame parent) {
        if (reminderShown) return; // Prevent multiple popups
        reminderShown = true;

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

        JLabel message = new JLabel("<html><center>🎉 Don't forget to claim your tickets! 🎉</center></html>");
        message.setFont(new Font("Monospaced", Font.BOLD, 16));
        message.setForeground(Color.WHITE);
        message.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(message, BorderLayout.CENTER);

        JButton okBtn = new JButton("CONTINUE PLAYING");
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

}
