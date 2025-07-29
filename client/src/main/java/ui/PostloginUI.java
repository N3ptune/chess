package ui;

import facade.ServerFacade;
import state.ClientState;

public class PostloginUI {

    public static void handleCommand(String command, String[] commandArgs, ClientState state, ServerFacade facade){
        switch (command) {
            case "help" -> showHelp();
            case "logout" -> doLogout(commandArgs, state, facade);
            case "create" -> doCreate(commandArgs, state, facade);
            case "list" -> doList(state, facade);
            case "join" -> doJoin(commandArgs, state, facade);
            case "observe" -> doObserve(commandArgs, state, facade);
            case "quit" -> {
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

    private static void doLogout(String[] args, ClientState state, ServerFacade facade){}

    private static void doCreate(String[] args, ClientState state, ServerFacade facade){}

    private static void doList(ClientState state, ServerFacade facade){}

    private static void doJoin(String[] args, ClientState state, ServerFacade facade){}

    private static void doObserve(String[] args, ClientState state, ServerFacade facade){}
}
