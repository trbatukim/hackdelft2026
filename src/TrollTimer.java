import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class TrollTimer extends JFrame {

    private JLabel timerLabel;
    private Timer swingTimer;

    private int currentTenths = 100; // Starts at 10.0 seconds
    private boolean countingDown = true;
    private boolean fakeOutDone = false;
    private boolean flashState = false;

    // Reuse a single clip instance to stop audio leaks and engine jams
    private Clip alarmClip;
    private FloatControl alarmVolumeControl;

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

        // Pre-load the alarm asset so we don't open files 10 times a second
        initAlarmClip();

        swingTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logicTick();
            }
        });

        swingTimer.start();
    }

    private void initAlarmClip() {
        try {
            File alarmFile = new File("alarm.wav");
            if (!alarmFile.exists()) return;

            AudioInputStream rawStream = AudioSystem.getAudioInputStream(alarmFile);
            AudioFormat baseFormat = rawStream.getFormat();
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
                    baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false
            );

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(decodedFormat, rawStream);
            alarmClip = AudioSystem.getClip();
            alarmClip.open(audioStream);

            if (alarmClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                alarmVolumeControl = (FloatControl) alarmClip.getControl(FloatControl.Type.MASTER_GAIN);
            }
        } catch (Exception e) {
            System.out.println("Could not pre-load alarm.wav");
        }
    }

    private void logicTick() {
        if (currentTenths <= 0) {
            return;
        }

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

        // --- ALARM CADENCE ---
        if (secondsLeft <= 3.0 && secondsLeft > 0) {
            flashState = !flashState;
            timerLabel.setForeground(flashState ? Color.RED : Color.BLACK);

            if (currentTenths % 3 == 0) {
                playTickAlarm(secondsLeft);
            }
        } else if (secondsLeft <= 5.0 && secondsLeft > 0) {
            if (currentTenths % 2 == 0) {
                flashState = !flashState;
                timerLabel.setForeground(flashState ? Color.ORANGE : Color.BLACK);
                playTickAlarm(secondsLeft);
            }
        } else {
            timerLabel.setForeground(Color.GREEN);
            if (currentTenths % 10 == 0 && currentTenths > 0) {
                playTickAlarm(secondsLeft);
            }
        }

        timerLabel.setText(String.format("%.1f", secondsLeft));

        // --- END CONDITION ---
        if (currentTenths <= 0) {
            timerLabel.setText("0.0");
            timerLabel.setForeground(Color.RED);

            // 1. Instantly halt the Swing timer loop
            swingTimer.stop();

            // 2. FORCE STOP the ticking alarm immediately so it goes dead silent
            if (alarmClip != null && alarmClip.isRunning()) {
                alarmClip.stop();
            }

            // 3. Fire the vine boom jump-scare safely on its own thread
            triggerVineBoom();
        }
    }

    private void playTickAlarm(double secondsLeft) {
        if (alarmClip == null) return;

        try {
            // If it's currently playing, cut it short to restart it cleanly
            if (alarmClip.isRunning()) {
                alarmClip.stop();
            }

            // Adjust volume smoothly
            if (alarmVolumeControl != null) {
                float maxVolume = 0.0f;
                float minVolume = -12.0f;
                float volumePercent = (float) ((10.0 - secondsLeft) / 10.0);
                float targetGain = minVolume + (volumePercent * (maxVolume - minVolume));

                targetGain = Math.max(alarmVolumeControl.getMinimum(), Math.min(alarmVolumeControl.getMaximum(), targetGain));
                alarmVolumeControl.setValue(targetGain);
            }

            alarmClip.setFramePosition(0); // Rewind to start
            alarmClip.start();
        } catch (Exception e) {
            // Catch exceptions silently to maintain performance
        }
    }

    private void triggerVineBoom() {
        new Thread(() -> {
            try {
                File soundFile = new File("vine-boom.wav");

                if (soundFile.exists()) {
                    AudioInputStream rawStream = AudioSystem.getAudioInputStream(soundFile);
                    AudioFormat baseFormat = rawStream.getFormat();
                    AudioFormat decodedFormat = new AudioFormat(
                            AudioFormat.Encoding.PCM_SIGNED,
                            baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
                            baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false
                    );

                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(decodedFormat, rawStream);
                    Clip boomClip = AudioSystem.getClip();
                    boomClip.open(audioStream);

                    // Start playback immediately
                    boomClip.start();

                } else {
                    System.out.println("Error: 'vine-boom.wav' not found.");
                    Toolkit.getDefaultToolkit().beep();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toolkit.getDefaultToolkit().beep();
            }

            // Show popup without bottlenecking the clip stream
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(TrollTimer.this, "💥 BOOM! Just kidding.", "Boom", JOptionPane.WARNING_MESSAGE);
            });
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TrollTimer().setVisible(true);
        });
    }
}