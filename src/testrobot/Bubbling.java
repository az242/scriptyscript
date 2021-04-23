package testrobot;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

public class Bubbling extends BaseBot{
	MinimapData bubbling = new MinimapData("Bubbling", new Rectangle(6,72,259,79), new Rectangle(47,45,85,15),"minimapNames/bubblingName.png");
	MinimapData jrwraith = new MinimapData("jrwraith", new Rectangle(6,72,259,79), new Rectangle(47,45,85,15),"minimapNames/jrwraithName.png");
	long dropTimer = 0;
	public Bubbling(Robot robot, Screen[] screens) {
		super(robot, screens);
		minimapDatas.add(bubbling);
		minimapDatas.add(jrwraith);
		adjustMinimapData(mapleScreen,minimapDatas);
	}
	
	public void leech(int hours, MinimapData map) throws IOException {
		swapScreens(getScreen("bishop"));
		MaplePoint cords = getMinimapPosition(map);
		System.out.println("<--------------->");
		System.out.println("Starting leech script");
		System.out.println("<--------------->");
		botOutput("Starting at position: " + cords.x + ", " + cords.y);
		int position = 0;
		long currTime = System.currentTimeMillis();
		long startTime = System.currentTimeMillis();
		while(startTime + (hours * 60 * 60 * 1000) > currTime) {
			System.out.println("<--------------->");
			checkPots();
			position = jrWraithMovement(position, map);
			botOutput("Moved to position index: " + position);
			robot.delay(400);
			attack(1, KeyEvent.VK_C, 2750);
			feedPets();
		}
		exitScript();
	}
	public int bubblingMovement(int position, MinimapData map) throws IOException {
		MaplePoint pos = getMinimapPosition(map);
		switch(position){
		case 8:
		case 0:
			Zone att1 = new Zone(new MaplePoint(60,37), new MaplePoint(63,44));
			while(!att1.isInZone(pos)) {
				moveToZoneX(att1, map);
				if(!att1.isInYZone(pos))
					teleportUp();
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 7:
		case 1:
			Zone att2 = new Zone(new MaplePoint(122,37), new MaplePoint(132,44));
			while(!att2.isInZone(pos)) {
				moveToZoneX(att2, map);
				if(!att2.isInYZone(pos))
					teleportUp();
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 6:
		case 2:
			Zone att3 = new Zone(new MaplePoint(162,37), new MaplePoint(167,44));
			while(!att3.isInZone(pos)) {
				moveToZoneX(att3, map);
				if(!att3.isInYZone(pos))
					teleportUp();
				pos = getMinimapPosition(map);
			}
			rebuff(.75);
			return position + 1;
		case 5:
		case 3:
			Zone att4 = new Zone(new MaplePoint(184,37), new MaplePoint(190,44));
			while(!att4.isInXZone(pos)) {
				moveToZoneX(att4, map);
				if(!att4.isInYZone(pos))
					teleportUp();
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 4:
			Zone att5 = new Zone(new MaplePoint(218,37), new MaplePoint(225,44));
			while(!att5.isInXZone(pos)) {
				moveToZoneX(att5, map);
				if(!att5.isInYZone(pos))
					teleportUp();
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 9:
			return 0;
		}
		return 0;
	}
	public int jrWraithMovement(int position, MinimapData map) throws IOException {
		MaplePoint pos = getMinimapPosition(map);
		switch(position){
		case 7:
		case 0:
			Zone att1 = new Zone(new MaplePoint(54,37), new MaplePoint(58,42));
			while(!att1.isInZone(pos)) {
				moveToZoneX(att1, map);
				if(!att1.isInYZone(pos))
					teleportUp();
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 6:
		case 1:
			Zone att2 = new Zone(new MaplePoint(109,43), new MaplePoint(114,48));
			while(!att2.isInXZone(pos)) {
				moveToZoneX(att2, map);
//				if(!att2.isInYZone(pos[1])){
//					if(pos[1] >= att2.getTopBound()) {
//						teleportUp();
//					} else {
//						jumpDown();
//					}
//				}
				rebuff(.75);
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 5:
		case 2:
			Zone att3 = new Zone(new MaplePoint(142,37), new MaplePoint(148,42));
			while(!att3.isInXZone(pos)) {
				moveToZoneX(att3, map);
				if(!att3.isInYZone(pos)){
					if(pos.y >= att3.getTopBound()) {
						teleportUp();
					} else {
						jumpDown();
					}
				}
					
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 4:
		case 3:
			Zone att4 = new Zone(new MaplePoint(200,37), new MaplePoint(206,42));
			while(!att4.isInXZone(pos)) {
				moveToZoneX(att4, map);
				if(!att4.isInYZone(pos)){
					if(pos.y >= att4.getTopBound()) {
						teleportUp();
					} else {
						jumpDown();
					}
				}
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 8:
			return 0;
		}
		return 0;
	}
}
