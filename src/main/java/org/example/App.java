package org.example;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class App {
    private static final Random RANDOM = new Random();
    //Result
    static char AI; //Computer's Move
    static int xWin;
    static int oWin;

    static boolean finished(char[][] arr, boolean silent) { //check status(win/draw/incomplete)
        xWin = 0;
        oWin = 0;

//Horizontal
        for (int i = 0; i < 3; i++) {
            int markO = 0;
            int markX = 0;
            for (int j = 0; j < 3; j++) {
                if (arr[i][j] == 'X') {
                    markX++;
                } else if (arr[i][j] == 'O') {
                    markO++;
                }

            }
            if (markX == 3) {
                xWin = 1;
            }
            if (markO == 3) {
                oWin = 1;
            }
        }

//Vertical
        for (int i = 0; i < 3; i++) {
            int markO = 0;
            int markX = 0;
            for (int j = 0; j < 3; j++) {
                if (arr[j][i] == 'X') {
                    markX++;
                } else if (arr[j][i] == 'O') {
                    markO++;
                }

            }
            if (markX == 3) {
                xWin = 1;
            }
            if (markO == 3) {
                oWin = 1;
            }
        }


//Diagonal L to R
        int markO = 0;
        int markX = 0;
        for (int i = 0; i < 3; i++) {

            if (arr[i][i] == 'X') {
                markX++;
            } else if (arr[i][i] == 'O') {
                markO++;
            }


            if (markX == 3) {
                xWin = 1;
            }
            if (markO == 3) {
                oWin = 1;
            }
        }

//Diagonal R to L
        markX = 0;
        markO = 0;
        for (int i = 0; i < 3; i++) {
            if (arr[i][2 - i] == 'X') markX++;
            if (arr[i][2 - i] == 'O') markO++;
        }

        if (markX == 3) {
            xWin = 1;
        }
        if (markO == 3) {
            oWin = 1;
        }


// Check if any empty cells for 'Draw'
        boolean hasEmpty = false;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (arr[i][j] == ' ') {
                    hasEmpty = true;
                    break;
                }
            }
        }


