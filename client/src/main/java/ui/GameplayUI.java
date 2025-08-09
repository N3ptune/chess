package ui;

import chess.*;
import facade.ServerFacade;
import state.ClientState;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

public class GameplayUI {

    public static void handleCommand(String command, String[] commandArgs, ClientState state, ServerFacade facade){

        switch (command){
            case "help" -> showHelp();
            case "redraw" -> writeBoard(state.getPlayerColor(), state, facade, commandArgs);
            case "move" -> makeMove(commandArgs, state, facade);
            case "resign" -> resignGame(state, facade);
            case "highlight" -> highlightMoves(state.getPlayerColor(), commandArgs, state, facade);
            case "exit" -> {
                UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, state.getAuthToken(), state.getCurrentGameID());
                state.getEndpoint().sendMessage(userGameCommand);
                state.exitGame();
            }
            default -> System.out.println("Command not recognized. Please try again or type \"help\" for a list of available commands.");
        }
    }
    private static void showHelp(){
        System.out.println("See all commands: \"help\"");
        System.out.println("Redraw the current board: \"redraw\"");
        System.out.println("Make a move in the game you are in (must be a player in the game) (If no promotion applies, type null): \"move <START SQUARE> <END SQUARE> <PROMOTION PIECE>\"");
        System.out.println("Resign a game you are in (must be a player in the game): \"resign\"");
        System.out.println("Highlight the legal moves for a piece \"highlight <SQAURE>\"");
        System.out.println("Exit the game you are in: \"exit\"");
    }

    public static void writeBoard(ChessGame.TeamColor perspective, ClientState state, ServerFacade facade, String[] commandArgs){

        ChessGame game = new ChessGame();
        ChessBoard board = game.getBoard();

            System.out.print(EscapeSequences.ERASE_SCREEN);
            int startRow = (perspective == ChessGame.TeamColor.WHITE) ? 8 : 1;
            int endRow = (perspective == ChessGame.TeamColor.WHITE) ? 0 : 9;
            int step = (perspective == ChessGame.TeamColor.WHITE) ? -1 : 1;

            char[] columns = (perspective == ChessGame.TeamColor.WHITE)
                    ? new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'}
                    : new char[] {'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'};

            System.out.print("   ");
            for (char col : columns){
                System.out.print(col + "   ");
            }
            System.out.println();

            for (int row = startRow; row != endRow; row += step){
                System.out.print(" " + row + " ");
                for (int col = 0; col < 8; col++){
                    int file = (perspective == ChessGame.TeamColor.WHITE) ? col : 7-col;
                    ChessPiece piece = board.getPiece(new ChessPosition(row, file + 1));
                    boolean lightSquare = (row + file) % 2 == 0;

                    System.out.print(lightSquare ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY);

                    if (piece == null){
                        System.out.print(EscapeSequences.EMPTY);
                    } else {
                        String symbol = getUnicodePiece(piece);
                        String color = (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                ? EscapeSequences.SET_TEXT_COLOR_WHITE
                                : EscapeSequences.SET_TEXT_COLOR_BLACK;
                        System.out.print(color + symbol);
                    }
                    System.out.print(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
                }
                System.out.println(" " + row);
            }
            System.out.print("   ");
            for (char col : columns) {
                System.out.print(col + "   ");
            }
            System.out.println();
        }


    private static String getUnicodePiece(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case KING -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                    ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                    ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case ROOK -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                    ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case BISHOP -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                    ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                    ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case PAWN -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                    ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
        };
    }

    public static void makeMove(String[] commandArgs, ClientState state, ServerFacade facade){
        try {
            ChessPosition start = parsePosition(commandArgs[0]);
            ChessPosition end = parsePosition(commandArgs[1]);

            ChessPiece.PieceType promotionPiece = ChessPiece.PieceType.valueOf(commandArgs[2].toUpperCase());

            ChessMove move = new ChessMove(start, end, promotionPiece);
            MakeMoveCommand command = new MakeMoveCommand(state.getAuthToken(), state.getCurrentGameID(), move);
            state.getEndpoint().sendMessage(command);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void resignGame(ClientState state, ServerFacade facade){
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, state.getAuthToken(), state.getCurrentGameID());
        state.getEndpoint().sendMessage(command);
    }

    public static void highlightMoves(ChessGame.TeamColor perspective, String[] commandArgs, ClientState state, ServerFacade facade){

        if (commandArgs.length < 1){
            System.out.println("Please make sure to include <SQUARE> where <SQUARE> is the location of the piece you wish to highlight");
        }

        ChessPosition position = parsePosition(commandArgs[0]);

        ChessGame game = new ChessGame();
        ChessBoard board = game.getBoard();

        System.out.print(EscapeSequences.ERASE_SCREEN);
        int startRow = (perspective == ChessGame.TeamColor.WHITE) ? 8 : 1;
        int endRow = (perspective == ChessGame.TeamColor.WHITE) ? 0 : 9;
        int step = (perspective == ChessGame.TeamColor.WHITE) ? -1 : 1;

        char[] columns = (perspective == ChessGame.TeamColor.WHITE)
                ? new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'}
                : new char[] {'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'};

        System.out.print("   ");
        for (char col : columns){
            System.out.print(col + "   ");
        }
        System.out.println();

        for (int row = startRow; row != endRow; row += step){
            System.out.print(" " + row + " ");
            for (int col = 0; col < 8; col++){
                int file = (perspective == ChessGame.TeamColor.WHITE) ? col : 7-col;
                ChessPiece piece = board.getPiece(new ChessPosition(row, file + 1));
                boolean lightSquare = (row + file) % 2 == 0;

                System.out.print(lightSquare ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY);

                if (piece == null){
                    System.out.print(EscapeSequences.EMPTY);
                } else {
                    String symbol = getUnicodePiece(piece);
                    String color = (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                            ? EscapeSequences.SET_TEXT_COLOR_WHITE
                            : EscapeSequences.SET_TEXT_COLOR_BLACK;
                    System.out.print(color + symbol);
                }
                System.out.print(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
            }
            System.out.println(" " + row);
        }
        System.out.print("   ");
        for (char col : columns) {
            System.out.print(col + "   ");
        }
        System.out.println();
    }

    public static ChessPosition parsePosition(String square){
        int highCol = square.charAt(0) - 'a' + 1;
        int highRow = Character.getNumericValue(square.charAt(1));
        return new ChessPosition(highRow, highCol);
    }
}
