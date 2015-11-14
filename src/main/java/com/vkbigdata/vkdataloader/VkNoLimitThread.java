package main.java.com.vkbigdata.vkdataloader;

import java.util.Random;

public class VkNoLimitThread extends Thread {
	
	private boolean DBG = true;
	private String TAG = "      [VkNoLimitThread]";
	private VkApi vk;
	private VkPrint prnt;
	Random rand;
	
	private int NUM = 18;  //Size of reqs array
	private String reqs[][] = {{"friends.getOnline", "user_id=1&count=5"},
							{"status.get", "user_id=5592362"},
							{"status.get", "user_id=169902419"},
							{"status.get", "user_id=2677959"},
							{"status.get", "user_id=87896266"},
							{"status.get", "group_id=42184737"},
							{"audio.get", "owner_id=5592362&count=10"},
							{"audio.get", "owner_id=5592362&ofset=10&count=10"},
							{"audio.get", "owner_id=5592362&ofset=20&count=10"},
							{"audio.search", "q=NoiseMC&count=11"},
							{"audio.search", "q=Radio%20Record&count=37"},
							{"audio.search", "q=NickelBack&count=16"},
							{"audio.search", "q=Lumen&count=5"},
							{"audio.search", "q=Metallica&count=5"},
							{"groups.search", "q=Sport&count=28"},
							{"groups.search", "q=Life&count=21"},
							{"groups.search", "q=Discovery&count=50"},
							{"groups.search", "q=Polytech&count=5"},
							{"board.getTopics", "group_id=1061&count=50"}};
	
	public VkNoLimitThread(VkApi vkApi, VkPrint vkPrnt) {
		this.vk = vkApi;
		this.prnt = vkPrnt;
	}

	public void run() {

		long startTime, stopTime;
		rand = new Random();
		
		while (!Thread.interrupted()) {
			
			int i = rand.nextInt(NUM+1);
			int timeSleep = (int) (600+rand.nextGaussian()*400);
			if (timeSleep < 0) timeSleep = 0;
			
			synchronized(vk) {
				startTime = System.currentTimeMillis();
				vk.sendReqS(reqs[i][0], reqs[i][1]);
				
				//Check time limit
				stopTime = System.currentTimeMillis();
				int time = (int)(stopTime-startTime);
				if (DBG) prnt.log(TAG+" Slipped: "+timeSleep+", Num: "+i+", Time: "+time+"ms");
				if (time < 340) {
					try {
						Thread.sleep(340 - time);
					} catch (InterruptedException e) {
						return;
					}
				}
				
				randomPause();
			}
			
			try {
				Thread.sleep(timeSleep);
			} catch (InterruptedException e) {
				return;
			}
			
		}
		
	}
	
	private void randomPause() {
		
		int i = rand.nextInt(100);
		
		if (i>97) {
			if (DBG) prnt.log(TAG+" Random Big Pause: 5 min");
			try { 
				Thread.sleep(60000*5);
			} catch (InterruptedException e) {
				return;
			}
		} 
		else if (i > 70) {
			if (DBG) prnt.log(TAG+" randomPause: "+i*100+"ms");
			try {
				Thread.sleep(i*100);
			} catch (InterruptedException e) {
				return;
			}
		}
		
	}

}
