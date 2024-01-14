package testrobot;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class runner {
	public static void main(String args[]) {
		try {
			Screen bishop = new Screen("bishop","names/botCleric.png", true);
//			Screen hermit = new Screen("hermit","names/hermitName.png", false);
//			Screen spearman = new Screen("spearman","names/spearmanName.png", false);
			BaseBot bot = new Singapore(new Robot(), new Screen[]{
					bishop});
			bot.enableSkill("Holy Symbol");
			bot.enableSkill("Invincible");
			bot.enableSkill("Magic Gaurd");
//			bot.enableSkill("Maple Warrior");
//			bot.enableSkill("Magic");
//			bot.enableSkill("Hyperbody");
//			bot.enableSkill("Spearman Pot");
//			bot.enableSkill("Haste");
//			bot.enableSkill("Hermit Pot");
			bot.swapMapleScreen(bot.getScreen("bishop"));
			bot.leech(4, bot.getMap("gs2"));
//			bot.rebuff(.5);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}