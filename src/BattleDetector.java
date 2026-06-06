import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class BattleDetector {

    public static void listen() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8765), 0);
        server.createContext("/battle-end", new BattleEndHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("[BattleDetector] Listening on http://localhost:8765");
        System.out.println("[BattleDetector] Waiting for victory audio signal...\n");

        Thread clicker = new Thread(() -> {
            try {
                Thread.sleep(2000);
                Robot robot = new Robot();
                Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                robot.mouseMove(screen.width / 2, screen.height / 2);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                System.out.println("[BattleDetector] Clicked to unlock browser audio.");
            } catch (Exception e) {
                System.out.println("[BattleDetector] Auto-click failed: " + e.getMessage());
            }
        });
        clicker.setDaemon(true);
        clicker.start();
    }

    static class BattleEndHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            boolean isWin = query != null && query.contains("result=win");

            byte[] response = "OK".getBytes();
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }

            if (isWin) {
                System.out.println("╔══════════════════════════════════════╗");
                System.out.println("║   VICTORY AUDIO DETECTED!            ║");
                System.out.println("║   The Pokémon battle has been won!   ║");
                System.out.println("║   Victory jingle is now playing...   ║");
                System.out.println("╚══════════════════════════════════════╝");
            }
        }
    }
}
