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
	public Zone(int upper, int left, int lower, int right) {
		this.upperLeftBound = new MaplePoint(upper,left);
		this.lowerRightBound = new MaplePoint(lower,right);
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
			new BuffData("Hermit Pot", 3000, KeyEvent.VK_PAGE_DOWN,100, "hermit"),
			new BuffData("Hermit", 120, KeyEvent.VK_U,100, "hermit"),
			
			new BuffData("Hyperbody", 155, KeyEvent.VK_R, 100, "spearman"),
			new BuffData("Spearman Pot", 1200, KeyEvent.VK_PAGE_DOWN, 100, "spearman"),
			new BuffData("Holy Symbol", 120, KeyEvent.VK_U,2100,"bishop"),
			new BuffData("Bless", 200, KeyEvent.VK_Y,700,"bishop"),
			new BuffData("Invincible", 300, KeyEvent.VK_T,700,"bishop"),
			new BuffData("Magic Gaurd", 530, KeyEvent.VK_R,700,"bishop"),
			new BuffData("Maple Warrior", 600, KeyEvent.VK_D,1600,"bishop"),
			new BuffData("Magic", 550, KeyEvent.VK_DELETE,500,"bishop")
	};
	int startingLevel;
	long[] buffTimers = new long[buffs.length];
	BufferedImage[] numImages;
	BufferedImage[] levelImages;
	ArrayList<MinimapData> minimapDatas = new ArrayList<MinimapData>();
	public BaseBot(Robot robot, Screen[] screens) {
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
		try {
			initScreens();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
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
	public void initScreens() throws IOException {
		MaplePoint upperLeft = getCurrPosition(new Rectangle(100,1050,500,30), "mapleTaskIcon.png");
		upperLeft.y = upperLeft.y + 1050;
		upperLeft.x = upperLeft.x + 100;
//		outputCoords(upperLeft); 
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
						if(foundName.x > 0) {
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
			}else {
				//couldnt find expect amount of screens, exit.
				botOutput("Couldn't find expect number of screens. Exiting...");
				exitScript();
			}
		}
	}
	public void swapMapleScreen(Screen screen) throws IOException {
		MaplePoint upperLeft = getCurrPosition(new Rectangle(100,1050,500,30), "mapleTaskIcon.png");
		upperLeft.y = upperLeft.y + 1050;
		upperLeft.x = upperLeft.x + 100;
		int width = this.screens.length*165;
		//85 735 60 24
		Rectangle clickZone = new Rectangle(upperLeft.x-(width/2),upperLeft.y-165,width,165);
		click(upperLeft);
		robot.delay(400);
		clickZone = new Rectangle(upperLeft.x-(width/2) + (165*screen.index) ,upperLeft.y-165,width - (165*screen.index),165);
		MaplePoint foundIcon = getCurrPosition(clickZone, "mapleTaskIconSmall.png");
		//find icon
		if(foundIcon.x > 0) {
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
	public int getHp() throws IOException {
		int hp = 0;
		Rectangle hpRect = new Rectangle(241,739,35,7);
		hpRect = adjustRectangle(mapleScreen, hpRect);
		ArrayList<Integer> numbersFound = findNums(hpRect);
		for(int x=0;x<numbersFound.size();x++) {
			hp = hp + numbersFound.get(x)*10^(numbersFound.size()-x-1);
		}
		return hp;
	}
	public int getMp() throws IOException {
		int mp = 0;
		Rectangle MPRect = new Rectangle(353,739,35,7);
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
		boolean imageMatched = true;
		boolean addNum = false;
		ArrayList<Integer> numbersFound = new ArrayList<Integer>();
		int currNumX = 0;
		int numToAdd = 0;
		for(int k=0;k<5;k++) {
			numToAdd = 0;
			for(int i=0;i<numImages.length;i++) {
				imageMatched = true;
				for(int x2=0;x2<numImages[i].getWidth();x2++) {
					for(int y2=0;y2<numImages[i].getHeight();y2++) {
						Color pic1 = new Color(numImages[i].getRGB(x2, y2));
						if(pic1.getRed() == 255 && pic1.getBlue() == 255 && pic1.getGreen() == 255) {
							if(numImages[i].getRGB(x2, y2) != image.getRGB(currNumX+x2, y2)) {
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
			currNumX = currNumX + 6;
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
					robot.delay(50);
					tempCoords = getMinimapPosition(map);
				}
				robot.keyRelease(KeyEvent.VK_RIGHT);
			} else if(tempCoords.x >= portal.getRightBound()) {
				robot.keyPress(KeyEvent.VK_LEFT);
				while(tempCoords.x >= portal.getRightBound()  && !checkZone.isInZone(tempCoords)) {
					robot.keyPress(KeyEvent.VK_UP);
					robot.delay(50);
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
				} else if(pos.x < rope.getLeftBound()) {
					robot.keyPress(KeyEvent.VK_RIGHT);
					robot.keyPress(KeyEvent.VK_UP);
					robot.delay(100);
					robot.keyPress(KeyEvent.VK_SPACE);
					robot.keyRelease(KeyEvent.VK_RIGHT);
					robot.keyRelease(KeyEvent.VK_SPACE);
				} else {
					robot.keyPress(KeyEvent.VK_SPACE);
					robot.delay(50);
					robot.keyRelease(KeyEvent.VK_SPACE);
					robot.keyPress(KeyEvent.VK_UP);
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
	public void pickUp(String screen, int amount) throws IOException {
		String temp = new String(currScreen);
		botOutput("Picking up loot on screen " + screen + " " + amount + " times.");
		if(screen != currScreen) {
			swapMapleScreen(getScreen(screen));
			robot.delay(100);
			for(int x=0;x<amount;x++) {
				robot.keyPress(KeyEvent.VK_Z);
				robot.delay(randomNum(75,200));
			}
			robot.keyRelease(KeyEvent.VK_Z);
			robot.delay(100);
			swapMapleScreen(getScreen(temp));
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
		long temptime = System.currentTimeMillis();
		String oldScreen = new String(currScreen);
		for(int x=0;x<buffs.length;x++) {
			if(buffs[x].enabled && (temptime >= buffTimers[x] + ((buffs[x].buffLength*1000) * buffDuration))) {
				if(!currScreen.equals(buffs[x].screen)) {
					swapMapleScreen(getScreen(buffs[x].screen));
				}
				botOutput("Reapplying " + buffs[x].buffName);
				buffTimers[x] = temptime;
//				keyPress(KeyEvent.VK_PAGE_DOWN);
				keyPress(buffs[x].buffKey);
				robot.delay(buffs[x].delay);
			}
		}
		if(!oldScreen.equals(currScreen)) {
			swapMapleScreen(getScreen(oldScreen));
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
	public void attack(int numAttacks, int key, int delay, int mpCost) throws IOException {
		botOutput("Attacking " + numAttacks + " times with " + delay + "ms Delay.");
		int originalMp = getMp();
		for(int x=0;x<numAttacks;x++) {
			waitOnChat();
			robot.keyPress(key);
			robot.delay(200);
			int newMp = getMp();
			while(Math.abs(originalMp-newMp) < mpCost) {
				robot.keyPress(key);
				robot.delay(200);
				botOutput("Failed to attack... retrying");
				newMp = getMp();
			}
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
		robot.delay(1000);
	}
	public void botOutput(String output) {
		LocalTime myObj = LocalTime.now();
	    System.out.println("[" + myObj + "] " + output);
	}
	public void exitScript() {
//		robot.delay(4000);
//		keyPress(KeyEvent.VK_K);
//		robot.delay(400);
//		keyPress(KeyEvent.VK_UP);
//		robot.delay(400);
//		keyPress(KeyEvent.VK_UP);
//		robot.delay(400);
//		keyPress(KeyEvent.VK_UP);
		LocalTime now = LocalTime.now();
		int level = 0;
		try {
			level = getLevel();
		} catch (IOException e) {
			e.printStackTrace();
		}
		botOutput("Starting level: " + startingLevel + ", Ending Level: " + level);
		botOutput("Script ran for " + MINUTES.between(startTime, now) + " minutes");
		botOutput("Exiting script...");
		System.exit(0);
	}
	public void outputCoords(MaplePoint coords) {
		botOutput("X: " + coords.x + ", Y: " + coords.y);
	}
}
