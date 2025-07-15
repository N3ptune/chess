package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.*;

public class MemoryDAO implements DataAccess{

    private final Map<String, AuthData> authTokens = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private final Map<String, UserData> users = new HashMap<>();

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
    public UserData getUser(String username) throws DataAccessException {
        if (!users.containsKey(username)){
            throw new DataAccessException("User does not exist");
        }
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
    public AuthData getAuth(String authToken) throws DataAccessException {
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
        if (playerColor == ChessGame.TeamColor.WHITE) {
            game = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else if (playerColor == ChessGame.TeamColor.BLACK) {
            game = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        }
        games.put(gameID, game);
    }
}
