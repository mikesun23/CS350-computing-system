

public class TestSimulator{

	public static void main(String[] args) {

		// problem 1
		// MM1 simulator
		FinalSimulator sim = new FinalSimulator(6.0, 0.20, 1000);
		sim.controller();

		// problem 2
		// MM1K simulator
		MM1K_Simulator simA = new MM1K_Simulator(5.0, 0.15, 1000, 2);
		simA.controller();
		MM1K_Simulator simB = new MM1K_Simulator(6.0, 0.15, 1000, 2);
		simB.controller();
		MM1K_Simulator simC= new MM1K_Simulator(6.0, 0.2, 1000, 2);
		simC.controller();
		MM1K_Simulator simD = new MM1K_Simulator(5.0, 0.15, 1000, 4);
		simD.controller();
		MM1K_Simulator simE = new MM1K_Simulator(6.0, 0.15, 1000, 4);
		simE.controller();
		MM1K_Simulator simF = new MM1K_Simulator(6.0, 0.2, 1000, 4);
		simF.controller();
		MM1K_Simulator simG = new MM1K_Simulator(5.0, 0.15, 1000, 10);
		simG.controller();
		MM1K_Simulator simH = new MM1K_Simulator(6.0, 0.15, 1000, 10);
		simH.controller();
		MM1K_Simulator simI = new MM1K_Simulator(6.0, 0.2, 1000, 10);
		simI.controller();

	}

}