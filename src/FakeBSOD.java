import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Random;

public class FakeBSOD extends JWindow {

    private final Random rng = new Random();
    private JLabel msg;
    private JLabel percentLabel;
    private int percent = 0;
    private Timer progressTimer;

    public FakeBSOD() {
        setAlwaysOnTop(true);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen);
        setLocation(0, 0);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(0, 32, 140));
        setContentPane(root);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(20, 70, 20, 70);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel face = new JLabel(":(");
        face.setFont(new Font("Segoe UI", Font.PLAIN, 120));
        face.setForeground(Color.WHITE);
        gbc.gridy = 0;
        root.add(face, gbc);

        msg = new JLabel(messageHtml(false));
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        msg.setForeground(Color.WHITE);
        gbc.gridy = 1;
        root.add(msg, gbc);

        percentLabel = new JLabel("0% complete");
        percentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        percentLabel.setForeground(Color.WHITE);
        gbc.gridy = 2;
        root.add(percentLabel, gbc);

        JLabel code = new JLabel("Stop code: VERY_BADLY_WRITTEN_CODE");
        code.setFont(new Font("Consolas", Font.PLAIN, 18));
        code.setForeground(new Color(180, 200, 255));
        gbc.gridy = 3;
        root.add(code, gbc);

        setVisible(true);
        startProgress();
    }

    private String messageHtml(boolean restarting) {
        String body = restarting
            ? "Restarting..."
            : "Your PC ran into a problem that Windows couldn't handle, and now it needs to restart.<br><br>"
              + "We're just collecting some error info about your code, then we'll restart for you.";
        return "<html><div style='width:760px'>" + body + "</div></html>";
    }

    private void startProgress() {
        progressTimer = new Timer(140, e -> {
            percent = Math.min(100, percent + 1 + rng.nextInt(3));
            percentLabel.setText(percent + "% complete");
            if (percent >= 100) {
                progressTimer.stop();
                finish();
            }
        });
        progressTimer.start();
    }

    private void finish() {
        msg.setText(messageHtml(true));
        Timer closeTimer = new Timer(2500, e -> {
            dispose();
            revealDesktop();
            System.exit(0);
        });
        closeTimer.setRepeats(false);
        closeTimer.start();
    }

    private static final String MUSIC_PATH = "sfx/relaxing_music.mp3";

    private void revealDesktop() {
        try {
            playRelaxingMusic();
            typeHelloWorldAfterMusic();
            focusDesktop();
        } catch (Exception ignored) {
        }
    }

    private void playRelaxingMusic() throws Exception {
        String absPath = new File(MUSIC_PATH).getAbsolutePath().replace("'", "''");
        String script =
                "Add-Type -AssemblyName PresentationCore; " +
                "$player = New-Object System.Windows.Media.MediaPlayer; " +
                "$player.Open([Uri]::new('" + absPath + "')); " +
                "$player.Play(); " +
                "Start-Sleep -Seconds 600";
        new ProcessBuilder("powershell", "-WindowStyle", "Hidden", "-Command", script).start();
    }

    private void typeHelloWorldAfterMusic() throws Exception {
        String[] args = new String[1];
        JavaFXCodeFactory.main(args);
        String script =
                "Start-Sleep -Seconds 18; " +
                "Add-Type -AssemblyName System.Windows.Forms; " +
                "Start-Process cmd; " +
                "Start-Sleep -Seconds 1; " +
                "$text = 'echo Hello World!'; " +
                "foreach ($ch in $text.ToCharArray()) { " +
                "[System.Windows.Forms.SendKeys]::SendWait([string]$ch); " +
                "Start-Sleep -Milliseconds 150 }; " +
                "[System.Windows.Forms.SendKeys]::SendWait('~')";
        new ProcessBuilder("powershell", "-WindowStyle", "Hidden", "-Command", script).start();
    }

    private void focusDesktop() throws AWTException, InterruptedException {
        // Win+D both shows the desktop and gives it keyboard focus
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_WINDOWS);
        robot.keyPress(KeyEvent.VK_D);
        robot.keyRelease(KeyEvent.VK_D);
        robot.keyRelease(KeyEvent.VK_WINDOWS);
        Thread.sleep(150);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FakeBSOD::new);
    }
}
