
//Brandon Johnson
//Simulations Mid-Term


import java.util.*;
import java.text.DecimalFormat;

class Sim {

	// Class Sim variables
	public static double Clock, totalServiceTime, sumpartType1ServiceTime, sumpartType2ServiceTime, sumpartType3ServiceTime, lastArrivalTime,
		sumInterArrivalTimes, meanInterArrivalTime,totalTime, timeSinceLastArrival;

	public static int partType1Counter, partType2CallCounter, partType3CallCounter, totalparts, numDepartures, numparts, sixtyplus;
	
	public static int meanArr, spreadArr, meanpartType1, spreadpartType1, meanpartType2, spreadpartType2, meanpartType3, spreadpartType3;
	
	public static PriorityQueue<Event> parts;
	public static int numMachines, partsInQ, maxpartsInQ, numpartsInQ;

	public final static int arrival = 1;
	public final static int departure = 2;
	public final static int partType1 = 3;
	public final static int partType2 = 4;
	public final static int partType3 = 5;
	  
	// declare FEL, RNG
	public static EventList FutureEventList;
	public static Random stream;

	// main method
	public static void main(String argv[]) {

		// initialize RNG
		
		long seed = 12347;
		stream = new Random(seed);
		
		meanInterArrivalTime = 60;
		totalTime = 28800;

		// initialize the FEL
		FutureEventList = new EventList();

		// call Initialization method for remaining initializations
		Initialization();
		
		

		// loop until meet stopping condition
		while (Clock < totalTime) {
			Event evt = (Event) FutureEventList.getMin(); 	// get imminent event
			FutureEventList.dequeue(); 						// be rid of it
			Clock = evt.getTime(); 							// advance simulation time

			// determine event type and call handler
			if (evt.getType() == arrival)
				ProcessArrival(evt);
			else 											// if not Arrival, its a Departure
				ProcessDeparture(evt);
		}
		
		while(parts.size()>0){
			Event evt = (Event) FutureEventList.getMin();
			ProcessDeparture(evt);
		}

		// simulation has stopped call ReportGen method
		ReportGeneration();
	}

	// perform all initializations including FEL
	public static void Initialization() {
		Clock = 0.0;
		lastArrivalTime = 0.0;
		partType1Counter = 0;
		partType2CallCounter = 0;
		partType3CallCounter = 0;
		totalparts = 0;
		numDepartures = 0;
		
		totalServiceTime = 0;
		sumpartType1ServiceTime = 0;
		sumpartType2ServiceTime = 0;
		sumpartType3ServiceTime = 0;
		sumInterArrivalTimes = 0;

		meanpartType1 = 58;
		spreadpartType1 = 8;
		
		meanpartType2 = 55;
		spreadpartType2 = 9;
		
		meanpartType3 = 85;
		spreadpartType3 = 12;
		
		parts = new PriorityQueue<Event>();
		numMachines = 1;						// inc to find min number of ambulances to have 0 parts waiting
		partsInQ = 0;
		maxpartsInQ = 0;
		numpartsInQ = 0;

		// schedule first call arrival
		double arrivalTime = uniform(stream, meanArr, spreadArr);
		//Event evt = new Event(arrival, Clock + arrivalTime);
		Event evt = new Event(arrival, exponential(stream,meanInterArrivalTime)); 
		FutureEventList.enqueue(evt);
	}

	// EVENT HANDLER METHODS

	public static void ProcessArrival(Event evt) {
		totalparts++;
		
		if(parts.size() == 0) {			// no parts waiting
		  if (numMachines > 0) {
			  ScheduleDeparture(kindOfCall());
		  }
		  else {						// no ambulances available
			  parts.add(evt);	 
			  partsInQ++;
			  numpartsInQ++;
			  if (maxpartsInQ < partsInQ)
				  maxpartsInQ = partsInQ;
		  }
		}								// have parts waiting
		else {							
			parts.add(evt);
			partsInQ++;
			numpartsInQ++;
			  if (maxpartsInQ < partsInQ)
				  maxpartsInQ = partsInQ;
		}	  
		
		// schedule the next arrival
		Event next_arrival = new Event(arrival, Clock + exponential(stream, meanInterArrivalTime));
		FutureEventList.enqueue(next_arrival);
		
		// getting stats to verify interarrival modeling
		double timeSinceLastArrival = Clock - lastArrivalTime;
		sumInterArrivalTimes += timeSinceLastArrival;
		lastArrivalTime = Clock;
	}
	
	private static int kindOfCall() {
		// determine call type and schedule correct departure time
		double partType = stream.nextDouble();	// get random number to determine call type
				
		if (partType <= .5) {					// partType1 alarm = 15% of parts
			partType1Counter++;
			return partType1;
			
		} else if (partType > .5 && partType <= .80) {		// partType2 parts = 12.75% of parts
			partType2CallCounter++;
			return partType2;
			
		} else {												// rest of parts
			partType3CallCounter++;
			return partType3;
		}
	}

