package server;


import model.request.LoginRequest;
import model.result.LoginResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;

public class LoginHandler implements Route {

    private final UserService userService = new UserService();
    private final Gson gson = new Gson();

    @Override
    public Object handle(Request request, Response response) throws Exception {
        LoginRequest loginRequest = gson.fromJson(request.body(), LoginRequest.class);
        LoginResult loginResult = userService.login(loginRequest);

        if (loginResult.message() != null){
            if (loginResult.message().contains("request")){
                response.status(400);
            } else if (loginResult.message().contains("unauthorized")){
                response.status(401);
            }else {
                response.status(500);
            }
        } else {
            response.status(200);
        }

        return gson.toJson(loginResult);
    }
}
