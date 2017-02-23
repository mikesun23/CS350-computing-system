import java.util.concurrent.Semaphore;

class N_BatchedConcurrent extends Thread {
	private static Semaphore gate = new Semaphore(0);
	private static Semaphore mutex = new Semaphore(1);
	private static int counter =0;
	private int id;
	

	public N_BatchedConcurrent(int i) {
		id = i;
	}

	public void run() {
		// I decide for now we jsut run the crtical section once per process
		// and let the process sleep for some time
		try{sleep((int)Math.random()*10);} catch (InterruptedException e) {e.printStackTrace();}
		
		// now the new process starts
		// require the mutex at the first
		try {
			mutex.acquire();
		} catch (InterruptedException e) {};
		counter++;
		if(counter==3) {
			for(int i=0; i<3; i++) {
				gate.release();
			}
			System.out.println();
			counter=0;
		}
		mutex.release();
		try{
			gate.acquire();
		} catch(InterruptedException e) {};
		
		// In Critical section:
		System.out.println("Process " + id +" is in critical section");

	}



	public static void main(String[] args) {
		N_BatchedConcurrent[] p = new N_BatchedConcurrent[12];
		for(int i =0; i<12; i++ ) {
			p[i] = new N_BatchedConcurrent(i);
			p[i].start();
		}
	}

}




