package testrobot;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.LocalTime;

public class Leafre extends BaseBot{
	MinimapData skele2 = new MinimapData("skele2", new Rectangle(56,72,106,114), new Rectangle(47,45,65,15),"minimapNames/skele2Name.png");
	
	long dropTimer = 0;
	public Leafre(Robot robot, Screen[] screens) {
		super(robot, screens);
		minimapDatas.add(skele2);
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
		int lootPosition = 0;
		
		long currTime = System.currentTimeMillis();
		long startTime = System.currentTimeMillis();
//		rebuff(.75);
		int minutes = 0;
		while(startTime + (hours * 60 * 60 * 1000) > currTime) {
			checkPots();
//			if(getScreen("sweeper")!= null && sweeperTimer + 33*1000 < currTime) {
//				String temp = new String(currScreen);
//				swapMapleScreen(getScreen("sweeper"));
//				robot.delay(100);
//				sweeperTimer = currTime;
//				lootPosition = ulu2Sweeper(lootPosition, map);
//				botOutput("Sweeper Moved to position index: " + lootPosition);
//				swapMapleScreen(getScreen(temp));
//			}
			position = movement(position, map);
			botOutput("Moved to position index: " + position);
			robot.delay(200);
//			attack(map);
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
		case "skele2":
			return skele2Movement(position, map);
		}
		return 0;
	}
	
	public int skele2Movement(int position, MinimapData map) throws IOException {
//		Zone topRightRope = new Zone(38,52,38,72);
//		Zone checkZoneTopRight = new Zone(35,45,45,47);
		
		Zone topRightRope = new Zone(52,58,52,69);
		Zone checkZoneTopRight = new Zone(49,50,57,54);
		
		Zone botRightRope = new Zone(43,74,43,92);
		Zone checkZoneBotRight = new Zone(38,70,52,74);
		
		MaplePoint pos = getMinimapPosition(map);
		switch(position){
		case 0:
			Zone att1 = new Zone(21,45,27,48);
			while(!att1.isInZone(pos)) {
				if(!att1.isInYZone(pos.y)) {
					if(pos.y > att1.getBottomBound()) {
						if(pos.y> 75) {
							climbRope(botRightRope,checkZoneBotRight,map);
							teleportUp();
						} else {
//							climbRope(topRightRope,checkZoneTopRight,map);
							Zone jumpZone = new Zone(topRightRope.getLeftBound()-8, topRightRope.getTopBound(),topRightRope.getLeftBound()+8, topRightRope.getBottomBound());
							pos = getMinimapPosition(map);
							while(!checkZoneTopRight.isInYZone(pos) && pos.y < checkZoneBotRight.getBottomBound()) {
								if(pos.y >= checkZoneTopRight.getBottomBound()) {
									moveToZoneX(jumpZone, map);
									pos = getMinimapPosition(map);
									if(pos.x > topRightRope.getLeftBound()) {
										robot.keyPress(KeyEvent.VK_LEFT);
										robot.delay(150);
										robot.keyPress(KeyEvent.VK_SPACE);
										robot.keyPress(KeyEvent.VK_UP);
										robot.keyRelease(KeyEvent.VK_LEFT);
										robot.keyRelease(KeyEvent.VK_SPACE);
									} else if(pos.x < topRightRope.getLeftBound()) {
										robot.keyPress(KeyEvent.VK_RIGHT);
										robot.delay(150);
										robot.keyPress(KeyEvent.VK_SPACE);
										robot.keyPress(KeyEvent.VK_UP);
										robot.keyRelease(KeyEvent.VK_RIGHT);
										robot.keyRelease(KeyEvent.VK_SPACE);
									} else {
										robot.keyPress(KeyEvent.VK_SPACE);
										robot.delay(50);
										robot.keyRelease(KeyEvent.VK_SPACE);
										robot.keyPress(KeyEvent.VK_UP);
									}
								} else {
									moveToZoneX(topRightRope, map);
									robot.keyPress(KeyEvent.VK_DOWN);
									robot.delay(200);
									robot.keyRelease(KeyEvent.VK_DOWN);
								}
								pos = getMinimapPosition(map);
								robot.delay(100);
							}
							robot.delay(50);
							robot.keyRelease(KeyEvent.VK_UP);
							Zone leftZone = new Zone(48,52,49,57);
							moveToZoneX(leftZone,map);
							teleportUp();
						}
					}
				}else {
					moveToZoneX(att1, map);
				}
				pos = getMinimapPosition(map);
			}
			attackInZone(att1, map);
			return position + 1;
		case 1:
			Zone att2 = new Zone(42,45,44,52);
			while(!att2.isInXZone(pos)) {
				moveToZoneX(att2, map);
				pos = getMinimapPosition(map);
			}
			rebuff(.7);
			attackInZone(att2, map);
			return position + 1;
		case 2:
			Zone att3 = new Zone(35,68,38,74);
			while(!att3.isInZone(pos)) {
				if(!att3.isInYZone(pos.y)) {
					if(pos.y < att3.getTopBound()) {
						jumpDown();
					}else if(pos.y > att3.getBottomBound()) {
						climbRope(botRightRope,checkZoneBotRight,map);
						robot.delay(50);
						teleportUp();
					}
				} else {
					moveToZoneX(att3, map);
				}
				pos = getMinimapPosition(map);
			}
			attackInZone(att3, map);
			return position + 1;
		}
		
		return 0;
	}
	public void attackInZone(Zone attackZone, MinimapData map) throws IOException{
		moveToZoneX(attackZone,map);
		waitOnChat();
		attack(1, KeyEvent.VK_C, 2750, 3000);
		waitOnChat();
		attack(1,KeyEvent.VK_V, 615);
		moveToZoneX(attackZone,map);
		waitOnChat();
		attack(1, KeyEvent.VK_C, 2750, 3000);
	}
}
