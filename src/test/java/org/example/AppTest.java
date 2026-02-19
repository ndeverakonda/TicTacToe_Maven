package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    private void captureStdout() {
        System.setOut(new PrintStream(out));
    }

    private String stdout() {
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    @AfterEach
    void restoreStdout() {
        System.setOut(originalOut);
        out.reset();
    }

    private static char[][] emptyBoard() {
        char[][] b = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) b[i][j] = ' ';
        }
        return b;
    }

    private static int countMarks(char[][] b, char ch) {
        int c = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (b[i][j] == ch) c++;
            }
        }
        return c;
    }

    // ---------------------------
    // finished() tests
    // ---------------------------

    @Test
    void finished_detectsXHorizontalWin() {
        char[][] b = emptyBoard();
        b[1][0] = 'X';
        b[1][1] = 'X';
        b[1][2] = 'X';

        boolean done = App.finished(b, true);

        assertTrue(done);
        assertEquals(1, App.xWin);
        assertEquals(0, App.oWin);
    }

    @Test
    void finished_detectsOVerticalWin() {
        char[][] b = emptyBoard();
        b[0][2] = 'O';
        b[1][2] = 'O';
        b[2][2] = 'O';

        boolean done = App.finished(b, true);

        assertTrue(done);
        assertEquals(0, App.xWin);
        assertEquals(1, App.oWin);
    }

    @Test
    void finished_detectsDiagonalLeftToRightWin() {
        char[][] b = emptyBoard();
        b[0][0] = 'X';
        b[1][1] = 'X';
        b[2][2] = 'X';

        boolean done = App.finished(b, true);

        assertTrue(done);
        assertEquals(1, App.xWin);
        assertEquals(0, App.oWin);
    }

    @Test
    void finished_detectsDiagonalRightToLeftWin() {
        char[][] b = emptyBoard();
        b[0][2] = 'O';
        b[1][1] = 'O';
        b[2][0] = 'O';

        boolean done = App.finished(b, true);

        assertTrue(done);
        assertEquals(0, App.xWin);
        assertEquals(1, App.oWin);
    }

    @Test
    void finished_detectsDraw_whenNoEmptyAndNoWinners() {
        char[][] b = {
                {'X', 'O', 'X'},
                {'X', 'O', 'O'},
                {'O', 'X', 'X'}
        };

        boolean done = App.finished(b, true);

        assertTrue(done);
        assertEquals(0, App.xWin);
        assertEquals(0, App.oWin);
    }

    @Test
    void finished_returnsFalse_whenGameNotFinished() {
        char[][] b = emptyBoard();
        b[0][0] = 'X';
        b[1][1] = 'O';

        boolean done = App.finished(b, true);

        assertFalse(done);
        assertEquals(0, App.xWin);
        assertEquals(0, App.oWin);
    }

    @Test
    void finished_printsResult_whenNotSilent() {
        captureStdout();

        char[][] b = emptyBoard();
        b[0][0] = 'X';
        b[0][1] = 'X';
        b[0][2] = 'X';

        boolean done = App.finished(b, false);

        assertTrue(done);
        assertTrue(stdout().contains("X wins"));
    }

    // ---------------------------
    // userMove() tests
    // ---------------------------

    @Test
    void userMove_placesMark_whenValidCoordinatesProvided() {
        char[][] b = emptyBoard();

        // Coordinates are 1-based in your program
        String input = "2 3\n";
        Scanner sc = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        App.userMove(b, 'X', sc);

        assertEquals('X', b[1][2]);
    }

    @Test
    void userMove_repromptsOnNonNumeric_thenAcceptsValid() {
        captureStdout();
        char[][] b = emptyBoard();

        // First token is non-int -> should print "You should enter numbers!"
        // Then valid coordinates: 1 1
        String input = "a\n1 1\n";
        Scanner sc = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        App.userMove(b, 'O', sc);

        assertEquals('O', b[0][0]);
        assertTrue(stdout().contains("You should enter numbers!"));
    }

    @Test
    void userMove_rejectsOutOfRange_thenAcceptsValid() {
        captureStdout();
        char[][] b = emptyBoard();

        String input = "4 1\n3 3\n";
        Scanner sc = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        App.userMove(b, 'X', sc);

        assertEquals('X', b[2][2]);
        assertTrue(stdout().contains("Coordinates should be from 1 to 3!"));
    }

    @Test
    void userMove_rejectsOccupiedCell_thenAcceptsValid() {
        captureStdout();
        char[][] b = emptyBoard();
        b[0][0] = 'X';

        String input = "1 1\n1 2\n";
        Scanner sc = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        App.userMove(b, 'O', sc);

        assertEquals('O', b[0][1]);
        assertTrue(stdout().contains("This cell is occupied! Choose another one!"));
    }

    // ---------------------------
    // medium() tests
    // ---------------------------

    @Test
    void medium_makesWinningMove_whenAvailableForX() {
        char[][] b = emptyBoard();
        // X can win by placing at (0,2)
        b[0][0] = 'X';
        b[0][1] = 'X';

        App.medium(b, 'X');

        assertEquals('X', b[0][2], "Medium should take immediate winning move");
    }

    @Test
    void medium_blocksOpponentImmediateWin_whenPlayingO() {
        char[][] b = emptyBoard();
        // X is about to win on column 0, O must block at (2,0)
        b[0][0] = 'X';
        b[1][0] = 'X';

        App.medium(b, 'O');

        assertEquals('O', b[2][0], "Medium should block opponent's winning move");
    }

    @Test
    void medium_fallbackMakesExactlyOneMove_whenNoWinOrBlock() {
        char[][] b = emptyBoard();
        b[0][0] = 'X';
        b[1][1] = 'O';

        int before = countMarks(b, 'X') + countMarks(b, 'O');
        App.medium(b, 'X');
        int after = countMarks(b, 'X') + countMarks(b, 'O');

        assertEquals(before + 1, after, "Medium fallback should place exactly one mark");
    }

    // ---------------------------
    // hard() tests (minimax)
    // ---------------------------

    @Test
    void hard_takesImmediateWinningMove() {
        char[][] b = emptyBoard();
        // X can win immediately at (2,2)
        b[0][0] = 'X';
        b[1][1] = 'X';
        b[0][1] = 'O';
        b[0][2] = 'O';

        App.hard(b, 'X');

        assertEquals('X', b[2][2], "Hard should pick immediate winning move when available");
        assertTrue(App.finished(b, true));
        assertEquals(1, App.xWin);
    }

    @Test
    void minimax_returnsPositiveScore_whenMaximizerHasAlreadyWon() {
        char[][] b = emptyBoard();
        // Force an already-won board for X
        b[0][0] = 'X';
        b[0][1] = 'X';
        b[0][2] = 'X';

        // In your implementation, minimax depends on AI + finished()
        App.AI = 'X';

        // move parameter can be anything; there are no empties used in scoring if finished triggers
        int score = App.minimax(b, 'O', false);

        assertEquals(1, score, "If AI is X and X has won, score should be +1");
    }
}
