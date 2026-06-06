import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
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

    public static void readQRToFile(String imagePath, String outputPath) throws Exception {
        String content = readQRWithAnimation(imagePath);
        Files.writeString(Path.of(outputPath), content);
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
                // glow
                g2d.setColor(new Color(0, 255, 0, 60));
                g2d.fillRect(0, scanY[0] - 8, getWidth(), 16);
                // main line
                g2d.setColor(new Color(0, 255, 0, 220));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(0, scanY[0], getWidth(), scanY[0]);
            }
        };
        panel.setPreferredSize(new java.awt.Dimension(w, h));

        JFrame frame = new JFrame("SCANNING QR CODE...");
        frame.setBackground(Color.BLACK);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);

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

    public static void main(String[] args) throws Exception {
        String outputPath = "./qr.txt";
        String inputPath = "./qr.png";

        readQRToFile(inputPath, outputPath);
        System.out.println("QR content written to " + outputPath);
    }
}
