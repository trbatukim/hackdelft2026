import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.URI;

public class DoomInterrupter {

    private static final String YOUTUBE_URL = "https://youtu.be/FRNdidcy7Y4?si=RvRA7u9ZN82cusAC";

    public static void main(String[] args) throws Exception {
        Thread.sleep(10000);

        JWindow popup = new JWindow();
        JLabel label = new JLabel("This is pretty boring right. How about something more fun...", SwingConstants.CENTER);
        label.setFont(new Font("Monospaced", Font.BOLD, 32));
        label.setForeground(Color.WHITE);
        label.setOpaque(true);
        label.setBackground(Color.BLACK);
        label.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        popup.getContentPane().add(label);
        popup.pack();
        popup.setAlwaysOnTop(true);
        popup.setLocationRelativeTo(null);
        popup.setVisible(true);

        Thread.sleep(5000);

        popup.dispose();
        Desktop.getDesktop().browse(new URI(YOUTUBE_URL));

        Thread.sleep(9000);

        Robot robot = new Robot();

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int cx = screen.width / 2;
        int cy = screen.height / 2;
        robot.mouseMove(cx, cy);
        robot.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);

        Thread.sleep(500);

        robot.keyPress(KeyEvent.VK_F);
        robot.keyRelease(KeyEvent.VK_F);

        Thread.sleep(12000);

        robot.keyPress(KeyEvent.VK_F);
        robot.keyRelease(KeyEvent.VK_F);
        robot.keyPress(KeyEvent.VK_K);
        robot.keyRelease(KeyEvent.VK_K);

        popup = new JWindow();
        label = new JLabel("Let's check how that DOOM bot is doing", SwingConstants.CENTER);
        label.setFont(new Font("Monospaced", Font.BOLD, 32));
        label.setForeground(Color.WHITE);
        label.setOpaque(true);
        label.setBackground(Color.BLACK);
        label.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        popup.getContentPane().add(label);
        popup.pack();
        popup.setAlwaysOnTop(true);
        popup.setLocationRelativeTo(null);
        popup.setVisible(true);

        Thread.sleep(5000);

        popup.dispose();

        for (int i = 0; i < 3; i++) {
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_W);
            robot.keyRelease(KeyEvent.VK_W);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            Thread.sleep(500);
        }
    }
}
