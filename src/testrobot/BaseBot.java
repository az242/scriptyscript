package testrobot;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

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
class MinimapData {
	Rectangle minimapName;
	Rectangle minimap;
	String name;
	String minimapNameFileName;
	public MinimapData(String name, Rectangle minimap, Rectangle minimapName, String minimapNameFileName) {
		this.name = name;
		this.minimap = minimap;
		this.minimapName = minimapName;
		this.minimapNameFileName = minimapNameFileName;
	}
	public void adjustPoint(int x, int y) {
		minimapName.setLocation(x + (int)minimapName.getX(), y + (int)minimapName.getY());
		minimap.setLocation(x + (int)minimap.getX(), y + (int)minimap.getY());
	}
}
class Zone {
	int[] upperLeftBound;
	int[] lowerRightBound;
	boolean ignoreY;
	public Zone(int[] upperLeftBound, int[] lowerRightBound, boolean ignoreY) {
		this.upperLeftBound = upperLeftBound;
		this.lowerRightBound = lowerRightBound;
		this.ignoreY = ignoreY;
	}
	public Zone(int[] upperLeftBound, int[] lowerRightBound) {
		this.upperLeftBound = upperLeftBound;
		this.lowerRightBound = lowerRightBound;
		this.ignoreY = false;
	}
	public int getLeftBound(){
		return upperLeftBound[0];
	}
	public int getRightBound(){
		return lowerRightBound[0];
	}
	public boolean isInZone(int x, int y) {
		if(ignoreY) {
			if(x>upperLeftBound[0] && x<lowerRightBound[0]){
				return true;
			}
		} else {
			if(x>upperLeftBound[0] && x<lowerRightBound[0] && y>upperLeftBound[1] && y<lowerRightBound[1]){
				return true;
			}
		}
		return false;
	}
	public boolean isInYZone(int y) {
		if(y>upperLeftBound[1] && y<lowerRightBound[1]){
			return true;
		}
		return false;
	}
	public boolean isInXZone(int x) {
		if(x>upperLeftBound[0] && x<lowerRightBound[0]){
			return true;
		}
		return false;
	}
}
class BuffData {
	int buffLength;
	int buffKey;
	String buffName;
	public BuffData(String buffName, int buffLength, int buffKey) {
		this.buffKey = buffKey;
		this.buffLength = buffLength;
		this.buffName = buffName;
	}
}
public class BaseBot {
	Robot robot;
	LocalTime startTime;
	Rectangle mapleScreen;
	public BaseBot(Robot robot) {
		this.robot = robot;
		startTime = LocalTime.now();
		try {
			mapleScreen = getMapleScreen();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public ArrayList<Rectangle> adjustRectangle(Rectangle mapleScreen, ArrayList<Rectangle> areas) {
		for(int x=0;x<areas.size();x++) {
			areas.get(x).setLocation((int)(mapleScreen.getX() + areas.get(x).getX()),(int)( mapleScreen.getY() + areas.get(x).getY()));
			botOutput("adjusted Search Area" + areas.get(x).getX() + ", " + areas.get(x).getY());
		}
		return areas;
	}
	public ArrayList<MinimapData> adjustMinimapData(Rectangle mapleScreen, ArrayList<MinimapData> data) {
		for(int x=0;x<data.size();x++) {
			data.get(x).adjustPoint((int)mapleScreen.getX(), (int)mapleScreen.getY());
			botOutput("adjusted map data for " + data.get(x).name);
		}
		return data;
	}
	public int[][] adjustScreens(Rectangle mapleScreen, int[][] screens) {
		for(int x=0;x<screens.length;x++) {
			screens[x] = new int[] { screens[x][0] + (int)mapleScreen.getX(), screens[x][1] + (int)mapleScreen.getY()};
		}
		return screens;
	}
	public Rectangle getMapleScreen() throws IOException {
		int[] upperLeft = getCurrPosition(new Rectangle(0,0,1920,1080), "mapleIcon.png");
		BufferedImage bi = robot.createScreenCapture(new Rectangle(upperLeft[0]-3,upperLeft[1]+ 21,1025,768));  // retrieve image
	    File outputfile = new File("saved.png");
	    ImageIO.write(bi, "png", outputfile);
		return new Rectangle(upperLeft[0]-3,upperLeft[1]+ 21,1025,768);
	}
	public int[] getMinimapPosition(MinimapData data) throws IOException {
		int[] matches = getCurrPosition(data.minimapName,data.minimapNameFileName);
		if(matches[0] == -1 || matches[1] == -1) {
			botOutput("Map changed! Exiting...");
			exitScript();
		}
		return getCurrPosition(data.minimap,"minimapLocation.png");
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
					File outputfile = new File("couldFind.png");
				    ImageIO.write(image, "png", outputfile);
//					System.out.println(x1 + ", " + y1);
					return new int[]{x1,y1};
				}
			}
		}
		File outputfile = new File("couldFind.png");
	    ImageIO.write(image, "png", outputfile);
		return new int[] {-1,-1};
	}
	public void moveToZoneX(Zone zone, MinimapData map) throws IOException {
		int[] tempCoords = getMinimapPosition(map);
		if(tempCoords[0] < zone.getLeftBound()) {
			while(tempCoords[0] < zone.getLeftBound()) {
				if(tempCoords[0] + 11 < zone.getLeftBound()){
					teleportRight();
				} else {
					moveRight(300);
				}
				tempCoords = getMinimapPosition(map);
			}
		} else if(tempCoords[0] > zone.getRightBound()) {
			while(tempCoords[0] > zone.getRightBound()) {
				if(tempCoords[0] - 11 > zone.getRightBound()){
					teleportLeft();
				} else {
					moveLeft(300);
				}
				tempCoords = getMinimapPosition(map);
			}
		}
	}
	public void swapScreens(int screenId,int[][] screens) {
		click(screens[screenId][0],screens[screenId][1]);
		robot.delay(200);
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
	public void teleportRight() {
		robot.keyPress(KeyEvent.VK_RIGHT);
		robot.keyPress(KeyEvent.VK_ALT);
		robot.delay(200);
		robot.keyRelease(KeyEvent.VK_RIGHT);
		robot.keyRelease(KeyEvent.VK_ALT);
		robot.delay(400);
	}
	public void teleportLeft() {
		robot.keyPress(KeyEvent.VK_LEFT);
		robot.keyPress(KeyEvent.VK_ALT);
		robot.delay(200);
		robot.keyRelease(KeyEvent.VK_LEFT);
		robot.keyRelease(KeyEvent.VK_ALT);
		robot.delay(400);
	}
	public void teleportUp() {
		robot.keyPress(KeyEvent.VK_UP);
		robot.keyPress(KeyEvent.VK_ALT);
		robot.delay(200);
		robot.keyRelease(KeyEvent.VK_UP);
		robot.keyRelease(KeyEvent.VK_ALT);
		robot.delay(400);
	}
	public void jumpDown() {
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyPress(KeyEvent.VK_SPACE);
		robot.delay(100);
		robot.keyRelease(KeyEvent.VK_DOWN);
		robot.keyRelease(KeyEvent.VK_SPACE);
		robot.delay(1000);
	}
	public void botOutput(String output) {
		LocalTime myObj = LocalTime.now();
	    System.out.println("[" + myObj + "] " + output);
	}
	public void exitScript() {
		LocalTime now = LocalTime.now();
		botOutput("Script ran for " + MINUTES.between(startTime, now) + " minutes");
		botOutput("Exiting script...");
		System.exit(0);
	}
}
