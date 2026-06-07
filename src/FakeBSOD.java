import javax.swing.*;
import java.awt.*;
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
            System.exit(0);
        });
        closeTimer.setRepeats(false);
        closeTimer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FakeBSOD::new);
    }
}
