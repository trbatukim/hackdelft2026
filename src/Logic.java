import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Scanner;

public class Logic {
    public static void print(String param) throws Exception {
        KeyboardTrap.keyboardTrap();
        QRReader.listen();
        Desktop.getDesktop().open(new File("./batch/batch.bat"));
    }

    public static void pokemonBattle() throws Exception {
        File relativeFile = new File("pokemon_battle/pokemon_battle.html");
        final File pokemonBattle = relativeFile.getCanonicalFile();

        if (!pokemonBattle.exists()) {
            System.out.println("ERROR: The file does not exist!");
            return;
        }

        // --- THE OS TRICK ---
        // Showing a native dialog resets the Windows focus restrictions.
        // Once the user clicks "OK", the OS treats the next execution command as a primary user action.
        JOptionPane.showMessageDialog(null,
                "Verification Complete!\nClick OK to print 'Hello World!'.",
                "System Redirect",
                JOptionPane.INFORMATION_MESSAGE);

        String os = System.getProperty("os.name").toLowerCase();
        String filePath = pokemonBattle.getAbsolutePath();

        try {
            if (os.contains("win")) {
                // Combined with the popup, this will now force Chrome to pull to the front
                new ProcessBuilder("cmd", "/c", "start", "", filePath).start();
            } else if (os.contains("mac")) {
                new ProcessBuilder("open", "-a", "Google Chrome", filePath).start();
            } else {
                new ProcessBuilder("xdg-open", filePath).start();
            }
        } catch (Exception e) {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(pokemonBattle.toURI());
            }
        }

        BattleDetector.listen();
    }

    public static String encrypt(String text, int key) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            result.append((char) (c ^ key));
        }
        return result.toString();
    }

    public static String decrypt(String encrypted, int key) {
        return encrypt(encrypted, key);
    }
}
