package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.util.Collection;

public interface DataAccess {
    void clear() throws SQLException, DataAccessException;
    void createUser(UserData user) throws DataAccessException, SQLException;
    UserData getUser(String username) throws DataAccessException;
    String createAuth(String username) throws DataAccessException, SQLException;
    AuthData getAuth(String authToken) throws DataAccessException;
    int createGame(String gameName) throws SQLException, DataAccessException;
    GameData getGame(int gameID) throws DataAccessException, SQLException;
    Collection<GameData> listGames() throws DataAccessException;
    void joinGame(int gameID, String username, ChessGame.TeamColor playerColor) throws DataAccessException, SQLException;
    void deleteAuth(String authToken) throws DataAccessException;
}
