package game.pieces;

import game.Board;
import game.Piece;
import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    public King(Color color, int row, int col) { super(color, row, col); }

    @Override
    public List<int[]> getValidMoves(Board board) {
        return getValidMoves(board, false);
    }

    public List<int[]> getValidMoves(Board board, boolean skipCastling) {
        // skipCastling=true when called from isInCheck to break recursion
        List<int[]> moves = new ArrayList<>();
        int[][] dirs = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
        for (int[] d : dirs) {
            int r = row + d[0], c = col + d[1];
            if (inBounds(r, c)) {
                Piece t = board.getPiece(r, c);
                if (t == null || t.getColor() != this.color) moves.add(new int[]{r, c});
            }
        }

        // Castling — skip during isInCheck calls to avoid infinite recursion
        boolean kingMoved = (color == Color.WHITE) ? board.whiteKingMoved : board.blackKingMoved;
        if (!skipCastling && !kingMoved && !board.isInCheck(color)) {
            int baseRow = (color == Color.WHITE) ? 7 : 0;
            boolean rookHMoved = (color == Color.WHITE) ? board.whiteRookHMoved : board.blackRookHMoved;
            boolean rookAMoved = (color == Color.WHITE) ? board.whiteRookAMoved : board.blackRookAMoved;

            // Kingside castling
            if (!rookHMoved &&
                board.getPiece(baseRow, 5) == null &&
                board.getPiece(baseRow, 6) == null) {
                // Make sure king doesn't pass through check
                Board mid = board.applyMove(row, col, baseRow, 5);
                if (!mid.isInCheck(color)) {
                    moves.add(new int[]{baseRow, 6});
                }
            }
            // Queenside castling
            if (!rookAMoved &&
                board.getPiece(baseRow, 1) == null &&
                board.getPiece(baseRow, 2) == null &&
                board.getPiece(baseRow, 3) == null) {
                Board mid = board.applyMove(row, col, baseRow, 3);
                if (!mid.isInCheck(color)) {
                    moves.add(new int[]{baseRow, 2});
                }
            }
        }
        return moves;
    }

    @Override
    public String getSymbol() { return color == Color.WHITE ? "♔" : "♚"; }
}