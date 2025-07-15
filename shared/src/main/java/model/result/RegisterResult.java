package model.result;

import java.util.Objects;

public class RegisterResult {

    private String username;
    private String authToken;
    private String message;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegisterResult that = (RegisterResult) o;
        return Objects.equals(username, that.username) && Objects.equals(authToken, that.authToken) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, authToken, message);
    }

    public RegisterResult(String username, String authToken){
        this.username = username;
        this.authToken = authToken;
    }

    public RegisterResult(String message){
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getMessage() {
        return message;
    }
}
