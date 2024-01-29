package testrobot;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.LocalTime;

public class Zipangu extends BaseBot{
	MinimapData ninto = new MinimapData("ninto", new Rectangle(6,72,167,52), new Rectangle(47,45,40,15),"minimapNames/ninjacastleName.png");
	MinimapData redslime = new MinimapData("Red Slime", new Rectangle(6,72,158,56), new Rectangle(47,45,50,15),"minimapNames/redSlimeName.png");
	long dropTimer = 0;
	public Zipangu(Robot robot, Screen[] screens) {
		super(robot, screens);
		minimapDatas.add(ninto);
		minimapDatas.add(redslime);
		adjustMinimapData(mapleScreen);
	}

	@Override
	public void leech(int hours, MinimapData map) throws IOException {
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
			attack(map);
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
	public void attack(MinimapData map) throws IOException {
		switch(map.name) {
		case "ninto":
			attackheal();
			break;
		case "Red Slime":
			attack(1, KeyEvent.VK_C, 2750, 3000);
			break;
		}
	}
	public void attackheal() throws IOException {
		waitOnChat();
		attack(1, KeyEvent.VK_C, 2750, 3000);
		waitOnChat();
		attack(1,KeyEvent.VK_V, 615);
		waitOnChat();
		attack(1, KeyEvent.VK_C, 2750, 3000);
	}
	public int movement(int position, MinimapData map) throws IOException {
		switch(map.name) {
		case "ninto":
			return nintoMovement(position, map);
		case "Red Slime":
			return redSlimeMovement(position, map);
		}
		return 0;
	}
	public int nintoMovement(int position, MinimapData map) throws IOException {
		
		Zone leftCheck = new Zone(5,23,65,40);
		Zone attack1 = new Zone(22,10,25,44);
		Zone attack2 = new Zone(49,10,52,44);
		
		Zone rightCheck = new Zone(90,23,165,40);
		Zone attack3 = new Zone(100,10,105,44);
		Zone attack4 = new Zone(141,10,146,44);
		
		Zone teleZoneRight = new Zone(91,10,91,40);
		Zone teleZoneCheck = new Zone(10,1,18,18);
		Zone teleZoneLeft = new Zone(24,10,24,40);
		MaplePoint pos = getMinimapPosition(map);
		switch(position) {
		case 0:
		case 2:
			if(!leftCheck.isInZone(pos)) {
				while(!teleZoneCheck.isInYZone(pos.y)) {
					moveToZoneX(teleZoneRight, map);
					teleportUp();
					pos = getMinimapPosition(map);
				}
				moveToZoneX(teleZoneLeft, map);
				pos = getMinimapPosition(map);
				while(!leftCheck.isInZone(pos)) {
					moveToZoneX(teleZoneLeft, map);
					jumpDown();
					pos = getMinimapPosition(map);
				}
			}
			while(!attack1.isInXZone(pos)) {
				moveToZoneX(attack1, map);
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 1:
		case 3:
			while(!attack2.isInXZone(pos)) {
				moveToZoneX(attack2, map);
				pos = getMinimapPosition(map);
			}
//			rebuff(.6);
			return position + 1;
		case 4:
		case 6:
			if(!rightCheck.isInZone(pos)) {
				while(!teleZoneCheck.isInYZone(pos.y)) {
					moveToZoneX(teleZoneLeft, map);
					teleportUp();
					pos = getMinimapPosition(map);
				}
				moveToZoneX(teleZoneRight, map);
				pos = getMinimapPosition(map);
				while(!rightCheck.isInZone(pos)) {
					moveToZoneX(teleZoneRight, map);
					jumpDown();
					pos = getMinimapPosition(map);
				}
			}
			while(!attack3.isInXZone(pos)) {
				moveToZoneX(attack3, map);
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 5:
		case 7:
			while(!attack4.isInXZone(pos)) {
				moveToZoneX(attack4, map);
				pos = getMinimapPosition(map);
			}
			if( position == 7) {
				return 0;
			} else {
				return position + 1;
			}
		}
		return 0;
	}
	public int redSlimeMovement(int position, MinimapData map) throws IOException {
		switch(position){
		case 5:
		case 0:
			Zone att1 = new Zone(new MaplePoint(32,30), new MaplePoint(36,38));
			moveToPlatform(att1, map);
			robot.delay(200);
			rebuff(.75);
 			return position + 1;
		case 3:
		case 1:
			Zone att2 = new Zone(new MaplePoint(63,34), new MaplePoint(67,43));
			moveToPlatform(att2, map);
			if(position == 3 ) 
				return 0;
			return position + 1;
		case 2:
			Zone att3 = new Zone(new MaplePoint(108,34), new MaplePoint(110,43));
			moveToPlatform(att3, map);
			return position + 1;
		}
		return 0;
	}
	public int nintoMovement2(int position, MinimapData map) throws IOException {
		Zone attack1 = new Zone(24,10,37,44);
		Zone attack21 = new Zone(65,10,70,44);
		Zone attack3 = new Zone(105,10,118,44);
		Zone attack4 = new Zone(145,10,158,44);
		MaplePoint pos = getMinimapPosition(map);
		switch(position) {
		case 0:
			while(!attack1.isInXZone(pos)) {
				moveToZoneX(attack1, map);
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 1:
		case 5:
			while(!attack21.isInXZone(pos)) {
				moveToZoneX(attack21, map);
				pos = getMinimapPosition(map);
			}
			if(position == 5 ) {
				return 0;
			} else {
				return position + 1;
			}
		case 2:
		case 4:
			while(!attack3.isInXZone(pos)) {
				moveToZoneX(attack3, map);
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 3:
			while(!attack4.isInXZone(pos)) {
				moveToZoneX(attack4, map);
				pos = getMinimapPosition(map);
			}
			return position + 1;
		}
		return 0;
	}

	@Override
	public void sellEquips() throws IOException {
		// TODO Auto-generated method stub
		
	}
}
