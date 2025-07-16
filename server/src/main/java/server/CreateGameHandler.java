package server;


import model.request.CreateGameRequest;
import model.result.CreateGameResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;

public class CreateGameHandler implements Route {

    private final GameService gameService;

    public CreateGameHandler(GameService gameService){
        this.gameService = gameService;
    }

    private final Gson gson = new Gson();

    @Override
    public Object handle(Request request, Response response) throws Exception {

        String authToken = request.headers("Authorization");

        CreateGameRequest createGameRequest = gson.fromJson(request.body(), CreateGameRequest.class);
        CreateGameRequest requestWithAuth = new CreateGameRequest(authToken, createGameRequest.gameName());
        CreateGameResult createGameResult = gameService.createGame(requestWithAuth);

        if (createGameResult.message() != null){
            if (createGameResult.message().contains("request")){
                response.status(400);
            } else if (createGameResult.message().contains("unauthorized")){
                response.status(401);
            } else if (createGameResult.message().contains("exist")) {
                response.status(401);
            } else {
                response.status(500);
            }
        } else {
            response.status(200);
        }

        return gson.toJson(createGameResult);

    }
}
