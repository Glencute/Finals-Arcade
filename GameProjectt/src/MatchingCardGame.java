import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class MatchingCardGame extends JFrame implements ActionListener {

    // Total pairs and total cards
    private static final int PAIRS = 5;
    private static final int CARDS = PAIRS * 2;

    // UI components
    private JButton[] cards = new JButton[CARDS];
    private int[] cardIds = new int[CARDS]; // stores which card image is where
    private ImageIcon[] icons = new ImageIcon[PAIRS];
    private ImageIcon backIcon;

    private JLabel infoLabel;
    private int firstIndex = -1;   // first card clicked
    private int secondIndex = -1;  // second card clicked
    private boolean lock = false;  // prevent clicking while checking match

    private boolean reminderShown = false;
    
    private int moves = 0;
    private int pairsFound = 0;

    // store dashboard balance to reopen
    private double dashboardBalance;

    public MatchingCardGame(double balance) {
        this.dashboardBalance = balance;

        customizeOptionPaneUI();

        //Window setup
        setTitle("Arcade Memory Game");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // important to trigger windowClosed
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(8, 10, 30));

        loadIcons(); // load images

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(new Color(8, 10, 30));
        header.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        JLabel title = new JLabel("ARCADE MEMORY GAME");
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Find matching arcade cabinets to win!");
        sub.setForeground(new Color(180, 190, 255));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoLabel = new JLabel("Moves: 0    Pairs: 0/" + PAIRS, JLabel.CENTER);
        infoLabel.setForeground(new Color(160, 220, 255));

        JPanel statsBox = new JPanel();
        statsBox.setOpaque(false);
        statsBox.setBorder(BorderFactory.createLineBorder(new Color(255, 64, 180), 2, true));
        statsBox.add(infoLabel);

        header.add(title);
        header.add(Box.createRigidArea(new Dimension(0, 4)));
        header.add(sub);
        header.add(Box.createRigidArea(new Dimension(0, 10)));
        header.add(statsBox);

        add(header, BorderLayout.NORTH);

        // Center panel (cards)
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(8, 10, 30));
        center.setBorder(BorderFactory.createEmptyBorder(18, 40, 18, 40));

        JPanel grid = new JPanel(new GridLayout(2, 5, 18, 18));
        grid.setBackground(new Color(8, 10, 30));
        grid.setBorder(BorderFactory.createLineBorder(new Color(255, 64, 180), 2, true));

        // create 10 card buttons
        for (int i = 0; i < CARDS; i++) {
            JButton btn = new JButton();
            btn.setBackground(new Color(20, 22, 40));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(new Color(255, 64, 180), 3, true));
            btn.addActionListener(this);

            cards[i] = btn;
            grid.add(btn);
        }

        center.add(grid, BorderLayout.CENTER);

        //Footer (tip + reset button)
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(new Color(8, 10, 30));

        JLabel tip = new JLabel("Tip: Try to remember the position like a real arcade machine!", JLabel.CENTER);
        tip.setForeground(new Color(120, 140, 200));
        tip.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton reset = new JButton("Shuffle / Reset");
        reset.setBackground(new Color(45, 136, 255));
        reset.setForeground(Color.WHITE);
        reset.setFocusPainted(false);
        reset.setAlignmentX(Component.CENTER_ALIGNMENT);
        reset.addActionListener(e -> setupBoard());

        footer.add(tip);
        footer.add(Box.createRigidArea(new Dimension(0, 10)));
        footer.add(reset);

        center.add(footer, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        // Show welcome message
        JOptionPane.showMessageDialog(this, "Welcome to the Arcade Memory Game!");

        setupBoard(); // start the game

        // ----------------------------
        // Window listener to reopen Dashboard when this window closes
        // ----------------------------
     // Replace your existing WindowListener with this
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                Dashboard dash = new Dashboard(dashboardBalance);
                dash.setVisible(true);
                SwingUtilities.invokeLater(() -> showClaimTicketsReminder(dash));
            }
        });


    }

    // Load card images
    private void loadIcons() {
        icons[0] = loadIcon("card1.png");
        icons[1] = loadIcon("card2.png");
        icons[2] = loadIcon("card3.png");
        icons[3] = loadIcon("card4.png");
        icons[4] = loadIcon("card5.png");

        backIcon = loadIcon("backcard.png");
    }

    // Load and resize an image for the cards
    private ImageIcon loadIcon(String file) {
        var url = getClass().getResource("/images/" + file);
        ImageIcon img = new ImageIcon(url);
        Image scaled = img.getImage().getScaledInstance(130, 130, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private void customizeOptionPaneUI() {
        Color bg = new Color(8, 10, 30);
        Color accent = new Color(255, 64, 180);

        UIManager.put("OptionPane.background", bg);
        UIManager.put("Panel.background", bg);

        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 13));

        UIManager.put("Button.background", new Color(45, 136, 255));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.select", accent);
    }

    // Create a smaller icon for the dialog 
    private ImageIcon smallIcon(ImageIcon icon) {
        if (icon == null) return null;
        Image img = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    // shuffle cards and reset variables 
    private void setupBoard() {
        List<Integer> nums = new ArrayList<>();

        for (int i = 0; i < PAIRS; i++) {
            nums.add(i);
            nums.add(i);
        }

        Collections.shuffle(nums);

        for (int i = 0; i < CARDS; i++) {
            cardIds[i] = nums.get(i);
            cards[i].setEnabled(true);
            cards[i].setIcon(backIcon);
            cards[i].setDisabledIcon(null); // reset disabled icon
        }

        firstIndex = -1;
        secondIndex = -1;
        lock = false;
        moves = 0;
        pairsFound = 0;
        updateInfo();
    }

    // Update the moves/pairs info label
    private void updateInfo() {
        infoLabel.setText("Moves: " + moves + "    Pairs: " + pairsFound + "/" + PAIRS);
    }

    // When the card is clicked
    @Override
    public void actionPerformed(ActionEvent e) {
        if (lock) return;

        int index = -1;
        for (int i = 0; i < CARDS; i++) {
            if (cards[i] == e.getSource()) {
                index = i;
                break;
            }
        }

        if (index == -1 || !cards[index].isEnabled() || index == firstIndex) return;

        reveal(index);

        if (firstIndex == -1) {
            firstIndex = index;
        } else {
            secondIndex = index;
            moves++;
            updateInfo();
            checkMatch();
        }
    }

    private void reveal(int index) {
        cards[index].setIcon(icons[cardIds[index]]);
        cards[index].setDisabledIcon(icons[cardIds[index]]); // keep color when disabled
    }

    private void hide(int index) {
        cards[index].setIcon(backIcon);
    }

    // Check if two flipped cards match 
    private void checkMatch() {
        if (cardIds[firstIndex] == cardIds[secondIndex]) {
            // match found
            cards[firstIndex].setEnabled(false);
            cards[secondIndex].setEnabled(false);
            pairsFound++;
            firstIndex = -1;
            secondIndex = -1;
            updateInfo();

            if (pairsFound == PAIRS) win();
        } else {
            // not a match → flip back after 0.8 sec
            lock = true;
            Timer t = new Timer(800, e -> {
                hide(firstIndex);
                hide(secondIndex);
                firstIndex = -1;
                secondIndex = -1;
                lock = false;
            });
            t.setRepeats(false);
            t.start();
        }
    }

    //Player wins 
    private void win() {
        // Use a smaller version of one of the card icons as the dialog icon
        ImageIcon icon = smallIcon(icons[0]);

        int ans = JOptionPane.showConfirmDialog(
                this,
                "You win!\nMoves: " + moves + "\nPlay again?",
                "Winner!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                icon
        );
        if (ans == JOptionPane.YES_OPTION) setupBoard();
        else dispose(); // triggers windowClosed → reopens Dashboard
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MatchingCardGame(250.00).setVisible(true));
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
