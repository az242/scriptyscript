package testrobot;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class runner {
	public static void main(String args[]) {
		try {
			Screen bishop = new Screen("bishop","names/worstWarriorname.png", true);
			Screen spearman = new Screen("spearman","names/spearmanName.png");
			Screen hermit = new Screen("hermit","names/hermitName.png");
			BaseBot bot = new NLC(new Robot(), new Screen[]{bishop,spearman});
			bot.enableSkill("Holy Symbol");
			bot.enableSkill("Invincible");
			bot.enableSkill("Magic Gaurd");
			bot.enableSkill("Maple Warrior");
//			bot.enableSkill("Hyperbody");
//			bot.enableSkill("Hermit");
			bot.leech(3, bot.getMap("Wolf Spiders"));
//			bot.swapMapleScreen(bot.getScreen("bishop"));
//			bot.robot.delay(1000);
//			System.out.println(bot.getMp());
//			bot.attack(1, KeyEvent.VK_C, 2750, 3500);
//			bot.outputCoords(bot.getMinimapPosition(bot.getMap("ulu2")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
