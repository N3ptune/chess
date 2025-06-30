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

                int[][] legalMoves = {
                        {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}
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

            case QUEEN -> {

                int[][] directions = {
                        {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}
                };

                for (int[] direction : directions) {

                    int row = myPosition.getRow() + direction[0];
                    int col = myPosition.getColumn() + direction[1];

                    while (row >= 1 && row <= 8 && col >= 1 && col <= 8){
                        ChessPosition newPosition = new ChessPosition(row, col);
                        if (board.getPiece(newPosition) == null) {
                            ChessMove movetoAdd = new ChessMove(myPosition, newPosition, null);
                            moves.add(movetoAdd);
                        }
                        else {
                            if (board.getPiece(newPosition).getTeamColor() != this.getTeamColor()){
                                ChessMove movetoAdd = new ChessMove(myPosition, newPosition, null);
                                moves.add(movetoAdd);
                            }
                            break;
                        }
                        row += direction[0];
                        col += direction[1];
                    }

                }
            }

            case ROOK -> {

                int[][] directions = {
                        {-1, 0}, {0, 1}, {1, 0}, {0, -1}
                };

                for (int[] direction : directions) {

                    int row = myPosition.getRow() + direction[0];
                    int col = myPosition.getColumn() + direction[1];

                    while (row >= 1 && row <= 8 && col >= 1 && col <= 8){
                        ChessPosition newPosition = new ChessPosition(row, col);
                        if (board.getPiece(newPosition) == null) {
                            ChessMove movetoAdd = new ChessMove(myPosition, newPosition, null);
                            moves.add(movetoAdd);
                        }
                        else {
                            if (board.getPiece(newPosition).getTeamColor() != this.getTeamColor()){
                                ChessMove movetoAdd = new ChessMove(myPosition, newPosition, null);
                                moves.add(movetoAdd);
                            }
                            break;
                        }
                        row += direction[0];
                        col += direction[1];
                    }

                }
            }


            case BISHOP -> {

                int[][] directions = {
                        {-1, -1}, {-1, 1}, {1, 1}, {1, -1}
                };

                for (int[] direction : directions) {

                    int row = myPosition.getRow() + direction[0];
                    int col = myPosition.getColumn() + direction[1];

                    while (row >= 1 && row <= 8 && col >= 1 && col <= 8){
                        ChessPosition newPosition = new ChessPosition(row, col);
                        if (board.getPiece(newPosition) == null) {
                            ChessMove movetoAdd = new ChessMove(myPosition, newPosition, null);
                            moves.add(movetoAdd);
                        }
                        else {
                            if (board.getPiece(newPosition).getTeamColor() != this.getTeamColor()){
                                ChessMove movetoAdd = new ChessMove(myPosition, newPosition, null);
                                moves.add(movetoAdd);
                            }
                            break;
                        }
                        row += direction[0];
                        col += direction[1];
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
                int[][] whitelegalMoves = { {1, 0} };
                int[][] whiteCaptures = { {1, -1}, {1,1} };

                int[][] blacklegalMoves = { {-1, 0} };
                int[][] blackCaptures = { {-1, -1}, {-1,1} };

                if (this.getTeamColor() == ChessGame.TeamColor.WHITE){
                    for (int[] move: whitelegalMoves){
                        ChessPosition newPosition = new ChessPosition(myPosition.getRow() + move[0], myPosition.getColumn() + move[1]);
                        if (newPosition.getRow() >=1 && newPosition.getRow() <= 8 && newPosition.getColumn() >= 1 && newPosition.getColumn() <=8){
                            if (board.getPiece(newPosition) == null){
                                if (newPosition.getRow() == 8){
                                    for (ChessPiece.PieceType promotion : new ChessPiece.PieceType[]{
                                            ChessPiece.PieceType.QUEEN,
                                            ChessPiece.PieceType.ROOK,
                                            ChessPiece.PieceType.BISHOP,
                                            ChessPiece.PieceType.KNIGHT
                                    }){
                                    ChessMove movetoAdd = new ChessMove(myPosition, newPosition, promotion);
                                    moves.add(movetoAdd);}
                                } else {
                                    ChessMove movetoAdd = new ChessMove(myPosition, newPosition, null);
                                    moves.add(movetoAdd);
                                }
                            }
                        }
                    }

                    if (myPosition.getRow() == 2){
                        ChessPosition oneMove = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
                        ChessPosition twoMove = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
                        if (oneMove.getRow() >= 1 && oneMove.getRow() <= 8 && oneMove.getColumn() >= 1 && oneMove.getColumn() <= 8){
                            if (twoMove.getRow() >= 1 && twoMove.getRow() <= 8 && twoMove.getColumn() >= 1 && twoMove.getColumn() <= 8){
                                if (board.getPiece(oneMove) == null && board.getPiece(twoMove) == null){
                                    ChessMove movetoAdd = new ChessMove(myPosition, twoMove, null);
                                    moves.add(movetoAdd);
                                }
                            }
                        }
                    }

                    for (int[] move : whiteCaptures){
                        ChessPosition newPosition = new ChessPosition(myPosition.getRow() + move[0], myPosition.getColumn() + move[1]);
                        if (newPosition.getRow() >= 1 && newPosition.getRow() <= 8 && newPosition.getColumn() >= 1 && newPosition.getColumn() <= 8) {
                            if (board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != this.getTeamColor()){
                                if (myPosition.getRow() + move[0] == 8){
                                    for (ChessPiece.PieceType promotion : new ChessPiece.PieceType[]{
                                            ChessPiece.PieceType.QUEEN,
                                            ChessPiece.PieceType.ROOK,
                                            ChessPiece.PieceType.BISHOP,
                                            ChessPiece.PieceType.KNIGHT
                                    }){
                                        ChessMove movetoAdd = new ChessMove(myPosition, newPosition, promotion);
                                        moves.add(movetoAdd);}
                                } else {
                                    ChessMove movetoAdd = new ChessMove(myPosition, newPosition, null);
                                    moves.add(movetoAdd);
                                }
                            }
                        }
                    }
                }


                else if (this.getTeamColor() == ChessGame.TeamColor.BLACK){
                    for (int[] move: blacklegalMoves){
                        ChessPosition newPosition = new ChessPosition(myPosition.getRow() + move[0], myPosition.getColumn() + move[1]);
                        if (newPosition.getRow() >=1 && newPosition.getRow() <= 8 && newPosition.getColumn() >= 1 && newPosition.getColumn() <=8){
                            if (board.getPiece(newPosition) == null){
                                if (newPosition.getRow() == 1){
                                    for (ChessPiece.PieceType promotion : new ChessPiece.PieceType[]{
                                            ChessPiece.PieceType.QUEEN,
                                            ChessPiece.PieceType.ROOK,
                                            ChessPiece.PieceType.BISHOP,
                                            ChessPiece.PieceType.KNIGHT
                                    }){
                                        ChessMove movetoAdd = new ChessMove(myPosition, newPosition, promotion);
                                        moves.add(movetoAdd);}
                                } else {
                                    ChessMove movetoAdd = new ChessMove(myPosition, newPosition, null);
                                    moves.add(movetoAdd);
                                }
                            }
                        }
                    }

                    if (myPosition.getRow() == 7){
                        ChessPosition oneMove = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
                        ChessPosition twoMove = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
                        if (oneMove.getRow() >= 1 && oneMove.getRow() <= 8 && oneMove.getColumn() >= 1 && oneMove.getColumn() <= 8){
                            if (twoMove.getRow() >= 1 && twoMove.getRow() <= 8 && twoMove.getColumn() >= 1 && twoMove.getColumn() <= 8){
                                if (board.getPiece(oneMove) == null && board.getPiece(twoMove) == null){
                                    ChessMove movetoAdd = new ChessMove(myPosition, twoMove, null);
                                    moves.add(movetoAdd);
                                }
                            }
                        }
                    }

                    for (int[] move : blackCaptures){
                        ChessPosition newPosition = new ChessPosition(myPosition.getRow() + move[0], myPosition.getColumn() + move[1]);
                        if (newPosition.getRow() >= 1 && newPosition.getRow() <= 8 && newPosition.getColumn() >= 1 && newPosition.getColumn() <= 8) {
                            if (board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != this.getTeamColor()){
                                if (myPosition.getRow() + move[0] == 1){
                                    for (ChessPiece.PieceType promotion : new ChessPiece.PieceType[]{
                                            ChessPiece.PieceType.QUEEN,
                                            ChessPiece.PieceType.ROOK,
                                            ChessPiece.PieceType.BISHOP,
                                            ChessPiece.PieceType.KNIGHT
                                    }){
                                        ChessMove movetoAdd = new ChessMove(myPosition, newPosition, promotion);
                                        moves.add(movetoAdd);}
                                } else {
                                    ChessMove movetoAdd = new ChessMove(myPosition, newPosition, null);
                                    moves.add(movetoAdd);
                                }
                            }
                        }
                    }
                }
            }
        }


        return moves;

    }


}
