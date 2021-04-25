package testrobot;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.LocalTime;

public class Singapore extends BaseBot{
	MinimapData gs2Dungeon = new MinimapData("gs2dungeon", new Rectangle(34,72,121,53), new Rectangle(47,45,135,15),"minimapNames/gs2dungeon.png");
	MinimapData gs2 = new MinimapData("gs2", new Rectangle(6,72,121,53), new Rectangle(47,45,75,15),"minimapNames/gs2mapName.png");
	MinimapData gs1 = new MinimapData("gs1", new Rectangle(6,72,115,53), new Rectangle(47,45,75,15),"minimapNames/gs1mapName.png");
	
	MinimapData ulu2 = new MinimapData("ulu2", new Rectangle(6,72,132,81), new Rectangle(47,45,70,15),"minimapNames/ulu2Mapname.png");
	
	Zone topFloor = new Zone(new MaplePoint(2,20),new MaplePoint(140,33));
	Zone midFloor = new Zone(new MaplePoint(2,37),new MaplePoint(140,54));
	Zone botFloor = new Zone(new MaplePoint(2,58),new MaplePoint(140,75));
	MinimapData cd = new MinimapData("cds", new Rectangle(33,72,141,80), new Rectangle(47,45,115,15),"minimapNames/cdName.png");
	
	long dropTimer = 0;
	public Singapore(Robot robot, Screen[] screens) {
		super(robot, screens);
		minimapDatas.add(gs2Dungeon);
		minimapDatas.add(gs2);
		minimapDatas.add(gs1);
		minimapDatas.add(ulu2);
		adjustMinimapData(mapleScreen);
	}

	@Override
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
		rebuff(.75);
		int minutes = 0;
		while(startTime + (hours * 60 * 60 * 1000) > currTime) {
			checkPots();
			position = movement(position, map);
			botOutput("Moved to position index: " + position);
			robot.delay(400);
			attackheal();
//			attack(1, KeyEvent.VK_C, 2750);
			feedPets();
			currTime = System.currentTimeMillis();
			LocalTime now = LocalTime.now();
			if(MINUTES.between(this.startTime, now) > minutes ) {
				minutes = (int) MINUTES.between(this.startTime, now);
				botOutput("Script ran for " + minutes + " minutes");
			}
		}
		exitScript();
	}
	public int movement(int position, MinimapData map) throws IOException {
		switch(map.name) {
		case "ulu2":
			return ulu2(position, map);
		case "gs2":
			return GS2Movement(position, map);
		}
		return 0;
	}
	public int ulu2(int position, MinimapData map) throws IOException {
		Zone attack1 = new Zone(new MaplePoint(33,27), new MaplePoint(40,66));
		Zone attack2 = new Zone(new MaplePoint(67,27), new MaplePoint(75,66));
		Zone attack3 = new Zone(new MaplePoint(97,27), new MaplePoint(112,66));
		
		Zone rope = new Zone(new MaplePoint(93,27), new MaplePoint(93,66));
		Zone checkZone = new Zone(new MaplePoint(10,35), new MaplePoint(130,41));
		
		MaplePoint pos = getMinimapPosition(map);
		switch(position) {
		case 0:
			while(!attack1.isInXZone(pos)) {
				moveToZoneX(attack1, map);
				pos = getMinimapPosition(map);
			}
			rebuff(.6);
			while(pos.y < 58) {
				jumpDown();
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 1:
			while(!attack2.isInXZone(pos)) {
				moveToZoneX(attack2, map);
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 2:
			while(!attack3.isInXZone(pos)) {
				moveToZoneX(attack3, map);
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 3:
//			climbRope(rope,checkZone, map);
			while(!attack2.isInXZone(pos)) {
				moveToZoneX(attack2, map);
				pos = getMinimapPosition(map);
			}
			return 0;
		case 4:
			while(!attack2.isInXZone(pos)) {
				moveToZoneX(attack2, map);
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 5:
			while(!attack1.isInXZone(pos)) {
				moveToZoneX(attack1, map);
				pos = getMinimapPosition(map);
			}
			return 0;
			
		}
		return 0;
	}
	
	public void climbRope(Zone rope, Zone checkZone, MinimapData map) throws IOException {
		Zone jumpZone = new Zone(new MaplePoint(rope.getLeftBound()-5, 55),new MaplePoint(rope.getLeftBound()+5, 65));
		MaplePoint pos = getMinimapPosition(map);
		while(!checkZone.isInYZone(pos)) {
			moveToZoneX(jumpZone, map);
			pos = getMinimapPosition(map);
			if(pos.x > rope.getLeftBound()) {
				robot.keyPress(KeyEvent.VK_LEFT);
				robot.delay(100);
				robot.keyPress(KeyEvent.VK_SPACE);
				robot.keyPress(KeyEvent.VK_UP);
				robot.keyRelease(KeyEvent.VK_LEFT);
				robot.keyRelease(KeyEvent.VK_SPACE);
			} else if(pos.x < rope.getLeftBound()) {
				robot.keyPress(KeyEvent.VK_RIGHT);
				robot.delay(100);
				robot.keyPress(KeyEvent.VK_SPACE);
				robot.keyPress(KeyEvent.VK_UP);
				robot.keyRelease(KeyEvent.VK_RIGHT);
				robot.keyRelease(KeyEvent.VK_SPACE);
			} else {
				robot.keyPress(KeyEvent.VK_UP);
			}
			pos = getMinimapPosition(map);
			robot.delay(100);
		}
		robot.keyRelease(KeyEvent.VK_UP);
	}
	public void attackheal() throws IOException {
		waitOnChat();
		robot.keyPress(KeyEvent.VK_C);
		robot.delay(2750);
		robot.keyRelease(KeyEvent.VK_C);
		waitOnChat();
		robot.keyPress(KeyEvent.VK_V);
		robot.delay(620);
		robot.keyRelease(KeyEvent.VK_V);
		waitOnChat();
		robot.keyPress(KeyEvent.VK_C);
		robot.delay(2750);
		robot.keyRelease(KeyEvent.VK_C);
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
			rebuff(.8);
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
			while(!droppedZone.isInYZone(currentCoords) && retries < 10){
				botOutput("Trying to drop down...");
				waitOnChat();
				moveToZoneX(dropdownZone,map);
				jumpDown();
				currentCoords = getMinimapPosition(map);
				retries++;
			}
			if(retries == 10) {
				botOutput("Failed to drop. Exiting...");
				exitScript();
			} else {
				botOutput("Move from " + startCoords.toString() + " to " + currentCoords.toString());
			}
		}else if(teleUpZone.isInYZone(startCoords)){
			MaplePoint currentCoords = getMinimapPosition(map);
			int retries = 0;
			while(!teledZone.isInYZone(currentCoords) && retries < 15){
				botOutput("Trying to tele up...");
				waitOnChat();
				moveToZoneX(teleUpZone,map);
				teleportUp();
				teleportUp();
				currentCoords = getMinimapPosition(map);
				retries++;
			}
			if(retries == 15) {
				botOutput("Failed to teleport up. Exiting...");
				exitScript();
			} else {
				botOutput("Move from " + startCoords.toString() + " to " + currentCoords.toString());
			}
		}
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
}
