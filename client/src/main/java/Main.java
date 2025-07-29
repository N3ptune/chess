import chess.*;
import facade.ServerFacade;
import state.ClientState;
import ui.GameplayUI;
import ui.PostloginUI;
import ui.PreloginUI;

import java.util.Arrays;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ClientState state = new ClientState();
    private static ServerFacade facade;

    public static void main(String[] args) {
        facade = new ServerFacade("http://localhost:8080/");
        System.out.println("♕ Welcome to Chess! Sign in to start. ♕");
        System.out.println("Options: ");
        System.out.println("Login as an existing user: \"l\", \"login\" <USERNAME> <PASSWORD>");
        System.out.println("Register as a new user: \"r\", \"register\" <USERNAME> <PASSWORD> <EMAIL>");
        System.out.println("Exit the program \"q\", \"quit\"");
        System.out.println("Print this message: \"h\", \"help\"");


        while (true){

            System.out.println("Chess >>> ");

            String line = scanner.nextLine().trim();

            if (line.isEmpty()){
                continue;
            }

            String [] parts = line.split("\\s+");
            String command = parts[0].toLowerCase();
            String[] commandArgs = Arrays.copyOfRange(parts, 1, parts.length);

            if (!state.isLoggedIn()){
                PreloginUI.handleCommand(command, commandArgs, state, facade);
            } else if (state.isInGame()){
                GameplayUI.handleCommand(command, commandArgs, state, facade);
            } else {
                PostloginUI.handleCommand(command, commandArgs, state, facade);
            }
        }
    }
}