//PRINT RESULT


        if (xWin == 1 && oWin == 0) {

            if (!silent) System.out.println("X wins");

        } else if (oWin == 1 && xWin == 0) {

            if (!silent) System.out.println("O wins");
        } else if (!hasEmpty) { // DRAW

            if (!silent) System.out.println("Draw");
        } else {
            return false; //UNFINISHED GAME
        }
        return true;
    }


    static void printGrid(char[][] arr) {
        System.out.println("---------");
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println("|");

        }
        System.out.println("---------");
    }


    static void userMove(char[][] arr, char ch, Scanner sc) {

        int a = 0;
        int b = 0;
        boolean validMove = false;

        while (!validMove) {
            System.out.println("Enter the coordinates: ");

            if (!sc.hasNextInt()) {
                System.out.println("You should enter numbers!");
                sc.nextLine();
                continue;
            }
            a = sc.nextInt();

            if (!sc.hasNextInt()) {
                System.out.println("You should enter numbers!");
                sc.nextLine();
                continue;
            }
            b = sc.nextInt();
            sc.nextLine(); // clear buffer

            if (a < 1 || a > 3 || b < 1 || b > 3) {
                System.out.println("Coordinates should be from 1 to 3!");
            } else if (arr[a - 1][b - 1] != ' ') {
                System.out.println("This cell is occupied! Choose another one!");
            } else {
                validMove = true;
            }
        }

        arr[a - 1][b - 1] = ch;
    }


    static void easyMove(char[][] arr, char ch) {

        System.out.println("Making move level \"easy\"");
        //Computer Move - EASY

        int x = 0;
        int y = 0;

        while (arr[x][y] != ' ') { //check if cell is occupied
            //generate a random move

            x = RANDOM.nextInt(3);
            y = RANDOM.nextInt(3); //range from 0 to 2
        }

        arr[x][y] = ch; //mark on grid
    }

    static void medium(char[][] arr, char move) {
        /*Computer Move = char move - Medium: 1) Winning Move
         *                         2) Blocking Move
         *                         3) Fallback = Random */

        System.out.println("Making move level \"medium\"");

        //Check if there's a winning move, else find out blocking move simultaneously

        int blocki = -1; //block coordinates
        int blockj = -1;
        boolean winmove = false; //if there isn't a winning move make blocking move

        //HORIZONTAL check
        for (int i = 0; i < 3; i++) {
            int xcnt = 0; //count no. of X and Y
            int ocnt = 0;

            int iemp = 0; //keep track of empty cells to make winning/blocking move
            int jemp = 0;

            for (int j = 0; j < 3; j++) {
                if (arr[i][j] == 'X') {
                    xcnt++;
                } else if (arr[i][j] == 'O') {
                    ocnt++;
                } else {
                    iemp = i;
                    jemp = j;
                }
            }
            //Check winning and mark on grid + return immediately
            if (move == 'X' && xcnt == 2 && ocnt == 0) {
                arr[iemp][jemp] = 'X';
                winmove = true;
                return;
            } else if (move == 'O' && xcnt == 0 && ocnt == 2) {
                arr[iemp][jemp] = 'O';
                winmove = true;
                return;
            }

            //Check blocking for each row, track coordinates and continue checking other ways
            if (move == 'X' && xcnt == 0 && ocnt == 2) {
                blocki = iemp;
                blockj = jemp;
            } else if (move == 'O' && xcnt == 2 && ocnt == 0) {
                blocki = iemp;
                blockj = jemp;
            }

        }

        //VERTICAL check

        for (int i = 0; i < 3; i++) {
            int xcnt = 0;
            int ocnt = 0;

            int iemp = 0;
            int jemp = 0;

            for (int j = 0; j < 3; j++) {
                if (arr[j][i] == 'X') {
                    xcnt++;
                } else if (arr[j][i] == 'O') {
                    ocnt++;
                } else {
                    iemp = j;
                    jemp = i;
                }
            }
            //Winning move
            if (move == 'X' && xcnt == 2 && ocnt == 0) {
                arr[iemp][jemp] = 'X';
                winmove = true;
                return;
            } else if (move == 'O' && xcnt == 0 && ocnt == 2) {
                arr[iemp][jemp] = 'O';
                winmove = true;
                return;
            }

            //Blocking move and continue to check others
            if (move == 'X' && xcnt == 0 && ocnt == 2) {
                blocki = iemp;
                blockj = jemp;
            }
            if (move == 'O' && xcnt == 2 && ocnt == 0) {
                blocki = iemp;
                blockj = jemp;
            }
        }

        //DIAGONAL L to R

        int xcnt = 0;
        int ocnt = 0;

        int iemp = 0;
        int jemp = 0;
        for (int i = 0; i < 3; i++) {

            if (arr[i][i] == 'X') {
                xcnt++;
            } else if (arr[i][i] == 'O') {
                ocnt++;
            } else {
                iemp = i;
                jemp = i;
            }
        }
        //Winning move

        if (move == 'X' && xcnt == 2 && ocnt == 0) {
            arr[iemp][jemp] = 'X';
            winmove = true;
            return;
        } else if (move == 'O' && xcnt == 0 && ocnt == 2) {
            arr[iemp][jemp] = 'O';
            winmove = true;
            return;
        }

        //Blocking move
        if (move == 'X' && xcnt == 0 && ocnt == 2) {
            blocki = iemp;
            blockj = jemp;
        } else if (move == 'O' && xcnt == 2 && ocnt == 0) {
            blocki = iemp;
            blockj = jemp;
        }

//DIAGONAL R TO L
        xcnt = 0;
        ocnt = 0;

        iemp = 0;
        jemp = 0;
        for (int i = 0; i < 3; i++) {

            if (arr[i][2 - i] == 'X') {
                xcnt++;
            } else if (arr[i][2 - i] == 'O') {
                ocnt++;
            } else {
                iemp = i;
                jemp = 2 - i;
            }
        }

        //Winning move
        if (move == 'X' && xcnt == 2 && ocnt == 0) {
            winmove = true;
            arr[iemp][jemp] = 'X';
            return;
        } else if (move == 'O' && xcnt == 0 && ocnt == 2) {
            winmove = true;
            arr[iemp][jemp] = 'O';
            return;
        }
        //Blocking move
        if (move == 'X' && xcnt == 0 && ocnt == 2) {
            blocki = iemp;
            blockj = jemp;
        } else if (move == 'O' && xcnt == 2 && ocnt == 0) {
            blocki = iemp;
            blockj = jemp;
        }


        //FINAL: MARKING THE BLOCKING move on grid
        if (!winmove && blocki != -1) {
            arr[blocki][blockj] = move;
        }

        //FALLBACK move - if there's no blocking move: Make a random
        else if (blocki == -1) {
            easyMove(arr, move);
        }

    }


    static void hard(char[][] arr, char move) {
        System.out.println("Making move level \"hard\"");

        int bestScore = Integer.MIN_VALUE; //minimax algorithm

        int bestMovei = 0; //track coordinates which give the best score
        int bestMovej = 0;

        AI = move; //the maximising move

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                int score = Integer.MIN_VALUE;

                if (arr[i][j] == ' ') {

                    char nxtMove; //opponents move
                    if (move == 'X') {
                        nxtMove = 'O';
                    } else {
                        nxtMove = 'X';
                    }

                    arr[i][j] = move; //make move and check score

                    if (finished(arr, true)) {//if game ends
                        if ((xWin == 1 && AI == 'X') || (oWin == 1 && AI == 'O')) {
                            // Always prioritize a direct winning move.
                            return;
                        }
                        score = -1;
                        if (!(xWin == 1 || oWin == 1)) {
                            score = 0; //if DRAW
                        }
                    } else {//if game didnt end calculate further
                        score = minimax(arr, nxtMove, nxtMove == AI); //opponents move
                    }

                    arr[i][j] = ' ';//undo the move after checking
                }
                if (score > bestScore) { //record the move
                    bestScore = score;
                    bestMovej = j;
                    bestMovei = i;
                }

            }
        }
        arr[bestMovei][bestMovej] = move;

    }

    static int minimax(char[][] arr, char move, boolean isMaximising) {

        int bestscore = Integer.MIN_VALUE;

        if (isMaximising) {//find max score if its maximising's turn
            bestscore = Integer.MIN_VALUE;
        } else {//find min score if it's minimising's turn
            bestscore = Integer.MAX_VALUE;
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                if (arr[i][j] == ' ') {
                    arr[i][j] = move;

                    int score;

                    char nxtMove;
                    if (move == 'X') {
                        nxtMove = 'O';
                    } else nxtMove = 'X';


                    if (finished(arr, true)) {
                        if ((xWin == 1 && AI == 'X') || (oWin == 1 && AI == 'O')) score = 1; //maximising
                        else if (xWin == 1 || oWin == 1) score = -1; //minimising
                        else score = 0; //draw
                    } else {
                        score = minimax(arr, nxtMove, !isMaximising); //switch turns
                    }

                    arr[i][j] = ' ';

                    if (isMaximising)
                        //calculate max of all possibilities if maximising
                        bestscore = max(bestscore, score);
                    else
                        //calculate min of all possibilities if minimising
                        bestscore = min(bestscore, score);
                }
            }
        }
        return bestscore;
    }

    private static void makeMove(char[][] arr, String type, char symbol, Scanner sc) {

        switch (type) {
            case "user":
                userMove(arr, symbol, sc);
                break;
            case "easy":
                easyMove(arr, symbol);
                break;
            case "medium":
                medium(arr, symbol);
                break;
            case "hard":
                hard(arr, symbol);
                break;
            default:
                System.out.println("Invalid Input");
        }
    }


    private static void playGame(char[][] arr, String p1, String p2, Scanner sc) {

        while (true) {

            makeMove(arr, p1, 'X', sc);
            printGrid(arr);
            if (finished(arr, false)) break;

            makeMove(arr, p2, 'O', sc);
            printGrid(arr);
            if (finished(arr, false)) break;
        }
    }

    public static void main(String[] args) {

        try (Scanner sc = new Scanner(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            char[][] arr = new char[3][3];

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    arr[i][j] = ' ';
                }
            }

            printGrid(arr);

            while (true) {

                System.out.println("Input command:");
                String line = sc.nextLine();

                if (line.equals("exit")) {
                    break;
                }

                String[] menu = line.split(" ");
                if (!menu[0].equals("start") || menu.length != 3) {
                    System.out.println("Bad parameters!");
                    continue;
                }

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        arr[i][j] = ' ';
                    }
                }

                printGrid(arr);

                playGame(arr, menu[1], menu[2], sc);
            }
        }
    }

}
