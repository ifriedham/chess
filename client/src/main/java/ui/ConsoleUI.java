package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import chess.*;
import client.ServerFacade;
import model.AuthData;
import model.GameData;

import static ui.EscapeSequences.*;

public class ConsoleUI {
    ServerFacade facade;
    ChessBoard board;
    boolean loggedIn;
    private AuthData auth;
    private Map<Integer, GameData> numberedList;
    PrintStream out;
    Scanner scanner;

    public ConsoleUI(ServerFacade facade) {
        this.facade = facade;
        board = new ChessBoard();
        loggedIn = false;
        numberedList = new HashMap<>();
    }

    public void run() {
        out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        String input = "";
        scanner = new Scanner(System.in);

        out.print(ERASE_SCREEN);
        out.println("♕ Welcome to 240 chess. Type 'help' to get started. ♕");

        while (!Objects.equals(input, "quit")) {
            if (!(loggedIn)) {
                out.print("[LOGGED_OUT] >>> ");
                input = scanner.nextLine();
                preLogin(input);
            } else {
                out.print("[LOGGED_IN] >>> ");
                input = scanner.nextLine();
                postLogin(input);
            }
        }
        out.println("Hope you had fun, bye!");
    }


    public void preLogin(String input) {
        switch (input) {
            case "register":
                register();
                break;
            case "login":
                login();
                break;
            case "help":
                help();
                break;
            case "quit":
                break;
            default:
                out.println("Invalid command. Type 'help' for a list of commands.");
        }
    }

    private void postLogin(String input) {
        switch (input) {
            case "create":
                create();
                break;
            case "list":
                list();
                break;
            case "play":
                play();
                break;
            case "observe":
                observe();
                break;
            case "logout":
                logout();
                break;
            case "help":
                help();
                break;
            case "quit":
                break;
            case "exit":
                break;
            default:
                out.println("Invalid command. Type 'help' for a list of commands.");
        }
    }


    private void logout() {
        try {
            facade.logout(auth.authToken());
            out.println("Successfully logged out.");
            loggedIn = false;
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    private void observe() {
        out.print("Enter the number of the game you'd like to observe: ");
        String input = scanner.nextLine();

        if (isInList(input)) {
            int gameNum = Integer.parseInt(input);
            GameData listedGame = numberedList.get(gameNum);
            ChessGame game = listedGame.game();

            // print out boards
            new BoardUI(out, game).printBoard();
        }
    }

    private void play() {
        out.print("Enter either the number of the game you'd like to join: ");
        String input = scanner.nextLine();

        if (isInList(input)) {
            // get game ID from list
            int gameNum = Integer.parseInt(input);
            GameData listedGame = numberedList.get(gameNum);
            int gameID = listedGame.gameID();
            ChessGame game = listedGame.game();

            // get desired team
            out.print("Please enter your desired team (WHITE or BLACK): ");
            String givenTeam = scanner.nextLine();
            String team = givenTeam.toUpperCase(); // in case they give a lowercase team
            // check if input is valid
            if (!team.equals("WHITE") && !team.equals("BLACK")) {
                out.println("Invalid team. Please enter either 'WHITE' or 'BLACK'.");
                return;
            }

            // join game
            try {
                facade.joinGame(auth.authToken(), team, gameID);
                out.println("Joined game ["+ gameID + "] as the " + team + " player");

                // proceed to Gameplay menu
                new GameplayUI(scanner, out, game);


                new BoardUI(out, game).printBoard();
            } catch (Exception e) {
                out.println("Failed to join game");
            }
        }
    }

    private void list() {
        try {
            var gamesList = facade.listGames(auth.authToken());
            int i = 1;
            if (numberedList != null) numberedList.clear();
            out.println("Games:");
            for (var game : gamesList) {
                out.println(i + " -- Name: " + game.gameName() + ", White player: " + game.whiteUsername() + ", Black player: " + game.blackUsername());
                numberedList.put(i, game);
                i++;
            }
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    private void create() {
        out.print("Enter a new game name: ");
        String gameName = scanner.nextLine();
        try {
            int id = facade.createGame(auth.authToken(), gameName);
            out.println("Game created with ID: " + id);
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    public void register (){
        out.print("Enter new username: ");
        String username = scanner.nextLine();
        out.print("Enter new password: ");
        String password = scanner.nextLine();
        out.print("Enter new email: ");
        String email = scanner.nextLine();

        try {
            auth = facade.register(username, password, email);
            out.println("Registration successful.  Hello, " + username + "!");
            loggedIn = true;
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    public void login (){
        out.print("Enter username: ");
        String username = scanner.nextLine();
        out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            auth = facade.login(username, password);
            out.println("Login successful.  Hello, " + username + "!");
            loggedIn = true;
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    public void help() {
        if (loggedIn) {
            out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "create" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + " - make a new game");
            out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "list" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + " - get a list of games");
            out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "play" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + " - join a game as either BLACK or WHITE");
            out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "observe" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + " - view a game as a spectator");
            out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "logout" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + " - logout and return to the main menu");
        }
        else {
            out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "register" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + " - to create an account");
            out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "login" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + " - to login");
        }
        out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "help" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + " - to display this help menu");
        out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "quit" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + " - to exit this program");
    }

    private boolean isInList(String input) {
        int gameNum = 0;
        // check valid input
        try {
            gameNum = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            out.println("Invalid input. Please enter a number.");
            return false;
        }

        if (numberedList == null || !numberedList.containsKey(gameNum)) {
            out.println("That game doesn't exist. Please enter a valid number (use 'list' to see available games).");
            return false;
        }
        return true;
    }

}
