package game;


import java.util.List;
import java.io.Serializable;

public abstract class Piece implements Serializable {
    public enum Color { WHITE, BLACK }

    protected Color color;
    protected int row, col;

    public Piece(Color color, int row, int col) {
        this.color = color;
        this.row = row;
        this.col = col;
    }

    public Color getColor() { return color; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public void setPosition(int row, int col) { this.row = row; this.col = col; }

    // Returns all pseudo-legal moves (doesn't account for leaving king in check)
    public abstract List<int[]> getValidMoves(Board board);

    // Unicode symbol for terminal display
    public abstract String getSymbol();

    protected boolean inBounds(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }

    // Helper for sliding pieces (rook, bishop, queen)
    protected void addSlidingMoves(Board board, List<int[]> moves, int[][] directions) {
        for (int[] dir : directions) {
            int r = row + dir[0];
            int c = col + dir[1];
            while (inBounds(r, c)) {
                Piece target = board.getPiece(r, c);
                if (target == null) {
                    moves.add(new int[]{r, c});
                } else {
                    if (target.getColor() != this.color) moves.add(new int[]{r, c});
                    break; // blocked
                }
                r += dir[0];
                c += dir[1];
            }
        }
    }
}