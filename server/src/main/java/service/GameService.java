package service;

import model.GameData;
import model.request.JoinGameRequest;
import model.request.CreateGameRequest;
import model.result.CreateGameResult;
import model.result.JoinGameResult;
import model.result.ListGamesResult;


// This will have Join Game, Create Game, and List Games

public class GameService {
    public JoinGameResult joinGame(JoinGameRequest joinGame){return new JoinGameResult();}
    public CreateGameResult createGame(CreateGameRequest createGame){return new CreateGameResult();}
    public ListGamesResult listGames(String authToken){
        return new ListGamesResult();
    }
}
