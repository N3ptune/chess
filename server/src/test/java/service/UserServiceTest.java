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

public class UserServiceTest {

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


}
