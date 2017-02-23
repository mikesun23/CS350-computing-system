

import java.util.concurrent.Semaphore;


class PrioritySemaphore extends Thread {
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
	private static volatile int[] rankList = new int[5];
	private static volatile int counter =0;
	private int id;

	// constructer
	public PrioritySemaphore(int i) {
		id = i;
		rankList[id] = i;
	}

	public void run() {
		for(int i=0; i<5; i++) {
			
			// sleep some time to start each round
			try{sleep((int) Math.random()*1000);} catch (InterruptedException e) {e.printStackTrace();}

			// new wait()
			try {
				mutex.acquire();
			} catch (InterruptedException e) {};

			rankList[id] = id;
			counter++;
			System.out.println("Process " +id+" is requesting CS");

			// if there are more than one process, signal the high priority process at the first
			if(counter>1) {
				int p = -1;
					for(int j=0; j<5; j++) {
						p = rankList[j]>p ? rankList[j] : p;
				}
				biSema[p].release();
			}
			
			
			mutex.release();

			// if there are more than 1 process in the system
			if(counter>1) {
				
				try {
					biSema[id].acquire();
				} catch(InterruptedException e) {};

			}

			try{
				mutex.acquire();
			} catch (InterruptedException e) {};
			// Critical Section
			System.out.println("\tProcess " + id+ " is in the CS");
			try{sleep((int) Math.random()*1000);} catch (InterruptedException e) {e.printStackTrace();}
      		//System.out.println("\tProcess "+id+" done sleep");
			

			// new signal()
			rankList[id] = 0;
			mutex.release();
			try {
				mutex.acquire();
			} catch (InterruptedException e) {};
			
			// dicrement the counter
			counter--;
			
			System.out.println("Process " +id+" is exiting the CS");
			
			// there are still some process is waiting.
			if(counter>0) {
				int pri = -1;
				for(int j=0; j<5; j++) {
					pri = rankList[j]>pri ? rankList[j] : pri;
				}
				biSema[pri].release();
			}
			mutex.release();


		}

	}


	public static void main(String[] args) {
		PrioritySemaphore[] p = new PrioritySemaphore[5];
		for(int i=0; i<5;i++) {
			p[i] = new PrioritySemaphore(i);
			p[i].start();
		}
	}


}

/*  starvation happens, when repeat =2 for each process
Process 0 is requesting CS
	Process 0 is in the CS
Process 1 is requesting CS
Process 2 is requesting CS
Process 3 is requesting CS
Process 4 is requesting CS
	Process 4 is in the CS
Process 0 is exiting the CS
Process 0 is requesting CS
	Process 3 is in the CS
Process 4 is exiting the CS
Process 4 is requesting CS
	Process 4 is in the CS
Process 3 is exiting the CS
Process 3 is requesting CS
	Process 2 is in the CS
Process 4 is exiting the CS
	Process 3 is in the CS
Process 2 is exiting the CS
Process 2 is requesting CS
	Process 1 is in the CS
Process 3 is exiting the CS
	Process 2 is in the CS
Process 1 is exiting the CS
Process 1 is requesting CS
Process 2 is exiting the CS
	Process 1 is in the CS
Process 1 is exiting the CS
	Process 0 is in the CS
Process 0 is exiting the CS

*/

