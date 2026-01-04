

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.CompoundBorder;

public class Hangman extends JFrame {

    private static final long serialVersionUID = 1L;
    public static final Color BG_COLOR = new Color(20, 15, 45); 
    public static final Color NEON_PINK = new Color(255, 60, 150); 
    public static final Color SOFT_BLUE = new Color(100, 180, 255); 
    public static final Color TEXT_WHITE = Color.WHITE;
    public static final Color BTN_BG = new Color(40, 30, 70); 
    private String[][] wordData = {
        {"TONI", "THE MAIN VLOGGER / MOMMY ONI"},
        {"TYRONIA", "TONI'S DAUGHTER / PRINCESS"},
        {"VINCE", "TITO ____ / TONI'S PARTNER"},
        {"FOWLER", "THE FAMOUS FAMILY SURNAME"},
        {"MOMMYONI", "TONI'S POPULAR NICKNAME"},
        {"FRESHBREAST", "TONI'S BEAUTY BRAND"},
        {"TORO", "CLASSIC GROUP: TO__ FAMILY"},
        {"AHHDADDY", "TONI'S TRENDING LINE"}
    };
    
    private String secretWord = "TONI"; 
    private String currentHint = "THE MAIN VLOGGER";
    
    private int mistakes = 0;
    private final int MAX_MISTAKES = 6;
    private int score = 0;
    private final int HINT_COST = 10;
    private boolean hintUnlocked = false;
    
    private ArrayList<String> guessedLetters = new ArrayList<>();
    private ArrayList<JButton> allButtons = new ArrayList<>();
    private JLabel lblWordDisplay;
    private JLabel lblStatus; 
    private JPanel keyboardPanel;
    private DrawingPanel gamePanel;
    private JTextArea txtHintDisplay;

