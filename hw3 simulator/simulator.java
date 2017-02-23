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
public class simulator{



	// instances:
	private Double lambda;
	private Double ts;
	private Double simTime;
	private Double systemTime; 

	// requests queue(waiting queue)
	private LinkedList<Tuple> queueLine;

	// service status log
	//(server state with reques start time and end time)
	private LinkedList<Tuple> server;
	// the current service 
	private Tuple curService;

	// constructors:
	public simulator(Double lmd, Double serviceT, int simT){
		this.lambda = lmd;
		this.ts = serviceT;
		this.simTime = 1.0*simT;
		
		//init queueLine
		this.queueLine = new LinkedList<Tuple>();

		//init server status
		this.server = new LinkedList<Tuple>();
		this.curService = new Tuple(0.0, 0.0);

	}

	// run function
	public void run() {
		controller();
	}

	// controller
	private void controller() {
		// set systemTime=0
		this.systemTime =0.0;
		// init the requests schedule
		init();
		// start service the program

		while(this.systemTime<this.simTime){
			//get the next event from the schedule
			if(queueLine.peek()==null) {break;}
			Tuple request= queueLine.remove();

			

			// adjust the current lock
			// add the arrival time to system time,
			// then according to the server schedule decide 
			// birth or death or monitor to change the server log
			// (which is our main goal to see how server works)
			
			// now, the systemTime is the request arrival time in
			// the system clock
			this.systemTime += request.x;

			System.out.print("IAT: "+request.x+" Ts: "+request.y
				+" Arrival time: "+ this.systemTime);

			birth(request);
			System.out.println(" start: "+this.curService.x+" end: "+
				this.curService.y);
			System.out.println();

		}



	}
	// initial function
		// gives a schedule of requests with poisson IAT and
		// exponential service time
	private void init() {
		Double schedulePeriod = 0.0;
		while(schedulePeriod<=this.simTime){
			Double arrivalTime = expoArrival();
			Double serviceTime = expoService();
			Tuple request = new Tuple(arrivalTime,serviceTime);
			this.queueLine.add(request);
			schedulePeriod += arrivalTime;

		}

		// the service status start from (0,0)
		// service status for each request (start, end)
		this.server.add(new Tuple(0.0,0.0));
	}





	// birth function
	private void birth(Tuple request) {
		// check if the server is empty
		if(this.curService.x== 0.0 && 
			this.curService.y==0.0) {

			// first request
			this.curService = new Tuple(request.x,request.x+request.y);
		}

		else if(this.curService.y<=this.systemTime){
			this.curService = new Tuple(this.systemTime, this.systemTime+request.y);
		}

		else{
			// the new service:
			// starts from the the end of curService
			// ends in start time(curService end time) + itself Ts
			// update the curService
			this.curService = new Tuple(this.curService.y,this.curService.y+request.y);
			
		}

		this.server.add(this.curService);
	}

	// death function
	private void death() {

	}

	// monitor function
	private void monitor() {

	}

//Done
	// poisson random generator
		// give one poisson value
	private Double expoArrival() {
		// we know lambda, but we dont know the mean
		// mean = 1/lambda 
		// same equation as the one in expoService
		return (-1.0/this.lambda)*Math.log(1.0-Math.random());
	}


//Done
	// exponential random generator 
		// give one expo value
	private Double expoService() {
		// lambda in exponential is the arrival rate
		// lambda is the number of events per unit time
		// so the mean=ts=1/lambda, so lambda = 1/ts
		// lambda = 1.0/mean = 1.0/ts;
		// (-1.0/lambda)*ln(1-y) = -ts * ln(1-y)
		return (-this.ts) * Math.log(1.0-Math.random());
	}


	// maybe need some functions to print
	// the results


	// set the queue be empty
	private void clean() {

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





























































































































































