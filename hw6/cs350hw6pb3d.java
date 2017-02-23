import java.util.concurrent.Semaphore;
import java.util.Random;




/* Problem 3d. 
Answer:
By using Peterson's algorithm, this algorithm combines 
waiting loop and turn checking loop because under this algorithm
each process politely gives the turn to others(take turns instead
of continuously checking who's turn is next). The turn
is determined at the beginning. it only
waits for the other process puts down the flag. 
Even the other process goes faster and start a new round,
but the other process gives the turn to the current waiting 
process, and the other process only puts up its flag and wait.
Therefore the current waiting process won't need to check if 
the other process gives the turn to it. it only needs to wait
the other process exit the critical section and puts down the
flag.


*/




class MyProcess extends Thread {
	private int id;
	private int flagnumber;

	static volatile boolean[] flag;
	static volatile int turn = -1;

	public MyProcess(int i, int numflags) {
		id =i;
		flag = new boolean[numflags];
		for(boolean f: flag) {
			f=false;
		}
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

			while(flag[otherid] && turn==otherid) {}; 

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
	}

	public static void main(String[] args) {
		final int N=2;
		MyProcess[] p = new MyProcess[N];

		for(int i=0; i<N; i++) {
			p[i] = new MyProcess(i,N);
			p[i].start();
		}
	}
}







