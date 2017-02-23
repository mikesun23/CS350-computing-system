
import java.util.concurrent.Semaphore;


public class Simulation {
	public static void main(String args[]) {
		
		// tune parameters
		int capacity=2, maxFloor=3, numOccupants = 5;
		int personSleepTime = 10000;

		Elevator e = new Elevator(capacity, maxFloor, numOccupants);
		e.start();
		Occupants[] p = new Occupants[numOccupants];
		for(int i=0;i<numOccupants;i++) {
			//System.out.println("hello fucker");
			p[i] = new Occupants(i, personSleepTime, e);
			p[i].start();
		}

		
		// int index = (int)(Math.random() / (1.0/3));
		// System.out.println(index);

		
	}
}



/*
mikesun:CS350 mikesun$ java Simulation
Process 1 is requesting on 0 destination: 2
Process 0 is requesting on 0 destination: 1
Process 4 is requesting on 0 destination: 1
Process 3 is requesting on 0 destination: 1
Process 2 is requesting on 0 destination: 1
Elevator is on 0 floor, direction: 1
Elevator is on 1 floor, direction: 1
Process 0 has arrived 1
Process 0 is requesting on 1 destination: 0
Elevator is on 2 floor, direction: 1
Process 1 has arrived 2
Process 1 is requesting on 2 destination: 0
Elevator is on 1 floor, direction: -1
Elevator is on 0 floor, direction: -1
Process 0 has arrived 0
Elevator is on 1 floor, direction: 1
Process 4 has arrived 1
Process 3 has arrived 1
Process 3 is requesting on 1 destination: 0
Process 4 is requesting on 1 destination: 0
Elevator is on 2 floor, direction: 1
Elevator is on 1 floor, direction: -1
Elevator is on 0 floor, direction: -1
Process 1 has arrived 0
Process 3 has arrived 0
Elevator is on 1 floor, direction: 1
Process 2 has arrived 1
Process 2 is requesting on 1 destination: 2
Elevator is on 0 floor, direction: -1
Process 4 has arrived 0
Elevator is on 1 floor, direction: 1
Elevator is on 2 floor, direction: 1
Elevator is waiting
Elevator is waiting
Elevator is waiting
Elevator is waiting
Elevator is waiting
Elevator is waiting
Elevator is waiting
Elevator is waiting
Elevator is waiting



*/