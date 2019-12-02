package com.petermarshall;

//import de.vandermeer.asciitable.AsciiTable;
import com.github.freva.asciitable.AsciiTable;
import edu.cmu.ri.createlab.terk.robot.finch.Finch;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Scanner;

public class Game {
    private static Finch CONTROL_FINCH;
    private static ColouredFinch[] colouredFinches;
    private static ArrayList<ColouredFinch> correctOrder;
    private static ArrayList<ColouredFinch> userOrder;
    private static boolean gameOver;
    private static Scanner scanner;

    public static void main (String[] args) {
        setup();
        welcomeUser();
        checkControlFinchForGameOver();

        if (!gameOver) {
//            readyToBegin();
            sleep(2000);
            playGame();
        }
    }

    private static void setup() {
        CONTROL_FINCH = new Finch();
        ColouredFinch redFinch = new ColouredFinch( Color.RED, "Red" );
        ColouredFinch blueFinch = new ColouredFinch( Color.BLUE, "Blue" );
//        ColouredFinch greenFinch = new ColouredFinch( Color.GREEN, "Green" );
//        ColouredFinch yellowFinch = new ColouredFinch( Color.YELLOW, "Yellow" );
        colouredFinches = new ColouredFinch[]{ redFinch, blueFinch };
//        greenFinch, yellowFinch
        correctOrder = new ArrayList<>();
        userOrder = new ArrayList<>();
        gameOver = false;
        scanner = new Scanner(System.in);
    }

    private static void welcomeUser() {
        System.out.println("Hello user. We are starting the game. If you want to quit the game, put your control " +
                "finch's beak facing upwards.");
    }

    private static void playGame() {
        while (!gameOver) {
            addExtraFinchToList();
            emptyUserGuesses();
            showOrderToUser();
            getAndCheckUserOrder();
            sleep(500);
        }
    }

    private static void addExtraFinchToList() {
        ColouredFinch finch = getRandomFinch();
        correctOrder.add(finch);
    }

    private static void emptyUserGuesses() {
        userOrder.clear();
    }

    private static void showOrderToUser() {
        int displayTime = 500;
        for (ColouredFinch currFinch: correctOrder) {
            currFinch.turnOnLight(displayTime);
            sleep(displayTime); //Thread is slept for the same amount of time we display the light for. This means the thread
            //sleeps for the same amount of time a finches light is displayed. This is important so the
            //finches lights appear to be displayed one after the other. Without sleeping the main thread
            //the lights would all appear to come on at the same time, as the computer would take ~1ms
            //to go through each stage of the loop. So the first finch would turn on, 1ms later the second
            //finch would turn on etc etc. They'd all appear to come on and turn off at the same time.
        }
    }

    private static void getAndCheckUserOrder() {
        while (userNeedsToInput() && !gameOver) {
            waitForUserInput();
            checkControlFinchForGameOver();
        }

        if (!gameOver) {
            congratulateUser();
        }
    }

    private static void readyToBegin() {
        System.out.println("Press any key or enter to start the game, or put your Finch's beak upwards to quit the game.");

        while (!scanner.hasNext() && !gameOver) {
            checkControlFinchForGameOver();
        }
    }

    private static void congratulateUser() {
        System.out.println("Well done, you have completed level " + correctOrder.size() + ". Press any key then enter to continue to the next level, or point your Control Finch up to quit.");
        while (!scanner.hasNext() && !gameOver) {
            checkControlFinchForGameOver();
        }
        scanner.nextLine();

        if (!gameOver) {
            clearConsole(); //clears the console output after each level so the user cannot just copy what they inputted for the last level.
        }
    }

    private static void clearConsole() {
        int numbBlankLines = 200;
        for(int i = 0; i < numbBlankLines; i++) {
            System.out.println("\b") ;
        }
    }

    private static boolean userNeedsToInput() {
        return correctOrder.size() != userOrder.size();
    }

    private static void waitForUserInput() {
        for (ColouredFinch finch: colouredFinches) {
            sleep(100);
            if (finch.isTapped()) {
                sleep(100);
                checkUserInput(finch);
            }
        }
    }

    private static void checkUserInput(ColouredFinch selectedFinch) {
        if (incorrectUserInput(selectedFinch)) {
            selectedFinch.buzz();
            gameOverBadInput(selectedFinch);
        } else {
            userOrder.add(selectedFinch);
            sleep(200); //to make sure we do not add duplicate finches for the same tap. finch isTapped method
            //may report multiple taps for the same tap.
        }
    }

    private static ColouredFinch getCorrectInput() {
        return correctOrder.get(userOrder.size());
    }

    private static boolean incorrectUserInput(ColouredFinch selectedFinch) {
        return getCorrectInput() != selectedFinch;
    }

    private static ColouredFinch getRandomFinch() {
        int numbColouredFinches = colouredFinches.length;
        int randomIndex = (int) (Math.random()*numbColouredFinches);
        return colouredFinches[randomIndex];
    }

    private static void checkControlFinchForGameOver() {
        if (CONTROL_FINCH.isBeakUp()) {
            gameOver = true;
            gameOverControlFinch();
        }
    }

    private static void gameOverControlFinch() {
        System.out.println("Control finch is down, game ended. Completed " + getCompletedLevels() + " levels.");
    }

    private static void gameOverBadInput(ColouredFinch selectedFinch) {
        gameOver = true;

        System.out.println("Wrong input. You selected " + selectedFinch.getDescription() + ", but the correct answer was " + getCorrectInput().getDescription());
        System.out.println(getInputTable(selectedFinch));
        System.out.println("Bye bye.");

        flashAllFinchesX3();

    }

    private static String getInputTable(ColouredFinch selectedFinch) {
        String[] headers = {"", "Correct order", "Your order"};
        String[][] rows = new String[getCompletedLevels()+1][3];

        for (int i = 0; i<correctOrder.size(); i++) {
            String correctColour = correctOrder.get(i).getDescription();
            String userColour;
            try {
                userColour = userOrder.get(i).getDescription();
            } catch (Exception e) {
                userColour = selectedFinch.getDescription().toUpperCase();
            }

            String[] row = new String[]{(i+1)+"", correctColour, userColour};
            rows[i] = row;
        }

        return AsciiTable.getTable(headers, rows);
    }

    private static int getCompletedLevels() {
        return Math.max(0, correctOrder.size()-1);
    }


    private static void flashAllFinchesX3() {
        int numFlashes = 3;
        int displayTime = 1000;

        for (int i = 0; i<numFlashes; i++) {
            for (ColouredFinch finch: colouredFinches) {
                finch.turnOnLight(displayTime);
            }
            sleep(2*displayTime);
        }
    }

    private static void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
