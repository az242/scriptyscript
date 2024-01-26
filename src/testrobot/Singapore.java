package testrobot;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

public class Singapore extends BaseBot{
	MinimapData gs2Dungeon = new MinimapData("gs2dungeon", new Rectangle(34,72,121,53), new Rectangle(47,45,135,15),"minimapNames/gs2dungeon.png");
	MinimapData gs2 = new MinimapData("gs2", new Rectangle(6,72,121,53), new Rectangle(47,45,75,15),"minimapNames/gs2mapName.png");
	MinimapData gs1 = new MinimapData("gs1", new Rectangle(6,72,115,53), new Rectangle(47,45,75,15),"minimapNames/gs1mapName.png");
	MinimapData gs6 = new MinimapData("gs6", new Rectangle(6,72,115,53), new Rectangle(47,45,75,15),"minimapNames/gs2mapName.png");
	
	MinimapData ulu2 = new MinimapData("ulu2", new Rectangle(6,72,132,81), new Rectangle(47,45,70,15),"minimapNames/ulu2Mapname.png");
	MinimapData ulu1 = new MinimapData("ulu1", new Rectangle(6,72,132,104), new Rectangle(47,45,70,15),"minimapNames/ulu2Mapname.png");
	
	Zone topFloor = new Zone(new MaplePoint(2,20),new MaplePoint(140,33));
	Zone midFloor = new Zone(new MaplePoint(2,37),new MaplePoint(140,54));
	Zone botFloor = new Zone(new MaplePoint(2,58),new MaplePoint(140,75));
	MinimapData cd = new MinimapData("cds", new Rectangle(33,72,141,80), new Rectangle(47,45,115,15),"minimapNames/cdName.png");
	MinimapData cddungeon = new MinimapData("cdsdungeon", new Rectangle(64,72,141,80), new Rectangle(47,45,115,15),"minimapNames/cdName.png");
	
	MinimapData cityMap = new MinimapData("singapore", new Rectangle(6,72,210,93), new Rectangle(48,46,70,15), "minimapNames/singapore.png");
	
