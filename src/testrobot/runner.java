package testrobot;

import java.awt.Robot;

public class runner {
	public static void main(String args[]) {
		try {
			Screen bishop = new Screen("bishop","names/worstWarriorname.png", true);
			Screen sweeper = new Screen("sweeper","names/sweepername.png", true);
			Screen spearman = new Screen("spearman","names/spearmanName.png");
			Screen hermit = new Screen("hermit","names/hermitName.png");
			BaseBot bot = new Singapore(new Robot(), new Screen[]{sweeper});
//			bot.enableSkill("Holy Symbol");
//			bot.enableSkill("Invincible");
//			bot.enableSkill("Magic Gaurd");
//			bot.enableSkill("Maple Warrior");
//			bot.enableSkill("Hyperbody");
//			bot.enableSkill("Hermit");
			bot.swapMapleScreen(bot.getScreen("sweeper"));
			bot.leech(6, bot.getMap("ulu2"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
