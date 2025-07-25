package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import model.request.LoginRequest;
import model.request.LogoutRequest;
import model.request.RegisterRequest;
import model.result.LoginResult;
import model.result.RegisterResult;
import model.result.LogoutResult;

import java.sql.SQLException;


public class UserService {

    private final DataAccess dao;

    public UserService(DataAccess dao){
        this.dao = dao;
    }

    public RegisterResult register(RegisterRequest registerRequest) {
            if (registerRequest.username() == null || registerRequest.password() == null){
                return new RegisterResult(null, null, "Error: bad request");
            }
            try {
                UserData existing = dao.getUser(registerRequest.username());
                if (existing != null) {
                    return new RegisterResult(null, null, "Error: already taken");
                }

                UserData newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
                dao.createUser(newUser);

                String authToken = dao.createAuth(registerRequest.username());

                return new RegisterResult(registerRequest.username(), authToken, null);
            } catch (DataAccessException | SQLException e){
                return new RegisterResult(null, null, "Error: " + e.getMessage());
            }
    }
    public LoginResult login(LoginRequest loginRequest) {

            if (loginRequest.username() == null || loginRequest.password() == null){
                return new LoginResult(null, null, "Error: bad request");
            }
            try {
                UserData existing = dao.getUser(loginRequest.username());

                if (existing == null) {
                    return new LoginResult(null, null, "Error: user does not exist");
                }


                if (!loginRequest.password().equals(existing.password())) {
                    return new LoginResult(null, null, "Error: unauthorized");
                }

                String authToken = dao.createAuth(loginRequest.username());

                return new LoginResult(loginRequest.username(), authToken, null);
            } catch (DataAccessException | SQLException e){
                return new LoginResult(null, null, "Error: " + e.getMessage());
            }

    }
    public LogoutResult logout(LogoutRequest logoutRequest) {
        try{

            AuthData authData = dao.getAuth(logoutRequest.authToken());

            if (!logoutRequest.authToken().equals(authData.authToken())){
                return new LogoutResult("Error: unauthorized");
            }

            dao.deleteAuth(logoutRequest.authToken());

            return new LogoutResult(null);
        } catch (DataAccessException e){
            return new LogoutResult("Error: " + e.getMessage());
        }
    }
}