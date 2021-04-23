package testrobot;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
public class Bot extends BaseBot{
	//600 heal minimun 
	Rectangle gs1AFK = new Rectangle(300,425,700,75);
	Rectangle gs2AFK = new Rectangle(300,425,150,75);
	MinimapData ulu2 = new MinimapData("ulu2Test",new Rectangle(6,72,145,81), new Rectangle(47,45,70,15),"ulu2Mapname.png");
	MinimapData gs2Dungeon = new MinimapData("Gs2 Dungeon", new Rectangle(34,72,121,53), new Rectangle(47,45,135,15),"gs2dungeon.png");
	MinimapData gs2 = new MinimapData("gs2", new Rectangle(6,72,121,53), new Rectangle(47,45,75,15),"gs2mapName.png");
	MinimapData gs1 = new MinimapData("gs1", new Rectangle(6,72,115,53), new Rectangle(47,45,75,15),"gs1mapName.png");
	ArrayList<Rectangle> areas = new ArrayList<Rectangle>();
	ArrayList<MinimapData> minimapDatas = new ArrayList<MinimapData>();
	ScriptArea[] scripts = {new ScriptArea("GS1 no move",gs1AFK,495,575), new ScriptArea("GS2 no move",gs2AFK,20,105)};
	
	public Bot(Robot robot, int[][] screens) {
		super(robot, screens);
		areas.add(gs1AFK);
		areas.add(gs2AFK);
		minimapDatas.add(ulu2);
		minimapDatas.add(gs2Dungeon);
		minimapDatas.add(gs2);
		minimapDatas.add(gs1);
		adjustRectangle(mapleScreen,areas);
		adjustMinimapData(mapleScreen,minimapDatas);
	}
	public void sweepFlow(MinimapData map, int hours) throws IOException {
		swapScreens(0, this.screens);
		int[] cords = getMinimapPosition(map);
		System.out.println("<--------------->");
		System.out.println("Starting Sweep Movement script");
		System.out.println("<--------------->");
		botOutput("Starting at position: " + cords[0] + ", " + cords[1]);
//		rebuff2();
		int positionIndex = 0;
		long startTime = System.currentTimeMillis();
		long currTime = System.currentTimeMillis();
		while(startTime + (hours * 60 * 60 * 1000) > currTime) {
			positionIndex = GS2Movement(positionIndex, map);
			attack(randomNum(2,4), KeyEvent.VK_V, 615);
			currTime = System.currentTimeMillis();
			feedPets();
		}
		exitScript();
	}
	public int GS1_1Movement(int positionIndex, MinimapData map) throws IOException {
//		Zone stairsZone = new Zone(new int[]{37,34},new int[]{41,46},true);
		Zone dropdownZone = new Zone(new int[]{35,20},new int[]{45,28}); //dropdown
		Zone teleUpZone = new Zone(new int[]{96,36},new int[]{103,42}); //teleup
		
		Zone droppedZone = new Zone(new int[]{35,36},new int[]{45,42}); //dropdown
		Zone teledZone = new Zone(new int[]{10,20},new int[]{41,28}); //teleup
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
				moveToZoneX(dropdownZone,map);
			}
		}else if(teleUpZone.isInYZone(startCoords[1])){
			if((teleUpZone.isInZone(startCoords[0],startCoords[1]) || startCoords[0] > teleUpZone.getRightBound()) && positionIndex == 1){
				int[] currentCoords = getMinimapPosition(map);
				int retries = 0;
				while(!teledZone.isInYZone(currentCoords[1]) && retries < 5){
					botOutput("Trying to tele up...");
					waitOnChat();
					moveToZoneX(teleUpZone,map);
					teleportUp();
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
				moveToZoneX(teleUpZone,map);
			}
		} else{
			botOutput("Something went wrong during movement. ");
			exitScript();
		}
		int[] currentCoords = getMinimapPosition(map);
		botOutput("Move from " + startCoords[0] + ", " + startCoords[1] + " to " + currentCoords[0] + ", " + currentCoords[1]);
		return positionIndex;
	}
	public int GS1Movement(int positionIndex, MinimapData map) throws IOException {
//		Zone stairsZone = new Zone(new int[]{37,34},new int[]{41,46},true);
		Zone dropdownZone = new Zone(new int[]{5,20},new int[]{35,28}); //dropdown
		Zone teleUpZone = new Zone(new int[]{96,36},new int[]{102,42}); //teleup
		
		Zone droppedZone = new Zone(new int[]{90,36},new int[]{120,42}); //dropdown
		Zone teledZone = new Zone(new int[]{98,20},new int[]{102,28}); //teleup
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
				teleportLeft();
				teleportLeft();
			}
		}else if(teleUpZone.isInYZone(startCoords[1])){
			if((teleUpZone.isInZone(startCoords[0],startCoords[1]) || startCoords[0] > teleUpZone.getRightBound()) && positionIndex == 1){
				int[] currentCoords = getMinimapPosition(map);
				int retries = 0;
				while(!teledZone.isInYZone(currentCoords[1]) && retries < 5){
					botOutput("Trying to tele up...");
					waitOnChat();
					moveToZoneX(teleUpZone,map);
					teleportUp();
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
				teleportRight();
				teleportRight();
			}
		} else{
			botOutput("Something went wrong during movement. ");
			exitScript();
		}
		int[] currentCoords = getMinimapPosition(map);
		botOutput("Move from " + startCoords[0] + ", " + startCoords[1] + " to " + currentCoords[0] + ", " + currentCoords[1]);
		return positionIndex;
	}
	public int GS2Movement(int positionIndex, MinimapData map) throws IOException {
//		Zone stairsZone = new Zone(new int[]{37,34},new int[]{41,46},true);
		Zone dropdownZone = new Zone(new int[]{90,25},new int[]{120,32}); //dropdown
		Zone teleUpZone = new Zone(new int[]{34,40},new int[]{40,45}); //teleup
		
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
					rebuff();
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
	public void mainAfkFlow(ScriptArea params, MinimapData map, String searchCriteria) throws IOException {
		if(params == null) {
			botOutput("No script with that name found! Exiting...");
			exitScript();
		}
		swapScreens(0, this.screens);
		botOutput("Starting at position: " + getCurrPosition(params.searchArea,searchCriteria)[0] + " with bounds: " + params.leftBound + ", " + params.rightBound);
		rebuff();
		for(int x=0;x<100;x++) {
			System.out.println("<--------------->");
			System.out.println("iteration " + x + " with bounds: " + params.leftBound + ", " + params.rightBound);
			System.out.println("<--------------->");
//			getMinimapPosition(gs2);
			rebuff();
			randomMove(params.leftBound,params.rightBound, searchCriteria, params.searchArea);
			int[] currentCoords = getMinimapPosition(map);
			attack(100 - randomNum(1,40), KeyEvent.VK_V, 615);
			if(x%9 == 0 && x!=0) {
				feedPets();
//				feedPet(1, KeyEvent.VK_PAGE_DOWN);
			}
			if(x%6 == 0 && x!=0) {
				pickUp(0,randomNum(10,20));
//				robot.delay(500);
//				pickUp(1,randomNum(10,20));
			}
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
}
