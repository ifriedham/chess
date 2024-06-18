package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import chess.*;
import client.ServerFacade;
import model.AuthData;
import model.GameData;

import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class ConsoleUI {
    private final ServerFacade facade;
    private ChessBoard board;
    private ChessGame game;
    private AuthData auth;
    private Map<Integer, GameData> numberedList;

    boolean loggedIn;
    boolean inGame;
    boolean isObserver;

    PrintStream out;
    Scanner scanner;
    private ChessGame.TeamColor playerColor;

    public ConsoleUI(ServerFacade facade) {
        this.facade = facade;
        board = new ChessBoard();
        game = new ChessGame();
        loggedIn = false;
        inGame = false;
        isObserver = true;
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
                if (inGame) inGame(input);
                else postLogin(input);
            }
        }
        out.println("Hope you had fun, bye!");
    }


    private void preLogin(String input) {
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
            case "exit", "quit":
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
            case "exit", "quit":
                break;
            default:
                out.println("Invalid command. Type 'help' for a list of commands.");
        }
    }

    private void inGame(String input){

        switch (input) {
            case "redraw":
                redraw();
                break;
            case "make move":
                makeMove();
                break;
            case "resign":
                resign();
                break;
            case "highlight":
                highlight();
                break;
            case "help":
                help();
                break;
            case "exit", "quit", "leave":
                inGame = false;
                break;
            default:
                out.println("Invalid command. Type 'help' for a list of commands. //INGAME");
        }
    }

    // INGAME LOGIC
    private void redraw() {
        new BoardUI(out, game).printBoard();
    }

    private void makeMove() {
        try {
            out.println("Which piece would you like to move? (ex: E4): ");
            String inputStart = scanner.nextLine();
            ChessPosition start = toPos(inputStart);

            out.println("Where would you like to move it? (ex: E5): ");
            String inputEnd = scanner.nextLine();
            ChessPosition end = toPos(inputEnd);

            ChessMove move = new ChessMove(start, end, null);
            facade.makeMove(move);
            new BoardUI(out, game).printBoard();
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    private void resign() {
        out.println("Are you sure you want to resign? (y/n): ");
        String input = scanner.nextLine();
        if (input.equals("y")) {
            try {
                facade.resign(auth.authToken());
                out.println("You have resigned the game.");
                inGame = false;
            } catch (Exception e) {
                out.println("Failed to resign.");
            }
        }
    }

    private void highlight() {
        out.println("Which piece's moves would you like to highlight? (ex: E4): ");
        String input = scanner.nextLine();


        ChessPosition pos = toPos(input);

        try {
            Collection<ChessMove> moves = facade.getValidMoves(pos);
            Collection<ChessPosition> endPositions = moves.stream().map(ChessMove::getEndPosition).toList();
            new BoardUI(out, game).printValidMoves(endPositions, pos, playerColor);
        } catch (Exception e) {
            out.println("Invalid move.");
        }
    }

    private ChessPosition toPos(String input) {
        if (input.length() != 2) {
            throw new IllegalArgumentException("Invalid input. Please enter a valid chess position (ex: E4).");
        }

        char colChar = input.charAt(0);
        char rowChar = input.charAt(1);

        if (!Character.isLetter(colChar) || !Character.isDigit(rowChar)) {
            throw new IllegalArgumentException("Invalid input. Please enter a valid chess position (ex: E4).");
        }

        int col = colChar - 'A' + 1;
        int row = Character.getNumericValue(rowChar);

        if (col < 1 || col > 8 || row < 1 || row > 8) {
            throw new IllegalArgumentException("Invalid input. Please enter a valid chess position (ex: E4).");
        }

        return new ChessPosition(row, col);
    }


    // STANDARD LOGIC
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
            game = listedGame.game();

            // print out board
            new BoardUI(out, game).printBoard(WHITE);
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
                out.println("Joined ["+ listedGame.gameName() + "] as the " + team + " player");

                // proceed to inGame ui
                game = listedGame.game();
                this.inGame = true;

                if (team.equals("WHITE")){
                    playerColor = ChessGame.TeamColor.WHITE;
                    new BoardUI(out, game).printBoard(WHITE);
                }
                else{
                    playerColor = ChessGame.TeamColor.BLACK;
                    new BoardUI(out, game).printBoard(ChessGame.TeamColor.BLACK);
                }

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
            out.println("Game created");
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
            if (inGame) {
                out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "make move" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + " - make a move in the game");
                out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "highlight" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + " - highlight possible legal moves");
                out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "redraw" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + " - redraw the board");
                out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "leave" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + " - leave the game");
                out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "resign" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + " - resign the game");
            } else {
                out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "create" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + " - make a new game");
                out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "list" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + " - get a list of games");
                out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "play" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + " - join a game as either BLACK or WHITE");
                out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "observe" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + " - view a game as a spectator");
                out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "logout" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + " - logout and return to the main menu");
            }
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
