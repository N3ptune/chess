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
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class MySQLDataAccess implements DataAccess{


    public MySQLDataAccess() throws DataAccessException{
        configureDatabase();
    }

    @Override
    public void clear() {
        try {

            executeUpdate("DELETE FROM auth");
            executeUpdate("DELETE FROM games");
            executeUpdate("DELETE FROM users");
        } catch (Exception e){
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException, SQLException {
        String sqlInput = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(sqlInput, user.username(), user.password(), user.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            var statement = "SELECT * FROM users WHERE username =?";
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
    public String createAuth(String username) throws DataAccessException, SQLException{
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, username);
        String sqlInput = "INSERT INTO auth (token, username) VALUES (?,?)";
        executeUpdate(sqlInput, token, username);
        return token;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()){
            var statement = "SELECT * FROM auth WHERE token = ?";
            try (var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data: %s" + e.getMessage());
        }
        return null;
    }

    @Override
    public int createGame(String gameName) throws SQLException, DataAccessException {
        ChessGame newGame = new ChessGame();
        String json = new Gson().toJson(newGame);
        String sqlInput = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        return executeUpdate(sqlInput, null, null, gameName, json);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()){
            var statement = "SELECT * FROM games where gameID = ?";
            try (var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()){
                    if (rs.next()){
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data: %s" + e.getMessage());
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        var games = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()){
            var statement = "SELECT * FROM games";
            try (var preparedStatement = conn.prepareStatement(statement)){
                var rs = preparedStatement.executeQuery();
                while (rs.next()){
                    games.add(readGame(rs));
                }
            }
        } catch (Exception e ){
            throw new DataAccessException("Unable to read data: %s" + e.getMessage());
        }
        return games;
    }

    @Override
    public void joinGame(int gameID, String username, ChessGame.TeamColor playerColor) throws DataAccessException, SQLException {

        try (var conn = DatabaseManager.getConnection()){
            String sqlSelect = "SELECT whiteUsername, blackUsername FROM games where gameID = ?";
            try (var preparedStatement = conn.prepareStatement(sqlSelect)) {
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()){
                    if (!rs.next()){
                        throw new DataAccessException("Game does not exist");
                    }

                    if (playerColor == ChessGame.TeamColor.WHITE){
                        String sqlInput = "UPDATE games SET whiteUsername =? WHERE gameID = ?";
                        try (var preparedStatement2 = conn.prepareStatement(sqlInput)){
                            preparedStatement2.setString(1, username);
                            preparedStatement2.setInt(2, gameID);
                            preparedStatement2.executeUpdate();
                        }
                    } else if (playerColor == ChessGame.TeamColor.BLACK) {
                        String sqlInput = "UPDATE games SET blackUsername =? WHERE gameID = ?";
                        try (var preparedStatement2 = conn.prepareStatement(sqlInput)){
                            preparedStatement2.setString(1, username);
                            preparedStatement2.setInt(2, gameID);
                            preparedStatement2.executeUpdate();
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data: %s" + e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

        try {
            if (executeUpdate("DELETE FROM auth WHERE token = ?", authToken) != 0){
                throw new DataAccessException("Auth token not found");
            }
        } catch (Exception e){
            throw new DataAccessException("Unable to delete auth: " + e.getMessage());
        }
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
        var json = rs.getString("game");
        var game = new Gson().fromJson(json, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var authToken = rs.getString("token");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
            `username` varchar(256) NOT NULL PRIMARY KEY,
            `password` varchar(256) NOT NULL,
            `email` varchar(256) NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
            `gameID` INT PRIMARY KEY AUTO_INCREMENT,
            `whiteUsername` varchar(256),
            `blackUsername` varchar(256),
            `gameName` varchar(256) NOT NULL,
            `game` TEXT NOT NULL,
            FOREIGN KEY (`whiteUsername`) REFERENCES `users`(`username`),
            FOREIGN KEY (`blackUsername`) REFERENCES `users`(`username`)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS auth (
            `token` varchar(256) NOT NULL PRIMARY KEY,
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
