
public class Instruction {
	
	String type;
	String Fi,Fj,Fk;
	short Vi,Vj,Vk;
	String status;
	int Qj = -1;
	int Qk = -1;
	int No_of_cycles;
	byte immediate;
	int FUindex;
	int ROBIndex;
	int instID;
	boolean branchTaken;
	short calculatedAddress;
	short address;
	int executeCount;
	int executionCycles;
	
	public String toString(){
	
		String instruction = "Type: " + type + " , Fi: " + Fi + " ,Vi: " + Vi + " ,Fj: " + Fj + " , Vj: "+ Vj +
				   " , Fk: " + Fk + " ,Vk: " + Vk + " ,Qj: " + Qj + " ,Qk: " + Qk + " , Status: " + status + " ,No of Cycles: "
				   + No_of_cycles + " ,immediate value: " + immediate + " ,FU Index: " + FUindex + "ROB Index: "+ ROBIndex + " , Calculated Address: " +
				   calculatedAddress + " ,address of branch: " + address + " , Branch Taken: "+ branchTaken + " ,execute count: " + executeCount + " ,Execution Cycles: "
				   + executionCycles +" ,Instruction ID: " + instID +"\n";
		
		
		return instruction;
	}

}
