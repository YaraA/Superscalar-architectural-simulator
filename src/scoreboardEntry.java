
public class scoreboardEntry {

	Instruction instruction;
	short address;
	int dest;
	boolean notEmpty;
	
	public String toString(){
		
		return instruction.toString() + "\nAddress: " + address + "\nDestination: " + dest + "\nNot Empty: " + notEmpty;
	}
	
}
