package testrobot;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.LocalTime;

public class Singapore extends BaseBot{
	MinimapData ulu2 = new MinimapData("ulu2", new Rectangle(6,72,132,81), new Rectangle(47,45,70,15),"minimapNames/ulu2Mapname.png");
	public Singapore(Robot robot, Screen[] screens) {
		super(robot, screens);
		minimapDatas.add(ulu2);
		adjustMinimapData(mapleScreen,minimapDatas);
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
}
