package state;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import websocket.GameClientEndpoint;

import java.util.List;

public class ClientState {

    private String authToken;
    private String username;
    private Integer currentGameID;
    private ChessGame.TeamColor playerColor;
    private List<GameData> lastListedGames;
    private GameClientEndpoint endpoint;
    private ChessGame game;

    public void setEndpoint(GameClientEndpoint endpoint){
        this.endpoint = endpoint;
    }

    public GameClientEndpoint getEndpoint() {
        return endpoint;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }

    public void setLastListedGames(List<GameData> games){
        this.lastListedGames = games;
    }

    public List<GameData> getLastListedGames(){
        return lastListedGames;
    }

    public void login(AuthData authData, String username){
        this.authToken = authData.authToken();
        this.username = username;
    }

    public String getAuthToken(){
        return authToken;
    }

    public void logout(){
        this.authToken = null;
        this.username = null;
        this.currentGameID = null;
        this.playerColor = null;
    }

    public Integer getCurrentGameID(){
        return currentGameID;
    }

    public ChessGame.TeamColor getPlayerColor(){
        return playerColor;
    }

    public void exitGame(){
        this.currentGameID = null;
        this.playerColor = null;
    }

    public void joinGame(Integer gameID, ChessGame.TeamColor playerColor){
        this.currentGameID = gameID;
        this.playerColor = playerColor;
    }

    public String getUsername(){
        return username;
    }

    public boolean isLoggedIn(){
        return authToken != null;
    }

    public boolean isInGame(){
        return currentGameID != null;
    }

    public void setCurrentGameID(Integer currentGameID) {
        this.currentGameID = currentGameID;
    }

    public void setPlayerColor(ChessGame.TeamColor playerColor){
        this.playerColor = playerColor;
    }
}
