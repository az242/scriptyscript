package testrobot;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class Malaysia extends BaseBot{
	MinimapData mb1 = new MinimapData("mb1", new Rectangle(6,72,149,94), new Rectangle(48,46,115,15),"minimapNames/mb1Name.png");
	MinimapData mb2 = new MinimapData("mb2", new Rectangle(8,72,127,71), new Rectangle(48,46,115,15),"minimapNames/mb1Name.png");
	MinimapData ftp1 = new MinimapData("ftp1", new Rectangle(19,72,145,111), new Rectangle(48,46,115,15),"minimapNames/ftp1Name.png");
	long dropTimer = 0;
	public Malaysia(Robot robot, Screen[] screens, String server) {
		super(robot, screens,server);
		minimapDatas.add(mb1);
		minimapDatas.add(mb2);
		minimapDatas.add(ftp1);
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
//		rebuff(.9);
		while(startTime + (hours * 60 * 60 * 1000) > currTime) {
			System.out.println("<--------------->");
			checkPots();
			botOutput("Current position index: " + position);
			position = movement(position, map);
//			botOutput("Moved to position index: " + position);
			feedPets();
//			rebuff(.9);
		}
		exitScript();
		
	}
	public int movement(int position, MinimapData map) throws IOException {
		switch(map.name) {
		case "mb1":
			return MB1(position, map);
		case "mb2":
			return MB2(position, map);
		case "ftp1":
			return ftp1(position,map);
		}
		return 0;
	}
	public int MB1(int position, MinimapData map) throws IOException{
		MaplePoint coords = getMinimapPosition(map);
		Zone botTele = new Zone(new MaplePoint(120,70), new MaplePoint(121,76));
		Zone botCheckZone = new Zone(new MaplePoint(115,70), new MaplePoint(125,76));
		Zone topTele = new Zone(new MaplePoint(104,27), new MaplePoint(105,33));
		Zone topCheckZone = new Zone(new MaplePoint(100,27), new MaplePoint(110,33));
		
		Zone z1 = new Zone(new MaplePoint(55,70),new MaplePoint(65,76));
		Zone z2 = new Zone(new MaplePoint(110,70),new MaplePoint(118,76));
		Zone z3 = new Zone(new MaplePoint(100,27),new MaplePoint(113,35));
		switch(position) {
		case 0:
			rebuff(.8);
			telecastAttackMoveSafe(attacks.get("genesis"),z2, map);
//			moveToZoneX(botTeleRight, map);
			robot.delay(200);
			
			return position + 1;
		case 1:
			telecastAttackMoveSafe(attacks.get("genesis"),z2, map);
			robot.delay(200);
			usePortal(botTele,topCheckZone, map);
			robot.delay(200);
			return position + 1;
		case 2:
			telecastAttackMoveSafe(attacks.get("genesis"),z3, map);
			robot.delay(200);
			usePortal(topTele,botCheckZone, map);
			robot.delay(200);
			telecastAttackMoveSafe(attacks.get("genesis"),z1, map);
			robot.delay(200);
			return 0;
		}
		return 3;
	}
	public int MB2(int position, MinimapData map) throws IOException{
		MaplePoint coords = getMinimapPosition(map);
		Zone z1 = new Zone(new MaplePoint(37,45),new MaplePoint(40,55));
		Zone z2 = new Zone(new MaplePoint(89,45),new MaplePoint(96,55));
		switch(position) {
		case 0:
			
			telecastAttackMoveSafe(attacks.get("genesis"),z1, map);
//			moveToZoneX(botTeleRight, map);
			robot.delay(200);
			
			return position + 1;
		case 1:
			rebuff(.8);
			telecastAttackMoveSafe(attacks.get("genesis"),z2, map);
			robot.delay(200);
			return 0;
		}
		return 1;
	}
	public int ftp1(int position, MinimapData map) throws IOException{
		MaplePoint coords = getMinimapPosition(map);
		Zone z1 = new Zone(new MaplePoint(65,41),new MaplePoint(76,51));
		
		Zone rope = new Zone(new MaplePoint(79,61),new MaplePoint(79,73));
		Zone ropeCheck = new Zone(new MaplePoint(77,54),new MaplePoint(83,59));
		Zone travel1 = new Zone(new MaplePoint(84,55),new MaplePoint(87,62));
		Zone checkTravel1 = new Zone(new MaplePoint(84,44),new MaplePoint(89,53));
		Zone z2 = new Zone(new MaplePoint(64,69),new MaplePoint(76,77));
		
		Zone bottomRope = new Zone(new MaplePoint(55,77),new MaplePoint(55,91));
		Zone bottomRopeCheck = new Zone(new MaplePoint(53,69),new MaplePoint(58,76));
		switch(position) {
		case 0:
			rebuff(.8);
			attack(attacks.get("genesis"),1);
//			moveToZoneX(botTeleRight, map);
//			robot.delay(200);
			moveToPlatform(z2, map);
			return position + 1;
		case 1:
			attack(attacks.get("genesis"),1);
			while(!checkTravel1.isInZone(coords)) {
				coords = getMinimapPosition(map);
				if(coords.y > 83) {
					climbRope(bottomRope,bottomRopeCheck,map);
				} else if (coords.y > 65 ) {
//					climbRope(rope,ropeCheck,map);
					Zone jumpZone = new Zone(new MaplePoint(rope.getLeftBound()-5, rope.getTopBound()),new MaplePoint(rope.getLeftBound()+5, rope.getBottomBound()));
					MaplePoint pos = getMinimapPosition(map);
					while(!ropeCheck.isInYZone(pos) && pos.y < 83) {
						if(pos.y >= ropeCheck.getBottomBound()) {
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
						}
						pos = getMinimapPosition(map);
						robot.delay(100);
					}
					robot.keyRelease(KeyEvent.VK_UP);
				} else {
					moveToZoneX(travel1,map);
					teleportUp();
				}
				
				coords = getMinimapPosition(map);
			}
			moveToZoneX(z1,map);
			return 0;
		}
		return 1;
	}
	@Override
	public void sellEquips() throws IOException {
		// TODO Auto-generated method stub
		
	}
}
