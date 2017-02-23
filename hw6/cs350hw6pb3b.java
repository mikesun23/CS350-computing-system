import java.util.concurrent.Semaphore;
import java.util.Random;

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
			int otherid = (id==0) ? 1 : 0;

			flag[id]=true;
			while(flag[otherid]) {
				if(turn==otherid) {
					flag[id] = false;
					while(flag[otherid]==true) {
					}
					flag[id]=true;

				}
			}

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
		

			turn = otherid;
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