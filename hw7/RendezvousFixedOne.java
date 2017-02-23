

import java.util.concurrent.Semaphore;

class RendezvousFixedOne extends Thread {
	private static Semaphore[] sema = new Semaphore[5];
    {
		for(int i=0; i<5; i++) {
			sema[i] = new Semaphore(0);
		}
	}
	private int id;

	public RendezvousFixedOne(int i) {
		id = i;
	}

	public void run() {
		for(int i=0; i<5; i++) {
			
			try{sleep((int)Math.random()*10);} catch (InterruptedException e) {e.printStackTrace();}
			
			System.out.println("Process "+id+" arrives at the Rendezvous Point at the "+ i+ " round!");
			
			for(int j=0; j<4; j++) {
				sema[id].release();
			}
			
			for(int k=0; k<5; k++) {
				if(id!=k) {
					try {
						sema[k].acquire(1);
					} catch (InterruptedException e){};
				}
			}

			System.out.println("Process "+id+" leaves the Rendezvous Point at the " + i + " round!");
		}
	}

	public static void main(String[] args) {
		RendezvousFixedOne[] p = new RendezvousFixedOne[5];
		for(int i =0; i<5; i++) {
			p[i] = new RendezvousFixedOne(i);
			p[i].start();
		}
	}


}



