package server;

import model.request.LogoutRequest;
import model.result.LogoutResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;

public class LogoutHandler implements Route{

    private final UserService userService = new UserService();
    private final Gson gson = new Gson();

    @Override
    public Object handle(Request request, Response response) throws Exception {
        LogoutRequest logoutRequest = gson.fromJson(request.body(), LogoutRequest.class);
        LogoutResult logoutResult = userService.logout(logoutRequest);

        if (logoutResult.message() != null){
            if (logoutResult.message().toLowerCase().contains("unauthorized")){
                response.status(401);
            }else {
                response.status(500);
            }
        } else {
            response.status(200);
        }

        return gson.toJson(logoutResult);
    }
}
