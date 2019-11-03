package com.petermarshall;

import edu.cmu.ri.createlab.terk.robot.finch.Finch;
import java.awt.Color;
import java.util.ArrayList;

public class Game {
    private static Finch CONTROL_FINCH;
    private static ColouredFinch[] colouredFinches;
    private static ArrayList<ColouredFinch> correctOrder;
    private static ArrayList<ColouredFinch> userOrder;
    private static boolean gameOver;

    public static void main (String[] args) {
        setup();
        welcomeUser();
        checkControlFinchForGameOver();

        if (!gameOver) {
            playGame();
        }
    }

    private static void setup() {
        ColouredFinch redFinch = new ColouredFinch( Color.RED );
        ColouredFinch blueFinch = new ColouredFinch( Color.BLUE );
        ColouredFinch greenFinch = new ColouredFinch( Color.GREEN );
        ColouredFinch yellowFinch = new ColouredFinch( Color.YELLOW );
        CONTROL_FINCH = new Finch();

        colouredFinches = new ColouredFinch[]{ redFinch, blueFinch, greenFinch, yellowFinch };
        correctOrder = new ArrayList<>();
        userOrder = new ArrayList<>();

        gameOver = false;
    }

    private static void welcomeUser() {
        System.out.println("Hello user. We are starting the game. If you want to quit the game, put your control " +
                "finch's beak facing upwards.");
    }

    private static void playGame() {
        while (!gameOver) {
            addExtraFinchToList();
            showOrderToUser();
            getUserOrder();
        }
    }

    private static void addExtraFinchToList() {
        ColouredFinch finch = getRandomFinch();
        correctOrder.add(finch);
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

    private static void getUserOrder() {
        while (userNeedsToInput() && !gameOver) {
            waitForUserInput();
            checkControlFinchForGameOver();
        }
    }

    private static boolean userNeedsToInput() {
        return correctOrder.size() != userOrder.size();
    }

    private static void waitForUserInput() {
        for (ColouredFinch finch: colouredFinches) {
            if (finch.isTapped()) {
                checkUserInput(finch);
            }
        }
    }

    private static void checkUserInput(ColouredFinch selectedFinch) {
        if (incorrectUserInput(selectedFinch)) {
            selectedFinch.buzz();
            gameOverBadInput();
        } else {
            userOrder.add(selectedFinch);
            sleep(100); //to make sure we do not add duplicate finches for the same tap. finch isTapped method
            //may report multiple taps for the same tap.
        }
    }

    private static boolean incorrectUserInput(ColouredFinch selectedFinch) {
        return correctOrder.get(userOrder.size()) != selectedFinch;
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

    private static void gameOverBadInput() {
        gameOver = true;
        System.out.println("Wrong input. Completed " + getCompletedLevels() + " levels.");
        flashAllFinchesX3();
    }

    private static int getCompletedLevels() {
        return Math.min(0, correctOrder.size()-1);
    }


    private static void flashAllFinchesX3() {
        for (int i = 0; i<3; i++) {
            for (ColouredFinch finch: colouredFinches) {
                finch.turnOnLight(100);
            }
            sleep(200);
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
