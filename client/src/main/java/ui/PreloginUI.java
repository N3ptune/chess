package ui;

import facade.ServerFacade;
import state.ClientState;

import java.util.Scanner;

public class PreloginUI {

    public static void handleCommand(String command, String[] commandArgs, ClientState state, ServerFacade facade){
        switch (command) {
            case "help" -> showHelp();
            case "register" -> doRegister(commandArgs, state, facade);
            case "login" -> doLogin();
            case "quit" -> {
                doLogout();
                System.out.println("You are logged out.");
                System.exit(0);
            }
            default -> System.out.println("Command not recognized. Please try again or type \"help\" for a list of available commands.");
        }
    }

    private static void showHelp(){
        System.out.println("Login as an existing user: \"l\", \"login\" <USERNAME> <PASSWORD>");
        System.out.println("Register as a new user: \"r\", \"register\" <USERNAME> <PASSWORD> <EMAIL>");
        System.out.println("Exit the program \"q\", \"quit\"");
        System.out.println("Chess >>> ");
    }

    private static void doRegister(String[] args, ClientState state, ServerFacade facade) {
        if (args.length < 3) {
            System.out.println("Please make sure to include <USERNAME> <PASSWORD> <EMAIL>");
            return;
        }
        String username = args[0];
        String password = args[1];
        String email = args[2];

        try {
            var authData = facade.register(username, password, email);
            state.login(authData, username);
        }
    }

    private static void doLogin(){}

    private static void doLogout(){}
}
