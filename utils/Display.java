package utils;

import game.Board;
import game.Piece;
import game.Piece.Color;

public class Display {

    // ANSI colors
    private static final String RESET  = "\u001B[0m";
    private static final String DARK_BG  = "\u001B[48;5;94m";   // brown
    private static final String LIGHT_BG = "\u001B[48;5;229m";  // cream
    private static final String WHITE_FG = "\u001B[97m";
    private static final String BLACK_FG = "\u001B[30m";
    private static final String HIGHLIGHT = "\u001B[48;5;226m"; // yellow highlight

    public static void printBoard(Board board, int[] selectedSquare) {
        System.out.println();
        System.out.println("    a   b   c   d   e   f   g   h");
        System.out.println("  +---+---+---+---+---+---+---+---+");

        for (int r = 0; r < 8; r++) {
            System.out.print((8 - r) + " |");
            for (int c = 0; c < 8; c++) {
                boolean isLight = (r + c) % 2 == 0;
                boolean isSelected = selectedSquare != null &&
                                     selectedSquare[0] == r &&
                                     selectedSquare[1] == c;

                String bg = isSelected ? HIGHLIGHT : (isLight ? LIGHT_BG : DARK_BG);
                Piece p = board.getPiece(r, c);

                if (p == null) {
                    System.out.print(bg + "   " + RESET + "|");
                } else {
                    String fg = (p.getColor() == Piece.Color.WHITE) ? WHITE_FG : BLACK_FG;
                    System.out.print(bg + fg + " " + p.getSymbol() + " " + RESET + "|");
                }
            }
            System.out.println(" " + (8 - r));
        }
        System.out.println("  +---+---+---+---+---+---+---+---+");
        System.out.println("    a   b   c   d   e   f   g   h");
        System.out.println();
    }

    public static void printBoard(Board board) {
        printBoard(board, null);
    }

    // Convert chess notation (e.g. "e2") to [row, col]
    public static int[] parseSquare(String input) {
        input = input.trim().toLowerCase();
        if (input.length() != 2) return null;
        int col = input.charAt(0) - 'a';
        int row = 8 - (input.charAt(1) - '0');
        if (col < 0 || col > 7 || row < 0 || row > 7) return null;
        return new int[]{row, col};
    }

    // Convert [row, col] to chess notation
    public static String toNotation(int row, int col) {
        return "" + (char)('a' + col) + (8 - row);
    }
}