    private JButton btnA, btnB, btnC, btnD, btnE, btnF, btnG, btnH, btnI, 
                    btnJ, btnK, btnL, btnM, btnN, btnO, btnP, btnQ, btnR, 
                    btnS, btnT, btnU, btnV, btnW, btnX, btnY, btnZ;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                Hangman frame = new Hangman();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Hangman() {
        setTitle("ARCADE HANGMAN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 800);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout(15, 15));
        contentPane.setBackground(BG_COLOR);
        contentPane.setBorder(new CompoundBorder(
            new LineBorder(NEON_PINK, 2), 
            new CompoundBorder(
                new LineBorder(BG_COLOR, 5),
                new CompoundBorder(
                    new LineBorder(NEON_PINK, 1), 
                    new EmptyBorder(20, 20, 20, 20)
                )
            )
        ));
        setContentPane(contentPane);
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BG_COLOR);   
        lblStatus = new JLabel("LIVES: 6 | SCORE: 0");
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 22)); 
        lblStatus.setForeground(SOFT_BLUE); 
        topBar.add(lblStatus, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setBackground(BG_COLOR);

        JButton btnHint = new JButton("BUY HINT (10pts)");
        styleSolidButton(btnHint, SOFT_BLUE); 
        btnHint.addActionListener(e -> {
            if (score >= HINT_COST) {
                score -= HINT_COST;
                hintUnlocked = true;
                btnHint.setEnabled(false);
                btnHint.setBackground(Color.GRAY);
                updateStatusLabel(); 
                updateRightPanelDisplay(); 
            } else {
                JOptionPane.showMessageDialog(this, "NOT ENOUGH POINTS!\nCorrect guesses earn 10 points.");
            }
        });
        actionPanel.add(btnHint);

        JButton btnReset = new JButton("NEW GAME");
        styleSolidButton(btnReset, NEON_PINK); 
        btnReset.addActionListener(e -> {
            startNewGame();
            btnHint.setEnabled(true);
            btnHint.setBackground(SOFT_BLUE);
        });
        actionPanel.add(btnReset);

        topBar.add(actionPanel, BorderLayout.EAST);
        contentPane.add(topBar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BG_COLOR);

        JLabel lblTopic = new JLabel("TOPIC: TORO FAMILY");
        lblTopic.setHorizontalAlignment(SwingConstants.CENTER);
        lblTopic.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 32)); 
        lblTopic.setForeground(TEXT_WHITE);
        lblTopic.setBorder(new EmptyBorder(0, 0, 15, 0));
        centerPanel.add(lblTopic, BorderLayout.NORTH);

        JPanel gameContainer = new JPanel(new BorderLayout(15, 0));
        gameContainer.setBackground(BG_COLOR);

        gamePanel = new DrawingPanel();
        gamePanel.setBackground(BG_COLOR); 
        gamePanel.setBorder(new LineBorder(NEON_PINK, 2)); 
        gameContainer.add(gamePanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(280, 0)); 
        rightPanel.setBackground(BG_COLOR);
        rightPanel.setBorder(new LineBorder(NEON_PINK, 2));
        
        JLabel lblHintTitle = new JLabel("HINT");
        lblHintTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblHintTitle.setForeground(SOFT_BLUE);
        lblHintTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblHintTitle.setBorder(new EmptyBorder(15, 10, 15, 10));
        rightPanel.add(lblHintTitle, BorderLayout.NORTH);
        
        txtHintDisplay = new JTextArea();
        txtHintDisplay.setEditable(false);
        txtHintDisplay.setLineWrap(true);
        txtHintDisplay.setWrapStyleWord(true);
        txtHintDisplay.setFont(new Font("Monospaced", Font.BOLD, 18)); 
        txtHintDisplay.setForeground(TEXT_WHITE);
        txtHintDisplay.setBackground(BG_COLOR);
        txtHintDisplay.setCaretColor(NEON_PINK);
        txtHintDisplay.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        rightPanel.add(txtHintDisplay, BorderLayout.CENTER);
        
        gameContainer.add(rightPanel, BorderLayout.EAST);
        centerPanel.add(gameContainer, BorderLayout.CENTER);

        lblWordDisplay = new JLabel("");
        lblWordDisplay.setHorizontalAlignment(SwingConstants.CENTER);
        lblWordDisplay.setFont(new Font("Monospaced", Font.BOLD, 48)); 
        lblWordDisplay.setForeground(TEXT_WHITE);
        lblWordDisplay.setBorder(new EmptyBorder(20, 0, 10, 0));
        centerPanel.add(lblWordDisplay, BorderLayout.SOUTH);

        contentPane.add(centerPanel, BorderLayout.CENTER);

        keyboardPanel = new JPanel();
        keyboardPanel.setBackground(BG_COLOR);
        keyboardPanel.setLayout(new GridLayout(3, 9, 10, 10)); 
        keyboardPanel.setBorder(new EmptyBorder(15, 50, 15, 50));
        contentPane.add(keyboardPanel, BorderLayout.SOUTH);

        ActionListener listener = e -> handleGuess((JButton) e.getSource());

        btnA = new JButton("A"); setupGridKey(btnA, listener); keyboardPanel.add(btnA);
        btnB = new JButton("B"); setupGridKey(btnB, listener); keyboardPanel.add(btnB);
        btnC = new JButton("C"); setupGridKey(btnC, listener); keyboardPanel.add(btnC);
        btnD = new JButton("D"); setupGridKey(btnD, listener); keyboardPanel.add(btnD);
        btnE = new JButton("E"); setupGridKey(btnE, listener); keyboardPanel.add(btnE);
        btnF = new JButton("F"); setupGridKey(btnF, listener); keyboardPanel.add(btnF);
        btnG = new JButton("G"); setupGridKey(btnG, listener); keyboardPanel.add(btnG);
        btnH = new JButton("H"); setupGridKey(btnH, listener); keyboardPanel.add(btnH);
        btnI = new JButton("I"); setupGridKey(btnI, listener); keyboardPanel.add(btnI);
        btnJ = new JButton("J"); setupGridKey(btnJ, listener); keyboardPanel.add(btnJ);
        btnK = new JButton("K"); setupGridKey(btnK, listener); keyboardPanel.add(btnK);
        btnL = new JButton("L"); setupGridKey(btnL, listener); keyboardPanel.add(btnL);
        btnM = new JButton("M"); setupGridKey(btnM, listener); keyboardPanel.add(btnM);
        btnN = new JButton("N"); setupGridKey(btnN, listener); keyboardPanel.add(btnN);
        btnO = new JButton("O"); setupGridKey(btnO, listener); keyboardPanel.add(btnO);
        btnP = new JButton("P"); setupGridKey(btnP, listener); keyboardPanel.add(btnP);
        btnQ = new JButton("Q"); setupGridKey(btnQ, listener); keyboardPanel.add(btnQ);
        btnR = new JButton("R"); setupGridKey(btnR, listener); keyboardPanel.add(btnR);
        btnS = new JButton("S"); setupGridKey(btnS, listener); keyboardPanel.add(btnS);
        btnT = new JButton("T"); setupGridKey(btnT, listener); keyboardPanel.add(btnT);
        btnU = new JButton("U"); setupGridKey(btnU, listener); keyboardPanel.add(btnU);
        btnV = new JButton("V"); setupGridKey(btnV, listener); keyboardPanel.add(btnV);
        btnW = new JButton("W"); setupGridKey(btnW, listener); keyboardPanel.add(btnW);
        btnX = new JButton("X"); setupGridKey(btnX, listener); keyboardPanel.add(btnX);
        btnY = new JButton("Y"); setupGridKey(btnY, listener); keyboardPanel.add(btnY);
        btnZ = new JButton("Z"); setupGridKey(btnZ, listener); keyboardPanel.add(btnZ);

        allButtons.add(btnA); allButtons.add(btnB); allButtons.add(btnC); allButtons.add(btnD);
        allButtons.add(btnE); allButtons.add(btnF); allButtons.add(btnG); allButtons.add(btnH);
        allButtons.add(btnI); allButtons.add(btnJ); allButtons.add(btnK); allButtons.add(btnL);
        allButtons.add(btnM); allButtons.add(btnN); allButtons.add(btnO); allButtons.add(btnP);
        allButtons.add(btnQ); allButtons.add(btnR); allButtons.add(btnS); allButtons.add(btnT);
        allButtons.add(btnU); allButtons.add(btnV); allButtons.add(btnW); allButtons.add(btnX);
        allButtons.add(btnY); allButtons.add(btnZ);

        if (!java.beans.Beans.isDesignTime()) {
            startNewGame();
        }
    }
    
    private void setupGridKey(JButton btn, ActionListener listener) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btn.setForeground(TEXT_WHITE); 
        btn.setBackground(BG_COLOR); 
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(NEON_PINK, 2)); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(listener);
    }
    
    private void styleSolidButton(JButton btn, Color bgColor) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(Color.WHITE); 
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 20, 8, 20)); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void startNewGame() {
        Random rand = new Random();
        int index = rand.nextInt(wordData.length);
        
        secretWord = wordData[index][0]; 
        currentHint = wordData[index][1]; 
        
        mistakes = 0;
        score = 0; 
        hintUnlocked = false; 
        guessedLetters.clear();
        
        updateStatusLabel();

        for (JButton btn : allButtons) {
            btn.setEnabled(true);
            btn.setBackground(BG_COLOR);
            btn.setBorder(new LineBorder(NEON_PINK, 2));
        }
        
        updateRightPanelDisplay();
        
        gamePanel.setMistakes(0); 
        updateWordDisplay();
    }

    private void handleGuess(JButton sourceBtn) {
        String letter = sourceBtn.getText();
        sourceBtn.setEnabled(false);
        guessedLetters.add(letter);

        if (secretWord.contains(letter)) {
            sourceBtn.setBorder(new LineBorder(SOFT_BLUE, 2));
            score += 10; 
            updateWordDisplay();
            updateStatusLabel();
            checkWin();
        } else {
            sourceBtn.setBackground(new Color(100, 20, 20));
            sourceBtn.setBorder(new LineBorder(Color.RED, 2));
            mistakes++;
            updateStatusLabel();
            gamePanel.setMistakes(mistakes);
            checkLoss();
        }
    }
    
    private void updateStatusLabel() {
        lblStatus.setText("LIVES: " + (MAX_MISTAKES - mistakes) + " | SCORE: " + score);
    }

    private void updateRightPanelDisplay() {
        if (hintUnlocked) {
            txtHintDisplay.setText(">> DECRYPTED <<\n\n" + currentHint);
            txtHintDisplay.setForeground(NEON_PINK);
        } else {
            txtHintDisplay.setText("\n>> LOCKED <<\n\nSPEND 10 POINTS\nFOR A HINT");
            txtHintDisplay.setForeground(Color.GRAY);
        }
    }

    private void updateWordDisplay() {
        if (lblWordDisplay == null) return;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < secretWord.length(); i++) {
            String c = String.valueOf(secretWord.charAt(i));
            if (guessedLetters.contains(c)) {
                sb.append(c).append(" ");
            } else {
                sb.append("_ ");
            }
        }
        lblWordDisplay.setText(sb.toString().trim());
    }

    private void checkWin() {
        if (!lblWordDisplay.getText().contains("_")) {
            JOptionPane.showMessageDialog(this, "HIGH SCORE ACHIEVED! YOU WON.");
            startNewGame();
        }
    }

    private void checkLoss() {
        if (mistakes >= MAX_MISTAKES) {
            JOptionPane.showMessageDialog(this, "GAME OVER");
            startNewGame();
        }
    }

    public static class DrawingPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private int currentMistakes = 0;

        public void setMistakes(int mistakes) {
            this.currentMistakes = mistakes;
            repaint();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
            
            int renderMistakes = currentMistakes;
            if (java.beans.Beans.isDesignTime()) {
                renderMistakes = 6; 
            }

            int w = getWidth();
            int h = getHeight();
            int centerX = w / 2;
            int bottomY = h - 80; 
            
            g2.setColor(NEON_PINK);
            g2.drawLine(centerX - 60, bottomY, centerX + 60, bottomY);     
            g2.drawLine(centerX, bottomY, centerX, bottomY - 220);        
            g2.drawLine(centerX, bottomY - 220, centerX - 80, bottomY - 220); 
            
            g2.setColor(SOFT_BLUE);
            g2.setStroke(new BasicStroke(3));
            g2.drawLine(centerX - 80, bottomY - 220, centerX - 80, bottomY - 180);
            
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(5));

            if (renderMistakes >= 1) g2.drawOval(centerX - 95, bottomY - 180, 30, 30);
            if (renderMistakes >= 2) g2.drawLine(centerX - 80, bottomY - 150, centerX - 80, bottomY - 90); 
            if (renderMistakes >= 3) g2.drawLine(centerX - 80, bottomY - 140, centerX - 110, bottomY - 110);
            if (renderMistakes >= 4) g2.drawLine(centerX - 80, bottomY - 140, centerX - 50, bottomY - 110);  
            if (renderMistakes >= 5) g2.drawLine(centerX - 80, bottomY - 90, centerX - 110, bottomY - 50);
            if (renderMistakes >= 6) {
                g2.drawLine(centerX - 80, bottomY - 90, centerX - 50, bottomY - 50);
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(centerX - 90, bottomY - 170, centerX - 86, bottomY - 164);
                g2.drawLine(centerX - 86, bottomY - 170, centerX - 90, bottomY - 164);
                g2.drawLine(centerX - 74, bottomY - 170, centerX - 70, bottomY - 164);
                g2.drawLine(centerX - 70, bottomY - 170, centerX - 74, bottomY - 164);
            }
        }
    }
}