package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.UUID;

public class MySQLDataAccess implements DataAccess{
    private int nextGameID = 1;

    @Override
    public void clear() {
        authTokens.clear();
        games.clear();
        users.clear();
    }

    @Override
    public void createUser(UserData user){
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username)  {
        return users.get(username);
    }

    @Override
    public String createAuth(String username){
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, username);
        authTokens.put(token, authData);
        return token;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException{
        if (authTokens.get(authToken) == null){
            throw new DataAccessException("AuthData does not exist");
        }
        return authTokens.get(authToken);
    }

    @Override
    public int createGame(String gameName){
        int gameID = nextGameID++;
        ChessGame newGame = new ChessGame();
        GameData gameData = new GameData(gameID, null, null, gameName, newGame);
        games.put(gameID, gameData);
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if (games.get(gameID) == null){
            throw new DataAccessException("Game does not exist");
        }
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames(){
        return games.values();
    }

    @Override
    public void joinGame(int gameID, String username, ChessGame.TeamColor playerColor) throws DataAccessException {
        GameData game = games.get(gameID);
        if (game == null){
            throw new DataAccessException("Game does not exist");
        }

        GameData updatedGame;

        if (playerColor == ChessGame.TeamColor.WHITE) {
            updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else {
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        }

        games.put(gameID, updatedGame);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            throw new DataAccessException("Auth token not found");
        }
        authTokens.remove(authToken);
    }
}
