package testrobot;

import java.awt.Robot;

public class runner {
	public static void main(String args[]) {
		try {
			Screen bishop = new Screen("bishop","names/worstWarriorname.png", true);
			Screen spearman = new Screen("spearman","names/spearmanName.png");
			Screen hermit = new Screen("hermit","names/hermitName.png");
			Screen hero = new Screen("hero","names/lifedistroyname.png");
			Screen sweeper = new Screen("sweeper","names/sweepername.png");
			Screen hero2 = new Screen("hero2","names/worstWizardName.png");
			BaseBot bot = new Singapore(new Robot(), new Screen[]{bishop, hermit, spearman,hero2 });
			bot.enableSkill("Holy Symbol");
			bot.enableSkill("Invincible");
			bot.enableSkill("Magic Gaurd");
			bot.enableSkill("Maple Warrior");
			bot.enableSkill("Hyperbody");
			bot.enableSkill("Spearman Pot");
			bot.enableSkill("Hermit");
			bot.enableSkill("Hermit Pot");
			bot.swapMapleScreen(bot.getScreen("bishop"));
			bot.leech(12, bot.getMap("ulu2"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
