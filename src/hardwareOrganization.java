import java.util.ArrayList;


public class hardwareOrganization {

	int [] reservationStations;
	Instruction [] instructionBuffer;
	ArrayList<Instruction> instructionsExecuted; //to ask about 
	//int ROBentries;
	int [] cycles;
	Functional_Unit [] Add;
	Functional_Unit [] Subtract;
	Functional_Unit [] Nand;
	Functional_Unit [] Mult;
	Register_File registers;
	memory mem;
	
	public hardwareOrganization(int [] FUs,int instructionBufferSize,int ROBentries, int [] cycles)
	{
		Add = new Functional_Unit [FUs[0]];
		Subtract = new Functional_Unit [FUs[1]];
		Nand = new Functional_Unit [FUs[2]];
		Mult = new Functional_Unit [FUs[3]];
		
		for(int i = 0; i < Add.length; i++){
			Add[i] = new Functional_Unit(UnitType.ADD, cycles[0]);
		}
		
		for(int i = 0; i < Subtract.length; i++){
			Subtract[i] = new Functional_Unit(UnitType.SUBTRACT, cycles[1]);
		}
		
		for(int i = 0; i < Nand.length; i++){
			Nand[i] = new Functional_Unit(UnitType.NAND, cycles[2]);
		}
		
		for(int i = 0; i < Mult.length; i++){
			Mult[i] = new Functional_Unit(UnitType.MULTIPLY, cycles[3]);
		}
		
		this.instructionBuffer = new Instruction[instructionBufferSize];

	}
	
	public void fetch(int address){
		
		Instruction instruction = null;
		String instr = mem.memor.get(address);
		String[] part1=instr.split(" ");
		String[] part2 = part1[1].split(",");
		
		instruction.type = part1[0];
		instruction.Fi = part2[0];
		instruction.Fj = part2[1];
		instruction.Fk = part2[2];
	}
}
