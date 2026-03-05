package client;

import game.*;
import game.Piece.Color;
import utils.Display;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameClient {
    private static Board board = new Board();
    private static Color myColor;
    private static Color currentTurn = Color.WHITE;
    private static PrintWriter out;

    public static void main(String[] args) throws IOException {
        String host = args.length > 0 ? args[0] : "localhost";

        System.out.println("♟  Connecting to Chess Server at " + host + ":5555...");
        Socket socket = new Socket(host, 5555);
        out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // First message tells us our color
        String colorMsg = in.readLine();
        myColor = colorMsg.contains("WHITE") ? Color.WHITE : Color.BLACK;
        System.out.println("You are playing as: " + myColor);
        System.out.println("White moves first.\n");

        // Listen for opponent moves in background thread
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    handleIncoming(line);
                }
            } catch (IOException e) {
                System.out.println("Connection lost.");
            }
        }).start();

        // Our turn input loop
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (currentTurn == myColor) {
                Display.printBoard(board);
                System.out.println("Your turn (" + myColor + ")");

                if (MoveValidator.isCheckmate(board, myColor)) {
                    System.out.println("You are in CHECKMATE. You lose!");
                    break;
                }
                if (MoveValidator.isStalemate(board, myColor)) {
                    System.out.println("STALEMATE. Draw!");
                    break;
                }
                if (board.isInCheck(myColor)) {
                    System.out.println("⚠️  You are in CHECK!");
                }

                System.out.print("Enter move (e.g. e2 e4) or 'resign': ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("resign")) {
                    out.println("RESIGN");
                    System.out.println("You resigned. Game over.");
                    break;
                }

                String[] parts = input.split("\\s+");
                if (parts.length != 2) {
                    System.out.println("Invalid format. Use: e2 e4");
                    continue;
                }

                int[] from = Display.parseSquare(parts[0]);
                int[] to   = Display.parseSquare(parts[1]);

                if (from == null || to == null) {
                    System.out.println("Invalid square. Use letters a-h and numbers 1-8.");
                    continue;
                }

                Piece p = board.getPiece(from[0], from[1]);
                if (p == null || p.getColor() != myColor) {
                    System.out.println("No your piece at " + parts[0]);
                    continue;
                }

                if (!MoveValidator.isLegal(board, from[0], from[1], to[0], to[1])) {
                    System.out.println("Illegal move.");
                    continue;
                }

                // Apply move locally
                board = board.applyMove(from[0], from[1], to[0], to[1]);
                currentTurn = (currentTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;

                // Send to server
                out.println("MOVE:" + parts[0] + ":" + parts[1]);

            } else {
                // Waiting for opponent
                System.out.println("Waiting for opponent...");
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            }
        }
        socket.close();
    }

    private static synchronized void handleIncoming(String msg) {
        if (msg.startsWith("MOVE:")) {
            String[] parts = msg.split(":");
            int[] from = Display.parseSquare(parts[1]);
            int[] to   = Display.parseSquare(parts[2]);
            if (from != null && to != null) {
                board = board.applyMove(from[0], from[1], to[0], to[1]);
                currentTurn = (currentTurn == Color.WHITE) ? Color.BLACK : Color.WHITE;
                System.out.println("\nOpponent moved: " + parts[1] + " → " + parts[2]);

                // Check if WE are now in check/checkmate after opponent's move
                if (MoveValidator.isCheckmate(board, myColor)) {
                    Display.printBoard(board);
                    System.out.println("CHECKMATE! You lose.");
                    System.exit(0);
                }
                if (MoveValidator.isStalemate(board, myColor)) {
                    Display.printBoard(board);
                    System.out.println("STALEMATE. Draw.");
                    System.exit(0);
                }
                if (board.isInCheck(myColor)) {
                    System.out.println("⚠️  You are in CHECK!");
                }
            }
        } else if (msg.equals("RESIGN")) {
            System.out.println("\nOpponent resigned. You win! 🎉");
            System.exit(0);
        }
    }
}