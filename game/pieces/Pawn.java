package game.pieces;


import game.Board;
import game.Piece;
import game.Piece.Color;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(Color color, int row, int col) { super(color, row, col); }

    @Override
    public List<int[]> getValidMoves(Board board) {
        List<int[]> moves = new ArrayList<>();
        int dir = (color == Color.WHITE) ? -1 : 1;
        int startRow = (color == Color.WHITE) ? 6 : 1;

        // Move forward 1
        if (inBounds(row + dir, col) && board.getPiece(row + dir, col) == null) {
            moves.add(new int[]{row + dir, col});
            // Move forward 2 from start
            if (row == startRow && board.getPiece(row + 2 * dir, col) == null)
                moves.add(new int[]{row + 2 * dir, col});
        }

        // Captures diagonally
        for (int dc : new int[]{-1, 1}) {
            int r = row + dir, c = col + dc;
            if (inBounds(r, c)) {
                Piece t = board.getPiece(r, c);
                if (t != null && t.getColor() != this.color)
                    moves.add(new int[]{r, c});

                // En passant
                if (board.enPassantTarget != null &&
                    board.enPassantTarget[0] == r &&
                    board.enPassantTarget[1] == c)
                    moves.add(new int[]{r, c});
            }
        }
        return moves;
    }

    @Override
    public String getSymbol() { return color == Color.WHITE ? "♙" : "♟"; }
}