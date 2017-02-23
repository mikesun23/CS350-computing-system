import java.util.concurrent.Semaphore;
import java.util.*;
public class ShuttleSynchronization extends Thread{
  private int id;
  private String type;//"shuttle" or "passenger";
  public static volatile int[] waitingNumber = new int[6]; //assuming K = 6, waitingNumber[i] represents number of passagers waiting at terminal i.
  public static volatile int[] getOffNumber = new int[6];//assuming K = 6, getOffNumber[i] represents number of passagers getting off at terminal i.
  public static volatile Semaphore go = new Semaphore(0);//allow bus to go when nobody waiting or the shuttle is full
  Random r = new Random();
  public static volatile int passangerNumOnBus = 0;//count the # of people on shuttle
  public static volatile int currentTerminal = 0;//trace the current terminal
  public static volatile Semaphore[] mutex = new Semaphore[]{new Semaphore(1), new Semaphore(1), new Semaphore(1)};//protect the counter
  public static volatile Semaphore[] terminal = new Semaphore[]{new Semaphore(0), new Semaphore(0),new Semaphore(0),new Semaphore(0),new Semaphore(0), new Semaphore(0)};//passenger at station i signal they are waiting for bus as they arrive. 
  
  public ShuttleSynchronization(int i, String s){
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
  
  public void run(){    
    if(this.type.equals("passenger")){
      for(int k = 0; k < 10; k++){
        //passengers choose their start point
        int start = r.nextInt(6);
        System.out.println("passenger " + this.id + " is waiting at terminal " + start);
        acquire(mutex[0]);
        waitingNumber[start]++;
        release(mutex[0]);
        acquire(terminal[start]);
        System.out.println("Passanger "+this.id+" is getting on bus from terminal "+ start);
        acquire(mutex[1]);
        passangerNumOnBus++;
        release(mutex[1]);
        //passengers get on the shuttle
        acquire(mutex[2]);
        waitingNumber[start]--;
        release(mutex[2]);
        if(passangerNumOnBus >= 10 || waitingNumber[start] == 0){
          release(go);
        }else{
          release(terminal[start]);
        }
        //passengers choose their destination
        int end = r.nextInt(6);
        while(end == start)
          end = r.nextInt(6);
        getOffNumber[end]++;
        System.out.println("Passanger " + this.id + " is going to terminal "+ end);
        //     passengers get off at their destination
        while (currentTerminal != end) { System.out.print("");};
        passangerNumOnBus--;
        System.out.println("Passanger " + this.id + " is getting off the shuttle.");
      }
    }
    
    if(this.type.equals("shuttle")){
      for(int i = 0; i < 100; i++){
        currentTerminal = i % 6;
        System.out.println("The shuttle is at Terminal " + currentTerminal + ": " + passangerNumOnBus + " people on the bus now. ");
        if(waitingNumber[currentTerminal] != 0){
          if(getOffNumber[currentTerminal] > 0)
            getOffNumber[currentTerminal]= 0;
          release(terminal[currentTerminal]);//The shuttle arrives and open the door, if needed, 
          acquire(go);//The shuttle leaves
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
  public static void main(String[] args) 
  {  
    final int N = 51;
    ShuttleSynchronization[] p = new ShuttleSynchronization[N];
    for (int i = 0; i < N; i++)
    {
      if(i == 0)
        p[i] = new ShuttleSynchronization(i, "shuttle");
      else
        p[i] = new ShuttleSynchronization(i, "passenger");
        p[i].start();
      }
    }
}
            
    