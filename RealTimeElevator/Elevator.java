


import java.util.concurrent.Semaphore;
import java.util.LinkedList;

public class Elevator extends Thread {

	// bacis instances:

	// capacity, maxFloor, number of people
	private static int capacity, maxFloor, numOccupants;

	// elevator working time specs
	private static int openDoor=2000, closeDoor=2000, working = 10000;

	// people working time specs
	private static int getin=500, getout=500;

	// the current floor that elevator is on
	private static volatile int nowFloor;

	// the current available spot in elevator
	private static volatile int available;

	private static volatile Event currentRequest;

	// monitoring the current direction, could be not used
	private static volatile int currentDirection; // 0 means up, 1 means down

	// event array record customers in cabin
	private static volatile LinkedList<Event>[] cabin;

	// monitoring the request queue
	private static volatile LinkedList<Event>[] requestQueue;

	// total request number counter
	private static volatile int totalNumRequest;
	
	


	//Semaphores:
	// mutex to get new request mutually
	private static Semaphore floorQueueMutex = new Semaphore(1);

	public static Semaphore waitingMutex = new Semaphore(1);


	// constructor
	public Elevator(int c, int mf, int n) {
		capacity = c;
		maxFloor = mf;
		numOccupants = n;
		totalNumRequest = 0;
		available = capacity;
		currentRequest = new Event();
		nowFloor = 0;

		cabin = new LinkedList[maxFloor];
		for(int i=0;i<maxFloor;i++) {
			cabin[i] = new LinkedList<Event>();
		}
		currentDirection = 1; // start from 1st floor, and going up
		

		
		requestQueue = new LinkedList[maxFloor];
		for(int i=0 ; i<maxFloor;i++) {
			requestQueue[i] = new LinkedList<Event>();
		}
	}


	// like main function
	public void run() {
		


		// i am on current floor

		while(true) {
//++++++++++++++++++++++++++++ open the foor to drop off +++++++++++++++++++++++
			// takes time to open the door for people to come in.
			try{Thread.sleep(openDoor);} catch (InterruptedException e) {e.printStackTrace();}

			System.out.println("Elevator is on "+nowFloor+" floor, direction: "+currentDirection);

			// if we have some people to drop off
			while(cabin[nowFloor]!=null && cabin[nowFloor].size()>0) {
				Event arrived = cabin[nowFloor].remove();
				if(arrived.getRequestedFloor()==nowFloor) {
					
					// person need time to get out
					try{Thread.sleep(getout);} catch (InterruptedException e) {e.printStackTrace();}
					
					// signal the person to get off the cabin					
					arrived.getId().release();
					available++;
					totalNumRequest--;

				} else {
					cabin[arrived.getRequestedFloor()].add(arrived);
				}
			}


			
	//+++++++++++++++++++++++ open the door to pick up ++++++++++++++++++++++++++++

			// if elevator is available, it will stop for pick up
			// and also if there are people waiting on this floor
			while(available>0 && requestQueue[nowFloor]!=null &&
				requestQueue[nowFloor].size()>0) {
				
				Event request = requestQueue[nowFloor].remove();
				int requestDestination = request.getRequestedFloor();
				// if the elevator is empty now, get one request to set
				// destination, remove that person from the queue of that floor
				if(available==capacity) {
					
					// set direction 
					currentDirection = (requestDestination-nowFloor)>0 ? 1 : -1;
					
					
					// get in takes time
					try{Thread.sleep(getin);} catch (InterruptedException e) {e.printStackTrace();}
					
					// add this person to cabin
					cabin[requestDestination].add(request);
					available--;
					
					

				}
				// the elevator is not empty, we need to update the destination
				// when we add new person. And the destination should be furtherst
				// on the same direction.
				else {
					int requestDirection = (requestDestination-nowFloor)>0 ? 1 : -1;
					// if request wrong direction, put it back to the queue
					// wait for the next turn
					if(currentDirection!=requestDirection) {
						requestQueue[nowFloor].addFirst(request);
					}
					// if request same direction, update the further destination
					// and add the person to the cabin
					else {						
						cabin[requestDestination].add(request);
						available--;

						// get in takes time
						try{Thread.sleep(getin);} catch (InterruptedException e) {e.printStackTrace();}

					}
				}



			}

	//++++++++++++++++++++++++++++ Travel to the next floor ++++++++++++++++++++++++

			
			// update floor number according to the destination
			
			// busy waiting at the current floor

			//while(totalNumRequest==0) {System.out.println("shittttt")};

			while(totalNumRequest==0) {System.out.println("Elevator is waiting");}
			if(totalNumRequest==1) {
				currentDirection = (currentRequest.getRequestedFloor()-nowFloor)>0 ? 1 : -1;
			}
			if(nowFloor== (maxFloor-1)) {
				currentDirection=-1;
				// go to next floor takes some time 
				try{Thread.sleep(working);} catch (InterruptedException e) {e.printStackTrace();}
				nowFloor+=currentDirection;
			} else if(nowFloor==0) {
				currentDirection=1;
				// go to next floor takes some time 
				try{Thread.sleep(working);} catch (InterruptedException e) {e.printStackTrace();}
				nowFloor+= currentDirection;
			} else {
				// go to next floor takes some time 
				try{Thread.sleep(working);} catch (InterruptedException e) {e.printStackTrace();}
				nowFloor += currentDirection;
			}
			
	}

		

	}

	

	public void sendRequest(int waitingFloor, Event event) {
		//System.out.println("no one is calling me");
		try{
			floorQueueMutex.acquire();
		} catch(InterruptedException e) {};
		
		// add the new request to the linkedlist of that floor
		currentRequest = event;
		requestQueue[waitingFloor].add(event);
		totalNumRequest++;

		floorQueueMutex.release();
	}




	// return the capacity of the elevator
	public int getMaxFloor() {
		return maxFloor;
	}
	// return the elevator capacity
	public int getCapacity() {
		return capacity;
	}





}












