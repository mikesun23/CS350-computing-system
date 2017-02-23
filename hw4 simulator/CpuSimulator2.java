
/*
************** Running Instruction:*******************

Just run the TestSimulator.java file, 
which is also uploaded with this file.

construct new CpuSimulator object with 5 parameters:
	(total lambda, CPU_ts, disk_ts, net_ts, simulation time) 

Then call Object.cpuController() function to run the simulation 
and print out all relative statistic result.




*/





import java.util.*;
import java.util.Random;
import java.util.ArrayList;


public class CpuSimulator2 {


	public static final int BIRTH =0;
	public static final int DEATH =1;


	// all arrival and depatures
	// [cpuA,cpuD,diskA,diskD,netA,netD]
	private Double[] nextList;


		// system instances
	private Double systemTime;
	private Double lambda; // generate the natural arrival
	private Double simulationPeriod;

		// cpu instances
	private Double avgTs;  // cpu service time 
	private LinkedList<Event> queueLine;  
	
		// disk instances
	private LinkedList<Event> diskQueue;
	private Double diskTs;

		// net instances
	private LinkedList<Event> netQueue;
	private Double netTs;


	// monitor instances
	private int numMonitors;
	private Double monitorLambda;
	private ArrayList<Double> monitorPoints; 

		// stats instances
	private Double tq, tw, ts;
	private int cpuQ, cpuW;
	private int cpuLambda, diskLambda, netLambda;

	private int totalRequests;


	private ArrayList<Integer> cpuwait;
	private ArrayList<Double> waitTime;

	private Double currentTq;

	// simulator constructor
	public CpuSimulator2(Double lmd, Double cpuTs, Double diskTs, Double netTs, int simT) {
		this.lambda = lmd;
		this.avgTs = cpuTs;
		this.diskTs = diskTs;
		this.netTs = netTs;
		this.simulationPeriod = 1.0*simT;
		this.monitorLambda = this.lambda*2;
	}

	public void init() {
		this.monitorPoints = new ArrayList<Double>();
		double t =0.0;

		// generate the whole monitor point list
		while(t<2.1*this.simulationPeriod) {
			this.numMonitors++;
			t+=monitorExpoArrival();
			this.monitorPoints.add(t); // exclude time 0.0
			                             // start from first arrival
		}

		// system instance and all queues
		this.systemTime =0.0;
		this.queueLine = new LinkedList<Event>();
		this.diskQueue = new LinkedList<Event>();
		this.netQueue = new LinkedList<Event>();

		this.cpuwait = new ArrayList<Integer>();
		this.waitTime = new ArrayList<Double>();

		// all arrival and depature schedule table
		nextList = new Double[7];
		for(int i=0; i<nextList.length; i++) {
			nextList[i] = Double.POSITIVE_INFINITY;
		}

		// first init cpu natural arrival and depature
		nextList[0] = expoArrival();
		nextList[1] = Double.POSITIVE_INFINITY;
		nextList[6] = monitorPoints.remove(0);

		// monitor instances:
		this.tq = 0.0;
		this.tw = 0.0;
		this.ts = 0.0;

		this.cpuQ = 0;
		this.cpuW = 0;

		this.cpuLambda =0;
		this.diskLambda =0;
		this.netLambda =0;

		this.totalRequests = 0;

		this.currentTq = 0.0;

		


	}

	public void monitor() {
		int currentQ = this.queueLine.size();
		int currentW = (currentQ>0) ? (this.queueLine.size()-1) : 0;
			

			// this.cpuwait.add(currentW);
			// this.waitTime.add(this.systemTime);
			
			// System.out.println("\t"+currentW+" "+this.systemTime);


		this.cpuW += currentW;
		this.cpuQ += currentQ;
		this.monitorPoints.remove(0);
		this.nextList[6] = this.monitorPoints.get(0);

		this.cpuwait.add(currentW);
		this.waitTime.add(this.systemTime);
		//System.out.println("\t"+currentW+" "+this.systemTime);

	}

