package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collection;
import java.util.UUID;

public class MySQLDataAccess implements DataAccess{
    private int nextGameID = 1;


    public MySQLDataAccess() throws DataAccessException{
        configureDatabase();
    }

    @Override
    public void clear() {
        authTokens.clear();
        games.clear();
        users.clear();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException, SQLException {
        String sqlInput = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(sqlInput, user.username(), user.password(), user.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            var statement = "SELECT id, json FROM user WHERE username =?";
            try (var preparedStatment = conn.prepareStatement(statement)){
                preparedStatment.setString(1, username);
                try (var rs = preparedStatment.executeQuery()){
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data: %s" + e.getMessage());
        }
        return null;
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

    private int executeUpdate(String statement, Object... params) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()){
            try (var preparedStatement = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++){
                    Object param = params[i];
                    if (param instanceof String s){
                        preparedStatement.setString(i+1, s);
                    } else if (param instanceof Integer x){
                        preparedStatement.setInt(i + 1, x);
                    } else if (param == null){
                        preparedStatement.setNull(i+1, Types.NULL);
                    } else {
                        throw new DataAccessException("Unsupported parameter type: " + param.getClass());
                    }
                }
                preparedStatement.executeUpdate();

                try (var rs = preparedStatement.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }

                return 0;
            } catch (SQLException e) {
                throw new DataAccessException("SQL error: " + e.getMessage());
            }
        }
    }

    private UserData readUser(ResultSet rs) throws SQLException{
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username,password,email);
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var json = rs.getString("json");
        var game = new Gson().fromJson(json, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
            `gameID` INT PRIMARY KEY AUTO_INCREMENT,
            `whiteUsername` varchar(256) NOT NULL,
            `blackUsername` varchar(256) NOT NULL,
            `gameName` varchar(256) NOT NULL,
            FOREIGN KEY (`whiteUsername`) REFERENCES `users`(`username`),
            FOREIGN KEY (`blackUsername`) REFERENCES `users`(`username`)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS users (
            `username` varchar(256) NOT NULL,
            `password` varchar(256) NOT NULL,
            `email` varchar(256) NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS auth (
            `token` varchar(256) NOT NULL,
            `username` varchar(256) NOT NULL,
            FOREIGN KEY (`username`) REFERENCES `users`(`username`)
            )
            """

    };

    private void configureDatabase() throws DataAccessException{
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection();){
            for (var statement : createStatements){
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to configure database: %s" + e.getMessage());
        }

    }
}
