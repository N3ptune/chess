package ui;

import chess.ChessGame;
import facade.ServerFacade;
import model.GameData;
import model.result.ListGamesResult;
import state.ClientState;
import java.util.List;

public class PostloginUI {

    public static void handleCommand(String command, String[] commandArgs, ClientState state, ServerFacade facade){
        switch (command) {
            case "help" -> showHelp();
            case "logout" -> doLogout(state, facade);
            case "create" -> doCreate(commandArgs, state, facade);
            case "list" -> doList(state, facade);
            case "join" -> doJoin(commandArgs, state, facade);
            case "observe" -> doObserve(commandArgs, state, facade);
            default -> System.out.println("Command not recognized. Please try again or type \"help\" for a list of available commands.");
        }
    }

    private static void showHelp(){
        System.out.println("Logout: \"lo\", \"logout\"");
        System.out.println("Create a new game: \"c\", \"create\" <GAME NAME>");
        System.out.println("List all active games: \"li\", \"list\"");
        System.out.println("Join an active game: \"j\", \"join\" <GAME ID> <USERNAME> <DESIRED COLOR>");
        System.out.println("Observe an active game: \"o\", \"observe\" <GAME ID> <USERNAME>");
        System.out.println("See all commands \"help\"");
        System.out.println("Chess >>> ");
    }

    private static void doLogout(ClientState state, ServerFacade facade){
        try {
            String authToken = state.getAuthToken();
            facade.logout(authToken);
            state.logout();
            System.out.println("Logged out succesfully");
        } catch (Exception e){
            System.out.println("Error logging out, please try again shortly");
        }
    }

    private static void doCreate(String[] args, ClientState state, ServerFacade facade){
        if (args.length < 1) {
            System.out.println("Please make sure to include <GAME NAME>");
            return;
        }

        String gameName = args[0];
        String authToken = state.getAuthToken();

        try {
            facade.createGame(gameName, authToken);
        } catch (Exception e) {
            System.out.println("Error creating game, please try again shortly");
        }
    }

    private static void doList(ClientState state, ServerFacade facade){

        String authToken = state.getAuthToken();

        try {
            List<GameData> games = facade.listGames(authToken).stream().toList();
            if (games.size() == 0 || games == null){
                System.out.println("No games to show");
            }

            state.setLastListedGames(games);

            for (int i = 0; i < games.size(); i++){
                GameData game = games.get(i);
                Integer gameID = game.gameID();
                String gameName = game.gameName();
                String white = game.whiteUsername();
                String black = game.blackUsername();
                System.out.println((i + 1) + ".   Game name: " + gameName + "   White: " + white + "   Black: " + black);
            }
        } catch (Exception e){
            System.out.println("Error listing games, please try again shortly " + e.getMessage());
        }
    }

    private static void doJoin(String[] args, ClientState state, ServerFacade facade){

        if (args.length < 3){
            System.out.println("Please make sure to include <GAME ID> <USERNAME> <DESIRED COLOR>");
            return;
        }

        String authToken = state.getAuthToken();
        try {
            List<GameData> games = state.getLastListedGames();
            Integer gameNum = Integer.parseInt(args[0]) - 1;
            if (gameNum < 0 || gameNum > games.size()){
                System.out.println("Invalid game number");
                return;
            }
            Integer gameID = games.get(gameNum).gameID();
        } catch (Exception e){
            System.out.println("Please make sure to order as <GAME ID> <USERNAME> <DESIRED COLOR>");
            return;
        }
        List<GameData> games = state.getLastListedGames();
        Integer gameNum = Integer.parseInt(args[0]) - 1;
        Integer gameID = games.get(gameNum).gameID();
        String username = args[1];
        ChessGame.TeamColor playerColor = null;

        if (args[2].toLowerCase().equals("white")){
            playerColor = ChessGame.TeamColor.WHITE;
        } else if (args[2].toLowerCase().equals("black")){
            playerColor = ChessGame.TeamColor.BLACK;
        } else {
            System.out.println("Please provide a valid team color to join as, not " + args[2]);
            return;
        }

        try {
            facade.joinGame(gameID, username, playerColor, authToken);
            state.joinGame(gameID, playerColor);
            System.out.println("Joined game as " + playerColor);
        } catch (Exception e){
            System.out.println("Error joining game, color already chosen or game does not exist");
        }
    }

    private static void doObserve(String[] args, ClientState state, ServerFacade facade){

        if (args.length < 2){
            System.out.println("Please make sure to include <GAME ID> <USERNAME>");
            return;
        }

        String authToken = state.getAuthToken();
        String username = args[1];
        ChessGame.TeamColor playerColor = null;

        List<GameData> games = state.getLastListedGames();
        if (games == null){
            System.out.println("List games first, or there are no games");
            return;
        }
        if (Integer.parseInt(args[0]) + 2 > games.size() || Integer.parseInt(args[0]) < 1){
            System.out.println("Please choose a valid number");
            return;
        }
        Integer gameNum = Integer.parseInt(args[0]) - 1;
        Integer gameID = games.get(gameNum).gameID();

        boolean found = false;

        try {
            // Confirm the game exists, go to gameplayUI
            for (GameData game : games){
                if (game.gameID() == gameID){
                    found = true;
                    break;
                }
            }

            if (!found){
                System.out.println("Game does not exist");
                return;
            } else {
                state.setCurrentGameID(gameID);
                state.setPlayerColor(ChessGame.TeamColor.WHITE);
                System.out.println("Viewing game");
            }
        } catch (Exception e){
            System.out.println("Error joining as an observer, please try again shortly");
        }
    }
}
