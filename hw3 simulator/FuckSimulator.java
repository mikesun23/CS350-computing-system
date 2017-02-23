


import java.util.*;
import java.util.Random;
import java.io.*;


public class FinalSimulator {

	public static final int BIRTH =0;
	public static final int DEATH =1;

	// instances
	
		// input instances
	private Double lambda;
	private Double avgTs;
	private Double simulationPeriod;
	
		// system instances
	private Double systemTime;
	private Double nextArrival;
	private Double nextDepature;
	private int numMonitors;                       //????????

		// storage instances
	private LinkedList<Event> queueLine;       
	private ArrayList<Double> monitorPoints;    // ???????

		// stats instances
	private Double tq, tw, ts;
	private int w, q;
	private int totalRequests, serviced;

	// simulator constructor
	public FinalSimulator(Double lmd, Double serviceT, int simT) {
		this.lambda = lmd;
		this.avgTs = serviceT;
		this.simulationPeriod = 1.0*simT;
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

	}


	public void controller() {
		init();


		while(this.systemTime<2*this.simulationPeriod) {



			if(this.monitorPoints.get(0)<this.nextArrival && this.monitorPoints.get(0)<this.nextDepature) {
				this.systemTime=this.monitorPoints.get(0);









			} else if(this.nextArrival <=this.nextDepature) {



				this.systemTime = this.nextArrival;
				birth(this.nextArrival);
				


				this.totalRequests++;










			} else {


				this.systemTime=this.nextDepature;
				death();


			}
		}


	}

	public void birth(Double time) {

	}



	public void death() {

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









