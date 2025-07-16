package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import model.request.*;
import model.result.*;
import dataaccess.*;

public class ClearServiceTest {

    private ClearService clearService;
    private GameService gameService;
    private UserService userService;
    private DataAccess dao;

    @BeforeEach
    public void setup(){
        dao = new MemoryDAO();
        clearService = new ClearService(dao);
        gameService = new GameService(dao);
        userService = new UserService(dao);
    }

    @Test
    @DisplayName("If clear when full then success")
    public void clearWhenFull(){
        RegisterRequest registerRequest = new RegisterRequest("userName", "passWord", "email@test.com");
        RegisterResult registerResult = userService.register(registerRequest);
        Assertions.assertNotNull(registerResult.authToken());

        ClearResult clearResult = clearService.clear(new ClearRequest());
        Assertions.assertNull(clearResult.message());

        LoginResult loginResult = userService.login(new LoginRequest("userName", "passWord"));
        Assertions.assertEquals("Error: user does not exist", loginResult.message());
    }

    @Test
    @DisplayName("If clear when empty then success")
    public void clearWhenEmpty(){
        ClearResult clearResult = clearService.clear(new ClearRequest());
        Assertions.assertNull(clearResult.message());
    }


}