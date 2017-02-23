import java.util.concurrent.Semaphore;
import java.util.Random;

class FunnyProcess extends Thread {
	private int id;
	private int flagnumber;
	private int waitingloop;
	static volatile boolean[] flag;
	static volatile int turn = -1;

	public FunnyProcess(int i, int numflags) {
		id =i;
		flag = new boolean[numflags];
		for(boolean f: flag) {
			f=false;
		}
		waitingloop=0;
	}

	public void run(){
		for(int i =0; i<5; i++) {

			// get the other id
			int otherid = (id==0) ? 1 : 0;

			// be polite assign turn to otherid
			turn=otherid;

			// then make self check flag up
			flag[id]=true;

			// if other is checking and it is other's turn, then wait

			while(flag[otherid] && turn==otherid) {waitingloop++;}

			// this is the critical section
		
			System.out.println("Thread " +id + " is starting iteration "+(i+1));
			try{sleep((int) Math.random()*20);} catch (InterruptedException e) {e.printStackTrace();}
			System.out.println("We hold these truths to be self-evident, that all men are created equal,");
			try{sleep((int) Math.random()*20);} catch (InterruptedException e) {e.printStackTrace();}
			System.out.println("that they are endowed by their Creator with certain unalienable Rights,");
			try{sleep((int) Math.random()*20);} catch (InterruptedException e) {e.printStackTrace();}
			System.out.println("that among these are Life, Liberty and the pursuit of Happiness.");
			try{sleep((int) Math.random()*20);} catch (InterruptedException e) {e.printStackTrace();}
			System.out.println("Thread "+id+ " is done with iteration "+(i+1));
			System.out.println();


			flag[id] = false;
		

		}
		System.out.println("thread: " +id +" busy waitingloop: "+ waitingloop);
		waitingloop = 0;
	}

	public static void main(String[] args) {
		final int N=2;
		FunnyProcess[] p = new FunnyProcess[N];

		for(int i=0; i<N; i++) {
			p[i] = new FunnyProcess(i,N);
			p[i].start();
		}
		for(int i=0; i<N; i++) {
			System.out.println("tttthread: "+i+" waitingloop "+p[i].waitingloop);
		}
		System.out.println("shit");
	}
}


/*









*/




