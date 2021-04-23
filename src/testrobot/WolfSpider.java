package testrobot;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

public class WolfSpider extends BaseBot{
	MinimapData wolfSpider = new MinimapData("Wolf Spiders", new Rectangle(6,72,201,136), new Rectangle(47,45,115,15),"wsName.png");
	ArrayList<MinimapData> minimapDatas = new ArrayList<MinimapData>();
	long dropTimer = 0;
	public WolfSpider(Robot robot, int[][] screens) {
		super(robot, screens);
		minimapDatas.add(wolfSpider);
		adjustMinimapData(mapleScreen,minimapDatas);
	}
	public void leech(int hours, MinimapData map) throws IOException {
		swapScreens(0, this.screens);
		int[] cords = getMinimapPosition(map);
		System.out.println("<--------------->");
		System.out.println("Starting leech script");
		System.out.println("<--------------->");
		botOutput("Starting at position: " + cords[0] + ", " + cords[1]);
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
	public void HolySymbol() {
		long temptime = System.currentTimeMillis();
		if(temptime >= buffTimers[0] + buffs[0].buffLength*1000 - 40000) {
			botOutput("Reapplying " + buffs[0].buffName);
			buffTimers[0] = temptime;
			keyPress(buffs[0].buffKey);
			robot.delay(randomNum(1750,2500));
		}
	}
	public void MapleWarrior() {
		long temptime = System.currentTimeMillis();
		if(temptime >= buffTimers[4] + buffs[4].buffLength*1000 - 40000) {
			botOutput("Reapplying " + buffs[4].buffName);
			buffTimers[4] = temptime;
			keyPress(buffs[4].buffKey);
			robot.delay(randomNum(1750,2500));
		}
	}
	public int wolfSpiderMovement(int position, MinimapData map) throws IOException{
		int[] coords = getMinimapPosition(map);
		Zone botTeleRight = new Zone(new int[]{116,108}, new int[]{117,113});
		Zone midTeleRight = new Zone(new int[]{148,88}, new int[]{148,94});
		Zone midTeleCheckRight = new Zone(new int[]{144,86}, new int[]{152,100});
		Zone topTeleRight = new Zone(new int[]{183,48}, new int[]{193,55});
		
		Zone middleAttack = new Zone(new int[]{95,104}, new int[]{106,118});
		
		Zone botTeleLeft = new Zone(new int[]{82,109}, new int[]{83,115});
		Zone midTeleLeft = new Zone(new int[]{48,87}, new int[]{48,91});
		Zone midTeleCheckLeft = new Zone(new int[]{46,85}, new int[]{55,93}); 
		Zone topTeleLeft = new Zone(new int[]{8,48}, new int[]{22,58});
		switch(position) {
		case 0:
			moveToZoneX(botTeleRight, map);
			rebuff(true);
			while(!midTeleCheckRight.isInZone(coords[0],coords[1])) {
				usePortal(botTeleRight, map);
				coords = getMinimapPosition(map);
			}
			Zone attackZone1 = new Zone(new int[]{158,83}, new int[]{164,88});
			moveToZoneX(attackZone1, map);
			return position + 1;
		case 1:
			while(!topTeleRight.isInZone(coords[0],coords[1])) {
				usePortal(midTeleRight, map);
				coords = getMinimapPosition(map);
			}
			return position + 1;
		case 2:
			Zone attackZone3 = new Zone(new int[]{153,64}, new int[]{160,69});
			while(!attackZone3.isInZone(coords[0],coords[1])) {
				moveToZoneX(attackZone3, map);
				coords = getMinimapPosition(map);
			}
			return position + 1;
		case 3:
			Zone attackZone4 = new Zone(new int[]{122,75}, new int[]{127,81});
			while(!attackZone4.isInZone(coords[0],coords[1])) {
				moveToZoneX(attackZone4, map);
				coords = getMinimapPosition(map);
			}
			return position + 1;
		case 4:
			while(!middleAttack.isInZone(coords[0],coords[1])) {
				moveToZoneX(middleAttack, map);
				coords = getMinimapPosition(map);
			}
			return position + 1;
		case 5:
			while(!midTeleCheckLeft.isInZone(coords[0],coords[1])) {
				usePortal(botTeleLeft, map);
				coords = getMinimapPosition(map);
			}
			Zone attackZoneLeft = new Zone(new int[]{34,83}, new int[]{46,88});
			moveToZoneX(attackZoneLeft, map);
			return position + 1;
		case 6:
			while(!topTeleLeft.isInZone(coords[0],coords[1])) {
				usePortal(midTeleLeft, map);
				coords = getMinimapPosition(map);
			}
			return position + 1;
		case 7:
			Zone attackTopLeft1 = new Zone(new int[]{40,64}, new int[]{49,69});
			while(!attackTopLeft1.isInZone(coords[0],coords[1])) {
				moveToZoneX(attackTopLeft1, map);
				coords = getMinimapPosition(map);
			}
			return position + 1;
		case 8:
			Zone attackTopLeft2 = new Zone(new int[]{73,76}, new int[]{80,81});
			while(!attackTopLeft2.isInZone(coords[0],coords[1])) {
				moveToZoneX(attackTopLeft2, map);
				coords = getMinimapPosition(map);
			}
			return position + 1;
		case 9:
			while(!middleAttack.isInZone(coords[0],coords[1])) {
				moveToZoneX(middleAttack, map);
				coords = getMinimapPosition(map);
			}
			return 0;
		}
		return 9;
	}
	public void usePortal(Zone zone, MinimapData map) throws IOException {
		int[] tempCoords = getMinimapPosition(map);
//		while(zone.isInZone(tempCoords[0], tempCoords[1])) {
//			
//		}
		if(tempCoords[0] < zone.getLeftBound()) {
			robot.keyPress(KeyEvent.VK_RIGHT);
			while(tempCoords[0] < zone.getLeftBound()) {
				if(tempCoords[0] + 11 < zone.getRightBound()){
					robot.delay(75);
					robot.keyPress(KeyEvent.VK_ALT);
					robot.delay(75);
					robot.keyRelease(KeyEvent.VK_ALT);
				} else {
					robot.delay(75);
				}
				tempCoords = getMinimapPosition(map);
			}
			robot.keyRelease(KeyEvent.VK_RIGHT);
		} else if(tempCoords[0] > zone.getRightBound()) {
			robot.keyPress(KeyEvent.VK_LEFT);
			while(tempCoords[0] > zone.getRightBound()) {
				if(tempCoords[0] - 11 > zone.getLeftBound()){
					robot.delay(75);
					robot.keyPress(KeyEvent.VK_ALT);
					robot.delay(75);
					robot.keyRelease(KeyEvent.VK_ALT);
				} else {
					robot.delay(75);
				}
				tempCoords = getMinimapPosition(map);
			}
			robot.keyRelease(KeyEvent.VK_LEFT);
		}
		robot.keyPress(KeyEvent.VK_UP);
		robot.keyRelease(KeyEvent.VK_UP);
		robot.delay(300);
	}
}