	long dropTimer = 0;
	public Singapore(Robot robot, Screen[] screens) {
		super(robot, screens);
		minimapDatas.add(gs6);
		minimapDatas.add(gs2Dungeon);
		minimapDatas.add(gs2);
		minimapDatas.add(gs1);
		minimapDatas.add(ulu2);
		minimapDatas.add(ulu1);
		minimapDatas.add(cd);
		minimapDatas.add(cddungeon);
		minimapDatas.add(cityMap);
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
		startingMesos = getMesos();
		long currTime = System.currentTimeMillis();
		long startTime = System.currentTimeMillis();
		rebuff(.75);
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
//			MaplePoint newCoords = getMinimapPosition(map);
//			botOutput("Moved to position index: " + newCoords.toString());
			robot.delay(100);
			attack(map);
			feedPets();
			currTime = System.currentTimeMillis();
			LocalTime now = LocalTime.now();
			if(MINUTES.between(this.startTime, now) > minutes ) {
				minutes = (int) MINUTES.between(this.startTime, now);
				botOutput("Script ran for " + minutes + " minutes");
				checkEquipment();
			}
			
		}
		exitScript();
	}
	
	public void attack(MinimapData map) throws IOException {
		switch(map.name) {
		case "ulu2":
//			attackheal();
//			attack(1, KeyEvent.VK_C, 2750, 2100);
//			attack(1, KeyEvent.VK_V, 615);
			
			break;
		case "gs2":
		case "gs1":
		case "gs6":
		case "gs2dungeon":
		case "ulu1":
//			attack(1, KeyEvent.VK_C, 2750, 2000);
			attack(1, KeyEvent.VK_V, 615);
//			attack(1, KeyEvent.VK_V, 1115);
			
			break;
		case "cds":
		case "cdsdungeon":
			attackheal();
			break;
		}
	}
	public int movement(int position, MinimapData map) throws IOException {
		switch(map.name) {
		case "ulu2":
			return ulu2(position, map);
		case "ulu1":
			return ulu1(position, map);
		case "gs2":
//			return GSSafeMovement(position,map);
		case "gs2dungeon":
			return GS2Movement(position, map);
		case "cds":
		case "cdsdungeon":
			return cdMovement(position, map);
		case "gs1":
			return GS1Movement(position, map);
		case "gs6":
			return GS6Movement(position, map);
		}
		return 0;
	}
	
	long sweeperTimer= 0;
	public int ulu2Sweeper(int position, MinimapData map) throws IOException {
		Zone rightRope = new Zone(new MaplePoint(93,46), new MaplePoint(93,57));
		Zone leftRope = new Zone(new MaplePoint(38,46), new MaplePoint(39,57));
		
		Zone checkZone = new Zone(new MaplePoint(99,47), new MaplePoint(115,54));
		
		Zone spot1 = new Zone(new MaplePoint(36,37), new MaplePoint(40,41));
		Zone spot2 = new Zone(new MaplePoint(58,37), new MaplePoint(64,40));
		Zone spot3 = new Zone(new MaplePoint(81,37), new MaplePoint(86,41));
		Zone spot4 = new Zone(new MaplePoint(96,37), new MaplePoint(103,40));
		MaplePoint pos = getMinimapPosition(map);
		switch(position) {
		case 0:
			refreshHiddenSight();
			climbRope(rightRope, spot1, map);
			while(!spot1.isInXZone(pos)) {
				moveToZoneX(spot1, map);
//				keyPress(KeyEvent.VK_SPACE);
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 1:
		case 7:
			while(!spot2.isInXZone(pos)) {
				moveToZoneX(spot2, map);
				robot.delay(200);
				keyPress(KeyEvent.VK_SPACE);
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 6:
		case 2:
			while(!spot3.isInXZone(pos)) {
				moveToZoneX(spot3, map);
//				keyPress(KeyEvent.VK_SPACE);
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 3:
			while(!spot4.isInXZone(pos)) {
				moveToZoneX(spot4, map);
				robot.delay(200);
				keyPress(KeyEvent.VK_SPACE);
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 4:
			//go down rope
			climbRope(rightRope,checkZone, map);
			robot.keyPress(KeyEvent.VK_C);
			robot.delay(300);
			robot.keyRelease(KeyEvent.VK_C);
			robot.delay(300);
			robot.keyPress(KeyEvent.VK_DELETE);
			robot.delay(300);
			robot.keyRelease(KeyEvent.VK_DELETE);
			return 0;
		case 5:
			//go up rope
			refreshHiddenSight();
			climbRope(rightRope,spot1, map);
			while(!spot4.isInXZone(pos)) {
				moveToZoneX(spot4, map);
				robot.delay(200);
				keyPress(KeyEvent.VK_SPACE);
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 8:
			while(!spot1.isInXZone(pos)) {
				moveToZoneX(spot1, map);
//				keyPress(KeyEvent.VK_SPACE);
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 9: 
			//go down rope
			climbRope(leftRope,checkZone, map);
			robot.keyPress(KeyEvent.VK_C);
			robot.delay(300);
			robot.keyRelease(KeyEvent.VK_C);
			return 0;
		}
		return 0;
	}
	public void refreshHiddenSight() {
		robot.keyPress(KeyEvent.VK_C);
		robot.delay(300);
		robot.keyRelease(KeyEvent.VK_C);
		robot.delay(300);
		robot.keyPress(KeyEvent.VK_V);
		robot.delay(300);
		robot.keyRelease(KeyEvent.VK_V);
	}
	
	public void checkEquipment() throws IOException {
		
		MaplePoint invLoc = findInventory();
		if(invLoc.x < 0) {
			robot.keyPress(KeyEvent.VK_I);
			robot.keyRelease(KeyEvent.VK_I);
			robot.delay(100);
			invLoc = findInventory();
			if(invLoc.x < 0) {
				botOutput("===========");
				botOutput("Couldnt find inventory! Is it in the right place?");
				botOutput("===========");
				return;
			}
		}
//		System.out.println(invLoc.toString());
		MaplePoint EquipButton = new MaplePoint(10 + invLoc.x,25 + invLoc.y);
		robot.mouseMove(EquipButton.x, EquipButton.y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		robot.delay(100);
		
		MaplePoint scrollbarLocation = new MaplePoint(155 + invLoc.x,220 + invLoc.y);
		robot.mouseMove(scrollbarLocation.x, scrollbarLocation.y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.delay(100);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
//		
		Rectangle equipfullsearch = new Rectangle(106 + invLoc.x ,209 + invLoc.y,34,34);
		MaplePoint equipExists = getCurrPosition(equipfullsearch, "inventory/emptyslot.png");
		if(equipExists.x > 0) {
			//inventory empty
//			botOutput("Equips not found, continuing");
//			botOutput("===========");
		} else {
			botOutput("===========");
			//inventory is full! initiate sell procedure
			int init  = getMesos();
			botOutput("Equips found, selling equips. starting mesos: " + formatMesos(init));
			sellEquips();
			int post = getMesos();
			botOutput("After selling we gained " + formatMesos(post-init) + " mesos. Ending mesos: " + formatMesos(post));
			botOutput("===========");
		}
	}
	
	
	
	public void sellEquips() throws IOException {
		int tries = 0;
		while(checkMapMatch(cityMap).x < 0 && tries < 5) {
			robot.keyPress(KeyEvent.VK_J);
			robot.keyRelease(KeyEvent.VK_J);
			robot.delay(600);
			robot.keyPress(KeyEvent.VK_UP);
			robot.keyRelease(KeyEvent.VK_UP);
			robot.delay(2000);
			tries ++;
		}
		if(tries >= 5 && checkMapMatch(cityMap).x < 0) {
			//something went wrong
			exitScript();
		}
		
		MaplePoint pos = getMinimapPosition(cityMap);
//		System.out.println(pos.toString());
		Zone sellZone = new Zone(new MaplePoint(98,27), new MaplePoint(100,80));
		moveToZoneX(sellZone, cityMap);
		robot.delay(2000);
//		MaplePoint invLoc = findInventory();
//		System.out.println(invLoc.toString());
		MaplePoint sellerClick = new MaplePoint(335 + mapleScreen.x,690 + mapleScreen.y);
		robot.mouseMove(sellerClick.x, sellerClick.y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		robot.delay(200);
		
		MaplePoint initSell = new MaplePoint(560 + mapleScreen.x, 350 + mapleScreen.y);
		robot.mouseMove(initSell.x, initSell.y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		robot.delay(1000);
		
		Rectangle equipsearch = new Rectangle(516 + mapleScreen.x ,341 + mapleScreen.y,36,36);
		MaplePoint equipExists = getCurrPosition(equipsearch, "inventory/storeEmptySlot.png");
		MaplePoint sellItembutton = new MaplePoint(700 + mapleScreen.x, 255 + mapleScreen.y);
		robot.mouseMove(sellItembutton.x, sellItembutton.y);
		robot.keyPress(KeyEvent.VK_Y);
		while(equipExists.x < 0) {
			robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			robot.delay(75);
			equipExists = getCurrPosition(equipsearch, "inventory/storeEmptySlot.png");
			robot.keyPress(KeyEvent.VK_Y);
		}
		robot.keyRelease(KeyEvent.VK_Y);
		robot.keyPress(KeyEvent.VK_ESCAPE);
		robot.keyRelease(KeyEvent.VK_ESCAPE);
		
		
		Zone tpBackZone = new Zone(new MaplePoint(81,27), new MaplePoint(83,80));
		moveToZoneX(tpBackZone, cityMap);
		robot.delay(200);
		robot.keyPress(KeyEvent.VK_UP);
		robot.keyRelease(KeyEvent.VK_UP);
		robot.delay(2000);
		botOutput("Finished Selling items.");
	}
	public int ulu1(int position, MinimapData map) throws IOException {
		MaplePoint pos = getMinimapPosition(map);
		switch(position) {
		case 0:
		case 5:
			Zone att1 = new Zone(30,51,31,54);
			while(!att1.isInZone(pos)) {
				moveToPlatform(att1, map);
				pos = getMinimapPosition(map);
			}
			rebuff(.7);
			if(position == 5)
				return 0;
			return position + 1;
		case 1:
		case 4:
			Zone att2 = new Zone(44,70,53,77);
			while(!att2.isInZone(pos)) {
				moveToPlatform(att2, map);
				pos = getMinimapPosition(map);
			}
			return position + 1;
		case 2:
		case 3:
			Zone att3 = new Zone(89,61,96,69);
			while(!att3.isInXZone(pos)) {
				moveToZoneX(att3, map);
				pos = getMinimapPosition(map);
			}
			return position + 1;
		}
		return 0;
	}
	public int ulu2(int position, MinimapData map) throws IOException {
		Zone attack1 = new Zone(new MaplePoint(30,27), new MaplePoint(37,66));
		Zone attack2 = new Zone(new MaplePoint(67,27), new MaplePoint(75,66));
		Zone attack3 = new Zone(new MaplePoint(97,27), new MaplePoint(112,66));
		Zone attack2Top = new Zone(new MaplePoint(58,27), new MaplePoint(65,66));
		Zone attack3Top = new Zone(new MaplePoint(101,27), new MaplePoint(107,66));
		Zone rope = new Zone(new MaplePoint(93,27), new MaplePoint(93,66));
		Zone checkZone = new Zone(new MaplePoint(10,35), new MaplePoint(130,41));
		
		MaplePoint pos = getMinimapPosition(map);
//		botOutput("Offset: " + offset);
		switch(position) {
		case 0:
			telecastAttackMove(attacks.get("genesis"),attack1, map);
			rebuff(.8);
			while(pos.y < 58) {
				jumpDown();
				botOutput("Ending sweep rotation.");
				pos = getMinimapPosition(map);
			}
			
			return position + 1;
		case 1:
			while(pos.y < 58) {
				jumpDown();
				pos = getMinimapPosition(map);
			}
			telecastAttackMove(attacks.get("genesis"),attack2, map);
			if(offset >=5) {
				offset = 0;
			}
			return position + 1;
		case 2:
			telecastAttackMove(attacks.get("genesis"),attack3, map);
			return position + 1;
		case 3:
			offset++;
			if(offset >= 5) {
				botOutput("Starting sweep rotation.");
				climbRope(rope,checkZone, map);
				telecastAttackMove(attacks.get("genesis"),attack3Top, map);
				robot.keyPress(KeyEvent.VK_SPACE);
				robot.keyRelease(KeyEvent.VK_SPACE);
				robot.delay(800);
				return position + 1;
			} else {
				telecastAttackMove(attacks.get("genesis"),attack2, map);
				return 0;
			  }
		case 4:
			if(offset >= 5) {
				telecastAttackMove(attacks.get("genesis"),attack2Top, map);
				robot.keyPress(KeyEvent.VK_SPACE);
				robot.keyRelease(KeyEvent.VK_SPACE);
				robot.delay(800);
			} else {
				return 0;
			}
			
			return position + 1;
		case 5:
			
			telecastAttackMove(attacks.get("genesis"),attack1, map);
			rebuff(.9);
			return 1;
			
		}
		return 0;
	}
	public void attackheal() throws IOException {
		waitOnChat();
		attack(1, KeyEvent.VK_C, 2400, 2000);
		waitOnChat();
//		attack(1, KeyEvent.VK_C, 2400, 2000);
//		waitOnChat();
		attack(1,KeyEvent.VK_V, 100);
		
	}
	int offset = 0;
	public int GS2Movement(int position, MinimapData map) throws IOException {
		Zone leftSide = new Zone(new MaplePoint(40,2),new MaplePoint(46,45));
		Zone rightSide = new Zone(new MaplePoint(80,23),new MaplePoint(92,45));
		long currTime = System.currentTimeMillis();
//		if(currTime > dropTimer + 120*1000 && position == 1) {
//			dropTimer = currTime;
//			swapPlatforms(map);
//			moveToZoneX(leftSide, map);
//		}
		MaplePoint currPos = getMinimapPosition(map);
		int attack = KeyEvent.VK_N;
//		if(offset >= 5) {
//			attack = KeyEvent.VK_V;
//		}
		
		botOutput("offset: " + offset);
		if(position == 0) {
			swapPlatforms(map);
			moveToZoneXAttack(leftSide, map, attack);
			MaplePoint newPos = getMinimapPosition(map);
			botOutput("Move from " + currPos.toString() + " to " + newPos.toString());
			rebuff(.8);
			return 1;
		} else if(position == 1){
			moveToZoneXAttack(rightSide, map, attack);
			MaplePoint newPos = getMinimapPosition(map);
			botOutput("Move from " + currPos.toString() + " to " + newPos.toString());
			return 2;
		}  else if(position == 2){
			swapPlatforms(map);
			moveToZoneXAttack(rightSide, map, attack);
			MaplePoint newPos = getMinimapPosition(map);
			botOutput("Move from " + currPos.toString() + " to " + newPos.toString());
			return 3;
		}  else {
			moveToZoneXAttack(leftSide, map, attack);
			MaplePoint newPos = getMinimapPosition(map);
			botOutput("Move from " + currPos.toString() + " to " + newPos.toString());
//			if(offset >=5) {
//				offset = 0;
//			} else {
//				offset++;
//			}
			return 0;
		} 
	}
	public void moveToZoneXAttack(Zone zone, MinimapData map, int attackKey) throws IOException {
		MaplePoint tempCoords = getMinimapPosition(map);
		if(tempCoords.x < zone.getLeftBound()) {
			robot.keyPress(KeyEvent.VK_RIGHT);
			while(tempCoords.x < zone.getLeftBound()) {
				if(tempCoords.x + 11 < zone.getRightBound()){
					robot.keyPress(attackKey);
					robot.keyPress(attackKey);
					robot.delay(100);
					robot.keyPress(KeyEvent.VK_ALT);
					robot.delay(75);
					robot.keyRelease(KeyEvent.VK_ALT);
					robot.delay(900 + randomPosNeg(randomNum(1,10)));
				} else {
					robot.delay(35);
				}
				tempCoords = getMinimapPosition(map);
			}
			robot.keyRelease(KeyEvent.VK_RIGHT);
		} else if(tempCoords.x > zone.getRightBound()) {
			robot.keyPress(KeyEvent.VK_LEFT);
			while(tempCoords.x > zone.getRightBound()) {
				if(tempCoords.x - 11 > zone.getLeftBound()){
					robot.keyPress(attackKey);
					robot.keyPress(attackKey);
					robot.delay(100);
					robot.keyPress(KeyEvent.VK_ALT);
					robot.delay(75);
					robot.keyRelease(KeyEvent.VK_ALT);
					robot.delay(900 + randomPosNeg(randomNum(1,10)));
				} else {
					robot.delay(35);
				}
				tempCoords = getMinimapPosition(map);
			}
			robot.keyRelease(KeyEvent.VK_LEFT);
		}
	}
	public int GS6Movement(int position, MinimapData map) throws IOException {
		Zone leftSide = new Zone(new MaplePoint(32,2),new MaplePoint(38,45));
		Zone rightSide = new Zone(new MaplePoint(70,2),new MaplePoint(85,45));
//		long currTime = System.currentTimeMillis();
//		if(currTime > dropTimer + 120*1000 && position == 1) {
//			dropTimer = currTime;
//			swapPlatforms2(map);
//			moveToZoneX(leftSide, map);
//		}
		MaplePoint currPos = getMinimapPosition(map);
		int attack = KeyEvent.VK_C;
		if(position == 0) {
			moveToZoneXAttack(leftSide, map,attack);
			MaplePoint newPos = getMinimapPosition(map);
			botOutput("Move from " + currPos.toString() + " to " + newPos.toString());
			return 1;
		} else{
			moveToZoneXAttack(rightSide, map, attack);
			MaplePoint newPos = getMinimapPosition(map);
			botOutput("Move from " + currPos.toString() + " to " + newPos.toString());
			rebuff(.8);
			return 0;
		}
	}
	public int GS1Movement(int position, MinimapData map) throws IOException {
		Zone leftSide = new Zone(new MaplePoint(32,2),new MaplePoint(38,45));
		Zone rightSide = new Zone(new MaplePoint(85,2),new MaplePoint(90,45));
		long currTime = System.currentTimeMillis();
//		if(currTime > dropTimer + 120*1000 && position == 1) {
//			dropTimer = currTime;
//			swapPlatforms2(map);
//			moveToZoneX(leftSide, map);
//		}
		MaplePoint currPos = getMinimapPosition(map);
		if(position == 0) {
			swapPlatforms2(map);
			moveToZoneX(leftSide, map);
			MaplePoint newPos = getMinimapPosition(map);
			botOutput("Move from " + currPos.toString() + " to " + newPos.toString());
			return 1;
		} else if (position == 1){
			moveToZoneX(rightSide, map);
			MaplePoint newPos = getMinimapPosition(map);
			botOutput("Move from " + currPos.toString() + " to " + newPos.toString());
			rebuff(.8);
			return 2;
		}else if(position == 2) {
			swapPlatforms2(map);
			moveToZoneX(rightSide, map);
			MaplePoint newPos = getMinimapPosition(map);
			botOutput("Move from " + currPos.toString() + " to " + newPos.toString());
			return 3;
		} else {
			moveToZoneX(leftSide, map);
			MaplePoint newPos = getMinimapPosition(map);
			botOutput("Move from " + currPos.toString() + " to " + newPos.toString());
			return 0;
		}
	}
	public int GSSafeMovement(int position, MinimapData map) throws IOException {
		int leftBound = 96;
		int rightBound = 101;
		
//		long currTime = System.currentTimeMillis();
//		if(currTime > dropTimer + 120*1000 && position == 1) {
//			dropTimer = currTime;
//			swapPlatforms2(map);
//			moveToZoneX(leftSide, map);
//		}
		MaplePoint currPos = getMinimapPosition(map);
		MaplePoint currPos2= getMinimapPosition(map);
		double rand = Math.random();
		if(currPos.x >=39) {
			rand = 0;
		} else if(currPos.x <= 37) {
			rand = 1;
		}
		if(rand > .5) {
			//move right
			robot.keyPress(KeyEvent.VK_RIGHT);
			while(currPos2.x == currPos.x) {
				robot.delay(30);
				currPos2 = getMinimapPosition(map);
			}
			robot.keyRelease(KeyEvent.VK_RIGHT);
		} else {
			//move left
			robot.keyPress(KeyEvent.VK_LEFT);
			while(currPos2.x == currPos.x) {
				robot.delay(30);
				currPos2 = getMinimapPosition(map);
			}
			robot.keyRelease(KeyEvent.VK_LEFT);
		}
		return 0;
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
	public void swapPlatforms2(MinimapData map) throws IOException {
		Zone dropdownZone = new Zone(new MaplePoint(32,22),new MaplePoint(39,32)); //dropdown
		Zone teleUpZone = new Zone(new MaplePoint(96,38),new MaplePoint(100,45)); //teleup
		
		Zone droppedZone = new Zone(new MaplePoint(32,38),new MaplePoint(39,45)); //dropdown
		Zone teledZone = new Zone(new MaplePoint(96,19),new MaplePoint(100,28)); //teleup
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
	public void swapPlatforms(MinimapData map) throws IOException {
		Zone dropdownZone = new Zone(new MaplePoint(90,25),new MaplePoint(120,32)); //dropdown
		Zone teleUpZone = new Zone(new MaplePoint(36,40),new MaplePoint(39,45)); //teleup
		
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
