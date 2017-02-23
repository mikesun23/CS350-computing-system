import java.util.concurrent.Semaphore;
import java.util.*;
public class TunnelSynchronization extends Thread{
  private int id;
  public static volatile Semaphore[] mutex = new Semaphore[]{new Semaphore(1), new Semaphore(1)};//protect the counter
  public static volatile Semaphore available = new Semaphore(4);//at most 4 cars are allowed in tunnel, so initialize to 4
  public static volatile Semaphore turn = new Semaphore(1);//the cars go one side has to check for turn first,then the first car grab the lock. 
  public static volatile Semaphore lock = new Semaphore(1);//the cars go the other side grab the turn first by the first car, and check for lock. 
  public static volatile int count[] = new int[2];//count the current cars in tunnel
  public TunnelSynchronization(int i){
    this.id = i;
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
    Random r = new Random();
    for (int k =0; k< 10;k++){
      String direction = "";
      if(r.nextDouble() < 0.5){
        direction = "going forward";
      }
      else{
        direction = "going backward";
      }
      
      if(direction == "going forward"){
        System.out.println("Vehicle " + this.id + " is requesting " + direction + " into the tunnel");
        acquire(turn);// every car going forward need the "turn" semaphore
        acquire(mutex[0]);
        count[0]++;
        if(count[0] == 1)
          acquire(lock);//the first car lock the tunnel, prevent cars from the other side
        release(turn);//turn check done
        release(mutex[0]);
        acquire(available);//see if the tunnel can accomodate more cars
        //in tunnel(critical section)
        System.out.println("Vehicle " + this.id + " is in tunnel (" + direction + " )");
        delay();
        //critical section done
        release(available);
        System.out.println( "Vehicle " + this.id + " exits the tunnel");
        acquire(mutex[0]);
        count[0]--;
        //the last car unlock the tunnel
        if(count[0] == 0){  
          release(lock);
        }
        release(mutex[0]);
      }
      
      if(direction == "going backward"){
        System.out.println("Vehicle " + this.id + " is requesting " + direction + " into the tunnel");
        //this is the unpopular side, to prevent starvation, just grab the turn semaphore to prevent cars from other side
        acquire(mutex[1]);
        count[1]++;
        if(count[1] == 1)
          acquire(turn);
        release(mutex[1]);
        //wait for cars from the other side go out, then lock the tunnel
        acquire(lock);
        //require to enter the tunnel, if permitted
        acquire(available);
        //before enter the tunnel, unlock it, so cars from this side can enter, but form the other side, cars can'tenter because then didn't have turn
        release(lock);
        System.out.println("Vehicle " + this.id + " is in tunnel (" + direction + " )");
        delay(); 
        System.out.println( "Vehicle " + this.id + " exits the tunnel");
        //release a position
        release(available);
        acquire(mutex[1]);
        count[1]--;
        //the last car release the turn, let cars from the other side go. Assume this side is unpopular, cars from the other side will not be starved. 
        if(count[1] == 0){
            release(turn);     
        }
        release(mutex[1]);
      }
    }
  }
  
  public void delay(){
    try {
      sleep((int)Math.random()*1000);
    } catch (InterruptedException e) {
    }
  }
  
  public static void main(String[] args) 
  {  
    final int N = 10;
    TunnelSynchronization[] p = new TunnelSynchronization[N];
    for (int i = 0; i < N; i++)
    {
      p[i] = new TunnelSynchronization(i);
      p[i].start();
    }
  }
}
    
  
  