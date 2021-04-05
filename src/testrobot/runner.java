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
	int[][] screen = {{100,100},{-1500,500}};
	static int currScreen = 0;
	static Rectangle gs2 = new Rectangle(100,475,700,75);
	static long buffTimer = 0;
	static int[] buffKeys = {KeyEvent.VK_U};
	public static void main(String args[]) {
		try {
			int leftBound = 235;
			int rightBound = 310;
			robot = new Robot();
			swapScreens(0);
			rebuff(buffKeys);
			System.out.println("Starting at position: " + getCurrPosition(gs2,"guildvert.png") + " with bounds: " + leftBound + ", " + rightBound);
			for(int x=0;x<100;x++) {
				System.out.println("<--------------->");
				System.out.println("iteration " + x + " with bounds: " + leftBound + ", " + rightBound);
				System.out.println("<--------------->");
				randomMove(leftBound,rightBound, "guildvert.png");
				attack(100 - randomNum(1,40), KeyEvent.VK_V, 50);
				if(x%9 == 0 && x!=0) {
					feedPet(0, KeyEvent.VK_PAGE_UP);
//					feedPet(1, KeyEvent.VK_PAGE_DOWN);
				}
				if(x%6 == 0 && x!=0) {
					pickUp(0,randomNum(10,20));
				}
			}
		} catch (AWTException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static int getCurrPosition(Rectangle rect, String fileName) throws IOException {
		BufferedImage imageRecog = ImageIO.read(new File(fileName));
		BufferedImage image = robot.createScreenCapture(rect);
		for(int x1=0;x1<image.getWidth()-imageRecog.getWidth();x1++) {
			for(int y1=0;y1<image.getHeight()-imageRecog.getHeight();y1++) {
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
	public static void pickUp(int screen, int amount) {
		int temp = currScreen;
		System.out.println("Picking up loot on screen " + screen + amount + " times.");
		if(screen != currScreen) {
			swapScreens(screen);
			robot.delay(100);
			for(int x=0;x<amount;x++) {
				robot.keyPress(KeyEvent.VK_Z);
				robot.delay(randomNum(75,200));
			}
			robot.keyRelease(KeyEvent.VK_Z);
			robot.delay(100);
			swapScreens(temp);
			robot.delay(100);
		} else {
			for(int x=0;x<amount;x++) {
				robot.keyPress(KeyEvent.VK_Z);
				robot.delay(randomNum(75,200));
			}
			robot.keyRelease(KeyEvent.VK_Z);
			robot.delay(100);
		}
	}
	public static void randomMove(int leftBound, int rightBound, String imageRecog) throws IOException {
		int minHoldTime = 80;
		int maxHoldTime = 120;
		int currPosition = getCurrPosition(gs2,imageRecog);
		int retries = 0;
		while(currPosition < 0 && retries < 3) {
			retries++;
			System.out.println("Couldn't find character! image recog blocked! Retrying after 5 seconds...");
			robot.delay(5000);
			currPosition = getCurrPosition(gs2,imageRecog);
		}
		if(currPosition == -1) {
			System.out.println("Couldn't find character! image recog blocked! Exiting...");
			System.exit(0);
		}
		int randomDelay = randomNum(minHoldTime,maxHoldTime);
		if(currPosition-leftBound < 23) {
			//move right
			moveRight(randomDelay);
		} else if(rightBound-currPosition < 23) {
			//move left
			moveLeft(randomDelay);
		} else {
			if(Math.random() > .5) {
				moveRight(randomDelay);
			} else {
				moveLeft(randomDelay);
			}
		}
		int newPosition = getCurrPosition(gs2,imageRecog);
		if(newPosition == currPosition) {
			System.out.println("Failed to move... trying again!");
			randomMove(leftBound, rightBound, imageRecog);
		} else {
			System.out.println("Moving from "+currPosition+" to " + newPosition);
			robot.delay(randomNum(500,1500));
		}
	}
	public static void attack(int numAttacks, int key, int buffLength) {
		int attackDelay = 615;
		System.out.println("Attacking " + numAttacks + " times with " + attackDelay + "ms Delay.");
		for(int x=0;x<numAttacks;x++) {
			long temptime = System.currentTimeMillis();
			if(buffLength > 0 && temptime >= buffTimer + buffLength*1000) {
				x +=rebuff(buffKeys);
			}
			robot.keyPress(key);
			robot.delay(attackDelay + randomPosNeg(randomNum(1,10)));
		}
		robot.keyRelease(key);
	}
	public static int rebuff(int[] keys) {
		buffTimer = System.currentTimeMillis();
		System.out.println("Rebuffing...");
		for(int x=0;x<keys.length;x++) {
			keyPress(keys[x]);
			robot.delay(randomNum(1000,1500));
		}
		return keys.length;
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
