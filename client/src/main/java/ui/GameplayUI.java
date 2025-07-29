package ui;

import facade.ServerFacade;
import state.ClientState;

public class GameplayUI {

    public static void handleCommand(String command, String[] commandArgs, ClientState state, ServerFacade facade){
        switch (command){
            case "help" -> showHelp();
            default -> System.out.println("Command not recognized. Please try again or type \"help\" for a list of available commands.");
        }
    }
    private static void showHelp(){
        System.out.println("Nothing to see here yet :)");
    }
}
