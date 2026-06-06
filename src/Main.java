import java.awt.*;
import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        final File pokemonBattle = new File("./pokemon_battle/pokemon_battle.html");
        Scanner sc = new Scanner(System.in);
        final String original = "Hello world!";
        int key = 29072006;

        final String encrypted = encrypt(original, key);

        int input = 0;
        boolean valid = false;

        Desktop.getDesktop().open(pokemonBattle);
        BattleDetector.listen();

        System.out.println("THIS IS A RANSOMWARE ATTACK!\n" +
                "YOUR PRECIOUS COMMAND HAS BEEN ENCRYPTED" +
                "\n\nENCRYPTED VALUE: " + encrypted);

        while (!valid){
            try {
                System.out.print("Please enter the decryption key to save your computer: ");
                input = sc.nextInt();
                valid = true;
                sc.close();
            } catch (Exception e) {
                valid = false;
                sc.next();
            }
        }

        KeyboardTrap.keyboardTrap();

        System.out.println(decrypt(encrypted, input));
    }

    public static String encrypt(String text, int key) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            result.append((char) (c ^ key));
        }
        return result.toString();
    }

    public static String decrypt(String encrypted, int key) {
        return encrypt(encrypted, key); // Same operation!
    }
}
