package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDAO;
import model.AuthData;
import model.GameData;
import model.request.JoinGameRequest;
import model.request.CreateGameRequest;
import model.result.CreateGameResult;
import model.result.JoinGameResult;
import model.result.ListGamesResult;
import model.result.LogoutResult;


// This will have Join Game, Create Game, and List Games

public class GameService {

    private final DataAccess dao = new MemoryDAO();

    public JoinGameResult joinGame(JoinGameRequest joinGame){
        try {
            AuthData authData = dao.getAuth(joinGame.authToken());
            GameData game = dao.getGame(joinGame.gameID());

            if (!joinGame.authToken().equals(authData.authToken())){
                return new JoinGameResult("Error: unauthorized");
            }

            if (joinGame.playerColor() == ChessGame.TeamColor.WHITE){
                if (game.whiteUsername() != null){
                    return new JoinGameResult("Error: color already taken");
                }
            } else if (joinGame.playerColor() == ChessGame.TeamColor.BLACK){
                if (game.blackUsername() != null) {
                    return new JoinGameResult("Error: color already taken");
                }
            } else {
                return new JoinGameResult("Error: not a valid color");
            }

            dao.joinGame(game.gameID(), authData.username(), joinGame.playerColor());

            return new JoinGameResult(null);

        } catch (DataAccessException e){
            return new JoinGameResult("Error: " + e.getMessage());
        }
    }
    public CreateGameResult createGame(CreateGameRequest createGame){
        try {

            AuthData authData = dao.getAuth(createGame.authToken());

            if (!createGame.authToken().equals(authData.authToken())){
                return new CreateGameResult(null, "Error: unauthorized");
            }

            int gameID = dao.createGame(createGame.gameName());

            GameData game = new GameData(gameID, null, null, createGame.gameName(), new ChessGame());

            return new CreateGameResult(gameID, null);

        } catch (DataAccessException e){
            return new CreateGameResult(null, "Error: unauthourized");
        }
    }
    public ListGamesResult listGames(String authToken){
        return new ListGamesResult();
    }
}
