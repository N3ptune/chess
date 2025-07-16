package server;

import model.request.JoinGameRequest;
import model.result.JoinGameResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;

public class JoinGameHandler implements Route {

    private final GameService gameService = new GameService();
    private final Gson gson = new Gson();

    @Override
    public Object handle(Request request, Response response) throws Exception {

        JoinGameRequest joinGameRequest = gson.fromJson(request.body(), JoinGameRequest.class);
        JoinGameResult joinGameResult = gameService.joinGame(joinGameRequest);

        if (joinGameResult.message() != null){
            if (joinGameResult.message().acontains("request")){
                response.status(400);
            } else if (joinGameResult.message().contains("unauthorized")){
                response.status(401);
            }else if (joinGameResult.message().contains("taken")){
                response.status(403);
            }else {
                response.status(500);
            }
        } else {
            response.status(200);
        }

        return gson.toJson(joinGameResult);

    }
}
