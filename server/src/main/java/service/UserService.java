package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDAO;
import model.UserData;
import model.request.LoginRequest;
import model.request.LogoutRequest;
import model.request.RegisterRequest;
import model.result.LoginResult;
import model.result.RegisterResult;
import model.result.LogoutResult;


// THis will have Register, Login, and Logout

public class UserService {

    private final DataAccess dao = new MemoryDAO();

    public RegisterResult register(RegisterRequest registerRequest) {
        try{
            if (registerRequest.username() == null || registerRequest.password() == null){
                return new RegisterResult(null, null, "Error: Username or Password empty");
            }

            UserData existing = dao.getUser(registerRequest.username());
            if (existing != null){
                return new RegisterResult(null, null, "Error: username already taken");
            }

            UserData newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            dao.createUser(newUser);

            String authToken = dao.createAuth(registerRequest.username());

            return new RegisterResult(registerRequest.username(), authToken, null);

        } catch (DataAccessException e){
            return new RegisterResult(null, null, "Error: " + e.getMessage());
        }
    }
    public LoginResult login(LoginRequest loginRequest) {return new LoginResult();}
    public LogoutResult logout(LogoutRequest logoutRequest) {return new LogoutResult();}
}