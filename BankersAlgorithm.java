//My Arrays class functions I got from researching on chatGPT, useful for dealing with all the arrays in this project
import java.util.Arrays;

public class BankersAlgorithm {
	public String[] Processes;
	public String[] Resources;
	public int[][] Allocation;
	public int[][] Max;
	public int[][] Need;
	public int[] Available;
	public String[] SafeSequence;
	
	public BankersAlgorithm(String[] Processes, String[] Resources, int[][] Allocation, int[][] Max, int[] Available) {
		this.Processes = Processes;
		this.Resources = Resources;
		this.Allocation = Allocation;
		this.Max = Max;
		this.Available = Available;
		this.Need = calculateNeedMatrix();
	}
	
	public int[][] calculateNeedMatrix() {
		Need = new int[Processes.length][Resources.length];
		for(int i=0; i < Processes.length; i++) {
			for(int j=0; j < Resources.length; j++) {
				Need[i][j] = Max[i][j] - Allocation[i][j];
			}
		}
		return Need;
	}
	
	public boolean safetyAlgorithm(int[][] Allocation, int[][] Need, int[] Available ) {
		int[] Work = Arrays.copyOf(Available, Resources.length);
		boolean[] Finish = new boolean[Processes.length];
		for(int i = 0; i < Processes.length; i++) {
			Finish[i] = false;
		}
		SafeSequence = new String[Processes.length];
		
		System.out.println("Running Safety Check...");
		
		//Loop until all processes in safe sequence
		boolean found;
		boolean canAllocate;
		boolean loop = true;
		int count = 0;
		while(loop) {
			found = false;
			
			//Find unfinished processes that can complete
			for(int i = 0; i < Processes.length; i++) {
				if(Finish[i] == false) {
					canAllocate = true;
					for(int j = 0; j < Resources.length; j++) {
						if(Need[i][j] > Work[j]) {
							canAllocate = false;
							break;
						}
					}
					
					//If process can complete
					if(canAllocate == true) {
						SafeSequence[count] = Processes[i];
						count++;
						//Mark as finished
						Finish[i] = true;
						//Release resources
						for(int j = 0; j < Resources.length; j++) {
							Work[j] = Work[j] + Allocation[i][j];
						}
						
						found = true;
					}
				}
			}
			
			//If no process could be found, system unsafe
			if(found == false) {
				System.out.println("System is NOT in a safe state.");
				return false;
			}
			
			for(int i = 0; i < Finish.length; i++) {
				if(Finish[i] == false) {
					break;
				}
				else {
					loop = false;
				}
			}
		}
		System.out.println("System is safe.");
		return true;
	}

	/*public boolean bankersAlgorithm() {
		//Calc need matrix
		this.Need = calculateNeedMatrix();
		return safetyAlgorithm();
	}*/
	
	public boolean resourceRequestAlgorithm(int pid, int[] Request) {
		System.out.println("\nProcess " + Processes[pid] + " is requesting: " + Arrays.toString(Request));
	    System.out.println("Available resources before request: " + Arrays.toString(Available));
		
		//Step 1, Check if request exceeds need
		for(int j = 0; j < Resources.length; j++) {
			if(Request[j] > Need[pid][j]) {
				System.out.println("Request denied: Exceeds maximum need.");
				return false;
			}
		}
		
		//Step 2, Check if resources are available
		for(int j = 0; j < Resources.length; j++) {
			if(Request[j] > Available[j]) {
				System.out.println("Request denied: Not enough available resources.");
				return false;
			}
		}
		
		//Step 3 Allocate resources
		
		for(int j = 0; j < Resources.length; j++) {
			Allocation[pid][j] += Request[j];
			Available[j] -= Request[j];
		}
		
		//Process Complete, Release resources
		System.out.println("Process completed.");
		for(int j = 0; j < Resources.length; j++) {
			Available[j] += Allocation[pid][j];
			Allocation[pid][j] = 0;
		}
		
		
		System.out.println("\nChecking if system remains safe...");
		
		//Check if resulting state is safe
		boolean isSafe = safetyAlgorithm(Allocation, Need, Available);
		if (!isSafe) {
	        System.out.println("Request denied: Would make system unsafe. Rolling back...");
	        for(int j = 0; j < Resources.length; j++) {
	            Allocation[pid][j] -= Request[j];
	            Available[j] += Request[j];
	            Need[pid][j] += Request[j];
	        }
	    }
		return isSafe;
	}
	
	public static void main(String args[]) {
		
		BankersAlgorithm ba;
		//Define processes and resources
		String[] Processes = {"P0","P1","P2","P3","P4"};
		String[] Resources = {"R1","R2","R3"};
		
		//define matrix's and array from example
		int[][] Allocation = {
			{0,1,0},
			{2,0,0},
			{3,0,2},
			{2,1,1},
			{0,0,2}};
		
		int[][] Max = {
			{7,5,3},
			{3,2,2},
			{9,0,2},
			{2,2,2},
			{4,3,3}};
		
		int[] Available = {3,3,2};
		
		ba = new BankersAlgorithm(Processes, Resources, Allocation, Max, Available);
		
		//Check safety
		if(!ba.safetyAlgorithm(ba.Allocation, ba.Need, ba.Available)) {
			System.out.println("System is not safe.");
			return;
		}
		else {
			System.out.println("Safe Sequence: {" + Arrays.toString(ba.SafeSequence) + "}\n");
			int[] Request = new int[Resources.length];
			//Request resources in order of the safe sequence found
			for(String process : ba.SafeSequence) {
				int pid = Arrays.asList(Processes).indexOf(process);
				for(int j=0; j< Resources.length; j++) {
					Request[j] = Math.min(ba.Available[j], ba.Need[pid][j]);
				}
				if(ba.resourceRequestAlgorithm(pid, Request))
					System.out.println("Completed");
				else
					System.out.println("Denied");
			}
		}
	}
}
