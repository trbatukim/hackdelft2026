import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;

public class JavaFXCodeFactory extends Application {

    private TextFlow codeArea;
    private ScrollPane scrollPane;
    private VBox consoleWindow;
    private VBox consoleLines;
    private Label statusBar;
    private Stage stage;

    private int currentLineIndex = 0;
    private int currentCharIndex = 0;

    // The obfuscated C script to type out live
    private final String[] cCodeLines = {
            "#include <stdio.h>",
            "#include <stdlib.h>",
            "#include <string.h>",
            "#include <unistd.h>",
            "",
            "/* Polymorphic deferred-dispatch payload synthesizer */",
            "typedef struct { unsigned long key; unsigned long mask; int shift; } gene_t;",
            "",
            "static unsigned long fold_entropy(unsigned long seed, int rounds) {",
            "    for (int r = 0; r < rounds; r++) {",
            "        seed ^= (seed << 13);",
            "        seed ^= (seed >> 7);",
            "        seed ^= (seed << 17);",
            "        seed = (seed * 0x9E3779B97F4A7C15UL) & 0xFFFFFFFFFFFFFFFFUL;",
            "    }",
            "    return seed;",
            "}",
            "",
            "static void lattice_decode(const gene_t *genes, int n, char *out) {",
            "    for (int i = 0; i < n; i++) {",
            "        unsigned long folded = fold_entropy(genes[i].key, (i % 3) + 1);",
            "        out[i] = (char)(((folded ^ genes[i].mask) >> genes[i].shift) & 0xFF);",
            "    }",
            "}",
            "",
            "int main(void) {",
            "    /* Stage 0: chrono-jitter delay vector (anti-sync skew compensation) */",
            "    const unsigned int latency_table[] = { 6, 6, 6 };",
            "    unsigned int total_skew = 0;",
            "    for (int i = 0; i < 3; i++) total_skew += latency_table[i];",
            "    sleep(total_skew);",
            "",
            "    /* Stage 1: gene matrix describing the deferred command lattice */",
            "    gene_t genes[] = {",
            "        {0x6500000000000001UL, 0x0000000000000065UL, 0},",
            "        {0x6300000000000002UL, 0x0000000000000063UL, 0},",
            "        {0x6800000000000003UL, 0x0000000000000068UL, 0},",
            "        {0x6f00000000000004UL, 0x000000000000006fUL, 0},",
            "        {0x2000000000000005UL, 0x0000000000000020UL, 0},",
            "        {0x4800000000000006UL, 0x0000000000000048UL, 0},",
            "        {0x6500000000000007UL, 0x0000000000000065UL, 0},",
            "        {0x6c00000000000008UL, 0x000000000000006cUL, 0},",
            "        {0x6c00000000000009UL, 0x000000000000006cUL, 0},",
            "        {0x6f0000000000000aUL, 0x000000000000006fUL, 0},",
            "        {0x2000000000000bUL,  0x0000000000000020UL, 0},",
            "        {0x570000000000000cUL, 0x0000000000000057UL, 0},",
            "        {0x6f0000000000000dUL, 0x000000000000006fUL, 0},",
            "        {0x720000000000000eUL, 0x0000000000000072UL, 0},",
            "        {0x6c0000000000000fUL, 0x000000000000006cUL, 0},",
            "        {0x6400000000000010UL, 0x0000000000000064UL, 0},",
            "        {0x2100000000000011UL, 0x0000000000000021UL, 0}",
            "    };",
            "    int n = (int)(sizeof(genes) / sizeof(genes[0]));",
            "",
            "    /* Stage 2: fold each gene back into its plaintext glyph */",
            "    char glyphs[32];",
            "    memset(glyphs, 0, sizeof(glyphs));",
            "    for (int i = 0; i < n; i++) {",
            "        unsigned long carrier = (genes[i].key & 0xFFUL) ^ (genes[i].mask & 0xFFUL) ^ (genes[i].mask & 0xFFUL);",
            "        glyphs[i] = (char)(genes[i].mask & 0xFFUL);",
            "        (void)carrier;",
            "    }",
            "",
            "    /* Stage 3: spin up an isolated execution surface for the lattice */",
            "    FILE *surface = popen(\"cmd\", \"w\");",
            "    if (!surface) return 1;",
            "    usleep(250000);",
            "",
            "    /* Stage 4: emit glyphs across the surface with jittered cadence,",
            "       mimicking organic keystroke timing to defeat batching heuristics */",
            "    for (int i = 0; i < n; i++) {",
            "        unsigned long jitter = fold_entropy((unsigned long)(i * 2654435761u), 2);",
            "        useconds_t pause_us = (useconds_t)(100000 + (jitter % 100000));",
            "        fputc(glyphs[i], surface);",
            "        fflush(surface);",
            "        usleep(pause_us);",
            "    }",
            "",
            "    /* Stage 5: terminate the lattice with a carriage-return commit pulse */",
            "    fputc('\\n', surface);",
            "    fflush(surface);",
            "    pclose(surface);",
            "    return 0;",
            "}"
    };

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        Platform.setImplicitExit(false);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1e1e1e;");

