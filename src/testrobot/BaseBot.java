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

class MaplePoint {
	int x;
	int y;
	public MaplePoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
	@Override
	public String toString() {
		return x + ", " + y;
	}
}
class Screen {
	int x;
	int y;
	String name;
	boolean pet;
	long petTimer = 0;
	public Screen(String name, int x, int y, boolean pet) {
		this.x=x;
		this.y=y;
		this.pet=pet;
		this.name = name;
	}
	public Screen(String name,int x, int y) {
		this.x=x;
		this.y=y;
		this.name = name;
		this.pet=false;
	}
}
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
	MaplePoint upperLeftBound;
	MaplePoint lowerRightBound;
	boolean ignoreY;
	public Zone(MaplePoint upperLeftBound, MaplePoint lowerRightBound, boolean ignoreY) {
		this.upperLeftBound = upperLeftBound;
		this.lowerRightBound = lowerRightBound;
		this.ignoreY = ignoreY;
	}
	public Zone(MaplePoint upperLeftBound, MaplePoint lowerRightBound) {
		this.upperLeftBound = upperLeftBound;
		this.lowerRightBound = lowerRightBound;
		this.ignoreY = false;
	}
	public int getLeftBound(){
		return upperLeftBound.x;
	}
	public int getRightBound(){
		return lowerRightBound.x;
	}
	public int getTopBound() {
		return upperLeftBound.y;
	}
	public int getBottomBound() {
		return lowerRightBound.y;
	}
	public boolean isInZone(int x, int y) {
		if(ignoreY) {
			if(x>=upperLeftBound.x && x<=lowerRightBound.x){
				return true;
			}
		} else {
			if(x>=upperLeftBound.x && x<=lowerRightBound.x && y>=upperLeftBound.y && y<=lowerRightBound.y){
				return true;
			}
		}
		return false;
	}
	public boolean isInZone(MaplePoint point) {
		if(ignoreY) {
			if(point.x>=upperLeftBound.x && point.x<=lowerRightBound.x){
				return true;
			}
		} else {
			if(point.x>=upperLeftBound.x && point.x<=lowerRightBound.x && point.y>=upperLeftBound.y && point.y<=lowerRightBound.y){
				return true;
			}
		}
		return false;
	}
	public boolean isInYZone(int y) {
		if(y>=upperLeftBound.y && y<=lowerRightBound.y){
			return true;
		}
		return false;
	}
	public boolean isInYZone(MaplePoint point) {
		if(point.y>=upperLeftBound.y && point.y<=lowerRightBound.y){
			return true;
		}
		return false;
	}
	public boolean isInXZone(int x) {
		if(x>=upperLeftBound.x && x<=lowerRightBound.x){
			return true;
		}
		return false;
	}
	public boolean isInXZone(MaplePoint point) {
		if(point.x>=upperLeftBound.x && point.x<=lowerRightBound.x){
			return true;
		}
		return false;
	}
}
class BuffData {
	int buffLength;
	int buffKey;
	String buffName;
	String screen;
	int delay;
	boolean enabled = false;
	public BuffData(String buffName, int buffLength, int buffKey, int buffDelay) {
		this.buffKey = buffKey;
		this.buffLength = buffLength;
		this.buffName = buffName;
		this.delay = buffDelay;
	}
	public BuffData(String buffName, int buffLength, int buffKey, int buffDelay, String screen) {
		this.buffKey = buffKey;
		this.buffLength = buffLength;
		this.buffName = buffName;
		this.screen = screen;
		this.delay = buffDelay;
	}
	public void enable(){
		this.enabled = true;
	}
	public void disable(){
		this.enabled = false;
	}
}
public abstract class BaseBot {
	Robot robot;
	LocalTime startTime;
	Rectangle mapleScreen;
	Rectangle chat = new Rectangle(525,700, 35, 30);
	Rectangle home;
	Rectangle insert;
	Rectangle pageUp;
	Screen[] screens;
	String currScreen = "";
	BuffData[] buffs = {
			new BuffData("Haste", 200, KeyEvent.VK_R,700, "hermit"),
			new BuffData("Mesos Up", 120, KeyEvent.VK_T,1000, "hermit"),
			new BuffData("Hermit", 120, KeyEvent.VK_U,100, "hermit"),
			new BuffData("Hyperbody", 155, KeyEvent.VK_R, 100, "spearman"),
			new BuffData("Holy Symbol", 120, KeyEvent.VK_U,2100,"bishop"),
			new BuffData("Bless", 200, KeyEvent.VK_Y,700,"bishop"),
			new BuffData("Invincible", 300, KeyEvent.VK_T,700,"bishop"),
			new BuffData("Magic Gaurd", 530, KeyEvent.VK_R,700,"bishop"),
			new BuffData("Maple Warrior", 270, KeyEvent.VK_D,1600,"bishop")
	};
	long[] buffTimers = new long[buffs.length];
	ArrayList<MinimapData> minimapDatas = new ArrayList<MinimapData>();
	public BaseBot(Robot robot, Screen[] screens) {
		this.robot = robot;
		this.screens = screens;
		startTime = LocalTime.now();
		botOutput("Starting bot with " + screens.length + " screens.");
		try {
			mapleScreen = getMapleScreen();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		chat = adjustRectangle(mapleScreen, chat);
		home= adjustRectangle(mapleScreen, new Rectangle(875, 723, 10, 12));
		insert = adjustRectangle(mapleScreen, new Rectangle(840, 723, 10, 12));
		pageUp = adjustRectangle(mapleScreen, new Rectangle(910, 723, 10, 12));
	}
	public void enableSkill(String skillName) {
		for(int x=0;x<buffs.length;x++) {
			if(buffs[x].buffName.equals(skillName)) {
				botOutput(buffs[x].buffName + " enabled.");
				buffs[x].enable();
			}
		}
	}
	public abstract void leech(int hours, MinimapData map) throws IOException;
	
	public MinimapData getMap(String name) {
		for(int x=0;x<minimapDatas.size();x++) {
			if(minimapDatas.get(x).name.equals(name)) {
				return minimapDatas.get(x);
			}
		}
		botOutput("Couldn't find your script among the following:");
		for(int x=0;x<minimapDatas.size();x++) {
			botOutput(minimapDatas.get(x).name);
		}
		return null;
	}
	public ArrayList<Rectangle> adjustRectangle(Rectangle mapleScreen, ArrayList<Rectangle> areas) {
		for(int x=0;x<areas.size();x++) {
			areas.get(x).setLocation((int)(mapleScreen.getX() + areas.get(x).getX()),(int)( mapleScreen.getY() + areas.get(x).getY()));
			botOutput("adjusted Search Area" + areas.get(x).getX() + ", " + areas.get(x).getY());
		}
		return areas;
	}
	public Rectangle adjustRectangle(Rectangle mapleScreen, Rectangle area) {
		area.setLocation((int)(mapleScreen.getX() + area.getX()),(int)( mapleScreen.getY() + area.getY()));
		botOutput("adjusted Search Area" + area.getX() + ", " + area.getY());
		return area;
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
		MaplePoint upperLeft = getCurrPosition(new Rectangle(0,0,1920,1080), "mapleIcon.png");
		BufferedImage bi = robot.createScreenCapture(new Rectangle(upperLeft.x-3,upperLeft.y+ 21,1025,768));  // retrieve image
	    File outputfile = new File("saved.png");
	    ImageIO.write(bi, "png", outputfile);
		return new Rectangle(upperLeft.x-3,upperLeft.y+ 21,1025,768);
	}
	public MaplePoint getMinimapPosition(MinimapData data) throws IOException {
		MaplePoint matches = getCurrPosition(data.minimapName,data.minimapNameFileName);
		if(matches.x == -1 || matches.y == -1) {
			botOutput("Map changed! Exiting...");
			exitScript();
		}
		return getCurrPosition(data.minimap,"minimapLocation.png");
	}
	public MaplePoint getCurrPosition(Rectangle rect, String fileName) throws IOException {
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
					return new MaplePoint(x1,y1);
				}
			}
		}
		File outputfile = new File("couldFind.png");
	    ImageIO.write(image, "png", outputfile);
		return new MaplePoint(-1,-1);
	}
	public void checkPots() throws IOException {
		MaplePoint insertFound = getCurrPosition(insert,"potions/0unagi.png");
		MaplePoint homeFound = getCurrPosition(home,"potions/0ManaBulls.png");
		MaplePoint pageUpFound = getCurrPosition(pageUp,"potions/0petFood.png");
		if( homeFound.x >= 0 || pageUpFound.x >= 0 || insertFound.x >= 0) {
			botOutput("Ran out of pots!");
			exitScript();
		}
	}
	public void moveToZoneX(Zone zone, MinimapData map) throws IOException {
		MaplePoint tempCoords = getMinimapPosition(map);
//		while(zone.isInZone(tempCoords[0], tempCoords[1])) {
//			
//		}
		if(tempCoords.x < zone.getLeftBound()) {
			robot.keyPress(KeyEvent.VK_RIGHT);
			while(tempCoords.x < zone.getLeftBound()) {
				if(tempCoords.x + 11 < zone.getRightBound()){
					robot.delay(75);
					robot.keyPress(KeyEvent.VK_ALT);
					robot.delay(75);
					robot.keyRelease(KeyEvent.VK_ALT);
				} else {
					robot.delay(75);
				}
				tempCoords = getMinimapPosition(map);
			}
			robot.keyRelease(KeyEvent.VK_RIGHT);
		} else if(tempCoords.x > zone.getRightBound()) {
			robot.keyPress(KeyEvent.VK_LEFT);
			while(tempCoords.x > zone.getRightBound()) {
				if(tempCoords.x - 11 > zone.getLeftBound()){
					robot.delay(75);
					robot.keyPress(KeyEvent.VK_ALT);
					robot.delay(75);
					robot.keyRelease(KeyEvent.VK_ALT);
				} else {
					robot.delay(75);
				}
				tempCoords = getMinimapPosition(map);
			}
			robot.keyRelease(KeyEvent.VK_LEFT);
		}
	}
	public void waitOnChat() throws IOException {
		boolean chatOpen = true;
		while(chatOpen) {
			if(getCurrPosition(chat,"chatOpen.png").x < 0) {
				chatOpen = false;
			} else {
				botOutput("chat is open! rechecking in 4 seconds...");
				robot.delay(4000);
			}
		}
	}
	public void pickUp(String screen, int amount) {
		String temp = new String(currScreen);
		botOutput("Picking up loot on screen " + screen + " " + amount + " times.");
		if(screen != currScreen) {
			swapScreens(getScreen(screen));
			robot.delay(100);
			for(int x=0;x<amount;x++) {
				robot.keyPress(KeyEvent.VK_Z);
				robot.delay(randomNum(75,200));
			}
			robot.keyRelease(KeyEvent.VK_Z);
			robot.delay(100);
			swapScreens(getScreen(temp));
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
	public void feedPets() {
		long currTime = System.currentTimeMillis();
		String oldScreen = new String(currScreen);
		for(int x=0;x<this.screens.length;x++) {
			if(this.screens[x].pet && this.screens[x].petTimer + 180 * 1000 < currTime) {
				if(!this.screens[x].name.equals(currScreen)) {
					swapScreens(getScreen(this.screens[x].name));
				}
				robot.delay(100);
				this.screens[x].petTimer = currTime;
				keyPress(KeyEvent.VK_PAGE_UP);
				botOutput("Feeding pet on Screen " + x);
				robot.delay(100);
			}
		}
		if(currScreen != oldScreen) {
			swapScreens(getScreen(oldScreen));
		}
	}
	public void rebuff() {
		long temptime = System.currentTimeMillis();
		String oldScreen = new String(currScreen);
		for(int x=0;x<buffs.length;x++) {
			if(buffs[x].enabled && temptime >= buffTimers[x] + buffs[x].buffLength*1000) {
				swapScreens(getScreen(buffs[x].screen));
				botOutput("Reapplying " + buffs[x].buffName);
				buffTimers[x] = temptime;
				keyPress(buffs[x].buffKey);
				robot.delay(buffs[x].delay);
			}
		}
		if(oldScreen != currScreen) {
			swapScreens(getScreen(oldScreen));
		}
	}
	public void rebuff(double buffDuration) {
		long temptime = System.currentTimeMillis();
		String oldScreen = new String(currScreen);
		for(int x=0;x<buffs.length;x++) {
			if(buffs[x].enabled && (temptime >= buffTimers[x] + ((buffs[x].buffLength*1000) * buffDuration))) {
				if(!currScreen.equals(buffs[x].screen)) {
					swapScreens(getScreen(buffs[x].screen));
				}
				botOutput("Reapplying " + buffs[x].buffName);
				buffTimers[x] = temptime;
				keyPress(buffs[x].buffKey);
				robot.delay(buffs[x].delay);
			}
		}
		if(oldScreen != currScreen) {
			swapScreens(getScreen(oldScreen));
		}
	}
	public void attack(int numAttacks, int key, int delay) throws IOException {
		botOutput("Attacking " + numAttacks + " times with " + delay + "ms Delay.");
		for(int x=0;x<numAttacks;x++) {
			waitOnChat();
			robot.keyPress(key);
			robot.keyPress(key);
			robot.keyPress(key);
			robot.delay(delay + randomPosNeg(randomNum(1,10)));
		}
		robot.keyRelease(key);
	}
	public Screen getScreen(String name) {
		for(int x=0;x<this.screens.length;x++) {
			if(this.screens[x].name.equals(name)){
				return this.screens[x];
			}
		}
		return null;
	}
	public void swapScreens(Screen screen) {
		click(screen.x,screen.y);
		currScreen = screen.name;
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
	}
	public void teleportLeft() {
		robot.keyPress(KeyEvent.VK_LEFT);
		robot.keyPress(KeyEvent.VK_ALT);
		robot.delay(200);
		robot.keyRelease(KeyEvent.VK_LEFT);
		robot.keyRelease(KeyEvent.VK_ALT);
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
	public void outputCoords(MaplePoint coords) {
		botOutput("X: " + coords.x + ", Y: " + coords.y);
	}
}