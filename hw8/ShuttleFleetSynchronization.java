//Assume there are 3 shuttles in the airport, and if a shuttle is already on one of the terminal, other shuttles will pass that stop and go to the next terminal. 
import java.util.concurrent.Semaphore;
import java.util.*;
public class ShuttleFleetSynchronization extends Thread{
  private int id;
  private String type;//"shuttle" or "passenger";
  public static volatile int[] waitingNumber = new int[6]; //assuming K = 6, waitingNumber[i] represents number of passagers waiting at terminal i.
  public static volatile Semaphore[] go = new Semaphore[]{new Semaphore(1), new Semaphore(1), new Semaphore(1)};//allow shuttle i to go when nobody waiting or the shuttle is full
  Random r = new Random();
  public static volatile int[] passangerNumOnBus = new int[3];//count the # of people on shuttle i
  public static volatile int[] currentTerminal = new int[3];//trace the current terminal shuttle i is on
  public static volatile Semaphore mutex1 = new Semaphore(1);//prevent more than one shuttle laoding or unloading at the same terminal
  public static volatile Semaphore mutex2 = new Semaphore(1);//provent more than one passsenger get on a shuttle at the same time
  public static volatile Semaphore[] terminal = new Semaphore[]{new Semaphore(0), new Semaphore(0),new Semaphore(0),new Semaphore(0),new Semaphore(0), new Semaphore(0)};//passenger at station i signal they are waiting for bus as they arrive. 
  public static volatile Semaphore[] counterProtector = new Semaphore[]{new Semaphore(1), new Semaphore(1),new Semaphore(1),new Semaphore(1),new Semaphore(1), new Semaphore(1)};
  
  public ShuttleFleetSynchronization(int i, String s){
    this.id = i;
    this.type = s;
  }
  
  public void acquire(Semaphore sem){
    try{
      sem.acquire();
    }
    catch(InterruptedException e){
    }
  }
  
  public void release(Semaphore sem){
    sem.release();
  }
  
  public static boolean isEmapty(int a){// check if a station already has a shuttle stopped, if yes, then the comming shuttle just pass if and do to the next station. 
    for (int i = 1; i < 3; i++) {
      if (currentTerminal[i] ==a) {
        return false;
      }
    }
    return true;
  }
  
  public static int findIndex(int a){// allow passenger to find index for the shuttle at where they are waiting. 
    for (int i = 0; i < 3; i++) {
      if (currentTerminal[i] == a) {
        return i;
      }
    }
    return 0;
  }
  
  public void run() {
    if(this.type.equals("passenger")){ 
      for(int k = 0; k < 5; k++){
        //passengers choose their start point
       int start = r.nextInt(6); 
        System.out.println("passenger " + this.id + " is waiting at terminal " + start);
        acquire(counterProtector[0]);
        waitingNumber[start]++;
        release(counterProtector[0]);
        acquire(terminal[start]);
        int index = findIndex(start);  
        //passengers get on one of the shuttle 
        acquire(mutex2);
       if(passangerNumOnBus[index] < 10)
          passangerNumOnBus[index] ++;            
        System.out.println("Passanger "+this.id+" is getting on shuttle " + index + " from terminal "+ start);
       acquire(counterProtector[1]);
        waitingNumber[start]--;
        release(counterProtector[1]);
        release(mutex2);
        if (passangerNumOnBus[index] >=10 || waitingNumber[start] <=0) {
          release(go[index]);
        } else {
          release(terminal[start]);
        }         
        //passengers choose their destination
        int end = r.nextInt(6);
        while (start == end) {
          end = r.nextInt(6);
        }
       System.out.println("Passanger " + this.id + " is going to terminal "+ end + " shuttle " + index);
        
        while (currentTerminal[index] != end) {
          System.out.print("");
        }
        //  passengers get off at their destination
        acquire(mutex2);
        if(passangerNumOnBus[index] > 0){
          acquire(counterProtector[2]);
          passangerNumOnBus[index] --;
          release(counterProtector[2]);
        }
        if (passangerNumOnBus[index]==0) {
          release(go[index]);
        }else{
          if(waitingNumber[currentTerminal[index]] <= 0)
            release(go[index]);            
        }
        mutex2.release();
        System.out.println("Passanger " + this.id + " is getting off the shuttle " + index);  
        rest();
    } 
    }else { // this thread is a shuttle
      for (int i =0; i <100; i++) {
        acquire(mutex1);
        if (!isEmapty(i%6)) {//the shuttle keep driving if a terminal has another shuttle. 
          release(mutex1);
          continue;
        }else{//the shuttle arrives other wise, check if they need to stop, stop, and driving away. 
          System.out.println("The shuttle " + this.id + " is arriving at Terminal " + currentTerminal[this.id] + ": " + passangerNumOnBus[this.id] + " people on the bus now. ");
          currentTerminal[this.id] = i%6;
          mutex1.release();
          if(waitingNumber[currentTerminal[this.id]] > 0){
            release(terminal[currentTerminal[this.id]]);
            acquire(go[this.id]);
          }
        }
       driving();
      }
    }    
  }
    
  public void driving(){
    try{
      Thread.sleep(100);
    }catch (InterruptedException e) {
    }
  }
  
    public void rest(){
    try{
      Thread.sleep(200);
    }catch (InterruptedException e) {
    }
  }
    
    
  public static void main(String[] args) 
  {  
    final int N = 51;
    ShuttleFleetSynchronization[] p = new ShuttleFleetSynchronization[N];
    for (int i = 0; i < N; i++)
    {
      if(i < 3)
        p[i] = new ShuttleFleetSynchronization(i, "shuttle");
      else
        p[i] = new ShuttleFleetSynchronization(i, "passenger");
        p[i].start();
      }
    }
}