package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import facade.ServerFacade;
import state.ClientState;

public class GameplayUI {

    public static void handleCommand(String command, String[] commandArgs, ClientState state, ServerFacade facade){
        switch (command){
            case "help" -> showHelp();
            case "write" -> writeBoard(state, facade);
            case "exit" -> {
                state.exitGame();
            }
            default -> System.out.println("Command not recognized. Please try again or type \"help\" for a list of available commands.");
        }
    }
    private static void showHelp(){
        System.out.println("Nothing to see here yet :)");
    }

    public static void writeBoard(ChessGame.TeamColor perspective, ClientState state, ServerFacade facade){

        ChessGame.TeamColor playerColor = state.getPlayerColor();

        ChessBoard board = new ChessBoard();

        if (playerColor == ChessGame.TeamColor.WHITE || state.getPlayerColor == null){
            //WRITE the board as if white
            System.out.print(EscapeSequences.ERASE_SCREEN);
            int startRow = (perspective == ChessGame.TeamColor.WHITE) ? 8 : 1;
            int endRow = (perspective == ChessGame.TeamColor.WHITE) ? 1 : 8;
            int step = (perspective == ChessGame.TeamColor.WHITE) ? -1 : 1;

            char[] columns = (perspective == ChessGame.TeamColor.WHITE)
                    ? new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'}
                    : new char[] {'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'};

            System.out.print("   ");
            for (char col : columns){
                System.out.print(" " + col + " ");
            }
            System.out.println();

            for (int row = startRow; row != endRow; row += step){
                System.out.print(" " + row + " ");
                for (int col = 0; col < 8; col++){
                    int file = (perspective == ChessGame.TeamColor.WHITE) ? col : 7-col;
                    ChessPiece piece = board.getPiece(new ChessPosition(row, file + 1));
                    boolean lightSquare = (row + file) % 2 == 0;

                    System.out.print(lightSquare ? EscapeSequences.SET_BG_COLOR_DARK_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY);

                    if (piece == null){
                        System.out.print(EscapeSequences.EMPTY);
                    } else {
                        String symbol = getUnicodePiece(piece);
                        String color = (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                ? EscapeSequences.SET_TEXT_COLOR_BLACK
                                : EscapeSequences.SET_TEXT_COLOR_WHITE;
                    }
                }
            }
        } else if (playerColor == ChessGame.TeamColor.BLACK){
            //Write the board as if black
        }
    }
}
