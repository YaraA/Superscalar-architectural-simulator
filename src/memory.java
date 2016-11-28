

public class memory {
	//ArrayList <String> myMemory;
	
	public String [] fetchInstructions(int address, int noOfInstructions){
		String [] instructions = new String[noOfInstructions];
		
		instructions[0] = "BEQ R3,R3,-2";
		instructions[1] = "Add R4,R2,R3";
		instructions[2] = "Nand R1,R3,R2";
		
		return instructions;
	}
	
	public void writeToMemory(short address,short value){
		
	}
}
