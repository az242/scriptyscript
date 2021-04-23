package testrobot;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

public class NLC extends BaseBot{
	MinimapData wolfSpider = new MinimapData("Wolf Spiders", new Rectangle(6,72,201,136), new Rectangle(47,45,115,15),"wsName.png");
	ArrayList<MinimapData> minimapDatas = new ArrayList<MinimapData>();
	long dropTimer = 0;
	public NLC(Robot robot, Screen[] screens) {
		super(robot, screens);
		minimapDatas.add(wolfSpider);
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
			position = wolfSpiderMovement(position, map);
			botOutput("Moved to position index: " + position);
			attack(1, KeyEvent.VK_C, 2750);
			feedPets();
		}
		exitScript();
	}
	public int wolfSpiderMovement(int position, MinimapData map) throws IOException{
		MaplePoint coords = getMinimapPosition(map);
		Zone botTeleRight = new Zone(new MaplePoint(116,108), new MaplePoint(117,113));
		Zone midTeleRight = new Zone(new MaplePoint(148,88), new MaplePoint(148,94));
		Zone midTeleCheckRight = new Zone(new MaplePoint(144,86), new MaplePoint(152,100));
		Zone topTeleRight = new Zone(new MaplePoint(183,48), new MaplePoint(193,55));
		
		Zone middleAttack = new Zone(new MaplePoint(95,104), new MaplePoint(106,118));
		
		Zone botTeleLeft = new Zone(new MaplePoint(82,109), new MaplePoint(83,115));
		Zone midTeleLeft = new Zone(new MaplePoint(48,87), new MaplePoint(48,91));
		Zone midTeleCheckLeft = new Zone(new MaplePoint(46,85), new MaplePoint(55,93)); 
		Zone topTeleLeft = new Zone(new MaplePoint(8,48), new MaplePoint(22,58));
		switch(position) {
		case 0:
			moveToZoneX(botTeleRight, map);
			rebuff(.6);
			usePortal(botTeleRight, midTeleCheckRight, map);
			Zone attackZone1 = new Zone(new MaplePoint(158,83), new MaplePoint(164,88));
			moveToZoneX(attackZone1, map);
			return position + 1;
		case 1:
			usePortal(midTeleRight,topTeleRight, map);
			return position + 1;
		case 2:
			Zone attackZone3 = new Zone(new MaplePoint(153,64), new MaplePoint(160,69));
			while(!attackZone3.isInZone(coords)) {
				moveToZoneX(attackZone3, map);
				coords = getMinimapPosition(map);
			}
			return position + 1;
		case 3:
			Zone attackZone4 = new Zone(new MaplePoint(122,75), new MaplePoint(127,81));
			while(!attackZone4.isInZone(coords)) {
				moveToZoneX(attackZone4, map);
				coords = getMinimapPosition(map);
			}
			return position + 1;
		case 4:
			while(!middleAttack.isInZone(coords)) {
				moveToZoneX(middleAttack, map);
				coords = getMinimapPosition(map);
			}
			return position + 1;
		case 5:
			usePortal(botTeleLeft, midTeleCheckLeft, map);
			Zone attackZoneLeft = new Zone(new MaplePoint(34,83), new MaplePoint(46,88));
			moveToZoneX(attackZoneLeft, map);
			return position + 1;
		case 6:
			usePortal(midTeleLeft, topTeleLeft, map);
			return position + 1;
		case 7:
			Zone attackTopLeft1 = new Zone(new MaplePoint(40,64), new MaplePoint(49,69));
			while(!attackTopLeft1.isInZone(coords)) {
				moveToZoneX(attackTopLeft1, map);
				coords = getMinimapPosition(map);
			}
			return position + 1;
		case 8:
			Zone attackTopLeft2 = new Zone(new MaplePoint(73,76), new MaplePoint(80,81));
			while(!attackTopLeft2.isInZone(coords)) {
				moveToZoneX(attackTopLeft2, map);
				coords = getMinimapPosition(map);
			}
			return position + 1;
		case 9:
			while(!middleAttack.isInZone(coords)) {
				moveToZoneX(middleAttack, map);
				coords = getMinimapPosition(map);
			}
			return 0;
		}
		return 9;
	}
}
