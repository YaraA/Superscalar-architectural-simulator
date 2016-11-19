
public class InstructionBuffer {

	Instruction [] fetchedInstructions;
	int count;
	
	public InstructionBuffer(int size){
		this.fetchedInstructions = new Instruction[size];
	}
}
