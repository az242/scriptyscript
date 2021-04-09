package testrobot;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

class ScriptArea {
	Rectangle searchArea;
	int leftBound;
	int rightBound;
	String name;
	public ScriptArea(String name, Rectangle searchArea, int leftBound, int rightBound) {
		this.name = name;
		this.searchArea = searchArea;
		this.leftBound = leftBound;
		this.rightBound = rightBound;
	}
}
public class Bot {
	Robot robot;
	//600 heal minimun 
	int[][] screen = {{100,100},{100,1000}};
	int currScreen = 0;
	Rectangle gs1AFK = new Rectangle(300,425,700,75);
	Rectangle gs2AFK = new Rectangle(300,425,150,75);
	Rectangle chat = new Rectangle(525,700, 35, 30);
	ScriptArea[] scripts = {new ScriptArea("GS1 no move",gs1AFK,495,575), new ScriptArea("GS2 no move",gs2AFK,20,105)};
	long buffTimer = 0;
	int[] buffKeys = {KeyEvent.VK_U};
	
	public Bot(Robot robot) {
		this.robot = robot;
		try {
			Rectangle mapleScreen = getMapleScreen();
			adjustScripts(mapleScreen);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void adjustScripts(Rectangle mapleScreen) {
		for(int x=0;x<scripts.length;x++) {
			scripts[x].searchArea.setLocation((int)(mapleScreen.getX() + scripts[x].searchArea.getX()),(int)( mapleScreen.getY() + scripts[x].searchArea.getY()));
			System.out.println("adjusted Search Area" + scripts[x].searchArea.getX() + ", " + scripts[x].searchArea.getY());
		}
		chat.setLocation((int)(mapleScreen.getX() + chat.getX()),(int)( mapleScreen.getY() + chat.getY()));
		for(int x=0;x<screen.length;x++) {
			screen[x] = new int[] { screen[x][0] + (int)mapleScreen.getX(), screen[x][1] + (int)mapleScreen.getY()};
		}
	}
	public Rectangle getMapleScreen() throws IOException {
		int[] upperLeft = getCurrPosition(new Rectangle(0,0,1920,1080), "mapleIcon.png");
//		BufferedImage bi = robot.createScreenCapture(new Rectangle(upperLeft[0]-3,upperLeft[1]+ 21,1025,768));  // retrieve image
//	    File outputfile = new File("saved.png");
//	    ImageIO.write(bi, "png", outputfile);
		return new Rectangle(upperLeft[0]-3,upperLeft[1]+ 21,1025,768);
	}
	public void mainAfkFlow(ScriptArea params, String searchCriteria) throws IOException {
		if(params == null) {
			System.out.println("No script with that name found! Exiting...");
			System.exit(0);
		}
		swapScreens(0);
		System.out.println("Starting at position: " + getCurrPosition(params.searchArea,searchCriteria)[0] + " with bounds: " + params.leftBound + ", " + params.rightBound);
		rebuff(buffKeys);
		for(int x=0;x<100;x++) {
			System.out.println("<--------------->");
			System.out.println("iteration " + x + " with bounds: " + params.leftBound + ", " + params.rightBound);
			System.out.println("<--------------->");
			randomMove(params.leftBound,params.rightBound, searchCriteria, params.searchArea);
			attack(100 - randomNum(1,40), KeyEvent.VK_V, 120);
			if(x%9 == 0 && x!=0) {
				feedPet(0, KeyEvent.VK_PAGE_UP);
//				feedPet(1, KeyEvent.VK_PAGE_DOWN);
			}
			if(x%6 == 0 && x!=0) {
				pickUp(0,randomNum(10,20));
				robot.delay(500);
//				pickUp(1,randomNum(10,20));
			}
		}
	}
	public int[] getCurrPosition(Rectangle rect, String fileName) throws IOException {
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
//					System.out.println(x1 + ", " + y1);
					return new int[]{x1,y1};
				}
			}
		}
		File outputfile = new File("couldFind.png");
	    ImageIO.write(image, "png", outputfile);
		return new int[] {-1,-1};
	}
	public void swapScreens(int screenId) {
		click(screen[screenId][0],screen[screenId][1]);
		robot.delay(200);
	}
	public void pickUp(int screen, int amount) {
		int temp = currScreen;
		System.out.println("Picking up loot on screen " + screen + " " + amount + " times.");
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
	public void randomMove(int leftBound, int rightBound, String imageRecog, Rectangle area) throws IOException {
		int minHoldTime = 80;
		int maxHoldTime = 120;
		int currPosition = getCurrPosition(area,imageRecog)[0];
		int retries = 0;
		while(currPosition < 0 && retries < 3) {
			retries++;
			System.out.println("Couldn't find character! image recog blocked! Retrying after 5 seconds...");
			robot.delay(5000);
			currPosition = getCurrPosition(area,imageRecog)[0];
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
		int newPosition = getCurrPosition(area,imageRecog)[0];
		if(newPosition == currPosition) {
			System.out.println("Failed to move... trying again in 1 seconds!");
			robot.delay(1000);
			randomMove(leftBound, rightBound, imageRecog, area);
		} else {
			System.out.println("Moving from "+currPosition+" to " + newPosition);
			robot.delay(randomNum(500,1500));
		}
	}
	public void attack(int numAttacks, int key, int buffLength) throws IOException {
		int attackDelay = 615;
//		int attackDelay = 1000;
		System.out.println("Attacking " + numAttacks + " times with " + attackDelay + "ms Delay.");
		for(int x=0;x<numAttacks;x++) {
			boolean chatOpen = true;
			while(chatOpen) {
				if(getCurrPosition(chat,"chatOpen.png")[0] < 0) {
					chatOpen = false;
				} else {
					System.out.println("chat is open! rechecking in 4 seconds...");
					robot.delay(4000);
				}
			}
			long temptime = System.currentTimeMillis();
			if(buffLength > 0 && temptime >= buffTimer + buffLength*1000 - randomNum(1000,5000)) {
				x +=rebuff(buffKeys);
			}
			robot.keyPress(key);
			robot.delay(attackDelay + randomPosNeg(randomNum(1,10)));
		}
		robot.keyRelease(key);
	}
	public ScriptArea getScript(String name) {
		for(int x=0;x<this.scripts.length;x++) {
			if(this.scripts[x].name.equals(name)) {
				return this.scripts[x];
			}
		}
		return null;
	}
	public int rebuff(int[] keys) {
		buffTimer = System.currentTimeMillis();
		System.out.println("Rebuffing...");
		for(int x=0;x<keys.length;x++) {
			keyPress(keys[x]);
			robot.delay(randomNum(1500,2500));
		}
		return keys.length;
	}
	public void feedPet(int screen, int key) {
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
	public int randomPosNeg(int input) {
		double random = Math.random();
		if(random > .5) {
			return input;
		} else {
			return input * -1;
		}
	}
	public int randomNum(int min, int max) {
		return (int)(Math.random()*(max-min))+min;
	}
	public void click(int x, int y) {
		robot.mouseMove(x, y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	public void keyPress(int key) {
		robot.keyPress(key);
		robot.keyRelease(key);
	}
	public void keyHold(int key, int delay) {
		robot.keyPress(key);
		robot.delay(delay);
		robot.keyRelease(key);
	}
	public void moveRight(int amount) {
		robot.keyPress(KeyEvent.VK_RIGHT);
		robot.delay(amount);
		robot.keyRelease(KeyEvent.VK_RIGHT);
	}
	public void moveLeft(int amount) {
		robot.keyPress(KeyEvent.VK_LEFT);
		robot.delay(amount);
		robot.keyRelease(KeyEvent.VK_LEFT);
	}
}
