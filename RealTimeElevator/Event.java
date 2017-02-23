
import java.util.concurrent.Semaphore;

public class Event {
	private Semaphore id;
	private int floor;

	public Event() {
		id = null;
		floor = 0;
	}
	public Event(Semaphore i, int f) {
		this.id = i;
		this.floor = f;
	}

	public Semaphore getId() {
		return this.id;
	}

	public int getRequestedFloor() {
		return this.floor;
	}

	// public String toString() {
	// 	return (Integer.toString(id)+' '+Integer.toString(floor));
	// }
}