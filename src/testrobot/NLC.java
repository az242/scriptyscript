package testrobot;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

public class NLC extends BaseBot{
	MinimapData wolfSpider = new MinimapData("Wolf Spiders", new Rectangle(6,72,201,136), new Rectangle(47,45,115,15),"minimapNames/wsName.png");
	long dropTimer = 0;
	public NLC(Robot robot, Screen[] screens) {
		super(robot, screens);
		minimapDatas.add(wolfSpider);
		adjustMinimapData(mapleScreen);
	}
	public void leech(int hours, MinimapData map) throws IOException {
		MaplePoint cords = getMinimapPosition(map);
		System.out.println("<--------------->");
		System.out.println("Starting leech script");
		System.out.println("<--------------->");
		botOutput("Starting at position: " + cords.toString());
		int position = 0;
		long currTime = System.currentTimeMillis();
		long startTime = System.currentTimeMillis();
		rebuff(.9);
		while(startTime + (hours * 60 * 60 * 1000) > currTime) {
			System.out.println("<--------------->");
			checkPots();
			position = wolfSpiderMovement(position, map);
			botOutput("Moved to position index: " + position);
			feedPets();
//			rebuff(.9);
		}
		exitScript();
	}
	public void attackInZone(Zone attackZone, MinimapData map) throws IOException{
		moveToZoneX(attackZone,map);
		waitOnChat();
		attack(1, KeyEvent.VK_C, 2500, 3000);
		waitOnChat();
		attack(1,KeyEvent.VK_V, 615);
//		moveToZoneX(attackZone,map);
//		waitOnChat();
//		attack(1, KeyEvent.VK_C, 2600, 1500);
	}
	public int wolfSpiderMovement(int position, MinimapData map) throws IOException{
		MaplePoint coords = getMinimapPosition(map);
		Zone botTeleRight = new Zone(new MaplePoint(116,108), new MaplePoint(117,113));
		Zone midTeleRight = new Zone(new MaplePoint(148,88), new MaplePoint(148,94));
		Zone midTeleCheckRight = new Zone(new MaplePoint(141,84), new MaplePoint(156,100));
		Zone topTeleRight = new Zone(new MaplePoint(183,48), new MaplePoint(193,55));
		
		Zone middleAttack = new Zone(new MaplePoint(97,104), new MaplePoint(104,118));
		
		Zone botTeleLeft = new Zone(new MaplePoint(82,109), new MaplePoint(83,115));
		Zone midTeleLeft = new Zone(new MaplePoint(48,87), new MaplePoint(48,91));
		Zone midTeleCheckLeft = new Zone(new MaplePoint(46,85), new MaplePoint(58,96)); 
		Zone topTeleLeft = new Zone(new MaplePoint(8,48), new MaplePoint(22,58));
		switch(position) {
		case 0:
			moveToZoneX(botTeleRight, map);
			robot.delay(50);
			rebuff(.5);
			usePortal(botTeleRight, midTeleCheckRight, map);
			Zone attackZone1 = new Zone(new MaplePoint(158,83), new MaplePoint(164,88));
			attackInZone(attackZone1, map);
			return position + 1;
		case 1:
			usePortal(midTeleRight,topTeleRight, map);
			attackInZone(topTeleRight, map);
			return position + 1;
		case 2:
			Zone attackZone3 = new Zone(new MaplePoint(153,64), new MaplePoint(160,69));
			while(!attackZone3.isInZone(coords)) {
				moveToZoneX(attackZone3, map);
				coords = getMinimapPosition(map);
			}
			attackInZone(attackZone3, map);
			return position + 1;
		case 3:
			Zone attackZone4 = new Zone(new MaplePoint(70,75), new MaplePoint(127,81));
			while(!attackZone4.isInZone(coords)) {
				moveToZoneX(attackZone4, map);
				coords = getMinimapPosition(map);
			}
			attackInZone(attackZone4, map);
			return position + 1;
		case 4:
			while(!middleAttack.isInZone(coords)) {
				moveToZoneX(middleAttack, map);
				coords = getMinimapPosition(map);
			}
			attackInZone(middleAttack, map);
			return position + 1;
		case 5:
			usePortal(botTeleLeft, midTeleCheckLeft, map);
			Zone attackZoneLeft = new Zone(new MaplePoint(34,83), new MaplePoint(46,88));
			moveToZoneX(attackZoneLeft, map);
			attackInZone(attackZoneLeft, map);
			return position + 1;
		case 6:
			usePortal(midTeleLeft, topTeleLeft, map);
			attackInZone(topTeleLeft, map);
			return position + 1;
		case 7:
			Zone attackTopLeft1 = new Zone(new MaplePoint(40,64), new MaplePoint(49,69));
			while(!attackTopLeft1.isInZone(coords)) {
				moveToZoneX(attackTopLeft1, map);
				coords = getMinimapPosition(map);
			}
			attackInZone(attackTopLeft1, map);
			return position + 1;
		case 8:
			Zone attackTopLeft2 = new Zone(new MaplePoint(73,76), new MaplePoint(120,81));
			while(!attackTopLeft2.isInXZone(coords)) {
				moveToZoneX(attackTopLeft2, map);
				coords = getMinimapPosition(map);
			}
			attackInZone(attackTopLeft2, map);
			return position + 1;
		case 9:
			while(!middleAttack.isInZone(coords)) {
				moveToZoneX(middleAttack, map);
				coords = getMinimapPosition(map);
			}
			attackInZone(middleAttack, map);
			return 0;
		}
		return 9;
	}
}
