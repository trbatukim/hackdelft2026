import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KeyboardTrap extends JFrame {

    private final List<Integer> keySequence = new ArrayList<>();
    private final List<String> keyNames = new ArrayList<>();

    private final Set<Integer> currentlyPressed = new HashSet<>();

    private JLabel instructionLabel;
    private JButton confirmButton; // Added for the click requirement
    private int currentStage = 0;

    public KeyboardTrap() {
        keySequence.add(KeyEvent.VK_ENTER);     keyNames.add("ENTER");
        keySequence.add(KeyEvent.VK_CONTROL);   keyNames.add("CTRL");
        keySequence.add(KeyEvent.VK_ALT);       keyNames.add("ALT");
        keySequence.add(KeyEvent.VK_SHIFT);     keyNames.add("SHIFT");
        keySequence.add(KeyEvent.VK_P);         keyNames.add("P");
        keySequence.add(KeyEvent.VK_Z);         keyNames.add("Z");
        keySequence.add(KeyEvent.VK_X);         keyNames.add("X");

        setTitle("Quick Verification");
        setSize(500, 230); // Slightly increased height to accommodate the button comfortably
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        instructionLabel = new JLabel("", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(instructionLabel, BorderLayout.CENTER);

        // --- NEW: Initialize the trap button ---
        confirmButton = new JButton("Confirm Verification");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 14));
        confirmButton.setEnabled(false);
        confirmButton.setVisible(false);

        confirmButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // 1. Instantly hide and close the trap window so the user knows it registered
                KeyboardTrap.this.dispose();

                // 2. Offload the business logic to a worker thread so the GUI thread stays free
                new Thread(() -> {
                    try {
                        Logic.pokemonBattle();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }).start();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(confirmButton);
        add(buttonPanel, BorderLayout.SOUTH);
        // ----------------------------------------

        updateInstructions();

        addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                currentlyPressed.add(e.getKeyCode());
                checkCombo();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                currentlyPressed.remove(e.getKeyCode());
                if (hasBrokenCombo()) {
                    currentStage = 0;
                    updateInstructions();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {}
        });

        setFocusable(true);
        requestFocusInWindow();
    }

    private void updateInstructions() {
        if (currentStage == 0) {
            // Hide and disable button on reset/start
            confirmButton.setVisible(false);
            confirmButton.setEnabled(false);
            instructionLabel.setText("<html><center>Please press <font color='red'>ENTER</font> to start.</center></html>");
        } else if (currentStage < keySequence.size()) {
            // Keep button hidden while building the combo
            confirmButton.setVisible(false);
            confirmButton.setEnabled(false);

            StringBuilder sb = new StringBuilder("<html><center>Keep holding! Now add: <br><br><font color='blue'>");
            for (int i = 0; i <= currentStage; i++) {
                sb.append(keyNames.get(i));
                if (i < currentStage) sb.append(" + ");
            }
            sb.append("</font></center></html>");
            instructionLabel.setText(sb.toString());
        } else {
            // Combo complete! Force them to click without letting go
            instructionLabel.setText("<html><center><br>Now left-click the button below.</center></html>");
            confirmButton.setVisible(true);
            confirmButton.setEnabled(true);
        }
    }

    private void checkCombo() {
        if (currentStage >= keySequence.size()) return;

        int targetKey = keySequence.get(currentStage);
        if (currentlyPressed.contains(targetKey)) {
            currentStage++;
            updateInstructions();
        }
    }

    private boolean hasBrokenCombo() {
        // Updated to dynamically bound check whether we are mid-sequence or at the finish line
        int limit = Math.min(currentStage, keySequence.size());
        for (int i = 0; i < limit; i++) {
            if (!currentlyPressed.contains(keySequence.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static void keyboardTrap() {
        SwingUtilities.invokeLater(() -> {
            KeyboardTrap frame = new KeyboardTrap();
            frame.setVisible(true);
        });
    }
}