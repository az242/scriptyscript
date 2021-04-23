package testrobot;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

public class GhostShip extends BaseBot{
	MinimapData gs2Dungeon = new MinimapData("Gs2 Dungeon", new Rectangle(34,72,121,53), new Rectangle(47,45,135,15),"minimapNames/gs2dungeon.png");
	MinimapData gs2 = new MinimapData("gs2", new Rectangle(6,72,121,53), new Rectangle(47,45,75,15),"minimapNames/gs2mapName.png");
	MinimapData gs1 = new MinimapData("gs1", new Rectangle(6,72,115,53), new Rectangle(47,45,75,15),"minimapNames/gs1mapName.png");
	long dropTimer = 0;
	public GhostShip(Robot robot, Screen[] screens) {
		super(robot, screens);
		minimapDatas.add(gs2Dungeon);
		minimapDatas.add(gs2);
		minimapDatas.add(gs1);
		adjustMinimapData(mapleScreen,minimapDatas);
	}
	public void leech(int hours, MinimapData map) throws IOException {
		swapScreens(getScreen("bishop"));
		MaplePoint cords = getMinimapPosition(map);
		System.out.println("<--------------->");
		System.out.println("Starting leech script");
		System.out.println("<--------------->");
		botOutput("Starting at position: " + cords.toString());
		int position = 0;
		long currTime = System.currentTimeMillis();
		long startTime = System.currentTimeMillis();
		while(startTime + (hours * 60 * 60 * 1000) > currTime) {
			System.out.println("<--------------->");
			checkPots();
			rebuff();
			position = GS2Movement(position, map);
			attack(1, KeyEvent.VK_C, 2750);
			feedPets();
		}
		exitScript();
	}
	public int GS2Movement(int position, MinimapData map) throws IOException {
		Zone leftSide = new Zone(new MaplePoint(40,2),new MaplePoint(52,45));
		Zone rightSide = new Zone(new MaplePoint(80,23),new MaplePoint(92,45));
		long currTime = System.currentTimeMillis();
		if(currTime > dropTimer + 180*1000 && position == 1) {
			dropTimer = currTime;
			swapPlatforms(map);
			moveToZoneX(leftSide, map);
		}
		MaplePoint currPos = getMinimapPosition(map);
		if(position == 0) {
			moveToZoneX(leftSide, map);
			MaplePoint newPos = getMinimapPosition(map);
			botOutput("Move from " + currPos.toString() + " to " + newPos.toString());
			HolySymbol();
			return 1;
		} else{
			moveToZoneX(rightSide, map);
			MaplePoint newPos = getMinimapPosition(map);
			botOutput("Move from " + currPos.toString() + " to " + newPos.toString());
			return 0;
		}
	}
	public void HolySymbol() {
		long temptime = System.currentTimeMillis();
		if(temptime >= buffTimers[0] + buffs[0].buffLength*1000) {
			botOutput("Reapplying " + buffs[0].buffName);
			buffTimers[0] = temptime;
			keyPress(buffs[0].buffKey);
			robot.delay(randomNum(1750,2500));
		}
	}
	public void swapPlatforms(MinimapData map) throws IOException {
		Zone dropdownZone = new Zone(new MaplePoint(90,25),new MaplePoint(120,32)); //dropdown
		Zone teleUpZone = new Zone(new MaplePoint(34,40),new MaplePoint(40,45)); //teleup
		
		Zone droppedZone = new Zone(new MaplePoint(90,40),new MaplePoint(120,50)); //dropdown
		Zone teledZone = new Zone(new MaplePoint(10,25),new MaplePoint(40,32)); //teleup
		MaplePoint startCoords = getMinimapPosition(map);
		if(dropdownZone.isInYZone(startCoords)) {
			MaplePoint currentCoords = getMinimapPosition(map);
			int retries = 0;
			while(!droppedZone.isInYZone(currentCoords) && retries < 5){
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
				botOutput("Move from " + startCoords.toString() + " to " + currentCoords.toString());
			}
		}else if(teleUpZone.isInYZone(startCoords)){
			MaplePoint currentCoords = getMinimapPosition(map);
			int retries = 0;
			while(!teledZone.isInYZone(currentCoords) && retries < 5){
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
				botOutput("Move from " + startCoords.toString() + " to " + currentCoords.toString());
			}
		}
	}
}
