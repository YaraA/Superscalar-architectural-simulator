
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
	boolean branchTaken;
	short calculatedAddress;
	short address;
	int executeCount;
	int executionCycles;

}