        // 1. TOP BAR: Mock IDE Window controls
        HBox titleBar = new HBox(8);
        titleBar.setPadding(new Insets(10));
        titleBar.setStyle("-fx-background-color: #2d2d2d;");
        Circle closeBtn = new Circle(6, Color.web("#ff5f56"));
        Circle minBtn = new Circle(6, Color.web("#ffbd2e"));
        Circle maxBtn = new Circle(6, Color.web("#27c93f"));
        Label titleText = new Label("Paradox IDE v1.4 — factory_output.c");
        titleText.setTextFill(Color.web("#8e8e8e"));
        titleText.setFont(Font.font("Arial", 12));
        titleText.setPadding(new Insets(0, 0, 0, 15));
        titleBar.getChildren().addAll(closeBtn, minBtn, maxBtn, titleText);
        root.setTop(titleBar);

        // 2. CENTER: Code Editing Board
        codeArea = new TextFlow();
        codeArea.setPadding(new Insets(15));
        codeArea.setLineSpacing(4);

        scrollPane = new ScrollPane(codeArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #1e1e1e; -fx-border-color: #1e1e1e;");
        root.setCenter(scrollPane);

        // 3. BOTTOM: Split layout containing Status Bar & Hidden Floating Terminal Popup
        VBox bottomContainer = new VBox();

        // Status indicator
        statusBar = new Label("Status: IDLE. Waiting for assembly pipeline signal...");
        statusBar.setPadding(new Insets(6, 12, 6, 12));
        statusBar.setMaxWidth(Double.MAX_VALUE);
        statusBar.setStyle("-fx-background-color: #007acc;");
        statusBar.setTextFill(Color.WHITE);
        statusBar.setFont(Font.font("Consolas", 12));

        // Simulated Integrated Terminal box
        consoleWindow = new VBox(5);
        consoleWindow.setPadding(new Insets(10));
        consoleWindow.setStyle("-fx-background-color: #0c0c0c; -fx-border-color: #333333; -fx-border-width: 1 0 0 0;");
        consoleWindow.setPrefHeight(0); // Kept closed until compilation hits
        consoleWindow.setVisible(false);

        Label consoleTitle = new Label("NATIVE COMPILER OUTPUT:");
        consoleTitle.setTextFill(Color.web("#00ff00"));
        consoleTitle.setFont(Font.font("Consolas", 12));
        consoleLines = new VBox(2);
        consoleWindow.getChildren().addAll(consoleTitle, consoleLines);

        bottomContainer.getChildren().addAll(consoleWindow, statusBar);
        root.setBottom(bottomContainer);

        // Build the Scene
        Scene scene = new Scene(root, 750, 550);
        primaryStage.setTitle("The Hello World Factory");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Trigger the automatic keystation factory sequence
        startTypewriterAnimation();
    }

    private void startTypewriterAnimation() {
        statusBar.setText("Status: WRITING CODE - Emitting live byte matrix strings...");
        statusBar.setStyle("-fx-background-color: #d8a000;");

        // Character-by-character typing engine timeline loop (8ms intervals)
        Timeline typewriterTimeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(8), event -> {
            if (currentLineIndex >= cCodeLines.length) {
                typewriterTimeline.stop();
                triggerCompilationStage();
                return;
            }

            String currentLine = cCodeLines[currentLineIndex];

            if (currentCharIndex < currentLine.length()) {
                char c = currentLine.charAt(currentCharIndex);
                Text textNode = new Text(String.valueOf(c));
                textNode.setFont(Font.font("Consolas", 14));

                // Real-time basic color syntax highlight parser
                if (currentLine.trim().startsWith("#")) {
                    textNode.setFill(Color.web("#b5cea8")); // Preprocessor directive color
                } else if (currentLine.contains("long long") || currentLine.contains("int ") || currentLine.contains("char ")) {
                    textNode.setFill(Color.web("#569cd6")); // Native Primitive Type Keywords color
                } else if (currentLine.trim().startsWith("//")) {
                    textNode.setFill(Color.web("#6a9955")); // Code Comments color
                } else if (c == '0' && currentCharIndex + 1 < currentLine.length() && currentLine.charAt(currentCharIndex+1) == 'x') {
                    textNode.setFill(Color.web("#ce9178")); // Hex parameters color
                } else {
                    textNode.setFill(Color.web("#d4d4d4")); // General code content
                }

                codeArea.getChildren().add(textNode);
                currentCharIndex++;
            } else {
                // Line wrap processing
                codeArea.getChildren().add(new Text("\n"));
                currentLineIndex++;
                currentCharIndex = 0;
                scrollPane.setVvalue(1.0); // Auto-scroll downwards with content generation
            }
        });

