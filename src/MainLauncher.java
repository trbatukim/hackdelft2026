public class MainLauncher {
    public static void main(String[] args) {
        // This tricks the JVM into skipping the modular runtime component check
        JavaFXCodeFactory.main(args);
    }
}