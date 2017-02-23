

import java.util.*;
import java.util.Random;
import java.io.*;


public class MM1K_Simulator {

	public static final int BIRTH =0;
	public static final int DEATH =1;

	// instances
	
		// input instances
	private Double lambda;
	private Double avgTs;
	private Double simulationPeriod;




		// K value
	private int queueSize;
	private int totalRejects;
	
	



		// system instances
	private Double systemTime;
	private Double nextArrival;
	private Double nextDepature;
	private int numMonitors;                       

		// storage instances
	private LinkedList<Event> queueLine;       
	private ArrayList<Double> monitorPoints;    

		// stats instances
	private Double tq, tw, ts;
	private int w, q;
	private int totalRequests, serviced;

	// simulator constructor
	public MM1K_Simulator(Double lmd, Double serviceT, int simT, int k) {
		this.lambda = lmd;
		this.avgTs = serviceT;
		this.simulationPeriod = 1.0*simT;
		this.queueSize = k;
	}





	public void controller() {
		init();


		// the whole process
		// system needs warmup so use 2*simulationPeriod
		while(this.systemTime<2*this.simulationPeriod) {
			// do the fucking simulation
			if(this.monitorPoints.get(0)<this.nextArrival && this.monitorPoints.get(0)<this.nextDepature) {

				// update system time
				this.systemTime=this.monitorPoints.get(0);

				// call monitor function
				monitor();

				// if next request is before the next depature
			} else if(this.nextArrival <=this.nextDepature) {

				// update system time first
				this.systemTime = this.nextArrival;
				//System.out.println("\tarrival at "+this.systemTime);

				// call birth function
				birth(this.nextArrival);

			} else {
				// update system time 
				this.systemTime=this.nextDepature;
				//System.out.println("\tdepature at " +this.systemTime);

				death();
			}
		}

		printStats();
	}




	// birth function
	public void birth(Double time) {
		// if no one in the queue, serve the request immediately
		if(this.queueLine.isEmpty()) {
			scheduleDeath(time);
		} else {
			// add a new event to the queue line
			if(queueLine.size()<this.queueSize) {
				this.queueLine.add(new Event(time, BIRTH));
				
			} else {
				totalRejects++;
			}
		}
		// schedule a next arrival time
		this.nextArrival += expoArrival();
	}

	// death function
	public void death() {
		// remove the request from the queue since it is been serviced
		this.queueLine.remove();
		this.totalRequests++;


		// if queue is not empty
		if(!queueLine.isEmpty()) {
			// schedule the next one in the queue a death
			Event next = queueLine.remove();
			scheduleDeath(next.getTime());
		} else {
			// if the queue line is empty, like the init state,
			// generate the first request with infinit depature time
			//this.nextDepature =Double.POSITIVE_INFINITY;
			//birth(this.nextArrival);
			//this.nextArrival += expoArrival();
			scheduleDeath2(this.nextArrival);
			this.nextArrival += expoArrival();
		}
	}


	public void scheduleDeath2(Double arrivalTime) {
		// generate the service time and get the next depature time

		Double serviceTime = expoService();
		this.nextDepature = arrivalTime + serviceTime;

		this.systemTime = arrivalTime;
		// make the scheduled death request the first in the queue
		this.queueLine.addFirst(new Event(nextDepature,DEATH));
		



		

		// update statistic values
		if(this.systemTime>=this.simulationPeriod) {	
			tq += serviceTime;
			tw += 0;
			ts += serviceTime;
			this.serviced ++;
		}

	}
	// schedule the death function
	public void scheduleDeath(Double arrivalTime) {
		// generate the service time and get the next depature time
		this.nextDepature = this.systemTime + expoService();


		// make the scheduled death request the first in the queue
		this.queueLine.addFirst(new Event(nextDepature,DEATH));
		

		

		// update statistic values
		Double thisTw =this.systemTime-arrivalTime;
		Double thisTs = this.nextDepature-systemTime;

		if(this.systemTime>=this.simulationPeriod) {
			tw += thisTw;
			ts += thisTs;
			tq += (thisTw+thisTs); 
			this.serviced ++;
		}

	}

		// need to be called exponentially
		// according to the random expo value, take the snap shot
		// of the system
	public void monitor() {
		if(this.systemTime >= this.simulationPeriod) {
			int currentQ = this.queueLine.size();
			int currentW = (currentQ>0) ? (this.queueLine.size()-1) : 0;
			this.w += currentW;
			this.q += currentQ;
			this.monitorPoints.remove(0);
			//
			//System.out.println("monitorPoint: "+this.systemTime);
			//System.out.println("\tnumber of waiting(w): "+currentW);
		}
		else {
			this.monitorPoints.remove(0);
		}

	}
	// init all variables
	public void init() {
		this.monitorPoints = new ArrayList<Double>();
		double t =0.0;

		// generate the whole monitor point list
		while(t<2*this.simulationPeriod) {
			this.numMonitors++;
			t+=expoArrival();
			this.monitorPoints.add(t); // exclude time 0.0
			                             // start from first arrival
		}
		this.systemTime=0.0;
		this.queueLine = new LinkedList<Event>();


		// first arrival and depature
		this.nextArrival = expoArrival();
		this.nextDepature = Double.POSITIVE_INFINITY; // no depatures init
		this.tq=0.0; this.ts=0.0; this.tw=0.0;
		this.w=0; this.q=0;
		this.totalRequests=0;
		this.serviced=0;
		this.totalRejects=0;

	}

	private void printStats() {
		// out.println("\nSTATISTICS OF RUN");
  //   	out.println("w = " + w/numChecks);
  //   	out.println("q = " + q/numChecks);
  //   	out.println("Tw = " + Tw/requests);
  //   	out.println("Tq = " + Tq/serviced);
  //   	out.println("Ts = " + Ts/serviced);

    	System.out.println("STATISTICS OF RUN");
    	System.out.println("requests: " + totalRequests/2);
    	System.out.println("reject probability: " + 1.0*totalRejects/totalRequests);
    	System.out.println("serviced: " + serviced);
    	System.out.println("utilization: "+this.lambda*ts/serviced);
    	System.out.println("w = " + tw/serviced*this.lambda);
    	System.out.println("q = " + 1.0*q/serviced);
    	System.out.println("Tw = " + tw/serviced);
    	System.out.println("Tq = " + tq/serviced);
    	System.out.println("Ts = " + ts/serviced);

    	System.out.println();
    	System.out.println();


	}


	// random arrival / service time generator
	private Double expoArrival() {
		return (-1.0/this.lambda)*Math.log(1.0-Math.random());

	}

	private Double expoService() {
		return (-this.avgTs) * Math.log(1.0-Math.random());

	}




	// event class
	public class Event{
		private double time;
		private int type;

		public Event(double t, int e) {
			this.time =t;
			this.type =e;
		}

		public double getTime() {
			return time;
		}

		public int getType() {
			return type;
		}
	}













}









