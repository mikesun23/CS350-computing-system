
import java.util.concurrent.Semaphore;
import java.util.LinkedList;
import java.util.Random;
public class Occupants extends Thread {

	// instances:
	private final int id;
	private int currentFloor;

	private static int sleeptimeConstant;
	private static Elevator elevator;

	// used to send request to elevator
	private static Semaphore sendingRequestMutex = new Semaphore(1);
	
	// used to stop each individual customers, 
	// reasons: 1. overloading, 2. waiting for elevator 3. wating for finish
	public static Semaphore[] individualWaiting = new Semaphore[8];
	static {
		for(int i=0; i<8; i++) {
			individualWaiting[i] = new Semaphore(0);
		}
	}

	public Occupants(int i, int sleeptime, Elevator e) {
		this.id = i;
		sleeptimeConstant = sleeptime;
		
		elevator = e;
		this.currentFloor = 0;

	}

	public void run() {

		// repeate for 5 times.
		for(int i=0; i<2;i++) {

			// generate next floor I want to go
			int nextFloor = getNextFloor(currentFloor);
			Event newRequest = new Event(individualWaiting[id], nextFloor);

			// send the elevator request
			try {
				sendingRequestMutex.acquire();
			} catch(InterruptedException e) {};
			
			// send request to the elevator
			elevator.sendRequest(this.currentFloor, newRequest);
			System.out.println("Process " + id+ " is requesting on " + this.currentFloor + " destination: " +nextFloor);
			
			sendingRequestMutex.release();

			// wait for elevator either come or serve
			try{
				individualWaiting[id].acquire();
			} catch(InterruptedException e) {};

			// many things can happen here:
				// 1. missed the elevator because of opposite direction
				// 2. elevator is full, can't get in
				// 3. always be missed, lead to starvation
				// eventually, it will get to its currentfloor

			// all implemented in the elevator, won't give starvation
			this.currentFloor = nextFloor; // *** should be some functions in elevator
			System.out.println("Process " + id +" has arrived " + this.currentFloor);


			// sleep some time and repeat
			try{Thread.sleep((int)(Math.random()*sleeptimeConstant));} catch (InterruptedException e) {e.printStackTrace();}

			


		}
	}



	public int getNextFloor(int curFloor) {
		int index = curFloor;
		while (index == curFloor) {
			index = (int)(Math.random() / (1.0/elevator.getMaxFloor()));
		}
		return index;

	}


}








