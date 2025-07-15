package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {
    void clear();
    void createUser(UserData user);
    UserData getUser(String username) throws DataAccessException;
    String createAuth(String username);
    AuthData getAuth(String authToken) throws DataAccessException;
    int createGame(String gameName);
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames();
    void joinGame(int gameID, String username, ChessGame.TeamColor playerColor) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
}
