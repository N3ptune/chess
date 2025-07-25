package dataaccess;


import chess.ChessGame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import model.request.*;
import model.result.*;
import service.ClearService;
import service.GameService;
import service.UserService;

public class ServiceTests {

        private ClearService clearService;
        private GameService gameService;
        private UserService userService;
        private DataAccess dao;

        @BeforeEach
        public void setup() throws DataAccessException{
            dao = new MySQLDataAccess();
            clearService = new ClearService(dao);
            gameService = new GameService(dao);
            userService = new UserService(dao);

            clearService.clear(new ClearRequest());
        }

        @Test
        @DisplayName("If clear when full then success")
        public void clearWhenFull() {
            RegisterRequest registerRequest = new RegisterRequest("userName", "passWord", "email@test.com");
            RegisterResult registerResult = userService.register(registerRequest);
            Assertions.assertNotNull(registerResult.authToken());

            ClearResult clearResult = clearService.clear(new ClearRequest());
            Assertions.assertNull(clearResult.message());

            LoginResult loginResult = userService.login(new LoginRequest("userName", "passWord"));
            Assertions.assertNotNull(loginResult.message());
        }

        @Test
        @DisplayName("If clear when empty then success")
        public void clearWhenEmpty() {
            ClearResult clearResult = clearService.clear(new ClearRequest());
            Assertions.assertNull(clearResult.message());
        }

        @Test
        @DisplayName("If create when valid then success")
        public void createGameSuccess(){
            RegisterRequest registerRequest = new RegisterRequest("userName", "passWord", "email@test.com");
            RegisterResult registerResult = userService.register(registerRequest);
            Assertions.assertNotNull(registerResult.authToken());

            CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "TestGame");
            CreateGameResult createGameResult = gameService.createGame(createGameRequest);
            Assertions.assertNull(createGameResult.message());
        }

        @Test
        @DisplayName("If create when invalid then fail")
        public void  createGameFailure(){
            CreateGameRequest createGameRequest = new CreateGameRequest(null, null);
            CreateGameResult createGameResult = gameService.createGame(createGameRequest);
            Assertions.assertNotNull(createGameResult.message());
        }

        @Test
        @DisplayName("If join when valid then success")
        public void joinGameSuccess(){
            RegisterRequest registerRequest = new RegisterRequest("userName", "passWord", "email@test.com");
            RegisterResult registerResult = userService.register(registerRequest);
            Assertions.assertNotNull(registerResult.authToken());

            CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "TestGame");
            CreateGameResult createGameResult = gameService.createGame(createGameRequest);

            JoinGameRequest joinGameRequest = new JoinGameRequest(registerResult.authToken(), ChessGame.TeamColor.WHITE, createGameResult.gameID());
            JoinGameResult joinGameResult = gameService.joinGame(joinGameRequest);
            Assertions.assertNull(joinGameResult.message());
        }

        @Test
        @DisplayName("If join when invalid then fail")
        public void joinGameFail(){
            JoinGameRequest joinGameRequest = new JoinGameRequest(null, ChessGame.TeamColor.WHITE, 0);
            JoinGameResult joinGameResult = gameService.joinGame(joinGameRequest);
            Assertions.assertNotNull(joinGameResult.message());
        }

        @Test
        @DisplayName("If list when valid then success")
        public void listGamesSuccess(){
            RegisterRequest registerRequest = new RegisterRequest("userName", "passWord", "email@test.com");
            RegisterResult registerResult = userService.register(registerRequest);
            Assertions.assertNotNull(registerResult.authToken());

            ListGamesRequest listGamesRequest = new ListGamesRequest(registerResult.authToken());
            ListGamesResult listGamesResult = gameService.listGames(listGamesRequest);
            Assertions.assertNull(listGamesResult.message());

        }

        @Test
        @DisplayName("If list when invalid then fail")
        public void listGamesFail(){
            ListGamesRequest listGamesRequest = new ListGamesRequest(null);
            ListGamesResult listGamesResult = gameService.listGames(listGamesRequest);
            Assertions.assertNotNull(listGamesResult.message());
        }

        @Test
        @DisplayName("If register when valid the success")
        public void registerSuccess(){
            clearService.clear(new ClearRequest());
            RegisterRequest registerRequest = new RegisterRequest("userName", "passWord", "email@test.com");
            RegisterResult registerResult = userService.register(registerRequest);
            Assertions.assertNotNull(registerResult.authToken());
            Assertions.assertNull(registerResult.message());
        }

        @Test
        @DisplayName("If register when invalid then fail")
        public void registerFail(){
            RegisterRequest registerRequest = new RegisterRequest(null, "passWord", "email@test.com");
            RegisterResult registerResult = userService.register(registerRequest);
            Assertions.assertNotNull(registerResult.message());
        }

        @Test
        @DisplayName("If login when valid then success")
        public void loginSuccess(){
            RegisterRequest registerRequest = new RegisterRequest("userName", "passWord", "email@test.com");
            RegisterResult registerResult = userService.register(registerRequest);
            Assertions.assertNotNull(registerResult.authToken());

            LoginRequest loginRequest = new LoginRequest("userName", "passWord");
            LoginResult loginResult = userService.login(loginRequest);
            Assertions.assertNull(loginResult.message());
        }

        @Test
        @DisplayName("If login when invalid then fail")
        public void loginFail(){
            RegisterRequest registerRequest = new RegisterRequest("userName", "passWord", "email@test.com");
            RegisterResult registerResult = userService.register(registerRequest);
            Assertions.assertNotNull(registerResult.authToken());

            LoginRequest loginRequest = new LoginRequest("userName", "password");
            LoginResult loginResult = userService.login(loginRequest);
            Assertions.assertNotNull(loginResult.message());
        }

        @Test
        @DisplayName("If logout when valid then success")
        public void logoutSuccess(){
            RegisterRequest registerRequest = new RegisterRequest("userName", "passWord", "email@test.com");
            RegisterResult registerResult = userService.register(registerRequest);
            Assertions.assertNotNull(registerResult.authToken());

            LoginRequest loginRequest = new LoginRequest("userName", "passWord");
            LoginResult loginResult = userService.login(loginRequest);
            Assertions.assertNull(loginResult.message());

            LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
            LogoutResult logoutResult = userService.logout(logoutRequest);
            Assertions.assertNull(logoutResult.message());
        }

        @Test
        @DisplayName("If logout when invalid then fail")
        public void logoutFail(){
            RegisterRequest registerRequest = new RegisterRequest("userName", "passWord", "email@test.com");
            RegisterResult registerResult = userService.register(registerRequest);
            Assertions.assertNotNull(registerResult.authToken());

            LoginRequest loginRequest = new LoginRequest("userName", "passWord");
            LoginResult loginResult = userService.login(loginRequest);
            Assertions.assertNull(loginResult.message());

            LogoutRequest logoutRequest = new LogoutRequest(null);
            LogoutResult logoutResult = userService.logout(logoutRequest);
            Assertions.assertNotNull(logoutResult.message());
        }

}
