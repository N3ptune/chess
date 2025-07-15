package server;


import model.request.CreateGameRequest;
import model.result.CreateGameResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;

public class CreateGameHandler implements Route {


    private final GameService gameService = new GameService();
    private final Gson gson = new Gson();

    @Override
    public Object handle(Request request, Response response) throws Exception {

        CreateGameRequest createGameRequest = gson.fromJson(request.body(), CreateGameRequest.class);
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);

        if (createGameResult.message() != null){
            if (createGameResult.message().toLowerCase().contains("request")){
                response.status(400);
            } else if (createGameResult.message().toLowerCase().contains("unauthorized")){
                response.status(401);
            } else {
                response.status(500);
            }
        } else {
            response.status(200);
        }

        return gson.toJson(createGameRequest);

    }
}
