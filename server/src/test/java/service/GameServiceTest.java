package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameServiceTest {

    private GameService gameService;
    private UserService userService;
    private ClearService clearService;
    private DataAccess dao;

    @BeforeEach
    public void setup(){
        dao = new MemoryDAO();
        clearService = new ClearService(dao);
        gameService = new GameService(dao);
        userService = new UserService(dao);
    }

    @Test
    @DisplayName("If create when valid then success")
    public void createGameSuccess(){

    }

    @Test
    @DisplayName("If create when invalid then fail")
    public void  createGameFailure(){}

    @Test
    @DisplayName("If join when valid then success")
    public void joinGameSuccess(){}

    @Test
    @DisplayName("If join when invalid then fail")
    public void joinGameFail(){}

    @Test
    @DisplayName("If list when valid then success")
    public void listGamesSuccess(){}

    @Test
    @DisplayName("If list when invalid then fail")
    public void listGamesFail(){}

}
