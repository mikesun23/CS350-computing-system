import java.util.concurrent.Semaphore;
import java.util.Random;

class MyProcess extends Thread {
	private int id;

	public MyProcess(int i) {
		id =i;
	}

	public void run(){




		// this is the critical section
		for(int i =0; i<5; i++) {
			System.out.println("Thread " +id + " is starting iteration "+(i+1));
			try{sleep((int) Math.random()*20);} catch (InterruptedException e) {e.printStackTrace();}
			System.out.println("We hold these truths to be self-evident, that all men are created equal,");
			try{sleep((int) Math.random()*20);} catch (InterruptedException e) {e.printStackTrace();}
			System.out.println("that they are endowed by their Creator with certain unalienable Rights,");
			try{sleep((int) Math.random()*20);} catch (InterruptedException e) {e.printStackTrace();}
			System.out.println("that among these are Life, Liberty and the pursuit of Happiness.");
			try{sleep((int) Math.random()*20);} catch (InterruptedException e) {e.printStackTrace();}
			System.out.println("Thread "+id+ " is done with iteration "+(i+1));
		}
	}

	public static void main(String[] args) {
		final int N=2;
		MyProcess[] p = new MyProcess[N];

		for(int i=0; i<N; i++) {
			p[i] = new MyProcess(i);
			p[i].start();
		}
	}
}