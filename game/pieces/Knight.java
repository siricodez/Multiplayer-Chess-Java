package game.pieces;


import game.Board;
import game.Piece;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    public Knight(Color color, int row, int col) { super(color, row, col); }

    @Override
    public List<int[]> getValidMoves(Board board) {
        List<int[]> moves = new ArrayList<>();
        int[][] jumps = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};
        for (int[] j : jumps) {
            int r = row + j[0], c = col + j[1];
            if (inBounds(r, c)) {
                Piece t = board.getPiece(r, c);
                if (t == null || t.getColor() != this.color) moves.add(new int[]{r, c});
            }
        }
        return moves;
    }

    @Override
    public String getSymbol() { return color == Color.WHITE ? "♘" : "♞"; }
}