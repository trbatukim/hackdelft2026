import java.awt.*;
import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        File pokemonBattle = new File("./pokemon_battle/pokemon_battle.html");
        Scanner sc = new Scanner(System.in);
        String original = "Hello world!";
        int key = 42;

        String encrypted = encrypt(original, key);

        int input = 0;
        boolean valid = false;

        Desktop.getDesktop().open(pokemonBattle);

        while (!valid){
            try {
                input = sc.nextInt();
                valid = true;
                sc.close();
            } catch (Exception e) {
                valid = false;
                sc.next();
            }
        }

        String decrypted = decrypt(encrypted, input);

        System.out.println("Original:  " + original);
        System.out.println("Encrypted: " + encrypted);
        System.out.println("Decrypted: " + decrypted);
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
