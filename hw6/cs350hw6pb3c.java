import java.util.concurrent.Semaphore;
import java.util.Random;

class MyProcess extends Thread {
	private int id;
	private int flagnumber;
	

	static volatile boolean[] flag;
	static volatile int turn = -1;
	static volatile int[] entry;

	public MyProcess(int i, int numflags) {
		id =i;
		flag = new boolean[numflags];
		for(boolean f: flag) {
			f=false;
		}
		entry = new int[numflags];
		for(int e: entry) {
			e=0;
		}
		try{sleep((int) Math.random()*20);} catch (InterruptedException e) {e.printStackTrace();}



	}

	public void run(){
		for(int i =0; i<5; i++) {
			int otherid = (id==0) ? 1 : 0;

			flag[id]=true;

			// enter the entry section
			if(flag[otherid]==false){entry[id]=1;};


			while(flag[otherid]) {
				// add the print statement to here, we can see that 
				// if the other process run faster, which means the other
				// process always get to the entry section before 
				// the current process puts up its flag, then, the current
				// process will always keep checking 
				if(entry[otherid]==1) {System.out.println("thread "+id+" am waiting: ");}  
				if(turn==otherid) {
					flag[id] = false;
					while(flag[otherid]==true) {};
					flag[id]=true;

				}
			}

			try{sleep((int) Math.random()*20);} catch (InterruptedException e) {e.printStackTrace();}
			// exit the entry section
			entry[id] = 0;

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
/*

log shows:


*/

/*
Thread 0 is starting iteration 1
We hold these truths to be self-evident, that all men are created equal,
that they are endowed by their Creator with certain unalienable Rights,
that among these are Life, Liberty and the pursuit of Happiness.
Thread 0 is done with iteration 1

Thread 1 is starting iteration 1
We hold these truths to be self-evident, that all men are created equal,
that they are endowed by their Creator with certain unalienable Rights,
that among these are Life, Liberty and the pursuit of Happiness.
Thread 1 is done with iteration 1

Thread 1 is starting iteration 2
We hold these truths to be self-evident, that all men are created equal,
thread 0 am waiting: 
that they are endowed by their Creator with certain unalienable Rights,
that among these are Life, Liberty and the pursuit of Happiness.
Thread 1 is done with iteration 2

Thread 0 is starting iteration 2
We hold these truths to be self-evident, that all men are created equal,
that they are endowed by their Creator with certain unalienable Rights,
that among these are Life, Liberty and the pursuit of Happiness.
Thread 0 is done with iteration 2

thread 1 am waiting: 
Thread 1 is starting iteration 3
We hold these truths to be self-evident, that all men are created equal,
that they are endowed by their Creator with certain unalienable Rights,
that among these are Life, Liberty and the pursuit of Happiness.
Thread 1 is done with iteration 3

thread 0 am waiting: 
thread 0 am waiting: 
thread 0 am waiting: 
thread 0 am waiting: 
thread 0 am waiting: 
thread 1 am waiting: 
thread 0 am waiting: 
Thread 0 is starting iteration 3
We hold these truths to be self-evident, that all men are created equal,
that they are endowed by their Creator with certain unalienable Rights,
that among these are Life, Liberty and the pursuit of Happiness.
Thread 0 is done with iteration 3

thread 0 am waiting: 
Thread 1 is starting iteration 4
We hold these truths to be self-evident, that all men are created equal,
that they are endowed by their Creator with certain unalienable Rights,
that among these are Life, Liberty and the pursuit of Happiness.
Thread 1 is done with iteration 4

Thread 0 is starting iteration 4
We hold these truths to be self-evident, that all men are created equal,
that they are endowed by their Creator with certain unalienable Rights,
that among these are Life, Liberty and the pursuit of Happiness.
Thread 0 is done with iteration 4

thread 1 am waiting: 
Thread 0 is starting iteration 5
We hold these truths to be self-evident, that all men are created equal,
that they are endowed by their Creator with certain unalienable Rights,
that among these are Life, Liberty and the pursuit of Happiness.
Thread 0 is done with iteration 5

Thread 1 is starting iteration 5
We hold these truths to be self-evident, that all men are created equal,
that they are endowed by their Creator with certain unalienable Rights,
that among these are Life, Liberty and the pursuit of Happiness.
Thread 1 is done with iteration 5
*/