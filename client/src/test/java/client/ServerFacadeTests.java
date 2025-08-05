package client;

import chess.ChessGame;
import facade.ServerFacade;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;


import java.util.Collection;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    public void clearDB(){
        facade.makeRequest("DELETE", "/db", null, null, null);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("If register when valid then pass")
    public void register_pass(){
        AuthData authData = facade.register("testuser", "testpass", "testemail");
        Assertions.assertNotNull(authData);
    }

    @Test
    @DisplayName("If register when invalid then fail")
    public void register_fail(){
        facade.register("failuser", "failpass", "failmail");
        AuthData authData = facade.register("failuser", "failpass", "failmail");
        Assertions.assertNull(authData);
    }

    @Test
    @DisplayName("If login when valid then pass")
    public void login_pass(){
        facade.register("testuser", "testpass", "testemail");
        AuthData authData = facade.login("testuser", "testpass");
        Assertions.assertNotNull(authData.authToken());
    }

    @Test
    @DisplayName("If login when invalid then fail")
    public void login_fail(){
        facade.register("testuser", "testpass", "testemail");
        AuthData authData = facade.login("testuser", "wrongpass");
        Assertions.assertNull(authData);
    }

    @Test
    @DisplayName("If logout when valid then pass")
    public void logout_pass(){
        AuthData authData = facade.register("testuser", "testpass", "testemail");
        facade.login("testuser", "testpass");
        Assertions.assertDoesNotThrow(() -> facade.logout(authData.authToken()));
    }

    @Test
    @DisplayName("If logout when invalid then fail")
    public void logout_fail(){
        Assertions.assertDoesNotThrow(() -> facade.logout("fake-logout"));
    }

    @Test
    @DisplayName("If create game when valid then pass")
    public void create_game_pass(){
        AuthData authData = facade.register("testuser", "testpass", "testemail");
        facade.login("testuser", "testpass");
        Integer gameID = facade.createGame("testgame", authData.authToken());
        Assertions.assertNotNull(gameID);
    }

    @Test
    @DisplayName("If create game when invalid then fail")
    public void create_game_fail(){
        AuthData authData = facade.register("testuser", "testpass", "testemail");
        facade.login("testuser", "testpass");
        Integer gameID = facade.createGame("testgame", "fake");
        Assertions.assertNull(gameID);
    }

    @Test
    @DisplayName("If list games when valid then pass")
    public void list_game_pass(){
        AuthData authData = facade.register("testuser", "testpass", "testemail");
        facade.login("testuser", "testpass");
        Integer gameID = facade.createGame("testgame", authData.authToken());
        Collection<GameData> games = facade.listGames(authData.authToken());
        Assertions.assertEquals(1, games.size());
    }

    @Test
    @DisplayName("If list games when invalid then fail")
    public void list_game_fail(){
        AuthData authData = facade.register("testuser", "testpass", "testemail");
        facade.login("testuser", "testpass");
        Integer gameID = facade.createGame("testgame", authData.authToken());
        Collection<GameData> games = facade.listGames("fake");
        Assertions.assertNull(games);
    }

    @Test
    @DisplayName("If join game when valid then pass")
    public void join_game_pass(){
        AuthData authData = facade.register("testuser", "testpass", "testemail");
        facade.login("testuser", "testpass");
        Integer gameID = facade.createGame("testgame", authData.authToken());
        Assertions.assertDoesNotThrow(() -> facade.joinGame(gameID, "testuser", ChessGame.TeamColor.WHITE, authData.authToken()));
    }

    @Test
    @DisplayName("If join game when invalid then fail")
    public void join_game_fail(){
        AuthData authData = facade.register("testuser", "testpass", "testemail");
        facade.login("testuser", "testpass");
        Integer gameID = facade.createGame("testgame", authData.authToken());

        Assertions.assertDoesNotThrow(() -> facade.joinGame(9000, "testuser", ChessGame.TeamColor.WHITE, authData.authToken()));
    }

}
