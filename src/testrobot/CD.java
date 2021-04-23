package testrobot;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

public class CD extends BaseBot{
	Zone topFloor = new Zone(new MaplePoint(2,20),new MaplePoint(140,33));
	Zone midFloor = new Zone(new MaplePoint(2,37),new MaplePoint(140,54));
	Zone botFloor = new Zone(new MaplePoint(2,58),new MaplePoint(140,75));
	MinimapData cd = new MinimapData("cds", new Rectangle(33,72,141,80), new Rectangle(47,45,115,15),"minimapNames/cdName.png");
	long dropTimer = 0;
	public CD(Robot robot, Screen[] screens) {
		super(robot, screens);
		minimapDatas.add(cd);
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
			position = cdMovement(position, map);
			botOutput("Moved to position index: " + position);
			robot.delay(400);
			CDAttack();
			feedPets();
		}
		exitScript();
	}
	public int cdMovementAlone(int position, MinimapData map) throws IOException {
		Zone attack1 = new Zone(new MaplePoint(27,20), new MaplePoint(32,75));
		Zone attack2 = new Zone(new MaplePoint(65,20), new MaplePoint(69,75));
		Zone attack3 = new Zone(new MaplePoint(110,20), new MaplePoint(114,75));
		MaplePoint pos = getMinimapPosition(map);
		switch(position) {
		case 0:
			if(!topFloor.isInZone(pos)) {
				if(midFloor.isInZone(pos)){
					midToTop(map);
				}else {
					botToMid(map);
					midToTop(map);
				}
			}
			moveToZoneX(attack1,map);
			return position+1;
		case 1:
			moveToZoneX(attack2,map);
			rebuff(.5);
			return position+1;
		case 2:
			moveToZoneX(attack3,map);
			return position+1;
		case 3:
			while(!midFloor.isInZone(pos)) {
				jumpDown();
				pos = getMinimapPosition(map);
			}
			moveToZoneX(attack3,map);
			return position+1;
		case 4:
			moveToZoneX(attack2,map);
			return position+1;
		case 5:
			moveToZoneX(attack1,map);
			return position+1;
		case 6:
			while(!botFloor.isInZone(pos)) {
				jumpDown();
				pos = getMinimapPosition(map);
			}
			moveToZoneX(attack1,map);
			return position+1;
		case 7:
			moveToZoneX(attack2,map);
			return position+1;
		case 8:
			moveToZoneX(attack3,map);
			return 0;
		}
		return 0;
	}
	public int cdMovement(int position, MinimapData map) throws IOException{
		Zone attack1 = new Zone(new MaplePoint(27,20), new MaplePoint(32,75));
		Zone attack2 = new Zone(new MaplePoint(65,20), new MaplePoint(69,75));
		Zone attack3 = new Zone(new MaplePoint(110,20), new MaplePoint(114,75));
		MaplePoint pos = getMinimapPosition(map);
		switch(position) {
		case 0:
			if(!topFloor.isInZone(pos)) {
				if(midFloor.isInZone(pos)){
					midToTop(map);
				}else {
					botToMid(map);
					midToTop(map);
				}
			}
			moveToZoneX(attack1,map);
			return position+1;
		case 1:
			moveToZoneX(attack2,map);
			rebuff(.5);
			return position+1;
		case 2:
			moveToZoneX(attack3,map);
			return position+1;
		case 3:
			while(!midFloor.isInZone(pos)) {
				jumpDown();
				pos = getMinimapPosition(map);
			}
			moveToZoneX(attack3,map);
			return position+1;
		case 4:
			moveToZoneX(attack2,map);
			return position+1;
		case 5:
			moveToZoneX(attack1,map);
			return position+1;
		case 6:
			while(!botFloor.isInZone(pos)) {
				jumpDown();
				pos = getMinimapPosition(map);
			}
			moveToZoneX(attack1,map);
			return position+1;
		case 7:
			moveToZoneX(attack2,map);
			return position+1;
		case 8:
			moveToZoneX(attack3,map);
			return 0;
		}
		return 0;
	}
	public void botToMid(MinimapData map) throws IOException {
		Zone tele = new Zone(new MaplePoint(44,65), new MaplePoint(45,70));
		MaplePoint currPos = getMinimapPosition(map);
		while(!midFloor.isInZone(currPos)){
			moveToZoneX(tele, map);
			keyPress(KeyEvent.VK_UP);
			robot.delay(300);
			currPos = getMinimapPosition(map);
		}
	}
	public void midToTop(MinimapData map) throws IOException{
		Zone tele = new Zone(new MaplePoint(6,45), new MaplePoint(7,51));
		MaplePoint currPos = getMinimapPosition(map);
		while(!topFloor.isInZone(currPos)){
			moveToZoneX(tele, map);
			keyPress(KeyEvent.VK_UP);
			robot.delay(300);
			currPos = getMinimapPosition(map);
		}
	}
	public void CDAttack() throws IOException {
		waitOnChat();
		robot.keyPress(KeyEvent.VK_C);
		robot.delay(2750);
		robot.keyRelease(KeyEvent.VK_C);
		robot.keyPress(KeyEvent.VK_V);
		robot.delay(620);
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyPress(KeyEvent.VK_C);
		robot.delay(2750);
		robot.keyRelease(KeyEvent.VK_C);
	}
}
