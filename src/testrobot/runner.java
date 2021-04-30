package testrobot;

import java.awt.Robot;

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
			bot.enableSkill("Hyperbody");
//			bot.enableSkill("Hermit");
			bot.swapMapleScreen(bot.getScreen("bishop"));
			bot.leech(6, bot.getMap("Wolf Spiders"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
