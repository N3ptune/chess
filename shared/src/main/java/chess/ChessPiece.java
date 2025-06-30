package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.pieceType = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, pieceType);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        switch (getPieceType()) {
            case KING -> {

                int[][] legal_moves = {
                        {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}
                };

                for (int[] move : legal_moves) {
                    ChessPosition newPosition = new ChessPosition(myPosition.getRow() + move[0], myPosition.getColumn() + move[1]);
                    if (newPosition.getRow() >= 1 && newPosition.getRow() <= 8 && newPosition.getColumn() >= 1 && newPosition.getColumn() <= 8) {
                        if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != this.getTeamColor()) {
                            ChessMove movetoAdd = new ChessMove(myPosition, newPosition, null);
                            moves.add(movetoAdd);
                        }
                    }
                }
            }

            case QUEEN -> {

                int[][] legal_moves = {
                        {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}
                };

                for (int[] move : legal_moves) {
                    ChessPosition newPosition = new ChessPosition(myPosition.getRow() + move[0], myPosition.getColumn() + move[1]);
                    if (newPosition.getRow() >= 1 && newPosition.getRow() <= 8 && newPosition.getColumn() >= 1 && newPosition.getColumn() <= 8) {
                        if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != this.getTeamColor()) {
                            ChessMove movetoAdd = new ChessMove(myPosition, newPosition, null);
                            moves.add(movetoAdd);
                        }
                    }
                }
            }

            case ROOK -> {

                int[][] legal_moves = {
                        {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}
                };

                for (int[] move : legal_moves) {
                    ChessPosition newPosition = new ChessPosition(myPosition.getRow() + move[0], myPosition.getColumn() + move[1]);
                    if (newPosition.getRow() >= 1 && newPosition.getRow() <= 8 && newPosition.getColumn() >= 1 && newPosition.getColumn() <= 8) {
                        if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != this.getTeamColor()) {
                            ChessMove movetoAdd = new ChessMove(myPosition, newPosition, null);
                            moves.add(movetoAdd);
                        }
                    }
                }
            }

            case BISHOP -> {

                int[][] legal_moves = {
                        {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}
                };

                for (int[] move : legal_moves) {
                    ChessPosition newPosition = new ChessPosition(myPosition.getRow() + move[0], myPosition.getColumn() + move[1]);
                    if (newPosition.getRow() >= 1 && newPosition.getRow() <= 8 && newPosition.getColumn() >= 1 && newPosition.getColumn() <= 8) {
                        if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != this.getTeamColor()) {
                            ChessMove movetoAdd = new ChessMove(myPosition, newPosition, null);
                            moves.add(movetoAdd);
                        }
                    }
                }
            }

            case KNIGHT -> {

                int[][] legalMoves = {
                        {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}
                };

                for (int[] move : legalMoves) {
                    ChessPosition newPosition = new ChessPosition(myPosition.getRow() + move[0], myPosition.getColumn() + move[1]);
                    if (newPosition.getRow() >= 1 && newPosition.getRow() <= 8 && newPosition.getColumn() >= 1 && newPosition.getColumn() <= 8) {
                        if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != this.getTeamColor()) {
                            ChessMove movetoAdd = new ChessMove(myPosition, newPosition, null);
                            moves.add(movetoAdd);
                        }
                    }
                }
            }

            case PAWN -> {
                int[][] legal_moves = {
                        {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}
                };

                for (int[] move : legal_moves) {
                    ChessPosition newPosition = new ChessPosition(myPosition.getRow() + move[0], myPosition.getColumn() + move[1]);
                    if (newPosition.getRow() >= 1 && newPosition.getRow() <= 8 && newPosition.getColumn() >= 1 && newPosition.getColumn() <= 8) {
                        if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != this.getTeamColor()) {
                            ChessMove movetoAdd = new ChessMove(myPosition, newPosition, null);
                            moves.add(movetoAdd);
                        }
                    }
                }
            }
        }


        return moves;

    }


}
