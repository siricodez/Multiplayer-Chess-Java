package game;



import game.pieces.*;
import java.io.Serializable;
import java.util.List;

public class Board implements Serializable {
    private Piece[][] grid = new Piece[8][8];

    // Track castling eligibility
    public boolean whiteKingMoved = false;
    public boolean blackKingMoved = false;
    public boolean whiteRookAMoved = false; // queenside (col 0)
    public boolean whiteRookHMoved = false; // kingside  (col 7)
    public boolean blackRookAMoved = false;
    public boolean blackRookHMoved = false;

    // En passant target square (the square a pawn can capture onto), null if none
    public int[] enPassantTarget = null;

    public Board() {
        setupPieces();
    }

    private void setupPieces() {
        // Black pieces (top)
        grid[0][0] = new Rook(Piece.Color.BLACK, 0, 0);
        grid[0][1] = new Knight(Piece.Color.BLACK, 0, 1);
        grid[0][2] = new Bishop(Piece.Color.BLACK, 0, 2);
        grid[0][3] = new Queen(Piece.Color.BLACK, 0, 3);
        grid[0][4] = new King(Piece.Color.BLACK, 0, 4);
        grid[0][5] = new Bishop(Piece.Color.BLACK, 0, 5);
        grid[0][6] = new Knight(Piece.Color.BLACK, 0, 6);
        grid[0][7] = new Rook(Piece.Color.BLACK, 0, 7);
        for (int c = 0; c < 8; c++) grid[1][c] = new Pawn(Piece.Color.BLACK, 1, c);

        // White pieces (bottom)
        grid[7][0] = new Rook(Piece.Color.WHITE, 7, 0);
        grid[7][1] = new Knight(Piece.Color.WHITE, 7, 1);
        grid[7][2] = new Bishop(Piece.Color.WHITE, 7, 2);
        grid[7][3] = new Queen(Piece.Color.WHITE, 7, 3);
        grid[7][4] = new King(Piece.Color.WHITE, 7, 4);
        grid[7][5] = new Bishop(Piece.Color.WHITE, 7, 5);
        grid[7][6] = new Knight(Piece.Color.WHITE, 7, 6);
        grid[7][7] = new Rook(Piece.Color.WHITE, 7, 7);
        for (int c = 0; c < 8; c++) grid[6][c] = new Pawn(Piece.Color.WHITE, 6, c);
    }

    public Piece getPiece(int row, int col) { return grid[row][col]; }
    public void setPiece(int row, int col, Piece p) { grid[row][col] = p; }

    // Apply a move and return a new board (immutable style for check detection)
    public Board applyMove(int fromR, int fromC, int toR, int toC) {
        Board next = this.deepCopy();
        Piece p = next.grid[fromR][fromC];

        // Reset en passant each move
        next.enPassantTarget = null;

        // En passant capture
        if (p instanceof Pawn && toC != fromC && next.grid[toR][toC] == null) {
            int capturedRow = (p.getColor() == Piece.Color.WHITE) ? toR + 1 : toR - 1;
            next.grid[capturedRow][toC] = null;
        }

        // Set en passant target if pawn moves 2 squares
        if (p instanceof Pawn && Math.abs(toR - fromR) == 2) {
            int epRow = (fromR + toR) / 2;
            next.enPassantTarget = new int[]{epRow, toC};
        }

        // Castling: move rook alongside king
        if (p instanceof King && Math.abs(toC - fromC) == 2) {
            if (toC == 6) { // kingside
                next.grid[fromR][5] = next.grid[fromR][7];
                next.grid[fromR][7] = null;
                if (next.grid[fromR][5] != null) next.grid[fromR][5].setPosition(fromR, 5);
            } else { // queenside
                next.grid[fromR][3] = next.grid[fromR][0];
                next.grid[fromR][0] = null;
                if (next.grid[fromR][3] != null) next.grid[fromR][3].setPosition(fromR, 3);
            }
        }

        // Track castling rights
        if (p instanceof King) {
            if (p.getColor() == Piece.Color.WHITE) next.whiteKingMoved = true;
            else next.blackKingMoved = true;
        }
        if (p instanceof Rook) {
            if (fromR == 7 && fromC == 0) next.whiteRookAMoved = true;
            if (fromR == 7 && fromC == 7) next.whiteRookHMoved = true;
            if (fromR == 0 && fromC == 0) next.blackRookAMoved = true;
            if (fromR == 0 && fromC == 7) next.blackRookHMoved = true;
        }

        // Move piece
        next.grid[toR][toC] = p;
        next.grid[fromR][fromC] = null;
        p.setPosition(toR, toC);

        // Pawn promotion — auto queen
        if (p instanceof Pawn) {
            if (p.getColor() == Piece.Color.WHITE && toR == 0)
                next.grid[toR][toC] = new Queen(Piece.Color.WHITE, toR, toC);
            if (p.getColor() == Piece.Color.BLACK && toR == 7)
                next.grid[toR][toC] = new Queen(Piece.Color.BLACK, toR, toC);
        }

        return next;
    }

    public boolean isInCheck(Piece.Color color) {
        // Find king position
        int kr = -1, kc = -1;
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (grid[r][c] instanceof King && grid[r][c].getColor() == color) {
                    kr = r; kc = c;
                }

        // Check if any opponent piece can attack king
        Piece.Color opponent = (color == Piece.Color.WHITE) ? Piece.Color.BLACK : Piece.Color.WHITE;
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Piece p = grid[r][c];
                if (p != null && p.getColor() == opponent) {
                    List<int[]> attackMoves = (p instanceof game.pieces.King) ?
                        ((game.pieces.King)p).getValidMoves(this, true) :
                        p.getValidMoves(this);
                    for (int[] move : attackMoves) {
                        if (move[0] == kr && move[1] == kc) return true;
                    }
                }
            }
        return false;
    }

    // Deep copy for simulating moves without modifying actual board
    public Board deepCopy() {
        Board copy = new Board();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                copy.grid[r][c] = null; // clear default setup

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (grid[r][c] != null) {
                    copy.grid[r][c] = clonePiece(grid[r][c]);
                }
            }
        }
        copy.whiteKingMoved = this.whiteKingMoved;
        copy.blackKingMoved = this.blackKingMoved;
        copy.whiteRookAMoved = this.whiteRookAMoved;
        copy.whiteRookHMoved = this.whiteRookHMoved;
        copy.blackRookAMoved = this.blackRookAMoved;
        copy.blackRookHMoved = this.blackRookHMoved;
        copy.enPassantTarget = this.enPassantTarget != null ?
            new int[]{this.enPassantTarget[0], this.enPassantTarget[1]} : null;
        return copy;
    }

    private Piece clonePiece(Piece p) {
        if (p instanceof King)   return new King(p.getColor(), p.getRow(), p.getCol());
        if (p instanceof Queen)  return new Queen(p.getColor(), p.getRow(), p.getCol());
        if (p instanceof Rook)   return new Rook(p.getColor(), p.getRow(), p.getCol());
        if (p instanceof Bishop) return new Bishop(p.getColor(), p.getRow(), p.getCol());
        if (p instanceof Knight) return new Knight(p.getColor(), p.getRow(), p.getCol());
        if (p instanceof Pawn)   return new Pawn(p.getColor(), p.getRow(), p.getCol());
        return null;
    }
}