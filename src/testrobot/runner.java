package testrobot;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;

public class runner {
	public static void main(String args[]) {
		try {
			Screen bishop = new Screen("bishop",300,200, true);
			Screen spearman = new Screen("spearman",300,800);
			Screen hermit = new Screen("hermit",1200,200);
			BaseBot bot = new Singapore(new Robot(), new Screen[]{bishop,spearman,hermit});
			bot.enableSkill("Holy Symbol");
			bot.enableSkill("Invincible");
			bot.enableSkill("Magic Gaurd");
			bot.enableSkill("Maple Warrior");
			bot.enableSkill("Hyperbody");
			bot.enableSkill("Hermit");
			bot.leech(4, bot.getMap("ulu2"));
//			bot.outputCoords(bot.getMinimapPosition(bot.getMap("ulu2")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
