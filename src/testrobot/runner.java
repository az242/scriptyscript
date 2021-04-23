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
//			GhostShip bot = new GhostShip(new Robot(), new int[][]{{100,200}});
//			Bot bot = new Bot(new Robot(), new int[][]{{100,200},{100,900}});
//			WolfSpider bot = new WolfSpider(new Robot(), new int[][]{{300,200}});
//			Gallopera bot = new Gallopera(new Robot(), new int[][]{{300,200}});
//			CD bot = new CD(new Robot(), new int[][]{{300,200}});
//			Bubbling bot = new Bubbling(new Robot(), new int[][]{{300,200},{300,800}});
			BaseBot bot = new Singapore(new Robot(), new Screen[]{bishop,spearman,hermit});
//		    int[] cords = getCurrPosition(gs2, "worstWarrior.png");
//		    System.out.println(cords[0] + ", " + cords[1]);
//			bot.onlyRebuff();
			bot.enableSkill("Holy Symbol");
////			bot.mainAfkFlow(bot.scripts[1],bot.gs2, "worstWarrior.png");
			bot.enableSkill("Invincible");
			bot.enableSkill("Magic Gaurd");
			bot.enableSkill("Maple Warrior");
			bot.enableSkill("Hyperbody");
			bot.enableSkill("Hermit");
//			bot.enableSkill("Bless");
//			bot.enableSkill("Haste");
//			bot.enableSkill("Mesos Up");
			bot.leech(4, bot.getMap("ulu2"));
//			bot.sweepFlow(bot.gs2, 5);
//			bot.outputCoords(bot.getMinimapPosition(bot.getMap("ulu2")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
