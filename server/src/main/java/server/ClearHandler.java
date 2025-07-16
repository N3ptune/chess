package server;


import model.request.ClearRequest;
import model.result.ClearResult;
import service.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;
import com.google.gson.Gson;

public class ClearHandler implements Route{

    private final ClearService clearService;

    public ClearHandler(ClearService clearService){
        this.clearService = clearService;
    }
    private final Gson gson = new Gson();

    @Override
    public Object handle(Request request, Response response) throws Exception {


        ClearRequest clearRequest = new ClearRequest();
        ClearResult clearResult = clearService.clear(clearRequest);

        if (clearResult.message() != null){
            if (clearResult.message().contains("exist")){
                response.status(401);
            }
        }


        response.status(200);

        return gson.toJson(clearResult);
    }
}
