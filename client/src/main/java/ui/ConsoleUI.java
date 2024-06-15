package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
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
    private Map<Integer, GameData> numberedList = null;

    public ConsoleUI(ServerFacade facade) {
        this.facade = facade;
        board = new ChessBoard();
        loggedIn = false;
    }


    public void run() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        String input = "";
        Scanner scanner = new Scanner(System.in);

        out.print(ERASE_SCREEN);
        out.print("♕ Welcome to 240 chess. Type Help to get started. ♕\n");

        while (!Objects.equals(input, "quit")) {
            if (!(loggedIn)) {
                out.print("[LOGGED_OUT] >>> ");
                input = scanner.nextLine();
                preLogin(out, scanner,  input);
            } else {
                out.print("[LOGGED_IN] >>> ");
                input = scanner.nextLine();
                postLogin(out, scanner, input);
            }
        }
    }

    public void preLogin(PrintStream out, Scanner scanner, String input) {
        switch (input) {
            case "register":
                register(out, scanner);
                break;
            case "login":
                login(out, scanner);
                break;
            case "help":
                help(out);
                break;
            case "quit":
                break;
            default:
                out.println("Invalid command. Type 'help' for a list of commands.");
        }
    }

    private void postLogin(PrintStream out, Scanner scanner, String input) {
        switch (input) {
            case "create":
                create(out, scanner);
                break;
            case "list":
                list(out, scanner);
                break;
            case "play":
                play(out, scanner);
                break;
            case "observe":
                observe(out, scanner);
                break;
            case "logout":
                logout(out, scanner);
                break;
            case "quit":
                break;
            default:
                out.println("Invalid command. Type 'help' for a list of commands.");
        }
    }

    private void logout(PrintStream out, Scanner scanner) {
        try {
            facade.logout(auth.authToken());
            out.println("Successfully logged out.");
            loggedIn = false;
        } catch (Exception e) {
            out.println("Failed to logout: " + e.getMessage());
        }
    }

    private void observe(PrintStream out, Scanner scanner) {
        out.print("Enter the ID of the game you'd like to observe: ");
        int gameID = scanner.nextInt();

        // check if the given number is from the listgames function
        if (numberedList != null && numberedList.containsKey(gameID)) {
            GameData game = numberedList.get(gameID);
            gameID = game.gameID();
        }

        try {
            out.println("OBSERVE NOT YET IMPLEMENTED");
            out.println("CHESSBOARD HERE");
        } catch (Exception e) {
            out.println("Failed to observe game: " + e.getMessage());
        }
    }

    private void play(PrintStream out, Scanner scanner) {
        out.print("Enter either the number or ID of the game you'd like to join: ");
        int gameID = scanner.nextInt();
        out.print("Please enter your desired team (WHITE or BLACK): ");
        String team = scanner.nextLine();

        // check if the given number is from the listgames function
        if (numberedList != null && numberedList.containsKey(gameID)) {
            GameData game = numberedList.get(gameID);
            gameID = game.gameID();
        }

        try {
            facade.joinGame(auth.authToken(), team, gameID);
            out.println("Joined game ["+ gameID + "] as the " + team + " player");
            out.println("CHESSBOARD HERE");
        } catch (Exception e) {
            out.println("Failed to join game: " + e.getMessage());
        }
    }

    private void list(PrintStream out, Scanner scanner) {
        try {
            var gamesList = facade.listGames(auth.authToken());
            int i = 1;
            out.println("Games:");
            for (var game : gamesList) {
                out.println(i + " -- Name: " + game.gameName() + ", ID: " + game.gameID() + ", White player: " + game.whiteUsername() + ", Black player: " + game.blackUsername());
                numberedList.put(i, game);
                i++;
            }
        } catch (Exception e) {
            out.println("Failed to list games: " + e.getMessage());
        }
    }

    private void create(PrintStream out, Scanner scanner) {
        out.print("Enter a new game name: ");
        String gameName = scanner.nextLine();
        try {
            int id = facade.createGame(auth.authToken(), gameName);
            out.println("Game created with ID: " + id);
        } catch (Exception e) {
            out.println("Failed to create game: " + e.getMessage());
        }
    }




    public void register (PrintStream out, Scanner scanner){
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
            out.println("Registration failed: " + e.getMessage());
        }
    }

    public void login (PrintStream out, Scanner scanner){
        out.print("Enter username: ");
        String username = scanner.nextLine();
        out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            auth = facade.login(username, password);
            out.println("Login successful.  Hello, " + username + "!");
            loggedIn = true;
        } catch (Exception e) {
            out.println("Login failed: " + e.getMessage());
        }
    }



    public void help(PrintStream out) {
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
}