	public static void ScheduleDeparture(int partType) {
		double serviceTime;
		
		
		
		if (partType == partType1) {
			serviceTime = uniform(stream, meanpartType1, spreadpartType1);
			sumpartType1ServiceTime += serviceTime + 5;
			
			if (serviceTime > 60){
				sixtyplus++; 
			}
			
		}
		
		else if (partType == partType2) {
			serviceTime = uniform(stream, meanpartType2, spreadpartType2);
			sumpartType2ServiceTime += serviceTime + 5;
			
			if (serviceTime > 60){
				sixtyplus++; 
			}
		}
		
		else  {
			serviceTime = uniform(stream, meanpartType3, spreadpartType3);
			sumpartType3ServiceTime += serviceTime + 5;
			
			if (serviceTime > 60){
				sixtyplus++; 
			}
		}
		
		//double sixtyproportion = (sixtyplus/numDepartures);  
		
		totalServiceTime += serviceTime;
		
		Event depart = new Event(departure, Clock + serviceTime);
		FutureEventList.enqueue(depart);
		
		numMachines--;				// machine in service - dec number available
	}

	public static void ProcessDeparture(Event e) {
		
		numDepartures++;
		numMachines++;				// inc count - machine available
		
		if(parts.size() > 0) {			// parts waiting
			Event evt = parts.poll();
			partsInQ--;
			ScheduleDeparture(kindOfCall());
		}
		
	}
	

	// generate output
	public static void ReportGeneration() {
		
		
		// perform any math functions first
		double sixtyproportion =  (double) sixtyplus/numDepartures;  
		double percentpartType1 = (double) partType1Counter / numDepartures;
		double percentpartType2 = (double) partType2CallCounter / numDepartures;
		double percentpartType3 = (double) partType3CallCounter / numDepartures;
		double meanMachineTime =  (double) totalServiceTime / numDepartures;
		
		double hoursOfOperation = Clock / 60;
		double partType1ServiceTime = sumpartType1ServiceTime / partType1Counter;
		double partType2ServiceTime = sumpartType2ServiceTime / partType2CallCounter;
		double partType3ServiceTime = sumpartType3ServiceTime / partType3CallCounter;
		double avgInterArrivalTime = sumInterArrivalTimes / totalparts;
		
		DecimalFormat percent = new DecimalFormat("#0.0#%");
		DecimalFormat pretty = new DecimalFormat("#0.00#");

		// now structure the report
		System.out.println("Report");// always nice to have a title);
		
		
		System.out.println("\n" + percent.format(sixtyproportion) + " percent of parts take more than 60 for complete processing");
		
		System.out.println("\nAmount of Departures: " + numDepartures);
		System.out.println("\nAmount of arriving parts: " + totalparts);
		System.out.println("\nPercent of parts that were partType1: " + percent.format(percentpartType1));
		System.out.print("Amount of parts that were partType1: " + partType1Counter);
		
		System.out.println("\nPercent of parts that were partType2: " + percent.format(percentpartType2));
		System.out.println("Number of parts that were partType2: " + partType2CallCounter);
		System.out.println("Percent of parts that were Type3: " + percent.format(percentpartType3));
		
		System.out.println("Amount of parts that were Type3: " + partType3CallCounter);
		
		System.out.println("\nMean Service Time: " + pretty.format(meanMachineTime));
		
		System.out.println("\nAverage Interarrival time: " + pretty.format(avgInterArrivalTime)+ " seconds");
		System.out.println("\nAverage partType1 service time: " + pretty.format(partType1ServiceTime) + " seconds");
		System.out.println("\nAverage partType2 service time: " + pretty.format(partType2ServiceTime) + " seconds");
		System.out.println("\nAverage partType3 service time: " + pretty.format(partType3ServiceTime) + " seconds");
		
		System.out.print("\nMaximum number parts in queue was: " + maxpartsInQ);
		System.out.print("\nTotal amount parts that had to wait: " + numpartsInQ);
		System.out.print("\nNumber parts still in queue: " + parts.size());
		System.out.print("\nTotal time is " + Clock + " seconds");
	}

	// this method handles uniform random variates
	public static double uniform(Random rng, int m, int h) {
		int a = m - h;
		int b = m + h;
		
		return a + ((b - a) * rng.nextDouble());
	}

	// this method handles exponential random variates
	public static double exponential(Random rng, double mean) {
		return -mean * Math.log(rng.nextDouble());
	}

	// this method handles normal random variates
	// don't forget to test for negative values - which you don't want to use!
	public static double SaveNormal;
	public static int NumNormals = 0;
	public static final double PI = 3.1415927;

	public static double normal(Random rng, double mean, double sigma) {
		double ReturnNormal;
		// should we generate two normals?
		if (NumNormals == 0) {
			double r1 = rng.nextDouble();
			double r2 = rng.nextDouble();
			ReturnNormal = Math.sqrt(-2 * Math.log(r1)) * Math.cos(2 * PI * r2);
			SaveNormal = Math.sqrt(-2 * Math.log(r1)) * Math.sin(2 * PI * r2);
			NumNormals = 1;
		} else {
			NumNormals = 0;
			ReturnNormal = SaveNormal;
		}
		return ReturnNormal * sigma + mean;
	}
} // end class
