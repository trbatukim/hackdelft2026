import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
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

    private int currentLineIndex = 0;
    private int currentCharIndex = 0;

    // The obfuscated C script to type out live
    private final String[] cCodeLines = {
            "#include <stdio.h>",
            "#include <unistd.h>",
            "",
            "int main() {",
            "    // Hexadecimal character matrix (ASCII backwards)",
            "    long long p1 = 0x6f6c6c6548ULL;",
            "    long long p2 = 0x646c726f5720ULL;",
            "    char s[14];",
            "    ",
            "    // Byte-extraction via bitwise pointer masking",
            "    for(int i = 0; i < 5; i++) {",
            "        s[i] = (char)((p1 >> (i * 8)) & 0xFF);",
            "    }",
            "    for(int i = 0; i < 7; i++) {",
            "        s[5 + i] = (char)((p2 >> (i * 8)) & 0xFF);",
            "    }",
            "    s[12] = '!';",
            "    s[13] = '\\0';",
            "    ",
            "    // Safe pipeline flush to system stream",
            "    char *ptr = s;",
            "    while(*ptr) {",
            "        putchar(*ptr++);",
            "        fflush(stdout);",
            "        usleep(30000);",
            "    }",
            "    putchar('\\n');",
            "    return 0;",
            "}"
    };

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
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
        statusBar.setText("Status: COMPILING - Invoking System Native GCC Compiler backend...");
        statusBar.setStyle("-fx-background-color: #ca3c25;");

        // Expand the mock terminal tray UI
        consoleWindow.setPrefHeight(160);
        consoleWindow.setVisible(true);

        addConsoleLine("[GCC] gcc factory_output.c -o factory_output", Color.LIGHTGRAY);

        try {
            Process compile = new ProcessBuilder("gcc", "factory_output.c", "-o", "factory_output").start();
            int exitCode = compile.waitFor();

            if (exitCode != 0) {
                addConsoleLine("[CRITICAL ERROR] GCC build environment failed. Check system PATH configurations.", Color.RED);
                statusBar.setText("Status: BUILD CRASHED.");
                return;
            }

            addConsoleLine("[GCC] Compilation successful. Binary artifact generated.", Color.LAWNGREEN);
            addConsoleLine("[SYSTEM] Executing ./factory_output natively...", Color.LIGHTGRAY);

            // Give a 1-second dynamic processing pause before executing output display
            Timeline executionDelay = new Timeline(new KeyFrame(Duration.millis(1000), event -> {
                try {
                    Process execute = new ProcessBuilder("./factory_output").start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(execute.getInputStream()));
                    String output = reader.readLine();

                    if (output != null) {
                        // The climactic visual moment: highlight the ultimate output target payload
                        addConsoleLine(" >>> " + output + " <<<", Color.web("#00ffff"));
                    }
                    execute.waitFor();

                    statusBar.setText("Status: PIPELINE COMPLETE. Hello World printed successfully.");
                    statusBar.setStyle("-fx-background-color: #4eed50;");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }));
            executionDelay.play();

        } catch (Exception ex) {
            addConsoleLine("[CRITICAL EXCEPTION] " + ex.getMessage(), Color.RED);
            ex.printStackTrace();
        }
    }

    private void addConsoleLine(String message, Color color) {
        Text text = new Text(message);
        text.setFont(Font.font("Consolas", 13));
        text.setFill(color);
        consoleLines.getChildren().add(text);
    }
}