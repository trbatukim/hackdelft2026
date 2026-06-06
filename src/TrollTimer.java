import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class TrollTimer extends JFrame {

    private JLabel timerLabel;
    private Timer swingTimer;

    private int currentTenths = 100; // Starts at 10.0 seconds
    private boolean countingDown = true;
    private boolean fakeOutDone = false;
    private boolean flashState = false;

    public TrollTimer() {
        setTitle("Self-Destruct Sequence");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);

        timerLabel = new JLabel("10.0", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 48));
        timerLabel.setForeground(Color.GREEN);
        add(timerLabel, BorderLayout.CENTER);

        swingTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logicTick();
            }
        });

        swingTimer.start();
    }

    private void logicTick() {
        if (countingDown) {
            currentTenths--;
            if (currentTenths <= 50 && !fakeOutDone) {
                countingDown = false;
            }
        } else {
            currentTenths++;
            if (currentTenths >= 80) {
                countingDown = true;
                fakeOutDone = true;
            }
        }

        double secondsLeft = currentTenths / 10.0;
        int newFontSize = 48 + (int)((10.0 - secondsLeft) * 6);
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, Math.max(48, newFontSize)));

        if (secondsLeft <= 3.0 && secondsLeft > 0) {
            flashState = !flashState;
            timerLabel.setForeground(flashState ? Color.RED : Color.BLACK);
        } else if (secondsLeft <= 5.0 && secondsLeft > 0) {
            if (currentTenths % 2 == 0) {
                flashState = !flashState;
                timerLabel.setForeground(flashState ? Color.ORANGE : Color.BLACK);
            }
        } else {
            timerLabel.setForeground(Color.GREEN);
        }

        timerLabel.setText(String.format("%.1f", secondsLeft));

        if (currentTenths <= 0) {
            timerLabel.setText("0.0");
            timerLabel.setForeground(Color.RED);
            swingTimer.stop();

            // Trigger the Vine Boom!
            triggerSound();
        }
    }

    private void triggerSound() {
        try {
            File soundFile = new File("vine-boom.wav");

            if (soundFile.exists()) {
                AudioInputStream rawStream = AudioSystem.getAudioInputStream(soundFile);
                AudioFormat baseFormat = rawStream.getFormat();

                // Force conversion to a standard 16-bit PCM format that Java natively loves
                AudioFormat decodedFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(),
                        16, // 16-bit
                        baseFormat.getChannels(),
                        baseFormat.getChannels() * 2,
                        baseFormat.getSampleRate(),
                        false
                );

                AudioInputStream audioStream = AudioSystem.getAudioInputStream(decodedFormat, rawStream);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start(); // Play the Vine Boom

            } else {
                System.out.println("Error: 'vine-boom.wav' not found in project root folder.");
                Toolkit.getDefaultToolkit().beep();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toolkit.getDefaultToolkit().beep();
        }

        JOptionPane.showMessageDialog(this, "💥 BOOM! Just kidding.", "Boom", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TrollTimer().setVisible(true);
        });
    }
}