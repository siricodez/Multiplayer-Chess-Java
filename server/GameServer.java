package server;

import java.io.*;
import java.net.*;

public class GameServer {
    private static final int PORT = 5555;

    public static void main(String[] args) throws IOException {
        System.out.println("♟  Chess Server starting on port " + PORT + "...");
        ServerSocket serverSocket = new ServerSocket(PORT);

        System.out.println("Waiting for Player 1 (White)...");
        Socket p1 = serverSocket.accept();
        System.out.println("Player 1 connected: " + p1.getInetAddress());

        System.out.println("Waiting for Player 2 (Black)...");
        Socket p2 = serverSocket.accept();
        System.out.println("Player 2 connected: " + p2.getInetAddress());

        // Tell each player who they are
        PrintWriter out1 = new PrintWriter(p1.getOutputStream(), true);
        PrintWriter out2 = new PrintWriter(p2.getOutputStream(), true);
        out1.println("COLOR:WHITE");
        out2.println("COLOR:BLACK");
        System.out.println("Both players connected. Game starting!");

        // Relay moves between players in a new thread
        new Thread(() -> relay(p1, p2, out2, "White")).start();
        new Thread(() -> relay(p2, p1, out1, "Black")).start();
    }

    private static void relay(Socket from, Socket to, PrintWriter toWriter, String playerName) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(from.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[" + playerName + "] -> " + line);
                toWriter.println(line);
            }
        } catch (IOException e) {
            System.out.println(playerName + " disconnected.");
        }
    }
}