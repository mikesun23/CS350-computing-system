

import java.util.concurrent.Semaphore;
import java.util.LinkedList;

class FIFOSemaphore extends Thread {
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
	private static volatile LinkedList<Integer> comingOrder = new LinkedList<Integer>();
	private static volatile int counter =0;
	private int id;

	// constructer
	public FIFOSemaphore(int i) {
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

			comingOrder.add(id);
			counter++;
			System.out.println("Process " +id+" is requesting CS");

			mutex.release();

			// if there are more than 1 process in the system
			if(counter>1) {

				try {
					biSema[id].acquire();
				} catch(InterruptedException e) {};

			}

			
			// Critical Section
			System.out.println("\tProcess " + id+ " is in the CS");
			try{sleep((int) Math.random()*1000);} catch (InterruptedException e) {e.printStackTrace();}
      		//System.out.println("\tProcess "+id+" done sleep");
			

			// new signal()
			try {
				mutex.acquire();
			} catch (InterruptedException e) {};
			
			// dicrement the counter
			counter--;
			
			comingOrder.remove();
			
			System.out.println("Process " +id+" is exiting the CS");
			
			// there are still some process is waiting.
			if(counter>0) {

				int index = comingOrder.peek();
				System.out.println(index);

				biSema[index].release();
			}
			mutex.release();


		}

	}


	public static void main(String[] args) {
		FIFOSemaphore[] p = new FIFOSemaphore[5];
		for(int i=0; i<5;i++) {
			p[i] = new FIFOSemaphore(i);
			p[i].start();
		}
	}


}



