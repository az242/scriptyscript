package testrobot;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
public class Bot extends BaseBot{
	//600 heal minimun 
	int[][] screens = {{100,200},{100,1000}};
	int currScreen = 0;
	Rectangle gs1AFK = new Rectangle(300,425,700,75);
	Rectangle gs2AFK = new Rectangle(300,425,150,75);
	MinimapData ulu2 = new MinimapData("ulu2Test",new Rectangle(6,72,145,81), new Rectangle(47,45,70,15),"ulu2Mapname.png");
	MinimapData gs2Dungeon = new MinimapData("Gs2 Dungeon", new Rectangle(34,72,121,53), new Rectangle(47,45,135,15),"gs2dungeon.png");
	MinimapData gs2 = new MinimapData("Gs2", new Rectangle(6,72,121,53), new Rectangle(47,45,75,15),"gs2mapName.png");
	Rectangle chat = new Rectangle(525,700, 35, 30);
	ArrayList<Rectangle> areas = new ArrayList<Rectangle>();
	ArrayList<MinimapData> minimapDatas = new ArrayList<MinimapData>();
	ScriptArea[] scripts = {new ScriptArea("GS1 no move",gs1AFK,495,575), new ScriptArea("GS2 no move",gs2AFK,20,105)};
	BuffData[] buffs = {
//			new BuffData("Holy Symbol", 120, KeyEvent.VK_U),
			new BuffData("Bless", 200, KeyEvent.VK_Y),
			new BuffData("Invincible", 300, KeyEvent.VK_T),
			new BuffData("Magic Gaurd", 600, KeyEvent.VK_R)};
	long[] buffTimers = new long[buffs.length];
	
	
	public Bot(Robot robot) {
		super(robot);
		areas.add(gs1AFK);
		areas.add(gs2AFK);
		areas.add(chat);
		minimapDatas.add(ulu2);
		minimapDatas.add(gs2Dungeon);
		minimapDatas.add(gs2);
		adjustRectangle(mapleScreen,areas);
		adjustMinimapData(mapleScreen,minimapDatas);
	}
	public void onlyRebuff() {
		swapScreens(0, this.screens);
		for(int x=0;x<100;x++) {
			rebuff2();
			System.out.println("test");
			robot.delay(randomNum(1000,15000));
		}
	}
	public void sweepFlow(MinimapData map) throws IOException {
		swapScreens(0, this.screens);
		int[] cords = getMinimapPosition(map);
		System.out.println("<--------------->");
		System.out.println("Starting Sweep Movement script");
		System.out.println("<--------------->");
		botOutput("Starting at position: " + cords[0] + ", " + cords[1]);
//		rebuff2();
		int positionIndex = 0;
		for(int x=0;x<1000;x++) {
			System.out.println("<--------------->");
			System.out.println("iteration " + x);
			System.out.println("<--------------->");
			positionIndex = GS2Movement(positionIndex, map);
			attack(randomNum(4,6), KeyEvent.VK_V);
			if(x%53 == 0 && x!=0) {
				feedPet(0, KeyEvent.VK_PAGE_UP);
//				feedPet(1, KeyEvent.VK_PAGE_DOWN);
			}
		}
	}
	public int GS2Movement(int positionIndex, MinimapData map) throws IOException {
//		Zone stairsZone = new Zone(new int[]{37,34},new int[]{41,46},true);
		Zone dropdownZone = new Zone(new int[]{90,25},new int[]{120,32}); //dropdown
		Zone teleUpZone = new Zone(new int[]{34,40},new int[]{41,45}); //teleup
		
		Zone droppedZone = new Zone(new int[]{90,40},new int[]{120,50}); //dropdown
		Zone teledZone = new Zone(new int[]{10,25},new int[]{41,32}); //teleup
		int[] startCoords = getMinimapPosition(map);
		if(dropdownZone.isInYZone(startCoords[1])) {
			if(dropdownZone.isInZone(startCoords[0],startCoords[1])){
				int[] currentCoords = getMinimapPosition(map);
				int retries = 0;
				while(!droppedZone.isInYZone(currentCoords[1]) && retries < 5){
					botOutput("Trying to drop down...");
					waitOnChat();
					moveToZoneX(dropdownZone,map);
					jumpDown();
					currentCoords = getMinimapPosition(map);
					retries++;
				}
				if(retries == 5) {
					botOutput("Failed to drop. Exiting...");
					exitScript();
				} else {
					botOutput("Move from " + startCoords[0] + ", " + startCoords[1] + " to " + currentCoords[0] + ", " + currentCoords[1]);
				}
				return ++positionIndex;
			}else{
				teleportRight();
				teleportRight();
			}
		}else if(teleUpZone.isInYZone(startCoords[1])){
			if((teleUpZone.isInZone(startCoords[0],startCoords[1]) || startCoords[0] < teleUpZone.getLeftBound()) && positionIndex == 1){
				int[] currentCoords = getMinimapPosition(map);
				int retries = 0;
				while(!teledZone.isInYZone(currentCoords[1]) && retries < 5){
					botOutput("Trying to tele up...");
					waitOnChat();
					moveToZoneX(teleUpZone,map);
					teleportUp();
					botOutput("HSing party...");
					keyPress(KeyEvent.VK_U);
					robot.delay(1750);
					teleportUp();
					currentCoords = getMinimapPosition(map);
					retries++;
				}
				if(retries == 5) {
					botOutput("Failed to teleport up. Exiting...");
					exitScript();
				} else {
					botOutput("Move from " + startCoords[0] + ", " + startCoords[1] + " to " + currentCoords[0] + ", " + currentCoords[1]);
				}
				return 0;
			} else {
				teleportLeft();
				teleportLeft();
			}
		} else{
			botOutput("Something went wrong during movement. ");
			exitScript();
		}
		int[] currentCoords = getMinimapPosition(map);
		botOutput("Move from " + startCoords[0] + ", " + startCoords[1] + " to " + currentCoords[0] + ", " + currentCoords[1]);
		return positionIndex;
	}
	//teleport moves 11 pixels on minimap
	public void mainAfkFlow(ScriptArea params, String searchCriteria) throws IOException {
		if(params == null) {
			botOutput("No script with that name found! Exiting...");
			exitScript();
		}
		swapScreens(0, this.screens);
		botOutput("Starting at position: " + getCurrPosition(params.searchArea,searchCriteria)[0] + " with bounds: " + params.leftBound + ", " + params.rightBound);
		rebuff2();
		for(int x=0;x<100;x++) {
			System.out.println("<--------------->");
			System.out.println("iteration " + x + " with bounds: " + params.leftBound + ", " + params.rightBound);
			System.out.println("<--------------->");
			getMinimapPosition(gs2);
			randomMove(params.leftBound,params.rightBound, searchCriteria, params.searchArea);
			attack(100 - randomNum(1,40), KeyEvent.VK_V);
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
	public void pickUp(int screen, int amount) {
		int temp = currScreen;
		botOutput("Picking up loot on screen " + screen + " " + amount + " times.");
		if(screen != currScreen) {
			swapScreens(screen, this.screens);
			robot.delay(100);
			for(int x=0;x<amount;x++) {
				robot.keyPress(KeyEvent.VK_Z);
				robot.delay(randomNum(75,200));
			}
			robot.keyRelease(KeyEvent.VK_Z);
			robot.delay(100);
			swapScreens(temp, this.screens);
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
			botOutput("Couldn't find character! image recog blocked! Retrying after 5 seconds...");
			robot.delay(5000);
			currPosition = getCurrPosition(area,imageRecog)[0];
		}
		if(currPosition == -1) {
			botOutput("Couldn't find character! image recog blocked! Exiting...");
			exitScript();
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
			botOutput("Failed to move... trying again in 1 seconds!");
			robot.delay(1000);
			randomMove(leftBound, rightBound, imageRecog, area);
		} else {
			botOutput("Moving from "+currPosition+" to " + newPosition);
			robot.delay(randomNum(500,1500));
		}
	}
	public void attack(int numAttacks, int key) throws IOException {
		int attackDelay = 615;
//		int attackDelay = 1000;
		botOutput("Attacking " + numAttacks + " times with " + attackDelay + "ms Delay.");
		for(int x=0;x<numAttacks;x++) {
			waitOnChat();
			rebuff2();
			robot.keyPress(key);
			robot.delay(attackDelay + randomPosNeg(randomNum(1,10)));
		}
		robot.keyRelease(key);
	}
	public void waitOnChat() throws IOException {
		boolean chatOpen = true;
		while(chatOpen) {
			if(getCurrPosition(chat,"chatOpen.png")[0] < 0) {
				chatOpen = false;
			} else {
				botOutput("chat is open! rechecking in 4 seconds...");
				robot.delay(4000);
			}
		}
	}
	public ScriptArea getScript(String name) {
		for(int x=0;x<this.scripts.length;x++) {
			if(this.scripts[x].name.equals(name)) {
				return this.scripts[x];
			}
		}
		return null;
	}
	
	public long holySymbol(long newBuffTime) throws InterruptedException {
		swapScreens(0, this.screens);
		long tempTime = System.currentTimeMillis();
		while(tempTime < newBuffTime) {
			TimeUnit.SECONDS.sleep(1);
			tempTime = System.currentTimeMillis();
		}
		keyPress(KeyEvent.VK_U);
		return tempTime + 120*1000;
	}
	public void rebuff2() {
		long temptime = System.currentTimeMillis();
		for(int x=0;x<buffs.length;x++) {
			if(temptime >= buffTimers[x] + buffs[x].buffLength*1000) {
				botOutput("Reapplying " + buffs[x].buffName);
				buffTimers[x] = temptime;
				keyPress(buffs[x].buffKey);
				robot.delay(randomNum(1750,2500));
			}
		}
	}
	public void feedPet(int screen, int key) {
		int temp = currScreen;
		botOutput("Feeding pet on Screen " + screen);
		if(screen != currScreen) {
			swapScreens(screen, this.screens);
			robot.delay(100);
			keyPress(key);
			robot.delay(100);
			swapScreens(temp, this.screens);
			robot.delay(100);
		} else {
			keyPress(key);
			robot.delay(100);
		}
	}
}
