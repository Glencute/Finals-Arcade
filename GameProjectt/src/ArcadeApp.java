import javax.swing.SwingUtilities;

public class ArcadeApp {
    public static void main(String[] args) {
        // Start the application by launching the Welcome Screen
        SwingUtilities.invokeLater(() -> {
            new WelcomeScreen();
        });
    }
}