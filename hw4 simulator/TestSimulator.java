

public class TestSimulator {
	public static void main(String args[]) {

		CpuSimulator simulator = new CpuSimulator(40.0, 0.01, 0.1, 0.025,100);
		simulator.cpuController();

		CpuSimulator2 simulator2 = new CpuSimulator2(40.0, 0.01, 0.1, 0.025,100);
		simulator2.cpuController();
	}
}