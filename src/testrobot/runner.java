package testrobot;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class runner {
	public static void main(String args[]) {
		try {
			Screen bishop = new Screen("bishop","names/botCleric.png", true);
			BaseBot bot = new NLC(new Robot(), new Screen[]{bishop});
			bot.enableSkill("Holy Symbol");
			bot.enableSkill("Invincible");
			bot.enableSkill("Magic Gaurd");
			bot.enableSkill("Maple Warrior");
//			bot.enableSkill("Hyperbody");
//			bot.enableSkill("Spearman Pot");
//			bot.enableSkill("Hermit");
//			bot.enableSkill("Hermit Pot");
			bot.swapMapleScreen(bot.getScreen("bishop"));
			bot.leech(3, bot.getMap("Wolf Spiders"));
//			bot.rebuff(.5);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}