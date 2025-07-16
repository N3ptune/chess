package server;

import dataaccess.MemoryDAO;
import model.request.JoinGameRequest;
import model.result.JoinGameResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;

public class JoinGameHandler implements Route {

    private final GameService gameService;

    public JoinGameHandler(GameService gameService){
        this.gameService = gameService;
    }

    private final Gson gson = new Gson();

    @Override
    public Object handle(Request request, Response response) throws Exception {

        String authToken = request.headers("Authorization");

        JoinGameRequest joinGameRequest = gson.fromJson(request.body(), JoinGameRequest.class);
        JoinGameRequest joinWithAuth = new JoinGameRequest(authToken, joinGameRequest.playerColor(), joinGameRequest.gameID());
        JoinGameResult joinGameResult = gameService.joinGame(joinWithAuth);

        if (joinGameResult.message() != null){
            if (joinGameResult.message().contains("request")){
                response.status(400);
            } else if (joinGameResult.message().contains("unauthorized")){
                response.status(401);
            }else if (joinGameResult.message().contains("taken")){
                response.status(403);
            }else if (joinGameResult.message().contains("Game does not exist")){
                response.status(400);
            } else if (joinGameResult.message().contains("AuthData does not exist")){
                response.status(401);
            }else if (joinGameResult.message().contains("color")){
                response.status(400);
            }else {
                response.status(500);
            }
        } else {
            response.status(200);
        }

        return gson.toJson(joinGameResult);

    }
}
