import chess.*;
import facade.ServerFacade;
import state.ClientState;
import ui.PostloginUI;
import ui.PreloginUI;

import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ClientState state = new ClientState();
    private static ServerFacade facade;

    public static void main(String[] args) {
        facade = new ServerFacade();
        System.out.println("♕ Welcome to Chess! Sign in to start. ♕");
        System.out.println("Options: ");
        System.out.println("Login as an existing user: \"l\", \"login\" <USERNAME> <PASSWORD>");
        System.out.println("Register as a new user: \"r\", \"register\" <USERNAME> <PASSWORD> <EMAIL>");
        System.out.println("Exit the program \"q\", \"quit\"");
        System.out.println("Print this message: \"h\", \"help\"");
        System.out.println("Chess >>> ");

        while (true){
            if (!state.isLoggedIn()){
                PreloginUI.handleCommand(scanner.nextLine().trim().toLowerCase(), state, facade);
            } else {
                PostloginUI.handleCommand(scanner.nextLine().trim().toLowerCase(), state, facade);
            }
        }
    }
}