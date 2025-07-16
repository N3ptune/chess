package server;

import model.request.RegisterRequest;
import model.result.RegisterResult;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;

public class RegisterHandler implements Route{

    private final UserService userService;

    public RegisterHandler(UserService userService){
        this.userService = userService;
    }

    private final Gson gson = new Gson();

    @Override
    public Object handle(Request request, Response response) throws Exception {
        RegisterRequest registerRequest = gson.fromJson(request.body(), RegisterRequest.class);
        RegisterResult registerResult = userService.register(registerRequest);

        if (registerResult.message() != null){
            if (registerResult.message().contains("request")){
                response.status(400);
            } else if (registerResult.message().contains("taken")){
                response.status(403);
            } else {
                response.status(500);
            }
        } else {
            response.status(200);
        }

        return gson.toJson(registerResult);
    }
}