	public void cpuController() {
		init();
		int counter =0;

		//System.out.println("\tw\ttime");



		while(this.systemTime<2*this.simulationPeriod) {

			ArrayList<Double> list = new ArrayList<Double>(Arrays.asList(this.nextList));
			int minIndex = list.indexOf(Collections.min(list));
			Double nextEventTime = this.nextList[minIndex];

			if(minIndex==0) {
				// cpu natural arrival
				this.systemTime = nextEventTime;
				cpuBirth(nextEventTime);
			}
			else if(minIndex==1) {
				// cpu depature
				this.systemTime = nextEventTime;
				cpuDeath();
			}
			else if(minIndex==2) {
				// disk arrival from cpu depature
				this.systemTime = nextEventTime;
				diskBirth(nextEventTime);
			}
			else if(minIndex==3) {
				// disk depature 
				// 1. back to cpu, 2. stop
				this.systemTime = nextEventTime;
				diskDeath();
			}
			else if(minIndex==4) {
				// net arrival
				this.systemTime = nextEventTime;
				netBirth(nextEventTime);
			}
			else if(minIndex==5) {
				// net depature
				this.systemTime = nextEventTime;
				netDeath();
			}
			else if(minIndex==6) {
				// cpu mointor event
				this.systemTime = nextEventTime;
				monitor();
			}

			// counter++;
			// if(counter>1000) {
			// 	printList();
			// 	break;
			// }	
		}
		
		printResult();

		// while(!cpuwait.isEmpty()) {
		// 	System.out.println(cpuwait.remove(0));

		// }
		// while(!waitTime.isEmpty()) {
		// 	System.out.println(waitTime.remove(0));
		// }

		// System.out.println("hell yeah!");

	}


	// print all the result
	public void printResult() {
		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println("Total tq, ts, tw: ");
		System.out.println("\tTotal Requests " + this.totalRequests);
		System.out.println("\tTotal Tq is "+ this.tq);
		System.out.println("\tTotal Tw is "+ this.tw);
		System.out.println("\tTotal Ts is "+ this.ts);
		System.out.println();
		System.out.println("All device arrival rate: ");
		System.out.println("\tCPU  lambda: "+(this.cpuLambda/this.simulationPeriod));
		System.out.println("\tDisk lambda: "+(this.diskLambda/this.simulationPeriod));
		System.out.println("\tNet  lambda: "+(this.netLambda/this.simulationPeriod));
		System.out.println();
		System.out.println("Average tq, ts, tw: ");
		System.out.println("\tAverage Tq(turnaround): "+(this.tq/this.totalRequests));
		System.out.println("\tAverage Tw: "+ (this.tw/this.totalRequests));
		System.out.println("\tAverage Ts: "+(this.ts/this.totalRequests));

		System.out.println();
		System.out.println();
		System.out.println();
	}


	// check all devices' nextArrival and next Depature time
	public void printList() {
		for(int i =0; i<nextList.length;i++) {
			System.out.println("nextList["+i+"]: "+nextList[i]);

		}
		System.out.println();
	}


	public void cpuBirth(Double time) {
		if(this.queueLine.isEmpty()) {
			cpuScheduleDeath(time);
		}
		else {
			this.queueLine.add(new Event(time,BIRTH));
		}

		this.nextList[0] += expoArrival();

	}


	public void cpuDeath() {
		int destination = cpuExit();
		Event event = this.queueLine.remove();

		if(this.systemTime>this.simulationPeriod) {
			this.cpuLambda++;
		}


		//************* CPU EXIT !!!!!!!!!!!! **************
		if(destination==0) {
			//exit the system, just remove this request
			
			if(this.systemTime>this.simulationPeriod) {
				this.totalRequests++;
				
				
			}

			// System.out.println(this.tq-this.currentTq);
			// this.currentTq = this.tq;
		} else if(destination ==1) {
			// current request goes to disk

			this.nextList[2] = event.getTime(); // disk arrival time
		} else {
			this.nextList[4] = event.getTime();
		}

		if(!this.queueLine.isEmpty()) {
			Event next = queueLine.remove();
			cpuScheduleDeath(next.getTime());
		} else {
			this.nextList[1] = Double.POSITIVE_INFINITY;
		}
		
	}


	public void cpuScheduleDeath(Double arrivalTime) {
		this.nextList[1] = this.systemTime + expoService();
		this.queueLine.addFirst(new Event(this.nextList[1], DEATH));

		if(this.systemTime>=this.simulationPeriod) {
			Double tss = this.nextList[1]-this.systemTime;
			Double tww = this.systemTime -arrivalTime;
			this.tw += tww;
			this.ts += tss;
			this.tq += (tss+tww);
		}
	}

	public void diskBirth(Double time) {
		if(this.diskQueue.isEmpty()) {
			diskScheduleDeath(time);
		} else {
			this.diskQueue.add(new Event(time, BIRTH));
		}
		this.nextList[2] = Double.POSITIVE_INFINITY;

		// no need to update next disk arrival, because 
		// it is from the cpu death event, from there
		// the next disk arrival time is already set
	}

