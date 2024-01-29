package testrobot;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.time.LocalTime;

public class runner {
	public static void main(String args[]) {
		try {
			Screen bishop = new Screen("bishop","names/botCleric.png", true);
			Screen hermit = new Screen("hermit","names/hermitName.png", false);
//			Screen spearman = new Screen("spearman","names/spearmanName.png", false);
			BaseBot bot = new Leafre(new Robot(), new Screen[]{
					bishop});
			bot.enableSkill("Holy Symbol");
			bot.enableSkill("Invincible");
			bot.enableSkill("Magic Gaurd");
			bot.enableSkill("Maple Warrior");
//			bot.enableSkill("Magic");
//			bot.enableSkill("Hyperbody");
//			bot.enableSkill("Spearman Pot");
//			bot.enableSkill("Haste");
//			bot.enableSkill("Hermit Pot");
			bot.swapMapleScreen(bot.getScreen("bishop"));
			bot.leech(8, bot.getMap("skele2"));
//			bot.sellEquips();
//			MaplePoint pos = bot.getMinimapPosition(((Leafre)bot).skele2);
//			System.out.println(bot.formatMesos(bot.getMesos()));
//			LocalTime now = LocalTime.now();
//			System.out.println(pos);
//			Zone leftRope = new Zone(10,55,10,71);
//			Zone checkZoneleft = new Zone(5,50,15,56);
//			bot.climbRope(leftRope, checkZoneleft, ((Leafre)bot).skele2);
//			((Singapore) bot).getMesos();
//			bot.rebuff(.5);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}