import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KeyboardTrap extends JFrame {

    // The escalating sequence of keys they must hold down
    private final List<Integer> keySequence = new ArrayList<>();
    private final List<String> keyNames = new ArrayList<>();

    // Tracks which keys are currently being held down
    private final Set<Integer> currentlyPressed = new HashSet<>();

    private JLabel instructionLabel;
    private int currentStage = 0;

    public KeyboardTrap() {
        keySequence.add(KeyEvent.VK_ENTER);     keyNames.add("ENTER");
        keySequence.add(KeyEvent.VK_CONTROL);   keyNames.add("CTRL");
        keySequence.add(KeyEvent.VK_ALT);       keyNames.add("ALT");
        keySequence.add(KeyEvent.VK_SHIFT);     keyNames.add("SHIFT");
        keySequence.add(KeyEvent.VK_SPACE);     keyNames.add("SPACE");
        keySequence.add(KeyEvent.VK_Z);         keyNames.add("Z");
        keySequence.add(KeyEvent.VK_X);         keyNames.add("X");

        setTitle("Quick Verification");
        setSize(500, 200);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        instructionLabel = new JLabel("", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(instructionLabel, BorderLayout.CENTER);

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
            instructionLabel.setText("<html><center>Please press <font color='red'>ENTER</font> to continue.</center></html>");
        } else if (currentStage < keySequence.size()) {
            StringBuilder sb = new StringBuilder("<html><center>Keep holding! Now add: <br><br><font color='blue'>");
            for (int i = 0; i <= currentStage; i++) {
                sb.append(keyNames.get(i));
                if (i < currentStage) sb.append(" + ");
            }
            sb.append("</font></center></html>");
            instructionLabel.setText(sb.toString());
        } else {
            instructionLabel.setText("<html><center><font color='green'>✔ Access Granted! You can let go now.</font></center></html>");
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
        for (int i = 0; i < currentStage; i++) {
            if (!currentlyPressed.contains(keySequence.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            KeyboardTrap frame = new KeyboardTrap();
            frame.setVisible(true);
        });
    }
}