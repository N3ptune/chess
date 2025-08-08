package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.request.JoinGameRequest;
import model.request.CreateGameRequest;
import model.request.ListGamesRequest;
import model.result.CreateGameResult;
import model.result.JoinGameResult;
import model.result.ListGamesResult;
import model.result.LogoutResult;

import java.sql.SQLException;
import java.util.Collection;



public class GameService{

    private final DataAccess dao;

    public GameService(DataAccess dao){
        this.dao = dao;
    }

    public JoinGameResult joinGame(JoinGameRequest joinGame){

        if (joinGame.authToken() == null){
            return new JoinGameResult("Error: unauthorized");
        }

        try {
            AuthData authData = dao.getAuth(joinGame.authToken());

            if (authData == null){
                return new JoinGameResult("Error: unauthorized");
            }

            if (joinGame.gameID() <= 0){
                return new JoinGameResult("Error: bad request");
            }

            GameData game = dao.getGame(joinGame.gameID());

            if (game == null){
                return new JoinGameResult("Error: game does not exist");
            }


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

        } catch (DataAccessException | SQLException e){
            return new JoinGameResult("Error: " + e.getMessage());
        }
    }

    public CreateGameResult createGame(CreateGameRequest createGame){


            if (createGame.gameName() == null){
                return new CreateGameResult(null, "Error: bad request");
            }

            AuthData authData;

            try {
                authData = dao.getAuth(createGame.authToken());

                if (authData == null) {
                    return new CreateGameResult(null, "Error: unauthorized");
                }


                if (!createGame.authToken().equals(authData.authToken())) {
                    return new CreateGameResult(null, "Error: unauthorized");
                }

                int gameID = dao.createGame(createGame.gameName());

                return new CreateGameResult(gameID, null);

            } catch(DataAccessException | SQLException e) {
                return new CreateGameResult(null, "Error: " + e.getMessage());
            }

    }
    public ListGamesResult listGames(ListGamesRequest listGames){
        try {
            AuthData authData = dao.getAuth(listGames.authToken());

            if (authData == null) {
                return new ListGamesResult(null, "Error: unauthorized");
            }

            if (!listGames.authToken().equals(authData.authToken())) {
                return new ListGamesResult(null, "Error: unauthorized");
            }

            Collection<GameData> games = dao.listGames();

            return new ListGamesResult(games, null);
        } catch (DataAccessException e){
            return new ListGamesResult(null, "Error: " + e.getMessage());
        }
    }
}
