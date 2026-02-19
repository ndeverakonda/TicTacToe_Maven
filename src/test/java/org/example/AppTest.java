package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
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
        System.setIn(originalIn);
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
    // finished()
    // ---------------------------

    @Test
    void finished_detectsXHorizontalWin() {
        char[][] b = emptyBoard();
        b[1][0] = 'X';
        b[1][1] = 'X';
        b[1][2] = 'X';

        assertTrue(App.finished(b, true));
        assertEquals(1, App.xWin);
        assertEquals(0, App.oWin);
    }

    @Test
    void finished_detectsOVerticalWin() {
        char[][] b = emptyBoard();
        b[0][2] = 'O';
        b[1][2] = 'O';
        b[2][2] = 'O';

        assertTrue(App.finished(b, true));
        assertEquals(0, App.xWin);
        assertEquals(1, App.oWin);
    }

    @Test
    void finished_detectsDiagonalLeftToRightWin() {
        char[][] b = emptyBoard();
        b[0][0] = 'X';
        b[1][1] = 'X';
        b[2][2] = 'X';

        assertTrue(App.finished(b, true));
        assertEquals(1, App.xWin);
        assertEquals(0, App.oWin);
    }

    @Test
    void finished_detectsDiagonalRightToLeftWin() {
        char[][] b = emptyBoard();
        b[0][2] = 'O';
        b[1][1] = 'O';
        b[2][0] = 'O';

        assertTrue(App.finished(b, true));
        assertEquals(0, App.xWin);
        assertEquals(1, App.oWin);
    }

    @Test
    void finished_printsOWins_whenNotSilent() {
        captureStdout();

        char[][] b = emptyBoard();
        b[2][0] = 'O';
        b[2][1] = 'O';
        b[2][2] = 'O';

        assertTrue(App.finished(b, false));
        assertTrue(stdout().contains("O wins"));
    }

    @Test
    void finished_printsDraw_whenNotSilent() {
        captureStdout();

        char[][] b = {
                {'X', 'O', 'X'},
                {'X', 'O', 'O'},
                {'O', 'X', 'X'}
        };

        assertTrue(App.finished(b, false));
        assertTrue(stdout().contains("Draw"));
    }

    @Test
    void finished_returnsFalse_whenGameNotFinished_andHasEmptyBreaksEarly() {
        char[][] b = emptyBoard();
        b[0][0] = 'X';
        b[0][1] = 'O';
        // hasEmpty true and should hit the inner-loop break

        assertFalse(App.finished(b, true));
        assertEquals(0, App.xWin);
        assertEquals(0, App.oWin);
    }

    // ---------------------------
    // printGrid()
    // ---------------------------

    @Test
    void printGrid_printsBordersAndCells() {
        captureStdout();
        char[][] b = emptyBoard();
        b[0][0] = 'X';
        b[1][1] = 'O';

        App.printGrid(b);

        String s = stdout();
        assertTrue(s.contains("---------"));
        assertTrue(s.contains("| X"));
        assertTrue(s.contains(" O "));
    }

    // ---------------------------
    // userMove()
    // ---------------------------

    @Test
    void userMove_placesMark_whenValidCoordinatesProvided() {
        char[][] b = emptyBoard();

        String input = "2 3\n";
        Scanner sc = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        App.userMove(b, 'X', sc);

        assertEquals('X', b[1][2]);
    }

    @Test
    void userMove_repromptsOnNonNumeric_thenAcceptsValid() {
        captureStdout();
        char[][] b = emptyBoard();

        String input = "a\n1 1\n";
        Scanner sc = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        App.userMove(b, 'O', sc);

        assertEquals('O', b[0][0]);
        assertTrue(stdout().contains("You should enter numbers!"));
    }

    @Test
    void userMove_rejectsSecondTokenNonNumeric_thenAcceptsValid() {
        captureStdout();
        char[][] b = emptyBoard();

        // First int ok, second not-int triggers second "You should enter numbers!" branch
        String input = "1 a\n2 2\n";
        Scanner sc = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        App.userMove(b, 'X', sc);

        assertEquals('X', b[1][1]);
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
    // easyMove()
    // ---------------------------

    @Test
    void easyMove_placesExactlyOneMark_andPrints() {
        captureStdout();
        char[][] b = emptyBoard();

        int before = countMarks(b, 'X') + countMarks(b, 'O');
        App.easyMove(b, 'X');
        int after = countMarks(b, 'X') + countMarks(b, 'O');

        assertEquals(before + 1, after);
        assertTrue(stdout().contains("Making move level \"easy\""));
    }

    @Test
    void easyMove_skipsOccupiedInitialCell_thenFindsEmpty() {
        // Force the while(arr[x][y] != ' ') loop to run at least once,
        // because x=y=0 initially.
        char[][] b = emptyBoard();
        b[0][0] = 'O';

        int before = countMarks(b, 'X') + countMarks(b, 'O');
        App.easyMove(b, 'X');
        int after = countMarks(b, 'X') + countMarks(b, 'O');

        assertEquals(before + 1, after);
        assertEquals('O', b[0][0]); // ensure it didn't overwrite occupied cell
    }

    // ---------------------------
    // medium()
    // ---------------------------

    @Test
    void medium_makesWinningMove_whenAvailableForX_row() {
        char[][] b = emptyBoard();
        b[0][0] = 'X';
        b[0][1] = 'X';

        App.medium(b, 'X');

        assertEquals('X', b[0][2]);
    }

    @Test
    void medium_makesWinningMove_whenAvailableForO_column() {
        char[][] b = emptyBoard();
        b[0][1] = 'O';
        b[1][1] = 'O';

        App.medium(b, 'O');

        assertEquals('O', b[2][1]);
    }

    @Test
    void medium_blocksOpponentImmediateWin_whenPlayingO() {
        char[][] b = emptyBoard();
        b[0][0] = 'X';
        b[1][0] = 'X';

        App.medium(b, 'O');

        assertEquals('O', b[2][0]);
    }

    @Test
    void medium_blocksDiagonalThreat_whenPlayingX() {
        char[][] b = emptyBoard();
        // O threatens to win diagonal L->R with (2,2)
        b[0][0] = 'O';
        b[1][1] = 'O';

        App.medium(b, 'X');

        assertEquals('X', b[2][2]);
    }

    @Test
    void medium_fallbackCallsEasyMove_whenNoWinOrBlock() {
        captureStdout();
        char[][] b = emptyBoard();
        b[0][0] = 'X';
        b[1][1] = 'O';

        int before = countMarks(b, 'X') + countMarks(b, 'O');
        App.medium(b, 'X');
        int after = countMarks(b, 'X') + countMarks(b, 'O');

        assertEquals(before + 1, after);
        assertTrue(stdout().contains("Making move level \"medium\""));
        // In fallback, easyMove prints too
        assertTrue(stdout().contains("Making move level \"easy\""));
    }

    // ---------------------------
    // minimax()
    // ---------------------------

    @Test
    void minimax_returns1_whenAIWinsTerminal() {
        char[][] b = emptyBoard();
        b[0][0] = 'X';
        b[0][1] = 'X';
        b[0][2] = 'X';

        App.AI = 'X';
        assertEquals(1, App.minimax(b, 'O', false));
    }

    @Test
    void minimax_returnsMinus1_whenOpponentWinsTerminal() {
        char[][] b = emptyBoard();
        b[1][0] = 'O';
        b[1][1] = 'O';
        b[1][2] = 'O';

        App.AI = 'X'; // AI is X, but O has won => -1
        assertEquals(-1, App.minimax(b, 'X', true));
    }

    @Test
    void minimax_returns0_whenDrawTerminal() {
        char[][] b = {
                {'X', 'O', 'X'},
                {'X', 'O', 'O'},
                {'O', 'X', 'X'}
        };

        App.AI = 'X';
        assertEquals(0, App.minimax(b, 'O', true));
    }

    @Test
    void minimax_exercisesMaxAndMinPaths_onNonTerminalBoard() {
        // Non-terminal with empties -> forces recursion and both max/min updates
        char[][] b = emptyBoard();
        b[0][0] = 'X';
        b[0][1] = 'O';
        b[1][1] = 'X';
        b[2][2] = 'O';

        App.AI = 'X';

        int scoreMax = App.minimax(b, 'X', true);
        int scoreMin = App.minimax(b, 'O', false);

        assertTrue(scoreMax >= -1 && scoreMax <= 1);
        assertTrue(scoreMin >= -1 && scoreMin <= 1);
    }

    // ---------------------------
    // hard()
    // ---------------------------

    @Test
    void hard_immediateWinningMove_returnsEarlyPathCovered() {
        // Your hard() now has a direct return when it finds a winning move.
        // Make sure that path is taken (and does NOT place a different move later).
        char[][] b = emptyBoard();
        b[0][0] = 'X';
        b[0][1] = 'X';
        b[1][0] = 'O';
        b[1][1] = 'O';
        // winning for X at (0,2)

        App.hard(b, 'X');

        // It should immediately return after detecting winning in simulation,
        // so board should be in a terminal X-win state.
        assertTrue(App.finished(b, true));
        assertEquals(1, App.xWin);
    }

    @Test
    void hard_placesSomeMove_whenNoImmediateWinAvailable() {
        char[][] b = emptyBoard();
        b[0][0] = 'X';
        b[1][1] = 'O';

        int before = countMarks(b, 'X') + countMarks(b, 'O');
        App.hard(b, 'X');
        int after = countMarks(b, 'X') + countMarks(b, 'O');

        assertEquals(before + 1, after, "Hard should place exactly one mark when playing");
    }

    @Test
    void constructor_isCovered() {
        assertNotNull(new App());
    }

    @Test
    void privateMakeMove_coversAllCasesIncludingDefault() throws Exception {
        captureStdout();
        Method makeMove = App.class.getDeclaredMethod(
                "makeMove", char[][].class, String.class, char.class, Scanner.class);
        makeMove.setAccessible(true);

        char[][] userBoard = emptyBoard();
        Scanner userScanner = new Scanner(new ByteArrayInputStream("1 1\n".getBytes(StandardCharsets.UTF_8)));
        makeMove.invoke(null, userBoard, "user", 'X', userScanner);
        assertEquals('X', userBoard[0][0]);

        char[][] easyBoard = emptyBoard();
        makeMove.invoke(null, easyBoard, "easy", 'O', new Scanner(new ByteArrayInputStream(new byte[0])));
        assertEquals(1, countMarks(easyBoard, 'O'));

        char[][] mediumBoard = emptyBoard();
        mediumBoard[0][0] = 'X';
        mediumBoard[0][1] = 'X';
        makeMove.invoke(null, mediumBoard, "medium", 'X', new Scanner(new ByteArrayInputStream(new byte[0])));
        assertEquals('X', mediumBoard[0][2]);

        char[][] hardBoard = emptyBoard();
        hardBoard[0][0] = 'X';
        hardBoard[1][1] = 'O';
        int before = countMarks(hardBoard, 'X') + countMarks(hardBoard, 'O');
        makeMove.invoke(null, hardBoard, "hard", 'X', new Scanner(new ByteArrayInputStream(new byte[0])));
        int after = countMarks(hardBoard, 'X') + countMarks(hardBoard, 'O');
        assertEquals(before + 1, after);

        char[][] invalidBoard = emptyBoard();
        makeMove.invoke(null, invalidBoard, "invalid", 'X', new Scanner(new ByteArrayInputStream(new byte[0])));
        assertTrue(stdout().contains("Invalid Input"));
    }

    @Test
    void privatePlayGame_executesBothTurnsAndStopsOnFinish() throws Exception {
        Method playGame = App.class.getDeclaredMethod(
                "playGame", char[][].class, String.class, String.class, Scanner.class);
        playGame.setAccessible(true);

        char[][] b = emptyBoard();
        b[0][0] = 'X';
        b[0][1] = 'X';
        // X wins on first move at (1,3), so O turn is skipped by finished() break.
        Scanner sc = new Scanner(new ByteArrayInputStream("1 3\n".getBytes(StandardCharsets.UTF_8)));

        playGame.invoke(null, b, "user", "user", sc);

        assertEquals('X', b[0][2]);
        assertTrue(App.finished(b, true));
    }

    @Test
    void main_handlesBadParametersAndExit() {
        captureStdout();
        String input = String.join("\n",
                "oops",
                "start easy easy",
                "exit",
                "");
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        App.main(new String[0]);

        String s = stdout();
        assertTrue(s.contains("Input command:"));
        assertTrue(s.contains("Bad parameters!"));
        assertTrue(s.contains("Making move level \"easy\""));
    }
}
