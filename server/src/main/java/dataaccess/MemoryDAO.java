package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryDAO implements DataAccess{

    private Map<String, AuthData> authTokens = new HashMap<>();

    @Override
    public void clear() {

    }

    @Override
    public void createUser() {

    }

    @Override
    public UserData getUser() {
        return null;
    }

    @Override
    public String createAuth(String username) {
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, username);
        authTokens.put(token, authData);
        return token;
    }


    @Override
    public AuthData getAuth() {
        return null;
    }

    @Override
    public int createGame(GameData game) {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public void updateGame(GameData updated) {

    }
}
