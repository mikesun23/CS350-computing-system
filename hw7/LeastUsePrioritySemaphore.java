

import java.util.concurrent.Semaphore;


class LeastUsePrioritySemaphore extends Thread {
	// instances: 
		// rank array
		// counter
		// a binary semaphore (allows only one process at a time)



	private static Semaphore biSema[] = new Semaphore[5];
	static {
		for(int i=0; i<5; i++) {
			biSema[i] = new Semaphore(0);
		}
	}
	private static Semaphore mutex = new Semaphore(1);
	private static volatile int counter =0;
	private int id;

	private static volatile int[] lessUse = new int[5];
	static {
		for(int i=0;i<5;i++) {
			lessUse[i] = 0;
		}
	}

	// constructer
	public LeastUsePrioritySemaphore(int i) {
		id = i;
	}

	public void run() {
		for(int i=0; i<5; i++) {
			
			// sleep some time to start each round
			try{sleep((int) Math.random()*1000);} catch (InterruptedException e) {e.printStackTrace();}

			// new wait()
			try {
				mutex.acquire();
			} catch (InterruptedException e) {};

			counter++;
			System.out.println("Process " +id+" is requesting CS");
			if(counter>1) {
				int index = -1;
				int p = Integer.MAX_VALUE;
				for(int j=0; j<5; j++) {
					if(p>lessUse[j]) {
						p=lessUse[j];
						index=j;
					}
				}


				biSema[index].release();
			}
			mutex.release();

			// if there are more than 1 process in the system
			if(counter>1) {

				try {
					biSema[id].acquire();
				} catch(InterruptedException e) {};

			}
			
			try {
				mutex.acquire();
			} catch (InterruptedException e) {};
			// Critical Section
			System.out.println("\tProcess " + id+ " is in the CS");

			try{sleep((int) Math.random()*1000);} catch (InterruptedException e) {e.printStackTrace();}
      		//System.out.println("\tProcess "+id+" done sleep");
			
			// update the usage of the current process
			lessUse[id]++;
			System.out.println("\t\t\tP"+id+" has frequency: "+lessUse[id]);

			mutex.release();

			// new signal()
			try {
				mutex.acquire();
			} catch (InterruptedException e) {};
			
			// dicrement the counter
			counter--;
			
			System.out.println("Process " +id+" is exiting the CS");
			
			// there are still some process is waiting.
			if(counter>0) {
				int index = -1;
				int pri = Integer.MAX_VALUE;
				for(int j=0; j<5; j++) {
					if(pri>lessUse[j]) {
						pri=lessUse[j];
						index=j;
					}
				}
				biSema[index].release();
			}
			mutex.release();


		}

	}


	public static void main(String[] args) {
		LeastUsePrioritySemaphore[] p = new LeastUsePrioritySemaphore[5];
		for(int i=0; i<5;i++) {
			p[i] = new LeastUsePrioritySemaphore(i);
			p[i].start();
		}
	}


}



