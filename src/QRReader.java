import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class QRReader {

    public static String readQR(String imagePath) throws Exception {
        BufferedImage image = ImageIO.read(new File(imagePath));
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        return new MultiFormatReader().decode(bitmap).getText();
    }

    private static String readQRWithAnimation(String imagePath) throws Exception {
        BufferedImage qrImage = ImageIO.read(new File(imagePath));
        int w = Math.max(qrImage.getWidth(), 300);
        int h = Math.max(qrImage.getHeight(), 300);

        int[] scanY = {0};

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(qrImage, 0, 0, getWidth(), getHeight(), null);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 255, 0, 60));
                g2d.fillRect(0, scanY[0] - 8, getWidth(), 16);
                g2d.setColor(new Color(0, 255, 0, 220));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(0, scanY[0], getWidth(), scanY[0]);
            }
        };
        panel.setPreferredSize(new java.awt.Dimension(w, h));

        // --- UPDATED WINDOW FORCING LOGIC ---
        JFrame frame = new JFrame("SCANNING QR CODE...");
        frame.setBackground(Color.BLACK);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // 1. Force it to the absolute top of the OS window stack
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);

        // 2. Smash through the focus lock policies
        frame.toFront();
        frame.requestFocus();

        // 3. Optional: Once it has successfully stolen focus, you can disable
        // always-on-top so the user can move other windows again if they want.
        // frame.setAlwaysOnTop(false);
        // ------------------------------------

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                return readQR(imagePath);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Timer timer = new Timer(16, e -> {
            scanY[0] = (scanY[0] + 4) % h;
            panel.repaint();
        });
        timer.start();

        long start = System.currentTimeMillis();
        String result;
        try {
            result = future.get();
        } catch (ExecutionException e) {
            throw (Exception) e.getCause();
        }
        long elapsed = System.currentTimeMillis() - start;
        if (elapsed < 2000) Thread.sleep(2000 - elapsed);

        timer.stop();
        frame.dispose();

        return result;
    }

    static class QRTriggerHandler implements HttpHandler {
        private final String qrImagePath;
        private final String outputPath;

        QRTriggerHandler(String qrImagePath, String outputPath) {
            this.qrImagePath = qrImagePath;
            this.outputPath = outputPath;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            boolean isShown = query != null && query.contains("qr=shown");

            byte[] response = "OK".getBytes();
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }

            new Thread(() -> {
                try {
                    // 1. Read the QR code and wait for the scanning animation to complete
                    String content = readQRWithAnimation(qrImagePath);
                    Files.writeString(Path.of(outputPath), content);
                    System.out.println("[QRReader] QR content written: " + content);

                    // 2. FORCE SCREEN FOCUS FORWARD RIGHT HERE
                    forceWindowToFront();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        private void forceWindowToFront() {
            // Run the window-forcing sequence back on the Swing Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                // 1. Create a dummy invisible frame to act as a focus battering ram
                JFrame focusBuster = new JFrame();
                focusBuster.setType(JFrame.Type.UTILITY); // Hides it from the taskbar
                focusBuster.setUndecorated(true);
                focusBuster.setSize(1, 1);
                focusBuster.setLocationRelativeTo(null); // Center it

                // 2. Force it to the absolute top layer of the OS window stack
                focusBuster.setAlwaysOnTop(true);
                focusBuster.setVisible(true);

                // 3. Request focus aggressively
                focusBuster.toFront();
                focusBuster.requestFocus();

                // 4. Trigger the popup anchored DIRECTLY to our top-level invisible frame.
                // This forces the OS to pop the dialog directly into the user's active viewport.
                JOptionPane.showMessageDialog(focusBuster,
                        "CAPTCHA Verification Successful!\nReturning to game context.",
                        "Verification Complete",
                        JOptionPane.INFORMATION_MESSAGE);

                // 5. Clean up the dummy frame resource
                focusBuster.dispose();

                // 6. Final command shell push to wake up the default browser application instance
                String os = System.getProperty("os.name").toLowerCase();
                try {
                    if (os.contains("win")) {
                        new ProcessBuilder("cmd", "/c", "start", "").start();
                    } else if (os.contains("mac")) {
                        new ProcessBuilder("open", "-a", "Google Chrome").start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

        public static void startServer(String qrImagePath, String outputPath) throws Exception {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/trigger", new QRTriggerHandler(qrImagePath, outputPath));
            server.setExecutor(null);
            server.start();
            System.out.println("[QRReader] Listening on http://localhost:8080");
            System.out.println("[QRReader] Waiting for captcha QR trigger...");
        }

        public static void main(String[] args) throws Exception {
            String qrImagePath = "./fake_captcha/imgs/qr.png";
            String outputPath = "./qr.txt";
            startServer(qrImagePath, outputPath);
            Thread.currentThread().join();
        }
    }

