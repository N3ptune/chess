package state;

import chess.ChessGame;
import model.AuthData;

public class ClientState {

    private String authToken;
    private String username;
    private Integer currentGameID;
    private ChessGame.TeamColor playerColor;

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
}
