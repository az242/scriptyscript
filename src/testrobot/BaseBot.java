package testrobot;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

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
	int index;
	String name;
	String file;
	boolean pet;
	long petTimer = 0;
	public Screen(String name, String fileIdentifier, boolean pet) {
		this.name = name;
		this.file = fileIdentifier;
		this.index = -1;
		this.pet=pet;
	}
	public Screen(String name, String fileIdentifier) {
		this.name = name;
		this.file = fileIdentifier;
		this.index = -1;
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
	public Zone(int x1, int y1, int x2, int y2) {
		this.upperLeftBound = new MaplePoint(x1,y1);
		this.lowerRightBound = new MaplePoint(x2,y2);
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
	String image;
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
	public BuffData(String buffName, int buffLength, int buffKey, int buffDelay, String screen, String image) {
		this.buffKey = buffKey;
		this.buffLength = buffLength;
		this.buffName = buffName;
		this.screen = screen;
		this.delay = buffDelay;
		this.image = image;
	}
	public void enable(){
		this.enabled = true;
	}
	public void disable(){
		this.enabled = false;
	}
}
class AttackData {
	int delay;
	int key;
	int manaCost;
	public AttackData(int key, int delay, int manaCost) {
		this.delay =delay;
		this.key = key;
		this.manaCost = manaCost;
	}
	public AttackData(int key, int delay) {
		this.delay =delay;
		this.key = key;
	}
}
public abstract class BaseBot {
	Robot robot;
	LocalTime startTime;
	Rectangle mapleScreen;
	Rectangle chat = new Rectangle(605,705, 30, 30);
	Rectangle home;
	Rectangle insert;
	Rectangle pageUp;
	Rectangle buffZone;
	Screen[] screens;
	String currScreen = "";
	BuffData[] buffs = {
			new BuffData("Haste", 200, KeyEvent.VK_R,1000, "hermit"),
			new BuffData("Mesos Up", 120, KeyEvent.VK_T,1000, "hermit"),
			new BuffData("Hermit Pot", 2000, KeyEvent.VK_PAGE_DOWN,100, "hermit"),
			new BuffData("Hermit", 120, KeyEvent.VK_U,100, "hermit"),
			
			new BuffData("Hyperbody", 155 , KeyEvent.VK_R, 100, "spearman"),
			new BuffData("Spearman Pot", 800 , KeyEvent.VK_PAGE_DOWN, 100, "spearman"),
			
			new BuffData("Holy Symbol", 120, KeyEvent.VK_U,2100,"bishop","skills/hs.png"),
			new BuffData("Bless", 200, KeyEvent.VK_Y,700,"bishop","skills/hs.png"),
			new BuffData("Invincible", 300, KeyEvent.VK_T,700,"bishop","skills/invincible.png"),
			new BuffData("Magic Gaurd", 600, KeyEvent.VK_R,700,"bishop","skills/mg.png"),
			new BuffData("Maple Warrior", 600, KeyEvent.VK_D,1600,"bishop","skills/mw.png"),
			new BuffData("Magic", 1900, KeyEvent.VK_DELETE,500,"bishop")
	};
	HashMap<String, AttackData> attacks = new HashMap<String, AttackData>();
	int startingLevel;
	int startingMesos;
	long[] buffTimers = new long[buffs.length];
	int[] buffCounts = new int[buffs.length];
	String server;
	BufferedImage[] numImages;
	BufferedImage[] levelImages;
	BufferedImage[] mesosNumImages;
	ArrayList<MinimapData> minimapDatas = new ArrayList<MinimapData>();
	public BaseBot(Robot robot, Screen[] screens, String server) {
		this.server = server;
		this.attacks.put("genesis", new AttackData(KeyEvent.VK_C, 2375, 2500));
		this.attacks.put("heal", new AttackData(KeyEvent.VK_V, 590));
		this.attacks.put("shining", new AttackData(KeyEvent.VK_N, 1050));
		this.robot = robot;
		this.screens = screens;
		startTime = LocalTime.now();
		botOutput("Initializing bot with " + screens.length + " screens.");
		try {
			mapleScreen = getMapleScreen();
			botOutput("Found Maple screen");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		chat = adjustRectangle(mapleScreen, chat);
		home= adjustRectangle(mapleScreen, new Rectangle(875, 723, 10, 12));
		insert = adjustRectangle(mapleScreen, new Rectangle(840, 723, 10, 12));
		pageUp = adjustRectangle(mapleScreen, new Rectangle(910, 723, 10, 12));
		buffZone = adjustRectangle(mapleScreen, new Rectangle(990, 24, 30, 30));
		
		try {
			initScreens(this.server);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			mesosNumImages = new BufferedImage[]{
					ImageIO.read(new File("inventory/0.png")),ImageIO.read(new File("inventory/1.png")),ImageIO.read(new File("inventory/2.png"))
					,ImageIO.read(new File("inventory/3.png")),ImageIO.read(new File("inventory/4.png")),ImageIO.read(new File("inventory/5.png")),
					ImageIO.read(new File("inventory/6.png")),ImageIO.read(new File("inventory/7.png")),ImageIO.read(new File("inventory/8.png")),ImageIO.read(new File("inventory/9.png"))};
			numImages = new BufferedImage[]{
					ImageIO.read(new File("numbers/0.png")),ImageIO.read(new File("numbers/1.png")),ImageIO.read(new File("numbers/2.png"))
					,ImageIO.read(new File("numbers/3.png")),ImageIO.read(new File("numbers/4.png")),ImageIO.read(new File("numbers/5.png")),
					ImageIO.read(new File("numbers/6.png")),ImageIO.read(new File("numbers/7.png")),ImageIO.read(new File("numbers/8.png")),ImageIO.read(new File("numbers/9.png"))};
			levelImages = new BufferedImage[]{
					ImageIO.read(new File("numbers/lvl0.png")),ImageIO.read(new File("numbers/lvl1.png")),ImageIO.read(new File("numbers/lvl2.png"))
					,ImageIO.read(new File("numbers/lvl3.png")),ImageIO.read(new File("numbers/lvl4.png")),ImageIO.read(new File("numbers/lvl5.png")),
					ImageIO.read(new File("numbers/lvl6.png")),ImageIO.read(new File("numbers/lvl7.png")),ImageIO.read(new File("numbers/lvl8.png")),ImageIO.read(new File("numbers/lvl9.png"))};
			botOutput("Loaded numbers.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			startingMesos = getMesos();
			startingLevel = getLevel();
			botOutput("Starting level: " + startingLevel);
			botOutput("Starting mesos: " + startingMesos);
		} catch (IOException e) {
			botOutput("Error getting starting mesos/level");
		}
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
			System.out.println(minimapDatas.get(x).name);
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
		}
		return areas;
	}
	public Rectangle adjustRectangle(Rectangle mapleScreen, Rectangle area) {
		area.setLocation((int)(mapleScreen.getX() + area.getX()),(int)( mapleScreen.getY() + area.getY()));
//		botOutput("adjusted Search Area" + area.getX() + ", " + area.getY());
		return area;
	}
	public void adjustMinimapData(Rectangle mapleScreen) {
		for(int x=0;x<minimapDatas.size();x++) {
			minimapDatas.get(x).adjustPoint((int)mapleScreen.getX(), (int)mapleScreen.getY());
			botOutput("Adjusted map data for " + minimapDatas.get(x).name);
		}
	}
	public void initScreens(String server) throws IOException {
		switch(server) {
		case "royals":
			initScreensMapleRoyals();
			return;
		case "legends":
			initScreensLegends();
			return;
		}
		botOutput("Unrecognized Server type. Exiting...");
		exitScript();
		
	}
	public void initScreensMapleRoyals() throws IOException {
		if(this.screens.length == 1){
			botOutput("Found screen " + 0 + ". Verifying character...");
			click(100,100);
			robot.delay(500);
			Rectangle nameSearch = new Rectangle(85,735, 65, 30);
			nameSearch = adjustRectangle(this.mapleScreen, nameSearch);
			boolean found = false;
			for(int i=0;i<this.screens.length;i++) {
				if(this.screens[i].index < 0 ) {
					botOutput("Checking if this screen is " + this.screens[i].name);
					MaplePoint foundName = getCurrPosition(nameSearch, this.screens[i].file);
					if(foundName.x > 0) {
						this.screens[i].index = 0;
						found = true;
						botOutput("Verified: " + this.screens[i].name + " on screen " + 0);
						break;
					}
				}
			}
			if(!found) {
				// exit warning theres a unrecognized character
				BufferedImage bi = robot.createScreenCapture(nameSearch);  // retrieve image
			    File outputfile = new File("nameTest.png");
			    ImageIO.write(bi, "png", outputfile);
				botOutput("Unrecognized character. Exiting...");
				exitScript();
			}
			return;
		}
		Rectangle taskSearchArea = new Rectangle(600,1000,500,100);
		MaplePoint upperLeft = getCurrPosition(taskSearchArea, "mapleTaskIcon.png");
		if(upperLeft.x < 0) {
			botOutput("Couldn't find taskbar icon. Exiting...");
			exitScript();
		}
		upperLeft.y = upperLeft.y + taskSearchArea.y;
		upperLeft.x = upperLeft.x + taskSearchArea.x;
		//85 735 60 24
		for(int x=0;x<this.screens.length;x++) {
			MaplePoint clickPoint = new MaplePoint(upperLeft.x + (50*x), upperLeft.y);
			click(clickPoint);
			robot.delay(100);
			//find icon
			Rectangle nameSearch = new Rectangle(85,735, 65, 30);
			nameSearch = adjustRectangle(this.mapleScreen, nameSearch);
			boolean found = false;
			for(int i=0;i<this.screens.length;i++) {
				if(this.screens[i].index < 0 ) {
					botOutput("Checking if this screen is " + this.screens[i].name + ", " + i);
					MaplePoint foundName = getCurrPosition(nameSearch, this.screens[i].file);
					if(foundName.x >= 0) {
						this.screens[i].index = x;
						found = true;
						botOutput("Verified: " + this.screens[i].name + " on screen " + x);
						break;
					}
				}
			}
			if(!found) {
				// exit warning theres a unrecognized character
				BufferedImage bi = robot.createScreenCapture(nameSearch);  // retrieve image
			    File outputfile = new File("nameTest.png");
			    ImageIO.write(bi, "png", outputfile);
				botOutput("Unrecognized character. Exiting...");
				exitScript();
			}
		}
	}
	public void initScreensLegends() throws IOException {
		if(this.screens.length == 1){
			botOutput("Found screen " + 0 + ". Verifying character...");
			click(100,100);
			robot.delay(500);
			Rectangle nameSearch = new Rectangle(85,735, 65, 30);
			nameSearch = adjustRectangle(this.mapleScreen, nameSearch);
			boolean found = false;
			for(int i=0;i<this.screens.length;i++) {
				if(this.screens[i].index < 0 ) {
//					botOutput("Checking if this screen is " + this.screens[i].name);
					MaplePoint foundName = getCurrPosition(nameSearch, this.screens[i].file);
					if(foundName.x > 0) {
						this.screens[i].index = 0;
						found = true;
						botOutput("Verified: " + this.screens[i].name + " on screen " + 0);
						break;
					}
				}
			}
			if(!found) {
				// exit warning theres a unrecognized character
				BufferedImage bi = robot.createScreenCapture(nameSearch);  // retrieve image
			    File outputfile = new File("nameTest.png");
			    ImageIO.write(bi, "png", outputfile);
				botOutput("Unrecognized character. Exiting...");
				exitScript();
			}
			return;
		}
		MaplePoint upperLeft = getCurrPosition(new Rectangle(900,1400,500,100), "mapleTaskIcon.png");
		upperLeft.y = upperLeft.y + 1400;
		upperLeft.x = upperLeft.x + 900;
		outputCoords(upperLeft); 
		int width = this.screens.length*165;
		//85 735 60 24
		Rectangle clickZone = new Rectangle(upperLeft.x-(width/2),upperLeft.y-165,width,165);
		for(int x=0;x<this.screens.length;x++) {
			click(upperLeft);
			robot.delay(500);
			clickZone = new Rectangle(upperLeft.x-(width/2) + (165*x) ,upperLeft.y-165,width - (165*x),165);
			MaplePoint foundIcon = getCurrPosition(clickZone, "mapleTaskIconSmall.png");
			//find icon
			if(foundIcon.x > 0) {
				foundIcon.x += clickZone.x;
				foundIcon.y += clickZone.y;
				botOutput("Found screen " + x + ". Verifying character...");
				click(foundIcon);
				robot.delay(500);
				Rectangle nameSearch = new Rectangle(85,735, 65, 30);
				nameSearch = adjustRectangle(this.mapleScreen, nameSearch);
				boolean found = false;
				for(int i=0;i<this.screens.length;i++) {
					if(this.screens[i].index < 0 ) {
//						botOutput("Checking if this screen is " + this.screens[i].name);
						MaplePoint foundName = getCurrPosition(nameSearch, this.screens[i].file);
						if(foundName.x >= 0) {
							this.screens[i].index = x;
							found = true;
							botOutput("Verified: " + this.screens[i].name + " on screen " + x);
							break;
						}
					}
				}
				if(!found) {
					// exit warning theres a unrecognized character
					BufferedImage bi = robot.createScreenCapture(nameSearch);  // retrieve image
				    File outputfile = new File("nameTest.png");
				    ImageIO.write(bi, "png", outputfile);
					botOutput("Unrecognized character. Exiting...");
					exitScript();
				}
			} else {
				//couldnt find expect amount of screens, exit.
				botOutput("Couldn't find expect number of screens. Exiting...");
				exitScript();
			}
		}
	}
	public void swapMapleScreen(Screen screen) throws IOException {
		switch(this.server) {
		case "legends":
			swapMapleScreenLegends(screen);
			return;
		case "royals":
			swapMapleScreenRoyals(screen);
			return;
		}
	}
	public void swapMapleScreenLegends(Screen screen) throws IOException {
		if(this.screens.length == 1){
			botOutput("Swapped to screen " + screen.index);
			click(100,100);
			this.currScreen = screen.name;
			robot.delay(200);
			return;
		}
		MaplePoint upperLeft = getCurrPosition(new Rectangle(900,1400,500,50), "mapleTaskIcon.png");
		upperLeft.y = upperLeft.y + 1400;
		upperLeft.x = upperLeft.x + 900;
		int width = this.screens.length*165;
		//85 735 60 24
		Rectangle clickZone = new Rectangle(upperLeft.x-(width/2),upperLeft.y-165,width,165);
		click(upperLeft);
		robot.delay(400);
		clickZone = new Rectangle(upperLeft.x-(width/2) + (165*screen.index) ,upperLeft.y-165,width - (165*screen.index),165);
		MaplePoint foundIcon = getCurrPosition(clickZone, "mapleTaskIconSmall.png");
		//find icon
		if(foundIcon.x > 0 && this.screens.length > 1) {
			foundIcon.x += clickZone.x;
			foundIcon.y += clickZone.y;
			botOutput("Swapped to screen " + screen.index);
			click(foundIcon);
			this.currScreen = screen.name;
			robot.delay(200);
		}else {
			//couldnt find expect amount of screens, exit.
			botOutput("Couldn't find expect number of screens. Exiting...");
			exitScript();
		}
	}
	public void swapMapleScreenRoyals(Screen screen) throws IOException {
		if(this.screens.length == 1){
			botOutput("Swapped to screen " + screen.index);
			click(100,100);
			this.currScreen = screen.name;
			robot.delay(200);
			return;
		}
		Rectangle taskSearchArea = new Rectangle(600,1000,500,100);
		MaplePoint upperLeft = getCurrPosition(taskSearchArea, "mapleTaskIcon.png");
		if(upperLeft.x < 0) {
			botOutput("Couldn't find taskbar icon. Exiting...");
			exitScript();
		}
		upperLeft.y = upperLeft.y + taskSearchArea.y;
		upperLeft.x = upperLeft.x + taskSearchArea.x + (screen.index*50);
		//85 735 60 24
		click(upperLeft);
		//find icon
		this.currScreen = screen.name;
		robot.delay(200);
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
	public MaplePoint checkMapMatch(MinimapData data) throws IOException {
		MaplePoint matches = getCurrPosition(data.minimapName,data.minimapNameFileName);
		return matches;
	}
	public MaplePoint getCurrPosition(Rectangle rect, String fileName) throws IOException {
		BufferedImage imageRecog = ImageIO.read(new File(fileName));
		BufferedImage image = robot.createScreenCapture(rect);
		for(int x1=0;x1<image.getWidth()-imageRecog.getWidth();x1++) {
			for(int y1=0;y1<image.getHeight()-imageRecog.getHeight();y1++) {
				boolean matches = true;
				ImageLoop:
				for(int x2=0;x2<imageRecog.getWidth();x2++) {
					for(int y2=0;y2<imageRecog.getHeight();y2++) {
						if(imageRecog.getRGB(x2, y2) != image.getRGB(x1+x2, y1+y2)) {
							matches = false;
							break ImageLoop;
						}
					}
				}
				if(matches) {
//					File outputfile = new File("couldFind.png");
//				    ImageIO.write(image, "png", outputfile);
//					System.out.println(x1 + ", " + y1);
					return new MaplePoint(x1,y1);
				}
			}
		}
//		File outputfile = new File("couldntFind.png");
//	    ImageIO.write(image, "png", outputfile);
		return new MaplePoint(-1,-1);
	}
	public MaplePoint findInventory() throws IOException {
		Rectangle inventorySearch = new Rectangle(850,50, 175, 650);
		inventorySearch = adjustRectangle(mapleScreen, inventorySearch);
		MaplePoint x = getCurrPosition(inventorySearch, "inventory/inventory.png");
		if(x.x < 0) {
			
		} else {
			//adjust
			x.x = inventorySearch.x + x.x;
			x.y = inventorySearch.y + x.y;
		}
		
		return x;
	}
	public int getHp() throws IOException {
		int hp = 0;
		Rectangle hpRect = new Rectangle(262,739,35,7);
		hpRect = adjustRectangle(mapleScreen, hpRect);
		ArrayList<Integer> numbersFound = findNums(hpRect);
		for(int x=0;x<numbersFound.size();x++) {
			hp = hp + numbersFound.get(x)*10^(numbersFound.size()-x-1);
		}
		return hp;
	}
	public int getMp() throws IOException {
		int mp = 0;
		Rectangle MPRect = new Rectangle(395,739,35,7);
		MPRect = adjustRectangle(mapleScreen, MPRect);
		ArrayList<Integer> numbersFound = findNums(MPRect);
		for(int x=0;x<numbersFound.size();x++) {
			mp = (int) (mp + numbersFound.get(x)*Math.pow(10, numbersFound.size()-x-1));
		}
		//each num 5x7
		//1 pixel inbetween numbers
		//starts at 353 739
		return mp;
	}
	public int getLevel() throws IOException {
		int level = 0;
		Rectangle LevelRect = new Rectangle(38,744, 35, 13);
		LevelRect = adjustRectangle(mapleScreen, LevelRect);
		ArrayList<Integer> findLevelNums = findLevelNums(LevelRect);
		for(int x=0;x<findLevelNums.size();x++) {
			level = (int) (level + findLevelNums.get(x)*Math.pow(10, findLevelNums.size()-x-1));
		}
		return level;
	}
	public int getMesos() throws IOException {
		MaplePoint invLoc = findInventory();
		Rectangle billionmesos = new Rectangle(36 + invLoc.x,259 + invLoc.y,20,9);
		Rectangle millionmesos = new Rectangle(60 + invLoc.x,259 + invLoc.y,20,9);
		Rectangle thousandmesos = new Rectangle(84 + invLoc.x,259 + invLoc.y,20,9);
		Rectangle hundredmesos = new Rectangle(108 + invLoc.x,259 + invLoc.y,20,9);
		ArrayList<Integer> list1 = findMesosNums(hundredmesos);
		ArrayList<Integer> list2 = findMesosNums(thousandmesos);
		ArrayList<Integer> list3 = findMesosNums(millionmesos);
		ArrayList<Integer> list4 = findMesosNums(billionmesos);
		ArrayList<Integer> combinedList = new ArrayList<>();
		combinedList.addAll(list4);
		combinedList.addAll(list3);
		combinedList.addAll(list2);
		combinedList.addAll(list1);
        
		int total = 0;
		for (Integer i : combinedList) {
            total = 10 * total + i;
        }
		return total;
	}
	public String formatMesos(int number) {
		return String.format("%,d", number);
	}
	public ArrayList<Integer> findMesosNums(Rectangle rect) throws IOException {
		BufferedImage image = robot.createScreenCapture(rect);
		ArrayList<Integer> numbersFound = new ArrayList<Integer>();
		for(int x = 0; x < 3; x++) {
			int numoffset = 0;
			for(int i=0;i<mesosNumImages.length;i++) {
				boolean match = true;
				imageLoop:
				for(int x2=0;x2<mesosNumImages[i].getWidth();x2++) {
					for(int y2=0;y2<mesosNumImages[i].getHeight();y2++) {
						Color pic1 = new Color(mesosNumImages[i].getRGB(x2, y2));
						if(pic1.getRed() != 255 && pic1.getBlue() != 255 && pic1.getGreen() != 255) {
							if(mesosNumImages[i].getRGB(x2, y2) != image.getRGB((x*7)+x2+numoffset, y2)) {
								match = false;
								break imageLoop;
							}
						}
					}
				}
				if(match) {
					numbersFound.add(i);
					break;
				}
			}
		}
		return numbersFound;
	}
	public ArrayList<Integer> findLevelNums(Rectangle rect) throws IOException {
		BufferedImage image = robot.createScreenCapture(rect);
		boolean imageMatched = true;
		boolean addNum = false;
		ArrayList<Integer> numbersFound = new ArrayList<Integer>();
		int currNumX = 0;
		int numToAdd = 0;
		File outputfile = new File("numFind.png");
	    ImageIO.write(image, "png", outputfile);
		for(int x=0;x<image.getWidth()-10;x++) {
			for(int y=0;y<image.getHeight()-12;y++) {
				for(int i=0;i<levelImages.length;i++) {
					imageMatched = true;
					for(int x2=0;x2<levelImages[i].getWidth();x2++) {
						for(int y2=0;y2<levelImages[i].getHeight();y2++) {
							Color pic1 = new Color(levelImages[i].getRGB(x2, y2));
							if(pic1.getRed() == 255 && pic1.getBlue() == 255 && pic1.getGreen() == 255) {
								if(levelImages[i].getRGB(x2, y2) != image.getRGB(x+x2, y+y2)) {
									imageMatched = false;
									break;
								}
							}
						}
						if(!imageMatched) {
							break;
						}
					}
					if(imageMatched) {
						numToAdd = i;
						addNum = true;
					}
				}
				if(addNum) {
					numbersFound.add(numToAdd);
					currNumX = x + 12;
					break;
				}
			}
			if(addNum) {
				break;
			}
		}
		addNum = false;
		for(int k=0;k<2;k++) {
			numToAdd = 0;
			for(int i=0;i<levelImages.length && currNumX + 11 < image.getWidth();i++) {
				imageMatched = true;
				for(int x2=0;x2<levelImages[i].getWidth();x2++) {
					for(int y2=0;y2<levelImages[i].getHeight();y2++) {
						Color pic1 = new Color(levelImages[i].getRGB(x2, y2));
						if(pic1.getRed() == 255 && pic1.getBlue() == 255 && pic1.getGreen() == 255) {
							if(levelImages[i].getRGB(x2, y2) != image.getRGB(currNumX+x2, y2)) {
								imageMatched = false;
								break;
							}
						}
					}
					if(!imageMatched) {
						break;
					}
				}
				if(imageMatched) {
					numToAdd = i;
					addNum = true;
				}
			}
			if(addNum) {
				numbersFound.add(numToAdd);
				addNum=false;
			}else {
				break;
			}
			currNumX = currNumX + 12;
		}
		return numbersFound;
	}
	public ArrayList<Integer> findNums(Rectangle rect) throws IOException {
		BufferedImage image = robot.createScreenCapture(rect);
		ArrayList<Integer> numbersFound = new ArrayList<Integer>();
		for(int k=0;k<5;k++) {
			for(int i=numImages.length-1;i>=0;i--) {
				boolean match = true;
				bp1:
				for(int x2=0;x2<numImages[i].getWidth();x2++) {
					for(int y2=0;y2<numImages[i].getHeight();y2++) {
						Color pic1 = new Color(numImages[i].getRGB(x2, y2));
						if(pic1.getRed() == 255 && pic1.getBlue() == 255 && pic1.getGreen() == 255) {
							if(numImages[i].getRGB(x2, y2) != image.getRGB(x2 + (k*6), y2)) {
								match = false;
								break bp1;
							}
						}
					}
				}
				if(match) {
					numbersFound.add(i);
					break;
				}
			}
		}
		return numbersFound;
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
		if(tempCoords.x < zone.getLeftBound()) {
			robot.keyPress(KeyEvent.VK_RIGHT);
			while(tempCoords.x < zone.getLeftBound()) {
				if(tempCoords.x + 11 < zone.getRightBound()){
					robot.delay(35);
					robot.keyPress(KeyEvent.VK_ALT);
					robot.delay(75);
					robot.keyRelease(KeyEvent.VK_ALT);
				} else {
					robot.delay(35);
				}
				tempCoords = getMinimapPosition(map);
			}
			robot.keyRelease(KeyEvent.VK_RIGHT);
		} else if(tempCoords.x > zone.getRightBound()) {
			robot.keyPress(KeyEvent.VK_LEFT);
			while(tempCoords.x > zone.getRightBound()) {
				if(tempCoords.x - 11 > zone.getLeftBound()){
					robot.delay(35);
					robot.keyPress(KeyEvent.VK_ALT);
					robot.delay(75);
					robot.keyRelease(KeyEvent.VK_ALT);
				} else {
					robot.delay(35);
				}
				tempCoords = getMinimapPosition(map);
			}
			robot.keyRelease(KeyEvent.VK_LEFT);
		}
	}
	public void moveToPlatform(Zone zone, MinimapData map) throws IOException{
		MaplePoint pos = getMinimapPosition(map);
		while(!zone.isInYZone(pos) || !zone.isInXZone(pos)) {
			moveToZoneX(zone, map);
			pos = getMinimapPosition(map);
			if(pos.y > zone.getBottomBound()) {
				teleportUp();
			} else if (pos.y < zone.getTopBound()){
				jumpDown();
			}
			pos = getMinimapPosition(map);
		}
	}
	public void usePortal(Zone portal, Zone checkZone, MinimapData map) throws IOException {
		Zone upZone = new Zone(new MaplePoint(portal.getLeftBound()-5, portal.getTopBound()), new MaplePoint(portal.getRightBound()+5, portal.getBottomBound()));
		moveToZoneX(upZone, map);
		MaplePoint tempCoords = getMinimapPosition(map);
		while(!checkZone.isInZone(tempCoords)) {
			if(tempCoords.x <= portal.getLeftBound()) {
				robot.keyPress(KeyEvent.VK_RIGHT);
				while(tempCoords.x <= portal.getLeftBound() && !checkZone.isInZone(tempCoords)) {
					robot.keyPress(KeyEvent.VK_UP);
					robot.delay(100);
					tempCoords = getMinimapPosition(map);
				}
				robot.keyRelease(KeyEvent.VK_RIGHT);
			} else if(tempCoords.x >= portal.getRightBound()) {
				robot.keyPress(KeyEvent.VK_LEFT);
				while(tempCoords.x >= portal.getRightBound()  && !checkZone.isInZone(tempCoords)) {
					robot.keyPress(KeyEvent.VK_UP);
					robot.delay(100);
					tempCoords = getMinimapPosition(map);
				}
				robot.keyRelease(KeyEvent.VK_LEFT);
			}
			robot.keyRelease(KeyEvent.VK_UP);
			tempCoords = getMinimapPosition(map);
			robot.delay(200);
		}
		robot.delay(300);
//		System.out.println("finished portal");
	}
	/**
	 * 
	 * @param rope 		The zone of the rope itself (left and right MUST be same)
	 * @param checkZone The zone where we want to end up after climbing the rope
	 * @param map		The map we check
	 * @throws IOException
	 */
	public void climbRope(Zone rope, Zone checkZone, MinimapData map) throws IOException {
		Zone jumpZone = new Zone(new MaplePoint(rope.getLeftBound()-5, rope.getTopBound()),new MaplePoint(rope.getLeftBound()+5, rope.getBottomBound()));
		MaplePoint pos = getMinimapPosition(map);
		while(!checkZone.isInYZone(pos)) {
			if(pos.y >= checkZone.getBottomBound()) {
				moveToZoneX(jumpZone, map);
				pos = getMinimapPosition(map);
				if(pos.x > rope.getLeftBound()) {
					robot.keyPress(KeyEvent.VK_LEFT);
					robot.keyPress(KeyEvent.VK_UP);
					robot.delay(100);
					robot.keyPress(KeyEvent.VK_SPACE);
					robot.keyRelease(KeyEvent.VK_LEFT);
					robot.keyRelease(KeyEvent.VK_SPACE);
				} else if(pos.x <= rope.getLeftBound()) {
					robot.keyPress(KeyEvent.VK_RIGHT);
					robot.keyPress(KeyEvent.VK_UP);
					robot.delay(100);
					robot.keyPress(KeyEvent.VK_SPACE);
					robot.keyRelease(KeyEvent.VK_RIGHT);
					robot.keyRelease(KeyEvent.VK_SPACE);
				}
			} else {
				moveToZoneX(rope, map);
				robot.keyPress(KeyEvent.VK_DOWN);
				robot.delay(200);
				robot.keyRelease(KeyEvent.VK_DOWN);
			}
			pos = getMinimapPosition(map);
			robot.delay(100);
		}
		robot.keyRelease(KeyEvent.VK_UP);
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
	public void feedPets() throws IOException {
		long currTime = System.currentTimeMillis();
		String oldScreen = new String(currScreen);
		for(int x=0;x<this.screens.length;x++) {
			if(this.screens[x].pet && this.screens[x].petTimer + 180 * 1000 < currTime) {
				if(!this.screens[x].name.equals(currScreen)) {
					swapMapleScreen(getScreen(this.screens[x].name));
				}
				robot.delay(100);
				this.screens[x].petTimer = currTime;
				keyPress(KeyEvent.VK_PAGE_UP);
				botOutput("Feeding pet on Screen " + x);
				robot.delay(100);
			}
		}
		if(!currScreen.equals(oldScreen)) {
			swapMapleScreen(getScreen(oldScreen));
		}
	}
	public void rebuff(double buffDuration) throws IOException {
		robot.delay(300);
		long temptime = System.currentTimeMillis();
		String oldScreen = new String(currScreen);
		for(int x=0;x<buffs.length;x++) {
			if(buffs[x].enabled && (temptime >= buffTimers[x] + ((buffs[x].buffLength*1000) * buffDuration))) {
				if(!currScreen.equals(buffs[x].screen)) {
					swapMapleScreen(getScreen(buffs[x].screen));
				}
				
				buffTimers[x] = temptime;
				buffCounts[x]++;
				botOutput("Reapplying " + buffs[x].buffName + ", Count:" + buffCounts[x]);
//				keyPress(KeyEvent.VK_PAGE_DOWN);
				keyPress(buffs[x].buffKey);
				robot.delay(buffs[x].delay);
				if(buffs[x].image != null) {
					int max = 0;
					MaplePoint foundBuff = findBuff(buffZone,buffs[x].image);
					while(foundBuff.x == -1 && max<3) {
						keyPress(buffs[x].buffKey);
						robot.delay(buffs[x].delay);
						botOutput("trying again: " + buffs[x].buffName);
						foundBuff = findBuff(buffZone,buffs[x].image);
						max++;
					}
				}
			}
		}
		if(!oldScreen.equals(currScreen)) {
			swapMapleScreen(getScreen(oldScreen));
		}
	}
	public MaplePoint findBuff(Rectangle rect, String fileName) throws IOException {
		BufferedImage imageRecog = ImageIO.read(new File(fileName));
		BufferedImage image = robot.createScreenCapture(rect);
		for(int x1=0;x1<image.getWidth()-imageRecog.getWidth();x1++) {
			for(int y1=0;y1<image.getHeight()-imageRecog.getHeight();y1++) {
				boolean matches = true;
				for(int x2=0;x2<imageRecog.getWidth();x2++) {
					for(int y2=0;y2<imageRecog.getHeight();y2++) {
						Color pic1 = new Color(imageRecog.getRGB(x2, y2));
						if(pic1.getRed() <= 50 && pic1.getBlue() <= 50 && pic1.getGreen() <= 50) {
							Color pic2 = new Color(image.getRGB(x1+x2, y1+y2));
							if(pic2.getRed() > 50 || pic2.getBlue() > 50 || pic2.getGreen() > 50) {
								matches = false;
								//botOutput("trying to check something black at image:" + x2 + ", "+y2+" and on screen at "+(x1+x2)+", "+(y1+y2));
							}
						}
					}
				}
				if(matches) {
//					File outputfile = new File("couldFindBuff.png");
//				    ImageIO.write(image, "png", outputfile);
//					System.out.println(x1 + ", " + y1);
					return new MaplePoint(x1,y1);
				}
			}
		}
//		File outputfile = new File("couldFindBuff.png");
//	    ImageIO.write(image, "png", outputfile);
		return new MaplePoint(-1,-1);
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
	public void attack(int numAttacks, int key, int delay, int mpCost) throws IOException {
		botOutput("Attacking " + numAttacks + " times with " + delay + "ms Delay.");
		int originalMp = getMp();
		for(int x=0;x<numAttacks;x++) {
			waitOnChat();
			robot.keyPress(key);
			robot.delay(300);
			int newMp = getMp();
			while(Math.abs(originalMp-newMp) < mpCost) {
				botOutput("Failed to attack... retrying origMP:" + originalMp + ", new MP:" +newMp);
				robot.keyPress(key);
				robot.delay(300);
				newMp = getMp();
			}
			robot.delay(delay + randomPosNeg(randomNum(1,10)));
		}
		robot.keyRelease(key);
	}
	public void attack(AttackData attack, int numAttacks) throws IOException {
		botOutput("Attacking " + numAttacks + " times with " + attack.delay + "ms Delay.");
		int originalMp = 0;
		if(attack.manaCost != 0) {
			originalMp = getMp();
		}
		for(int x=0;x<numAttacks;x++) {
			waitOnChat();
			robot.keyPress(attack.key);
			robot.delay(300);
			if(attack.manaCost != 0) {
				int newMp = getMp();
				while(Math.abs(originalMp-newMp) < attack.manaCost) {
					botOutput("Failed to attack... retrying origMP:" + originalMp + ", new MP:" +newMp);
					robot.keyPress(attack.key);
					robot.delay(300);
					newMp = getMp();
				}
			}
			robot.delay(attack.delay + randomPosNeg(randomNum(1,10)));
		}
		robot.keyRelease(attack.key);
	}
	public void telecastAttackMove(AttackData attack, Zone zone, MinimapData map) throws IOException{
		telecastAttackMove(attack,zone, map, true);
	}
	public void telecastAttackMove(AttackData attack, Zone zone, MinimapData map, boolean ignoreY) throws IOException{
		waitOnChat();
		int originalMp = 0;
		long startTime = 0;
		if(attack.manaCost != 0) {
			originalMp = getMp();
		}
		robot.keyPress(KeyEvent.VK_UP);
		if(attack.manaCost != 0) {
			originalMp = getMp();
			int newMp = getMp();
			while(Math.abs(originalMp-newMp) < attack.manaCost) {
				robot.keyPress(KeyEvent.VK_ALT);
				robot.keyRelease(KeyEvent.VK_ALT);
				robot.keyPress(attack.key);
				startTime = System.currentTimeMillis();
				robot.keyRelease(attack.key);
				robot.delay(300);
				newMp = getMp();
				robot.delay(50);
			}
		} else {
			robot.keyPress(KeyEvent.VK_ALT);
			robot.keyRelease(KeyEvent.VK_ALT);
			robot.keyPress(attack.key);
			startTime = System.currentTimeMillis();
			robot.keyRelease(attack.key);
		}
		robot.keyRelease(KeyEvent.VK_UP);
		MaplePoint tempCoords = getMinimapPosition(map);
		if(tempCoords.x < zone.getLeftBound()) {
			robot.keyPress(KeyEvent.VK_RIGHT);
			while(tempCoords.x < zone.getLeftBound()) {
				if(tempCoords.x + 10 < zone.getRightBound()){
					robot.delay(200);
					robot.keyPress(KeyEvent.VK_ALT);
					robot.keyRelease(KeyEvent.VK_ALT);
				} else {
					robot.delay(35);
				}
				tempCoords = getMinimapPosition(map);
			}
			robot.keyRelease(KeyEvent.VK_RIGHT);
			if(!ignoreY) {
				if(tempCoords.y > zone.getBottomBound()) {
					robot.keyPress(KeyEvent.VK_UP);
					while(tempCoords.y > zone.getBottomBound()) {
						robot.delay(200);
						robot.keyPress(KeyEvent.VK_ALT);
						robot.keyRelease(KeyEvent.VK_ALT);
						tempCoords = getMinimapPosition(map);
					}
					robot.keyRelease(KeyEvent.VK_UP);
				} else if(tempCoords.y < zone.getTopBound()) {
					robot.keyPress(KeyEvent.VK_DOWN);
					while(tempCoords.y < zone.getTopBound()) {
						robot.delay(200);
						robot.keyPress(KeyEvent.VK_ALT);
						robot.keyRelease(KeyEvent.VK_ALT);
						tempCoords = getMinimapPosition(map);
					}
					robot.keyRelease(KeyEvent.VK_DOWN);
				}
				moveToZoneX(zone,map);
				robot.delay(300);
			}
		} else if(tempCoords.x > zone.getRightBound()) {
			robot.keyPress(KeyEvent.VK_LEFT);
			while(tempCoords.x > zone.getRightBound()) {
				if(tempCoords.x - 10 > zone.getLeftBound()){
					robot.delay(200);
					robot.keyPress(KeyEvent.VK_ALT);
					robot.keyRelease(KeyEvent.VK_ALT);
				} else {
					robot.delay(35);
				}
				tempCoords = getMinimapPosition(map);
			}
			robot.keyRelease(KeyEvent.VK_LEFT);
			if(!ignoreY) {
				if(tempCoords.y > zone.getBottomBound()) {
					robot.keyPress(KeyEvent.VK_UP);
					while(tempCoords.y > zone.getBottomBound()) {
						robot.delay(200);
						robot.keyPress(KeyEvent.VK_ALT);
						robot.keyRelease(KeyEvent.VK_ALT);
						tempCoords = getMinimapPosition(map);
					}
					robot.keyRelease(KeyEvent.VK_UP);
				} else if(tempCoords.y < zone.getTopBound()) {
					robot.keyPress(KeyEvent.VK_DOWN);
					while(tempCoords.y < zone.getTopBound()) {
						robot.delay(200);
						robot.keyPress(KeyEvent.VK_ALT);
						robot.keyRelease(KeyEvent.VK_ALT);
						tempCoords = getMinimapPosition(map);
					}
					robot.keyRelease(KeyEvent.VK_DOWN);
				}
				moveToZoneX(zone,map);
				robot.delay(300);
			}
			
		}
		long endTime = System.currentTimeMillis();
		while(endTime - startTime < attack.delay) {
			robot.delay(50);
			endTime = System.currentTimeMillis();
		}
	}
	public void telecastAttackMoveSafe(AttackData attack, Zone zone, MinimapData map) throws IOException{
		waitOnChat();
		int originalMp = 0;
		long startTime = 0;
		if(attack.manaCost != 0) {
			originalMp = getMp();
		}
		MaplePoint tempCoords = getMinimapPosition(map);
		if(tempCoords.x < zone.getLeftBound()) {
			robot.keyPress(KeyEvent.VK_RIGHT);
			if(attack.manaCost != 0) {
				originalMp = getMp();
				int newMp = getMp();
				while(Math.abs(originalMp-newMp) < attack.manaCost) {
					robot.keyPress(KeyEvent.VK_ALT);
					robot.keyRelease(KeyEvent.VK_ALT);
					robot.keyPress(attack.key);
					startTime = System.currentTimeMillis();
					robot.keyRelease(attack.key);
					robot.delay(300);
					newMp = getMp();
					robot.delay(50);
				}
			} else {
				robot.keyPress(KeyEvent.VK_ALT);
				robot.keyRelease(KeyEvent.VK_ALT);
				robot.keyPress(attack.key);
				startTime = System.currentTimeMillis();
				robot.keyRelease(attack.key);
			}
			while(tempCoords.x < zone.getLeftBound()) {
				if(tempCoords.x + 10 < zone.getRightBound()){
					robot.delay(200);
					robot.keyPress(KeyEvent.VK_ALT);
					robot.keyRelease(KeyEvent.VK_ALT);
				} else {
					robot.delay(35);
				}
				tempCoords = getMinimapPosition(map);
			}
			robot.keyRelease(KeyEvent.VK_RIGHT);
		} else if(tempCoords.x > zone.getRightBound()) {
			robot.keyPress(KeyEvent.VK_LEFT);
			if(attack.manaCost != 0) {
				originalMp = getMp();
				int newMp = getMp();
				while(Math.abs(originalMp-newMp) < attack.manaCost) {
					robot.keyPress(KeyEvent.VK_ALT);
					robot.keyRelease(KeyEvent.VK_ALT);
					robot.keyPress(attack.key);
					startTime = System.currentTimeMillis();
					robot.keyRelease(attack.key);
					robot.delay(300);
					newMp = getMp();
					robot.delay(50);
				}
			} else {
				robot.keyPress(KeyEvent.VK_ALT);
				robot.keyRelease(KeyEvent.VK_ALT);
				robot.keyPress(attack.key);
				startTime = System.currentTimeMillis();
				robot.keyRelease(attack.key);
			}
			while(tempCoords.x > zone.getRightBound()) {
				if(tempCoords.x - 10 > zone.getLeftBound()){
					robot.delay(200);
					robot.keyPress(KeyEvent.VK_ALT);
					robot.keyRelease(KeyEvent.VK_ALT);
				} else {
					robot.delay(35);
				}
				tempCoords = getMinimapPosition(map);
			}
			robot.keyRelease(KeyEvent.VK_LEFT);
			
		} else {
			originalMp = getMp();
			int newMp = getMp();
			while(Math.abs(originalMp-newMp) < attack.manaCost) {
				robot.keyPress(attack.key);
				startTime = System.currentTimeMillis();
				robot.keyRelease(attack.key);
				robot.delay(300);
				newMp = getMp();
				robot.delay(50);
			}
		}
		long endTime = System.currentTimeMillis();
		while(endTime - startTime < attack.delay) {
			robot.delay(50);
			endTime = System.currentTimeMillis();
		}
	}
	public void checkEquipment() throws IOException {
		MaplePoint invLoc = findInventory();
		if(invLoc.x < 0) {
			robot.keyPress(KeyEvent.VK_I);
			robot.keyRelease(KeyEvent.VK_I);
			robot.delay(100);
			invLoc = findInventory();
			if(invLoc.x < 0) {
				botOutput("===========");
				botOutput("Couldnt find inventory! Is it in the right place?");
				botOutput("===========");
				return;
			}
		}
//		System.out.println(invLoc.toString());
		MaplePoint EquipButton = new MaplePoint(10 + invLoc.x,25 + invLoc.y);
		robot.mouseMove(EquipButton.x, EquipButton.y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		robot.delay(100);
		
		MaplePoint scrollbarLocation = new MaplePoint(155 + invLoc.x,220 + invLoc.y);
		robot.mouseMove(scrollbarLocation.x, scrollbarLocation.y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.delay(100);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
//		
		Rectangle equipfullsearch = new Rectangle(106 + invLoc.x ,209 + invLoc.y,34,34);
		MaplePoint equipExists = getCurrPosition(equipfullsearch, "inventory/emptyslot.png");
		if(equipExists.x > 0) {
			//inventory empty
//			botOutput("Equips not found, continuing");
//			botOutput("===========");
		} else {
			botOutput("===========");
			//inventory is full! initiate sell procedure
			int init  = getMesos();
			botOutput("Equips found, selling equips. starting mesos: " + formatMesos(init));
			sellEquips();
			int post = getMesos();
			botOutput("After selling we gained " + formatMesos(post-init) + " mesos. Ending mesos: " + formatMesos(post));
			botOutput("===========");
		}
	}
	public abstract void sellEquips() throws IOException;
	
	public void moveToZoneXAttack(Zone zone, MinimapData map, int attackKey) throws IOException {
		MaplePoint tempCoords = getMinimapPosition(map);
		if(tempCoords.x < zone.getLeftBound()) {
			robot.keyPress(KeyEvent.VK_RIGHT);
			while(tempCoords.x < zone.getLeftBound()) {
				if(tempCoords.x + 11 < zone.getRightBound()){
					robot.keyPress(attackKey);
					robot.keyPress(attackKey);
					robot.delay(100);
					robot.keyPress(KeyEvent.VK_ALT);
					robot.delay(75);
					robot.keyRelease(KeyEvent.VK_ALT);
					robot.delay(900 + randomPosNeg(randomNum(1,10)));
				} else {
					robot.delay(35);
				}
				tempCoords = getMinimapPosition(map);
			}
			robot.keyRelease(KeyEvent.VK_RIGHT);
		} else if(tempCoords.x > zone.getRightBound()) {
			robot.keyPress(KeyEvent.VK_LEFT);
			while(tempCoords.x > zone.getRightBound()) {
				if(tempCoords.x - 11 > zone.getLeftBound()){
					robot.keyPress(attackKey);
					robot.keyPress(attackKey);
					robot.delay(100);
					robot.keyPress(KeyEvent.VK_ALT);
					robot.delay(75);
					robot.keyRelease(KeyEvent.VK_ALT);
					robot.delay(900 + randomPosNeg(randomNum(1,10)));
				} else {
					robot.delay(35);
				}
				tempCoords = getMinimapPosition(map);
			}
			robot.keyRelease(KeyEvent.VK_LEFT);
		}
	}
	public Screen getScreen(String name) {
		for(int x=0;x<this.screens.length;x++) {
			if(this.screens[x].name.equals(name)){
				return this.screens[x];
			}
		}
		return null;
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
	public void click(MaplePoint point) {
		robot.mouseMove(point.x, point.y);
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
		robot.delay(100);
	}
	public void jumpDown() {
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyPress(KeyEvent.VK_SPACE);
		robot.delay(100);
		robot.keyRelease(KeyEvent.VK_DOWN);
		robot.keyRelease(KeyEvent.VK_SPACE);
		robot.delay(500);
	}
	public void botOutput(String output) {
		LocalTime myObj = LocalTime.now();
	    System.out.println("[" + myObj + "] " + output);
	}
	public void exitScript() {
		LocalTime now = LocalTime.now();
		int level = 0;
		int endingMesos = 0;
		try {
			level = getLevel();
			endingMesos = getMesos();
		} catch (IOException e) {
			e.printStackTrace();
		}
		botOutput("Starting level: " + startingLevel + ", Ending Level: " + level);
		botOutput("Starting mesos: " + startingMesos + ", Ending mesos: " + formatMesos(endingMesos));
		botOutput("Script ran for " + MINUTES.between(startTime, now) + " minutes");
		botOutput("Exiting script...");
		System.exit(0);
	}
	public void outputCoords(MaplePoint coords) {
		botOutput("X: " + coords.x + ", Y: " + coords.y);
	}
}