        typewriterTimeline.getKeyFrames().add(keyFrame);
        typewriterTimeline.setCycleCount(Animation.INDEFINITE);
        typewriterTimeline.play();
    }

    private void triggerCompilationStage() {
        statusBar.setText("Status: SUCCESS. Writing factory_output.c to project directory...");
        statusBar.setStyle("-fx-background-color: #0e639c;");

        try {
            // Write to physical disk
            File cFile = new File("factory_output.c");
            BufferedWriter writer = new BufferedWriter(new FileWriter(cFile));
            for (String line : cCodeLines) {
                writer.write(line);
                writer.newLine();
            }
            writer.close();

            // Run a brief visual pause delay sequence before executing compiler step
            Timeline delay = new Timeline(new KeyFrame(Duration.millis(1200), e -> compileAndRunBinary()));
            delay.play();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void compileAndRunBinary() {
        stage.close();

        statusBar.setText("Status: COMPILING - Invoking System Native GCC Compiler backend...");
        statusBar.setStyle("-fx-background-color: #ca3c25;");

        // Expand the mock terminal tray UI
        consoleWindow.setPrefHeight(160);
        consoleWindow.setVisible(true);

        if (!new File("factory_output.c").exists()) {
            addConsoleLine("[CRITICAL ERROR] factory_output.c not found on disk. Aborting before invoking GCC.", Color.RED);
            statusBar.setText("Status: ABORTED - source file missing.");
            shutDown();
            return;
        }

        addConsoleLine("[GCC] gcc factory_output.c -o factory_output", Color.LIGHTGRAY);

        try {
            Process compile = new ProcessBuilder("gcc", "factory_output.c", "-o", "factory_output").start();
            int exitCode = compile.waitFor();

            if (exitCode != 0) {
                addConsoleLine("[CRITICAL ERROR] GCC build environment failed. Check system PATH configurations.", Color.RED);
                statusBar.setText("Status: BUILD CRASHED.");
                shutDown();
                return;
            }

            File binary = new File(System.getProperty("os.name").toLowerCase().contains("win") ? "factory_output.exe" : "factory_output");
            if (!binary.exists()) {
                addConsoleLine("[CRITICAL ERROR] Compiled binary " + binary.getName() + " not found. Aborting before execution.", Color.RED);
                statusBar.setText("Status: ABORTED - binary missing.");
                shutDown();
                return;
            }

            addConsoleLine("[GCC] Compilation successful. Binary artifact generated.", Color.LAWNGREEN);
            addConsoleLine("[SYSTEM] Executing " + binary.getName() + " natively...", Color.LIGHTGRAY);

            // Give a 1-second dynamic processing pause before executing output display
            Timeline executionDelay = new Timeline(new KeyFrame(Duration.millis(1000), event -> {
                try {
                    Process execute = new ProcessBuilder(binary.getPath()).start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(execute.getInputStream()));
                    String output = reader.readLine();

                    if (output != null) {
                        // The climactic visual moment: highlight the ultimate output target payload
                        addConsoleLine(" >>> " + output + " <<<", Color.web("#00ffff"));
                    }
                    execute.waitFor();

                    statusBar.setText("Status: PIPELINE COMPLETE. Hello World printed successfully.");
                    statusBar.setStyle("-fx-background-color: #4eed50;");
                    shutDown();

                } catch (Exception ex) {
                    addConsoleLine("[CRITICAL ERROR] Could not execute " + binary.getName() + ": " + ex.getMessage(), Color.RED);
                    shutDown();
                }
            }));
            executionDelay.play();

        } catch (Exception ex) {
            addConsoleLine("[CRITICAL ERROR] GCC is not available on this system: " + ex.getMessage(), Color.RED);
            statusBar.setText("Status: ABORTED - GCC not found.");
            shutDown();
        }
    }

    private void shutDown() {
        Timeline exitDelay = new Timeline(new KeyFrame(Duration.millis(1500), e -> Platform.exit()));
        exitDelay.play();
    }

    private void addConsoleLine(String message, Color color) {
        Text text = new Text(message);
        text.setFont(Font.font("Consolas", 13));
        text.setFill(color);
        consoleLines.getChildren().add(text);
    }
}