import java.util.ArrayList;


public class hardwareOrganization {

	int [] reservationStations;
	InstructionBuffer instructionBuffer;
	ArrayList<Instruction> instructionsExecuted; //to ask about 
	//int ROBentries;
	int [] cycles;
	Functional_Unit [] Add;
	Functional_Unit [] Subtract;
	Functional_Unit [] Nand;
	Functional_Unit [] Mult;
	Register_File registers;
	memory memory;
	ROB ROB;
	//int programCycles;
	
	public hardwareOrganization(int [] FUs,int instructionBufferSize,int ROBentries, int [] cycles, int ROBsize)
	{
		Add = new Functional_Unit [FUs[0]];
		Subtract = new Functional_Unit [FUs[1]];
		Nand = new Functional_Unit [FUs[2]];
		Mult = new Functional_Unit [FUs[3]];
		ROB = new ROB(ROBsize);
		
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
		
		this.instructionBuffer = new InstructionBuffer(instructionBufferSize);

	}
	
	public void fetch(int address,int fetchCount){
		String[] instr;
		int emptyPlaces = instructionBuffer.fetchedInstructions.length - instructionBuffer.count;
		
		if(emptyPlaces >= fetchCount)
			instr = memory.fetchInstructions(address,fetchCount);
		else
			instr = memory.fetchInstructions(address,emptyPlaces);

		for(int i = 0; i < instr.length; i++){
		  
		  String[] part1=instr[i].split(" ");
		  String[] part2 = part1[1].split(",");
		
		  Instruction instruction = new Instruction();
		  instruction.type = part1[0];
		  instruction.Fi = part2[0];
		  instruction.Fj = part2[1];
		  instruction.Fk = part2[2];
		
		  instruction.No_of_cycles++;
		  instructionBuffer.fetchedInstructions[i] = instruction;
		}
	}
	
	public void issue(Instruction instruction){
		int freeFU = 0;
		boolean freeROB = false;
		int index = 0;
		if(instruction.type.equalsIgnoreCase("add"))
		{
			for(int i = 0; i < Add.length; i++)
			{
				if(!Add[i].busy)
				{
					freeFU = 1;
					index = i;
					break;
				}
			}
		}
		if(instruction.type.equalsIgnoreCase("sub"))
		{
			for(int i = 0; i < Subtract.length; i++)
			{
				if(!Subtract[i].busy)
				{
					freeFU = 2;
					index = i;
					break;
				}
			}
		}
		if(instruction.type.equalsIgnoreCase("mult"))
		{
			for(int i = 0; i < Mult.length; i++)
			{
				if(!Mult[i].busy)
				{
					freeFU = 3;
					index = i;
					break;
				}
			}
		}
		if(instruction.type.equalsIgnoreCase("nand"))
		{
			for(int i = 0; i < Nand.length; i++)
			{
				if(!Nand[i].busy)
				{
					freeFU = 4;
					index = i;
					break;
				}
			}
		}
		if(freeFU != 0)
		{
			if(!(ROB.HeadPointer == ROB.TailPointer && ROB.ROBContent[ROB.HeadPointer].getType() != null && ROB.ROBContent[ROB.TailPointer].getType() != null))
			{
				ROB.ROBContent[ROB.TailPointer].setDestination(instruction.Fi);
				ROB.ROBContent[ROB.TailPointer].setType(instruction.type);
				ROB.ROBContent[ROB.TailPointer].setReady(false);
				switch(freeFU)
				{
					case 1: {   Add[index].busy = true;
								Add[index].instruction = instruction;
								Add[index].DestReg = instruction.Fi;
								break;
							 }
					case 2: {	Subtract[index].busy = true;
								Subtract[index].instruction = instruction;
								Subtract[index].DestReg = instruction.Fi;
								break;
							 }
					case 3: {	Mult[index].busy = true;
								Mult[index].instruction = instruction;
								Mult[index].DestReg = instruction.Fi;
								break;
							}
					case 4: {	Nand[index].busy = true;
								Nand[index].instruction = instruction;
								Nand[index].DestReg = instruction.Fi;
								break;
							}
				}
			}
			else System.out.println("There is no free ROB entries");	
		  }
		  else System.out.println("There is no free functional unit");
	
	}
	
	
	
	
}
