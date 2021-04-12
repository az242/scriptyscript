package testrobot;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;

public class runner {
	public static void main(String args[]) {
		try {
			Bot bot = new Bot(new Robot());
//		    int[] cords = getCurrPosition(gs2, "worstWarrior.png");
//		    System.out.println(cords[0] + ", " + cords[1]);
//			bot.onlyRebuff();
//			bot.mainAfkFlow(bot.getScript("GS2 no move"), "worstWarrior.png");
			bot.sweepFlow(bot.gs2);
//			long tempTime = System.currentTimeMillis();
//			for(int x=0;x<100;x++) {
//				tempTime = bot.holySymbol(tempTime);
//			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
