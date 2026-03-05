package game;

import java.util.ArrayList;
import java.util.List;

public class MoveValidator {

    // Returns only moves that don't leave own king in check
    public static List<int[]> getLegalMoves(Board board, int row, int col) {
        Piece piece = board.getPiece(row, col);
        if (piece == null) return new ArrayList<>();

        List<int[]> pseudoMoves = piece.getValidMoves(board);
        List<int[]> legalMoves = new ArrayList<>();

        for (int[] move : pseudoMoves) {
            Board next = board.applyMove(row, col, move[0], move[1]);
            if (!next.isInCheck(piece.getColor())) {
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    // Check if a specific move is legal
    public static boolean isLegal(Board board, int fromR, int fromC, int toR, int toC) {
        for (int[] move : getLegalMoves(board, fromR, fromC)) {
            if (move[0] == toR && move[1] == toC) return true;
        }
        return false;
    }

    // No legal moves + in check = checkmate; no legal moves + not in check = stalemate
    public static boolean hasAnyLegalMove(Board board, Piece.Color color) {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(r, c);
                if (p != null && p.getColor() == color) {
                    if (!getLegalMoves(board, r, c).isEmpty()) return true;
                }
            }
        return false;
    }

    public static boolean isCheckmate(Board board, Piece.Color color) {
        return board.isInCheck(color) && !hasAnyLegalMove(board, color);
    }

    public static boolean isStalemate(Board board, Piece.Color color) {
        return !board.isInCheck(color) && !hasAnyLegalMove(board, color);
    }
}