	public void diskDeath() {
		int destination = diskExit();
		Event event = this.diskQueue.remove();
		// if(this.systemTime>this.simulationPeriod) {
		// 	this.totalRequests++;
		// }
		if(this.systemTime>this.simulationPeriod) {
			this.diskLambda++;
		}
		if(destination==0) {
			// go to net
			if(this.netQueue.isEmpty()) {
				netScheduleDeath(event.getTime());

			}
			else {
				this.netQueue.add(new Event(event.getTime(),BIRTH));
			}

		} else {
			// disk back to cpu arrival
			// diskDeath means that this event is the earliest
			// event in the system, so it is eariler than the
			// cpu arrival, so this is the new birth for cpu
			if(this.queueLine.isEmpty()) {
				cpuScheduleDeath(event.getTime());
			}
			else {
				this.queueLine.add(new Event(event.getTime(),BIRTH));
			}
		}
		if(!this.diskQueue.isEmpty()) {
			Event next = this.diskQueue.remove();
			diskScheduleDeath(next.getTime());
		}
		else {
			this.nextList[3] = Double.POSITIVE_INFINITY;
		}


	}

	public void diskScheduleDeath(Double arrivalTime) {
		this.nextList[3] = this.systemTime + diskExpoService();

		this.diskQueue.add(new Event(this.nextList[3],DEATH));

		if(this.systemTime>=this.simulationPeriod) {
			Double tss = this.nextList[3]-this.systemTime;
			Double tww = this.systemTime -arrivalTime;
			this.tw += tww;
			this.ts +=tss;
			this.tq += (tss+tww);
		}
	}

	public void netBirth(Double time) {
		if(this.netQueue.isEmpty()) {
			netScheduleDeath(time);
		} else {
			this.netQueue.add(new Event(time, BIRTH));
		}
		this.nextList[4] = Double.POSITIVE_INFINITY;
	}

	public void netDeath() {
		Event event = this.netQueue.remove();
		// if(this.systemTime>this.simulationPeriod) {
		// 	this.totalRequests++;
		// }
		if(this.systemTime>this.simulationPeriod) {
			this.netLambda++;
		}
		if(this.queueLine.isEmpty()) {
			cpuScheduleDeath(event.getTime());
		} else {
			this.queueLine.add(new Event(event.getTime(), BIRTH));
		}
		if(!this.netQueue.isEmpty()) {
			Event next = this.netQueue.remove();
			netScheduleDeath(next.getTime());
		} 
		else {
			this.nextList[5] = Double.POSITIVE_INFINITY;
		}
	}

	public void netScheduleDeath(Double arrivalTime) {
		this.nextList[5] = this.systemTime + netExpoService();

		this.netQueue.add(new Event(this.nextList[5],DEATH));

		if(this.systemTime>=this.simulationPeriod) {
			Double tss = this.nextList[5]-this.systemTime;
			Double tww = this.systemTime -arrivalTime;

			this.tw += tww;
			this.ts += tss;
			this.tq += (tss+tww);
		}
	}


	public int cpuExit() {
		Double pb = Math.random();

		// exit the system
		if(pb>=0 && pb<0.5) {
			return 0;
		} 
		// arrive to the disk
		else if(pb>=0.5 && pb<0.6) {
			return 1;
		}
		// arrive to the net
		else {
			return 2;
		}
	}

	public int diskExit() {
		Double pb = Math.random();
		if(pb<0.5) {
			return 0;
		}
		return 1;
	}



	private Double expoArrival() {
		return (-1.0/this.lambda)*Math.log(1.0-Math.random());

	}

	private Double monitorExpoArrival() {
		return (-1.0/this.monitorLambda)*Math.log(1.0-Math.random());
	}

	// become uniformally distributed  between 0.001 and 0.039
	private Double expoService() {
		Double x = Math.random();
		Double y = x*39+1;
		//System.out.println("\tCPU uniform: "+y);
		if(y>39) {
			return 39.0/100;
		}
		else {
			return y/100;
		}
	}

	// become normally distributed with mean 100, and sd 30
	private Double diskExpoService() {
		Double zScore = 0.0;
		for(int i=0; i<100; i++) {
			zScore += Math.random();
		}
		// the real zScore by shiftting the mean by 1 to the left 
		//make it perfect indicator of zScore.
		zScore = zScore/100 -1.0;

		Double z = 0.1+zScore*0.03;
		//System.out.println("\tdisk normal: "+z);
		return z;

	}


	private Double netExpoService() {
		return (-this.netTs) * Math.log(1.0-Math.random());
	}




	// event class
	public class Event {
		private int id;
		private int floor;

		public Event(int i, int f) {
			this.id = i;
			this.floor = f;
		}

		public int getId() {
			return this.id;
		}

		public int getRequestedFloor() {
			return this.floor;
		}

		public String toString() {
			return (id+' '+floor);
		}
	}



}





