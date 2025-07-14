package service;

import model.request.LoginRequest;
import model.request.LogoutRequest;
import model.request.RegisterRequest;
import model.result.LoginResult;
import model.result.RegisterResult;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) {return new RegisterResult();}
    public LoginResult login(LoginRequest loginRequest) {return new LoginResult();}
    public void logout(LogoutRequest logoutRequest) {}
}