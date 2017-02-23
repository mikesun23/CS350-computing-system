/*
PROBLEM INSTRUCTION:

two parts:
1. controller
	1. current simulated time
	2. schedule of future events(linked list)
	* each event has a timestamp, and a pointer to the 
	  functions that needs to be executed at that time.

	controller logic:
	a. set t=0; and call a function to initialize the simulated
		world and the schedule.
		* so here should be a initializing function to generate
		  all the random events, the arrival time, service time
		  two numbers: arrival time & service time
	b. while t is less than the desired time of the simulation
		1. get the next event from the schedule
		2. adjust the current time to be the time of that event, and
		   * T needs to add the arrival time

		3. call the function that needs to be executed to reflect
			the occurence of that event

	c. call a function that prints the results of the simulation,
		etc.
		three functions: birth, death, monitor events

2. a simulated system(world)
	consists: data structures that represents the state of the
	'world'. for M/M/1 is just a single queue. a record of pending
	requests for that queue and other info(stats)

	birth & death events functions:
	need to figure out what need to be done when called
	record important information. service start time, end time

	logic of these two functions
	logic of how to initialize the simulated system

*/



/*

IMPLEMENTATION:
general picture:

structure:
write a whole simulator class as an object,
construc the new simulator object.

time line:
we can just use the number to represent the time.

measuring goal:
a record of pending requsts and arrival time, service start/end





################################################
Outside functions of the object:

1. constructor
	the construtor takes three things: 
	(lambda, service time, simulation time) 

2. run function
	then call the run function to run the simulation
	and print out the running statistical results

	* keep

* that is pretty much for the outside world.

################################################
Inside functions of the object:

implement two parts:

	controller part:
		function call flow:
			run function -> controller function -> init function

		control function:
			set t=0
			call init() //generate all random requests with arrival
			            //time and service time
			while(t<testTime) :
				get the next event from the schedule
				adjust the current time(clock)
				call birth/death function
				call monitor function
				* record the log
				monitor function
			call printResult() //print out final results


		birth function:
			add new request to list
			start/not start the service
			schedule a death event// by generating a random service time
			schedule a birth event// by generating a random IAT
			return the service time to the controller




		death event:
			remove the death event from the list
			compute statistics (find q(response time))
			print some information
			check & service the next event(random service time)

		monitor function:
			take the snapshot of the log
			and record the state of simulator
			writing values to a file

		printResult function:
		* don't think about the stats and print results for now
		* just think how to make the simulation	

*/


import java.util.*;
import java.lang.*;



public class MM1simulator {

	// instances:
	private Double lambda;
	private Double ts;
	private Double simTime;
	private Double systemTime;

	private LinkedList<Tuple> queueLine;
	private LinkedList<Tuple> serviceLog;
	private Tuple curService;

	// constructor:
	public simulator(Double lmd, Double serviceT, int simT) {
		this.lambda = lmd;
		this.ts = serviceT;
		this.simTime = 1.0*simT;
	}

	// controller:
	private void controller(){
		this.systemTime = 0.0;
		init();

		// this is the server
		while(this.systemTime<=simTime) {
			// get the next event from the queue
			Tuple request;
			if(queueLine.size()>0) {
				request=queueLine.peek();
			}
			else {
				request = new Tuple(expoArrival(),expoService());

			}
			// adjust the systemTime
			this.systemTime += request.x;

			// decide function to call
			if(serving()) {
				death(request);
			} else {
				birth(request);
			}
		}
	}

	private Boolean serving() {
		if(this.systemTime>=this.curService.y) {
			return false;
		}
		return true;
	}

	private void init() {
		this.queueLine = new LinkedList<Tuple>();
		this.serviceLog = new LinkedList<Tuple>();
		this.curService = new Tuple(0.0,0.0);
	}

	// add the request to the queue
	private void birth(Tuple request) {
		// first add the request to the queue 
		queueLine.add(request);
		// if the server is idling
		this.curSercice = new Tuple(this.systemTime, this.systemTime+request.y);
		death();


	}

	// remove the request from the queue and serve it
	private void death() {
		// the predicted ending time of curService is later 
		// than the new request

		//complete the current request
		Tuple request = queueLine.remove();
		this.curService = new Tuple(this.curService.y,this.curService.y+request.y);
		


	}



	// get the request arrival time
	private Double expoArrival() {
		return (-1.0/this.lambda)*Math.log(1.0-Math.random());

	}

	// get the request service time
	private Double expoService() {
		return (-this.ts) * Math.log(1.0-Math.random());
	}



	public class Tuple {
		public final Double x;
		public final Double y;
		public Tuple(Double x, Double y) {
			this.x =x;
			this.y =y;
		}
	}




















































































}





















