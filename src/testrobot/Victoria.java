package testrobot;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

public class Victoria extends BaseBot{
	MinimapData ironboar = new MinimapData("Iron Boar", new Rectangle(6,72,154,79), new Rectangle(47,45,65,15),"minimapNames/ironboarName.png");
	long dropTimer = 0;
	public Victoria(Robot robot, Screen[] screens) {
		super(robot, screens);
		minimapDatas.add(ironboar);
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
			position = ironBoarMovement(position, map);
			botOutput("Moved to position index: " + position);
			robot.delay(400);
			attack(1, KeyEvent.VK_C, 2750);
			feedPets();
		}
		exitScript();
	}
	public int ironBoarMovement(int position, MinimapData map) throws IOException {
		MaplePoint pos = getMinimapPosition(map);
		switch(position){
		case 0:
			Zone zone1 = new Zone(new MaplePoint(112,48), new MaplePoint(116,52));
			while(!zone1.isInZone(pos)) {
				moveToZoneX(zone1,map);
				teleportUp();
				pos = getMinimapPosition(map);
			}
			rebuff(.75);
			return position + 1;
		case 1:
			Zone zone2 = new Zone(new MaplePoint(68,52), new MaplePoint(74,56));
			while(!zone2.isInZone(pos)) {
				moveToZoneX(zone2,map);
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 2:
			Zone zone3 = new Zone(new MaplePoint(34,52), new MaplePoint(38,56));
			while(!zone3.isInZone(pos)) {
				moveToZoneX(zone3,map);
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 3:
			Zone zone4 = new Zone(new MaplePoint(68,52), new MaplePoint(74,56));
			while(!zone4.isInZone(pos)) {
				moveToZoneX(zone4,map);
				pos = getMinimapPosition(map);
			}
			return 0;
		}
		
		return 0;
	}
}
