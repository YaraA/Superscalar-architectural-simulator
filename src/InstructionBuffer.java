import java.util.ArrayList;


public class InstructionBuffer {

	ArrayList<Instruction> fetchedInstructions;
	int count;
	
	public InstructionBuffer(){
		this.fetchedInstructions = new ArrayList<Instruction>();
	}
}
