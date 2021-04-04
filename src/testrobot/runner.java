package testrobot;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class runner {
	static Robot robot;
	static //600 heal minimun 
	int[][] screen = {{-1500,0},{-1500,500}};
	static int currScreen = 0;
	static boolean facingRight = false;
	public static void main(String args[]) {
		try {
			robot = new Robot();
			swapScreens(0);
			int distanceFromStart = 0;
			int maxDistance = 150;
			int minHoldTime = 80;
			int maxHoldTime = 100;
			int healDelay = 615;
			for(int x=0;x<100;x++) {
				System.out.println("<--------------->");
				System.out.println("iteration " + x);
				System.out.println("<--------------->");
				distanceFromStart = moveRandomly(maxDistance,distanceFromStart,minHoldTime,maxHoldTime);
				attack(100 - randomNum(1,40), KeyEvent.VK_V, healDelay);
				if(x%9 == 0 && x!=0) {
					System.out.println("Feeding pet on Screen 0");
					keyPress(KeyEvent.VK_PAGE_UP);
//					swapScreens(1);
//					System.out.println("Feeding pet on Screen 1");
//					keyPress(KeyEvent.VK_PAGE_DOWN);
//					robot.delay(100);
//					swapScreens(0);
				}
//				if(x%6 == 0 && x!=0) {
//					System.out.println("Picking up loot on screen 1");
//					swapScreens(1);
//					pickUp(randomNum(10,20));
//					swapScreens(0);
//				}
			}
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void swapScreens(int screenId) {
		click(screen[screenId][0],screen[screenId][1]);
		robot.delay(100);
	}
	public static void pickUp(int amount) {
		for(int x=0;x<amount;x++) {
			robot.keyPress(KeyEvent.VK_Z);
			robot.delay(randomNum(75,200));
		}
		robot.keyRelease(KeyEvent.VK_Z);
		robot.delay(100);
	}
	public static int moveRandomly(int maxDistance, int distanceFromStart, int minHoldTime, int maxHoldTime) {
		int newRandomDelay = minHoldTime;
		double leftRight = 0;
		int predictedPosition = maxDistance + 1;
		while(Math.abs(predictedPosition) > maxDistance) {
			predictedPosition = distanceFromStart;
			newRandomDelay = randomNum(minHoldTime,maxHoldTime);
			leftRight = Math.random();
			if(leftRight < .5) { //left
				predictedPosition -= newRandomDelay;
			} else { //right
				predictedPosition += newRandomDelay;
			}
		}
		int newDistance;
		if(leftRight < .5) {
			moveLeft(newRandomDelay);
			robot.delay(randomNum(500,1000));
			newDistance = distanceFromStart -newRandomDelay;
		} else {
			moveRight(newRandomDelay);
			robot.delay(randomNum(500,1000));
			newDistance= distanceFromStart +newRandomDelay;
		}
		System.out.println("Resetting position from " + distanceFromStart + " to " + newDistance );
		return newDistance;
	}
	public static void attack(int numAttacks, int key, int attackDelay) {
		System.out.println("Attacking " + numAttacks + " times with " + attackDelay + "ms Delay.");
		for(int x=0;x<numAttacks;x++) {
			robot.keyPress(key);
			robot.delay(attackDelay + randomPosNeg(randomNum(1,10)));
		}
		robot.keyRelease(key);
	}
	public static int randomPosNeg(int input) {
		double random = Math.random();
		if(random > .5) {
			return input;
		} else {
			return input * -1;
		}
	}
	public static int randomNum(int min, int max) {
		return (int)(Math.random()*(max-min))+min;
	}
	public static void click(int x, int y) {
		robot.mouseMove(x, y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	public static void keyPress(int key) {
		robot.keyPress(key);
		robot.keyRelease(key);
	}
	public static void keyHold(int key, int delay) {
		robot.keyPress(key);
		robot.delay(delay);
		robot.keyRelease(key);
	}
	public static void moveRight(int amount) {
		robot.keyPress(KeyEvent.VK_RIGHT);
		robot.delay(amount);
		robot.keyRelease(KeyEvent.VK_RIGHT);
	}
	public static void moveLeft(int amount) {
		robot.keyPress(KeyEvent.VK_LEFT);
		robot.delay(amount);
		robot.keyRelease(KeyEvent.VK_LEFT);
	}
}
