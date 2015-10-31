package main.java.com.vkbigdata.vkdataloader;

public class VkNoLimitThread implements Runnable {
	
	private VkApi vk;
	private VkPrint prnt;
	private String reqs[][] = {{"friends.getOnline", "user_id=1&count=5"},
							{"status.get", "user_id=5592362"},
							{"status.get", "group_id=42184737"},
							{"audio.get", "owner_id=-42184737&count=10"},
							{"audio.search", "q=25/17&count=17"},
							{"groups.search", "q=Sport&count=28"},
							{"board.getTopics", "group_id=1061&count=50"}};
	
	public VkNoLimitThread(VkApi vkApi, VkPrint vkPrnt) {
		this.vk = vkApi;
		this.prnt = vkPrnt;
	}

	@Override
	public void run() {
		
		String result;
		long startTime, stopTime;
		
		synchronized(this) {
			startTime = System.currentTimeMillis();
			
			result = vk.sendReqS(reqs[0][0], reqs[0][1]);
			prnt.log(result);
			
			//Check time limit
			stopTime = System.currentTimeMillis();
			int time = (int)(stopTime-startTime);
			prnt.log("[VkNoLimitThread] Time:"+time+"ms");
			if (time < 340) {
				try {
					Thread.sleep(340 - time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

}
