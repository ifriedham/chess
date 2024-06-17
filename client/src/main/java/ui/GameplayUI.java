package ui;

import chess.ChessGame;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.ERASE_SCREEN;

public class GameplayUI {
    Scanner scanner;
    PrintStream out;
    ChessGame game;


    public GameplayUI(Scanner scanner, PrintStream out, ChessGame game) {
        this.scanner = scanner;
        this.out = out;
        this.game = game;
    }

    public void run() {
        new BoardUI(out, game).printBoard();

    }


}
