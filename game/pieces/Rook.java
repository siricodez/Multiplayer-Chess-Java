package game.pieces;


import game.Board;
import game.Piece;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
    public Rook(Color color, int row, int col) { super(color, row, col); }

    @Override
    public List<int[]> getValidMoves(Board board) {
        List<int[]> moves = new ArrayList<>();
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        addSlidingMoves(board, moves, dirs);
        return moves;
    }

    @Override
    public String getSymbol() { return color == Color.WHITE ? "♖" : "♜"; }
}