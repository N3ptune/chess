package server;

import model.request.ListGamesRequest;
import model.result.ListGamesResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;

public class ListGamesHandler implements Route {

    private final GameService gameService = new GameService();
    private final Gson gson = new Gson();

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String authToken = request.headers("Authorization");
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        ListGamesResult listGamesResult = gameService.listGames(listGamesRequest);

        if (listGamesResult.message() != null){
             if (listGamesResult.message().contains("unauthorized")){
                response.status(401);
            }else {
                response.status(500);
            }
        } else {
            response.status(200);
        }

        return gson.toJson(listGamesResult);

    }
}
