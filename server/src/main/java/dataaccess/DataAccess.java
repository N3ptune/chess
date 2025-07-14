package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {
    void clear();
    void createUser();
    UserData getUser();
    String createAuth(String username);
    AuthData getAuth();
    int createGame(GameData game);
    GameData getGame(int gameID);
    Collection<GameData> listGames();
    void updateGame(GameData updated);
}
