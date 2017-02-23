

import java.util.concurrent.Semaphore;

class RendezvousBuggyOne extends Thread {
	private static Semaphore[] sema = new Semaphore[5];
    {
		for(int i=0; i<5; i++) {
			sema[i] = new Semaphore(0);
		}
	}
	private int id;

	public RendezvousBuggyOne(int i) {
		id = i;
	}

	public void run() {
		for(int i=0; i<2; i++) {
			
			try{sleep((int)Math.random()*10);} catch (InterruptedException e) {e.printStackTrace();}
			
			System.out.println("Process "+id+" arrives at the Rendezvous Point at the "+ i+ " round!");
			
			sema[id].release();
			
			for(int j=0; j<5; j++) {
				if(id!=j) {
					try {
						sema[j].acquire(1);
					} catch (InterruptedException e){};
					
					sema[j].release(1);
					
				}
			}

			System.out.println("Process "+id+" leaves the Rendezvous Point at the " + i + " round!");
		}
	}

	public static void main(String[] args) {
		RendezvousBuggyOne[] p = new RendezvousBuggyOne[5];
		for(int i =0; i<5; i++) {
			p[i] = new RendezvousBuggyOne(i);
			p[i].start();
		}
	}


}



