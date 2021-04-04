package testrobot;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class runner {
	static Robot robot;
	static //600 heal minimun 
	int[][] screen = {{-1500,0},{-1500,500}};
	static int currScreen = 0;
	static ArrayList<Integer> movesDone = new ArrayList<Integer>();
	public static void main(String args[]) {
		try {
			robot = new Robot();
			swapScreens(0);
			int distanceFromStart = getCurrPosition(600,390,200,60,"guildIcon.png");
			int maxDistance = 140;
			int minHoldTime = 80;
			int maxHoldTime = 120;
			int healDelay = 615;
			
			for(int x=0;x<100;x++) {
				System.out.println("<--------------->");
				System.out.println("iteration " + x);
				System.out.println("<--------------->");
				distanceFromStart = moveRandomly(maxDistance,distanceFromStart,minHoldTime,maxHoldTime);
				robot.delay(750);
				attack(100 - randomNum(1,40), KeyEvent.VK_V, healDelay);
				if(x%9 == 0 && x!=0) {
					feedPet(0, KeyEvent.VK_PAGE_UP);
//					feedPet(1, KeyEvent.VK_PAGE_DOWN);
				}
				if(x%6 == 0 && x!=0) {
					System.out.println("Picking up loot on screen 1");
					swapScreens(1);
					pickUp(randomNum(10,20));
					swapScreens(0);
				}
				System.out.print("Current Move List: ");
				for(int y=0;y<movesDone.size();y++) {
					System.out.print(movesDone.get(y)+",");
				}
				System.out.println();
			}
		} catch (AWTException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static int getCurrPosition(int x, int y, int width, int height, String fileName) throws IOException {
		BufferedImage imageRecog = ImageIO.read(new File(fileName));
		BufferedImage image = robot.createScreenCapture(new Rectangle(x,y,width,height));
		for(int x1=0;x<image.getWidth()-imageRecog.getWidth();x1++) {
			for(int y1=0;y<image.getHeight()-imageRecog.getHeight();y1++) {
				boolean matches = true;
				for(int x2=0;x2<imageRecog.getWidth();x2++) {
					for(int y2=0;y2<imageRecog.getHeight();y2++) {
						if(imageRecog.getRGB(x2, y2) != image.getRGB(x1+x2, y1+y2)) {
							matches = false;
						}
					}
				}
				if(matches) {
					return x1;
				}
			}
		}
		return -1;
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
		int newDistance;
		int predictedPosition = maxDistance + 1;
		if(movesDone.size() > 1) {
			if(Math.random() > .2) {
				int randomMove = randomNum(0, movesDone.size());
				while(Math.abs(predictedPosition) > maxDistance) {
					randomMove = randomNum(0, movesDone.size());
					predictedPosition = distanceFromStart;
					predictedPosition += movesDone.get(randomMove)*-1;
				}
				if(movesDone.get(randomMove) > 0) {
					moveLeft(movesDone.get(randomMove));
					newDistance = distanceFromStart - movesDone.get(randomMove);
				} else {
					moveRight(movesDone.get(randomMove)*-1);
					newDistance = distanceFromStart + (movesDone.get(randomMove)*-1);
				}
				System.out.println("Using old move "+movesDone.get(randomMove)+" to move from " + distanceFromStart + " to " + newDistance );
				movesDone.remove(randomMove);
				return newDistance;
			}
		}
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
		if(leftRight < .5) {
			moveLeft(newRandomDelay);
			movesDone.add(newRandomDelay*-1);
			robot.delay(randomNum(500,1000));
			newDistance = distanceFromStart -newRandomDelay;
		} else {
			moveRight(newRandomDelay);
			movesDone.add(newRandomDelay);
			robot.delay(randomNum(500,1000));
			newDistance= distanceFromStart +newRandomDelay;
		}
		System.out.println("Resetting position with " +newRandomDelay +" from " + distanceFromStart + " to " + newDistance );
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
	public static void feedPet(int screen, int key) {
		int temp = currScreen;
		System.out.println("Feeding pet on Screen " + screen);
		if(screen != currScreen) {
			swapScreens(screen);
			robot.delay(100);
			keyPress(key);
			robot.delay(100);
			swapScreens(temp);
			robot.delay(100);
		} else {
			keyPress(key);
			robot.delay(100);
		}
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
