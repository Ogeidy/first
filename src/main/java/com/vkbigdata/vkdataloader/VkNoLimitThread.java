package main.java.com.vkbigdata.vkdataloader;

import java.util.Random;

public class VkNoLimitThread extends Thread {
	
	private VkApi vk;
	private VkPrint prnt;
	private int NUM = 6;  //Size of reqs array
	private String reqs[][] = {{"friends.getOnline", "user_id=1&count=5"},
							{"status.get", "user_id=5592362"},
							{"status.get", "group_id=42184737"},
							{"audio.get", "owner_id=5592362&count=10"},
							{"audio.search", "q=NoiseMC&count=17"},
							{"groups.search", "q=Sport&count=28"},
							{"board.getTopics", "group_id=1061&count=50"}};
	
	public VkNoLimitThread(VkApi vkApi, VkPrint vkPrnt) {
		this.vk = vkApi;
		this.prnt = vkPrnt;
	}

	@Override
	public void run() {

		long startTime, stopTime;
		Random rand = new Random();
		while (!Thread.interrupted()) {
			
			int i = rand.nextInt(NUM+1);
			int timeSleep = (int) (700+rand.nextGaussian()*100);
			
			synchronized(VkNoLimitThread.this) {
				startTime = System.currentTimeMillis();
				vk.sendReqS(reqs[i][0], reqs[i][1]);
				
				//Check time limit
				stopTime = System.currentTimeMillis();
				int time = (int)(stopTime-startTime);
				prnt.log("     [VkNoLimitThread] Slipped: "+timeSleep+", Num: "+i+", Time: "+time+"ms");
				if (time < 340) {
					try {
						Thread.sleep(340 - time);
					} catch (InterruptedException e) {
						return;
					}
				}
			}
			
			try {
				Thread.sleep(timeSleep);
			} catch (InterruptedException e) {
				return;
			}
			
		}
		
	}

}
