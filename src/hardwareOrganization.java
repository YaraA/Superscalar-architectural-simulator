import java.util.ArrayList;


public class hardwareOrganization {

	normalRegister PC;
	ArrayList<Instruction> instructionBuffer;
	RegisterStatusTable regStatusTable;
	Register_File registerFile;
	memory memory;
	ROB ROB;
	Scoreboard scoreboard;
	int ProgramCycles;
	int instructionID = 1;
	boolean commit = false;
	
	boolean endOfProgram = false;
	boolean endPipeline = false;
	
	int [] cycles;
	Functional_Unit [] Add;
	Functional_Unit [] Subtract;
	Functional_Unit [] Nand;
	Functional_Unit [] Mult;
	Functional_Unit [] Load;
	Functional_Unit [] Store;
	
	static int branchesEncountered;
	static int branchMisprediction;
	static int loadAndStoreInstructions;
	static ArrayList<Instruction> instructionsCompleted;
	
	
	public hardwareOrganization(int [] FUs, int [] cycles, int ROBsize, short startingAddress)
	{
		Add = new Functional_Unit [FUs[0]];
		Subtract = new Functional_Unit [FUs[1]];
		Nand = new Functional_Unit [FUs[2]];
		Mult = new Functional_Unit [FUs[3]];
		Load = new Functional_Unit [FUs[4]];
		Store = new Functional_Unit [FUs[5]];
		ROB = new ROB(ROBsize);
		
		scoreboard = new Scoreboard(Add.length , Subtract.length , Nand.length , Mult.length , Load.length ,Store.length);
		
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
		
		for(int i = 0; i < Load.length; i++){
			Load[i] = new Functional_Unit(UnitType.LOAD, cycles[4]);
		}
		
		for(int i = 0; i < Store.length; i++){
			Store[i] = new Functional_Unit(UnitType.STORE, cycles[5]);
		}
		
		this.instructionBuffer = new ArrayList<Instruction>();
		PC = new normalRegister("PC",startingAddress);
		memory = new memory();
		regStatusTable = new RegisterStatusTable();
		registerFile = new Register_File();
		instructionsCompleted = new ArrayList<Instruction>();
	}
	
	public void fetch(int fetchCount){
		
		if(endOfProgram == false){
		String[] instr;
		instr = memory.fetchInstructions(PC.value,fetchCount);
		 
		for(int i = 0; i < instr.length; i++){
		  
		 if(instr[i] != "Exit"){
		  String[] part1=instr[i].split(" ");
		  String[] part2 = part1[1].split(",");
		
		  Instruction instruction = new Instruction();
		  instruction.type = part1[0];
		  
		  instruction.address = PC.value;
		  if(!(instruction.type.equalsIgnoreCase("jmp") 
				  || instruction.type.equalsIgnoreCase("jalr") 
				  	|| instruction.type.equalsIgnoreCase("ret") 
				  		|| instruction.type.equalsIgnoreCase("beq")
				  			|| instruction.type.equalsIgnoreCase("sw")))
		  {
		  
		  instruction.Fi = part2[0];	  
		  instruction.Fj = part2[1];
		  if(instruction.type.equalsIgnoreCase("addi") 
				 || instruction.type.equalsIgnoreCase("lw")){
			  instruction.immediate = (byte) Integer.parseInt(part2[2]);
		  }
		  else{
			  instruction.Fk = part2[2];
		  }
		  }
		  else{
			  if(instruction.type.equalsIgnoreCase("jmp")){
				  instruction.Fj = part2[0];
				  instruction.immediate = (byte) Integer.parseInt(part2[1]);
			  }
			  else
				  if(instruction.type.equalsIgnoreCase("beq") || instruction.type.equalsIgnoreCase("sw")){
					  instruction.Fj = part2[0];
					  instruction.Fk = part2[1];
					  instruction.immediate = (byte) Integer.parseInt(part2[2]);
				  }
				  else
					  if(instruction.type.equalsIgnoreCase("jalr")){
						  instruction.Fi = part2[0];
						  instruction.Fj = part2[1];
					  }
					  else
						  if(instruction.type.equalsIgnoreCase("ret")){
							  instruction.Fj = part2[0];
						  }
			  
		     }
		  instruction.instID = instructionID;
		  instructionID++;
		  instruction.status = "Fetched";
		  PC.value++;
		
		  instruction.No_of_cycles++;
		  instructionBuffer.add(instruction);
		  
		  
		}
		 else{
			 if(!instructionBuffer.isEmpty()){
				 instructionBuffer.get(instructionBuffer.size()-1).instID = 6000000;
			 }
			 else
			 {
			   int wantedID = instructionID - 1;
			   
			   for(int j = 0; j < this.scoreboard.Add_Scoreboard_Entries.length; i++)
				   if(this.scoreboard.Add_Scoreboard_Entries[j].instruction.instID == wantedID)
					   this.scoreboard.Add_Scoreboard_Entries[j].instruction.instID = 6000000;
			   
			   for(int j = 0; j < this.scoreboard.Subtract_Scoreboard_Entries.length; i++)
				   if(this.scoreboard.Subtract_Scoreboard_Entries[j].instruction.instID == wantedID)
					   this.scoreboard.Subtract_Scoreboard_Entries[j].instruction.instID = 6000000;
			   
			   for(int j = 0; j < this.scoreboard.Multiply_Scoreboard_Entries.length; i++)
				   if(this.scoreboard.Multiply_Scoreboard_Entries[j].instruction.instID == wantedID)
					   this.scoreboard.Multiply_Scoreboard_Entries[j].instruction.instID = 6000000;
			   
			   for(int j = 0; j < this.scoreboard.Nand_Scoreboard_Entries.length; i++)
				   if(this.scoreboard.Nand_Scoreboard_Entries[j].instruction.instID == wantedID)
					   this.scoreboard.Nand_Scoreboard_Entries[j].instruction.instID = 6000000;
			   
			   for(int j = 0; j < this.scoreboard.Load_Scoreboard_Entries.length; i++)
				   if(this.scoreboard.Load_Scoreboard_Entries[j].instruction.instID == wantedID)
					   this.scoreboard.Load_Scoreboard_Entries[j].instruction.instID = 6000000;
			   
			   for(int j = 0; j < this.scoreboard.Store_Scoreboard_Entries.length; i++)
				   if(this.scoreboard.Store_Scoreboard_Entries[j].instruction.instID == wantedID)
					   this.scoreboard.Store_Scoreboard_Entries[j].instruction.instID = 6000000;
		
			 }
		   endOfProgram = true; 
		 }
		}
		}
	}
	
	public boolean issue(Instruction instruction){
		int freeFU = 0;
		boolean freeROB = false;
		int index = 0;
		boolean issued=false;
		
		if(instruction.type.equalsIgnoreCase("add") 
				|| instruction.type.equalsIgnoreCase("sub") 
					|| instruction.type.equalsIgnoreCase("mul") 
						|| instruction.type.equalsIgnoreCase("nand"))
		{
			for(int i = 0; i < registerFile.registers.length; i++)
			{
				if(registerFile.registers[i].name.equalsIgnoreCase(instruction.Fj))
				{
				 	if(instruction.Fj.equalsIgnoreCase("R0"))
				 		instruction.Vj =((zeroRegister)registerFile.registers[i]).value;
				 	else
				 		instruction.Vj =((normalRegister)registerFile.registers[i]).value;
				}
				
				if(registerFile.registers[i].name.equalsIgnoreCase(instruction.Fk))
				{
				 	if(instruction.Fk.equalsIgnoreCase("R0"))
				 		instruction.Vk =((zeroRegister)registerFile.registers[i]).value;
				 	else
				 		instruction.Vk =((normalRegister)registerFile.registers[i]).value;
				}
			}
		}
		else{
			if(instruction.type.equalsIgnoreCase("addi") 
					|| instruction.type.equalsIgnoreCase("lw") 
						|| instruction.type.equalsIgnoreCase("sw") 
							|| instruction.type.equalsIgnoreCase("beq")
								|| instruction.type.equalsIgnoreCase("jalr")
									|| instruction.type.equalsIgnoreCase("jmp"))
			{
				for(int i = 0; i < registerFile.registers.length; i++)
				{
					if(registerFile.registers[i].name.equalsIgnoreCase(instruction.Fj))
					{
					 	if(instruction.Fj.equalsIgnoreCase("R0"))
					 		instruction.Vj =((zeroRegister)registerFile.registers[i]).value;
					 	else
					 		instruction.Vj =((normalRegister)registerFile.registers[i]).value;
					}
					
					if(instruction.type.equalsIgnoreCase("sw") || instruction.type.equalsIgnoreCase("beq"))
					{
						if(registerFile.registers[i].name.equalsIgnoreCase(instruction.Fk))
						{
						 	if(instruction.Fk.equalsIgnoreCase("R0"))
						 		instruction.Vk =((zeroRegister)registerFile.registers[i]).value;
						 	else
						 		instruction.Vk =((normalRegister)registerFile.registers[i]).value;
						}
					}
						 
				}
			 
			}
			
		}
		
		if(instruction.type.equalsIgnoreCase("jmp"))
			PC.value = (short) (PC.value + instruction.Vj + instruction.immediate);
		else
			if(instruction.type.equalsIgnoreCase("jalr"))
			{
			 for(int i = 0; i < registerFile.registers.length; i++){
				 if(registerFile.registers[i].name.equalsIgnoreCase(instruction.Fj) && !instruction.Fj.equalsIgnoreCase("R0"))
					 ((normalRegister)registerFile.registers[i]).value = PC.value;
			 }
			 PC.value = (short) instruction.Vk;
			}
			else
				if(instruction.type.equalsIgnoreCase("ret"))
					PC.value = (short) instruction.Vj;	
				
		
//		if(((int)regStatusTable.statusTable.get(instruction.Fi)) != -1){
//			System.out.println("Stalled due to WAW hazard");
//		}
//		else{
			System.out.println("Da5al henaaa");
		if(instruction.type.equalsIgnoreCase("add") || instruction.type.equalsIgnoreCase("addi") || instruction.type.equalsIgnoreCase("jmp"))
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
		if(instruction.type.equalsIgnoreCase("sub") || instruction.type.equalsIgnoreCase("beq"))
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
		if(instruction.type.equalsIgnoreCase("mul"))
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
		if(instruction.type.equalsIgnoreCase("lw"))
		{
			for(int i = 0; i < Load.length; i++)
			{
				if(!Load[i].busy)
				{
					freeFU = 5;
					index = i;
					break;
				}
			}
		}
		if(instruction.type.equalsIgnoreCase("sw"))
		{
			for(int i = 0; i < Store.length; i++)
			{
				if(!Store[i].busy)
				{
					freeFU = 6;
					index = i;
					break;
				}
			}
		}
		
		if(freeFU != 0)
		{ 
			
			for(int i=0;i<ROB.ROBContent.length && issued == false;i++){
			   if(ROB.ROBContent[i].empty==true){
				    ROB.ROBContent[i].setDestination(instruction.Fi);
					ROB.ROBContent[i].setType(instruction.type);
					ROB.ROBContent[i].setReady(false);
					ROB.ROBContent[i].empty=false;
					instruction.ROBIndex = ROB.ROBContent[i].getId();
					issued=true;
			     }
			   }
			if(issued==false){
				System.out.println("Stalled due to ROB being full");
			}
			else
			{
				
				if(instruction.type.equalsIgnoreCase("BEQ"))
				{
				 if(instruction.immediate >= 0)
					 instruction.branchTaken = false;
				 else
					 {
					 instruction.branchTaken = true;
					 PC.value = (short) (PC.value + instruction.immediate);
					 }
				}
				
				switch(freeFU)
				{
					case 1: {  	Add[index].busy = true;
								Add[index].instruction = instruction;
								//Add[index].DestReg = instruction.Fi;
								instruction.FUindex = index;
								instruction.executionCycles = Add[index].No_of_exec_cycles;
								scoreboard.Add_Scoreboard_Entries[index] = new scoreboardEntry();
								scoreboard.Add_Scoreboard_Entries[index].instruction = instruction;
								scoreboard.Add_Scoreboard_Entries[index].dest = ROB.TailPointer;
								scoreboard.Add_Scoreboard_Entries[index].notEmpty = true;
								break;
							}
					case 2: {	Subtract[index].busy = true;
								Subtract[index].instruction = instruction;
								//Subtract[index].DestReg = instruction.Fi;
								instruction.FUindex = index;
								instruction.executionCycles = Subtract[index].No_of_exec_cycles;
								scoreboard.Subtract_Scoreboard_Entries[index] = new scoreboardEntry();
								scoreboard.Subtract_Scoreboard_Entries[index].instruction = instruction;
								scoreboard.Subtract_Scoreboard_Entries[index].dest = ROB.TailPointer;
								scoreboard.Subtract_Scoreboard_Entries[index].notEmpty = true;
								break;
							}
					case 3: {	
								Mult[index].busy = true;
								Mult[index].instruction = instruction;
								//Mult[index].DestReg = instruction.Fi;
								instruction.FUindex = index;
								instruction.executionCycles = Mult[index].No_of_exec_cycles;
								scoreboard.Multiply_Scoreboard_Entries[index] = new scoreboardEntry();
								scoreboard.Multiply_Scoreboard_Entries[index].instruction = instruction;
								scoreboard.Multiply_Scoreboard_Entries[index].dest = ROB.TailPointer;
								scoreboard.Multiply_Scoreboard_Entries[index].notEmpty = true;
								break;
							}
					case 4: {	Nand[index].busy = true;
								Nand[index].instruction = instruction;
								//Nand[index].DestReg = instruction.Fi;
								instruction.FUindex = index;
								instruction.executionCycles = Nand[index].No_of_exec_cycles;
								scoreboard.Nand_Scoreboard_Entries[index] = new scoreboardEntry();
								scoreboard.Nand_Scoreboard_Entries[index].instruction = instruction;
								scoreboard.Nand_Scoreboard_Entries[index].dest = ROB.TailPointer;
								scoreboard.Nand_Scoreboard_Entries[index].notEmpty = true;
								break;
							}
					case 5: {	Load[index].busy = true;
								Load[index].instruction = instruction;
								instruction.FUindex = index;
								instruction.executionCycles = Load[index].No_of_exec_cycles;
								scoreboard.Load_Scoreboard_Entries[index] = new scoreboardEntry();
								scoreboard.Load_Scoreboard_Entries[index].instruction = instruction;
								scoreboard.Load_Scoreboard_Entries[index].dest = ROB.TailPointer;
								scoreboard.Load_Scoreboard_Entries[index].notEmpty = true;
								break;
							}
					case 6: {	Store[index].busy = true;
								Store[index].instruction = instruction;
								instruction.FUindex = index;
								instruction.executionCycles = Store[index].No_of_exec_cycles;
								scoreboard.Store_Scoreboard_Entries[index] = new scoreboardEntry();
								scoreboard.Store_Scoreboard_Entries[index].instruction = instruction;
								scoreboard.Store_Scoreboard_Entries[index].dest = ROB.TailPointer;
								scoreboard.Store_Scoreboard_Entries[index].notEmpty = true;
								break;
							}
				}
				
				if(instruction.Fi != null)
					regStatusTable.statusTable.put(instruction.Fi,ROB.TailPointer);
				
				if(regStatusTable.statusTable.containsKey(instruction.Fj) && regStatusTable.statusTable.get(instruction.Fj) != null){
					instruction.Qj=(int) regStatusTable.statusTable.get(instruction.Fj);
				}
				if(regStatusTable.statusTable.containsKey(instruction.Fk) && regStatusTable.statusTable.get(instruction.Fk) != null){
					instruction.Qk=(int) regStatusTable.statusTable.get(instruction.Fk);
				}
				
				if(instruction.Fi != null && regStatusTable.statusTable.containsKey(instruction.Fi) && regStatusTable.statusTable.get(instruction.Fi) != null){
					regStatusTable.statusTable.remove(instruction.Fi);
					regStatusTable.statusTable.put(instruction.Fi,ROB.TailPointer);
				}
				
				if(ROB.TailPointer == ROB.ROBContent.length -1)
				  ROB.TailPointer = 0;
				else
					ROB.TailPointer++;
				
				instruction.status = "Issued";
				instructionBuffer.remove(instruction);
				
			}
			//else System.out.println("There is no free ROB entries");	
		  }
		  else System.out.println("There is no free functional unit");
		//}
		instruction.No_of_cycles++;
		return issued;
	}
	
	public void calculateAddress(Instruction instruction){
		
		if(instruction.type.equalsIgnoreCase("LW")){
		 instruction.calculatedAddress = (short) (instruction.Vj + instruction.immediate);
		 scoreboard.Load_Scoreboard_Entries[instruction.FUindex].address = instruction.calculatedAddress;
		}
		else 
			if(instruction.type.equalsIgnoreCase("SW")){
			instruction.calculatedAddress = (short) (instruction.Vk + instruction.immediate);
			scoreboard.Store_Scoreboard_Entries[instruction.FUindex].address = instruction.calculatedAddress;
			}
		instruction.status = "Calculated Address";
		instruction.No_of_cycles++;
	}
	
	public void execute(Instruction instruction, Instruction oldScoreboardInst){
		
		if(instruction.executeCount == 0){
		
		if(oldScoreboardInst.Qj != -1 || oldScoreboardInst.Qk != -1){
			System.out.println("Stalled due to RAW hazard");
			instruction.No_of_cycles++;	
		}
		else
		{
			if(instruction.type.equalsIgnoreCase("add") 
					|| instruction.type.equalsIgnoreCase("addi") 
						|| instruction.type.equalsIgnoreCase("jmp"))
			{
				instruction.Vi = Add[instruction.FUindex].execute();
				if(instruction.type.equalsIgnoreCase("jmp")){
					 short address = (short) (PC.value + 1 + instruction.Vi); 
				}
				instruction.No_of_cycles++;
			}
			else{
				if(instruction.type.equalsIgnoreCase("sub") || instruction.type.equalsIgnoreCase("beq")){
					
					instruction.Vi = Subtract[instruction.FUindex].execute();
					if(instruction.type.equalsIgnoreCase("beq")){
						if((instruction.Vi == 0 && instruction.branchTaken == false) 
								|| (instruction.Vi != 0 && instruction.branchTaken == true)){
							System.out.println(instruction.Vi);
							System.out.println(instruction.branchTaken);
							Flush(instruction);
						}
					}
					instruction.No_of_cycles++;
				}
				else{
					if(instruction.type.equalsIgnoreCase("mul")){
						instruction.Vi = Mult[instruction.FUindex].execute();
						instruction.No_of_cycles++;
					}
					else{
						if(instruction.type.equalsIgnoreCase("nand")){
							instruction.Vi = Nand[instruction.FUindex].execute();
							instruction.No_of_cycles++;
						}
						else{
							if(instruction.type.equalsIgnoreCase("lw")){
								instruction.Vi = Load[instruction.FUindex].execute();
								instruction.No_of_cycles++;
							}
							else{
								if(instruction.type.equalsIgnoreCase("sw")){
									Store[instruction.FUindex].execute();
									instruction.No_of_cycles++;
								}
								else{
									if(instruction.type.equalsIgnoreCase("ret")) 
													
									{
									short address = instruction.Vj;
									instruction.No_of_cycles++;
									}
									else{
										if(instruction.type.equalsIgnoreCase("jalr"))
										{
										 instruction.Vi = (short) (PC.value + 1);
										 instruction.No_of_cycles++;	
										}
										}
								}
							}
						}
					}
				}
			}
		  instruction.status = "Executed";
		  
		  if(instruction.type.equalsIgnoreCase("sw"))
			  instruction.executeCount = instruction.executionCycles;
		  else
			  instruction.executeCount++;
		  
		  System.out.println("Not Stalled");
		}
		
		}
		else{
			if(instruction.executeCount < instruction.executionCycles && instruction.status.equalsIgnoreCase("Executed"))
				{
				instruction.executeCount++;
				instruction.No_of_cycles++;
				}
		}
	}
	
	public void write(Instruction instruction, Scoreboard oldScoreboard){
		
		boolean stop = false;
		for(int i = 0; i < oldScoreboard.Add_Scoreboard_Entries.length && stop == false;i++){
			if(oldScoreboard.Add_Scoreboard_Entries[i] != null 
					&& oldScoreboard.Add_Scoreboard_Entries[i].instruction.instID < instruction.instID
						&& oldScoreboard.Add_Scoreboard_Entries[i].instruction.executeCount == oldScoreboard.Add_Scoreboard_Entries[i].instruction.executionCycles
							&& oldScoreboard.Add_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("Executed")){
				System.out.println("Stalled due higher priortiy instruction being written");
				stop = true;
			}
		}
		
		for(int i = 0; i < oldScoreboard.Subtract_Scoreboard_Entries.length && stop == false;i++){
			if(oldScoreboard.Subtract_Scoreboard_Entries[i] != null
					&& oldScoreboard.Subtract_Scoreboard_Entries[i].instruction.instID < instruction.instID
						&& oldScoreboard.Subtract_Scoreboard_Entries[i].instruction.executeCount == oldScoreboard.Subtract_Scoreboard_Entries[i].instruction.executionCycles
							&& oldScoreboard.Subtract_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("Executed")){
				System.out.println("Stalled due higher priortiy instruction being written");
				stop = true;
			}
		}
		for(int i = 0; i < oldScoreboard.Multiply_Scoreboard_Entries.length && stop == false;i++){
			if(oldScoreboard.Multiply_Scoreboard_Entries[i] != null
					&& oldScoreboard.Multiply_Scoreboard_Entries[i].instruction.instID < instruction.instID
					 && oldScoreboard.Multiply_Scoreboard_Entries[i].instruction.executeCount == oldScoreboard.Multiply_Scoreboard_Entries[i].instruction.executionCycles
					 	&& oldScoreboard.Multiply_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("Executed")){
				System.out.println("Stalled due higher priortiy instruction being written");
				stop = true;
			}
		}
		for(int i = 0; i < oldScoreboard.Nand_Scoreboard_Entries.length && stop == false;i++){
			if(oldScoreboard.Nand_Scoreboard_Entries[i] != null
					&& oldScoreboard.Nand_Scoreboard_Entries[i].instruction.instID < instruction.instID
						&& oldScoreboard.Nand_Scoreboard_Entries[i].instruction.executeCount == oldScoreboard.Nand_Scoreboard_Entries[i].instruction.executionCycles
							&& oldScoreboard.Nand_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("Executed")){
				System.out.println("Stalled due higher priortiy instruction being written");
				stop = true;
			}
		}
		for(int i = 0; i < oldScoreboard.Load_Scoreboard_Entries.length && stop == false;i++){
			if(oldScoreboard.Load_Scoreboard_Entries[i] != null
					&& oldScoreboard.Load_Scoreboard_Entries[i].instruction.instID < instruction.instID
						&& oldScoreboard.Load_Scoreboard_Entries[i].instruction.executeCount == oldScoreboard.Load_Scoreboard_Entries[i].instruction.executionCycles
							&&oldScoreboard.Load_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("Executed")){
				System.out.println("Stalled due higher priortiy instruction being written");
				stop = true;
			}
		}
		for(int i = 0; i < oldScoreboard.Store_Scoreboard_Entries.length && stop == false;i++){
			if(oldScoreboard.Store_Scoreboard_Entries[i] != null
					&& oldScoreboard.Store_Scoreboard_Entries[i].instruction.instID < instruction.instID
						&& oldScoreboard.Store_Scoreboard_Entries[i].instruction.executeCount == oldScoreboard.Store_Scoreboard_Entries[i].instruction.executionCycles
							&& oldScoreboard.Store_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("Executed")){
				System.out.println("Stalled due higher priortiy instruction being written");
				stop = true;
			}
		}
		
		if(stop == false){
			
		if(!(instruction.type.equalsIgnoreCase("sw")) || (instruction.type.equalsIgnoreCase("sw") && instruction.executeCount == 0))
		{	
		int ROBId = -1;
		
		if(instruction.type.equalsIgnoreCase("add") || instruction.type.equalsIgnoreCase("addi")){
			ROBId = scoreboard.Add_Scoreboard_Entries[instruction.FUindex].dest;
			scoreboard.Add_Scoreboard_Entries[instruction.FUindex].notEmpty = false;
			}
		else
			if(instruction.type.equalsIgnoreCase("sub") || instruction.type.equalsIgnoreCase("beq")){
				ROBId = scoreboard.Subtract_Scoreboard_Entries[instruction.FUindex].dest;
				scoreboard.Subtract_Scoreboard_Entries[instruction.FUindex].notEmpty = false;
				}
			else
				if(instruction.type.equalsIgnoreCase("mul")){
					ROBId = scoreboard.Multiply_Scoreboard_Entries[instruction.FUindex].dest;
					scoreboard.Multiply_Scoreboard_Entries[instruction.FUindex].notEmpty = false;
					}
				else
					if(instruction.type.equalsIgnoreCase("nand")){
						ROBId = scoreboard.Nand_Scoreboard_Entries[instruction.FUindex].dest;
						scoreboard.Nand_Scoreboard_Entries[instruction.FUindex].notEmpty = false;
						}
		
		for(int i = 0; i < scoreboard.Add_Scoreboard_Entries.length; i++){
		  if(scoreboard.Add_Scoreboard_Entries[i] != null){
			if(scoreboard.Add_Scoreboard_Entries[i].instruction.Qj == ROBId)
				scoreboard.Add_Scoreboard_Entries[i].instruction.Qj = -1;
				
			if(scoreboard.Add_Scoreboard_Entries[i].instruction.Qk == ROBId)
					scoreboard.Add_Scoreboard_Entries[i].instruction.Qk = -1;
		  }
		}
		
		for(int i = 0; i < scoreboard.Subtract_Scoreboard_Entries.length; i++){
		  if(scoreboard.Subtract_Scoreboard_Entries[i] != null){
			if(scoreboard.Subtract_Scoreboard_Entries[i].instruction.Qj == ROBId)
				scoreboard.Subtract_Scoreboard_Entries[i].instruction.Qj = -1;
				
			if(scoreboard.Subtract_Scoreboard_Entries[i].instruction.Qk == ROBId)
					scoreboard.Subtract_Scoreboard_Entries[i].instruction.Qk = -1;
			}
		}
		
		for(int i = 0; i < scoreboard.Multiply_Scoreboard_Entries.length; i++){
		  if(scoreboard.Multiply_Scoreboard_Entries[i] != null){
			if(scoreboard.Multiply_Scoreboard_Entries[i].instruction.Qj == ROBId)
				scoreboard.Multiply_Scoreboard_Entries[i].instruction.Qj = -1;
				
			if(scoreboard.Multiply_Scoreboard_Entries[i].instruction.Qk == ROBId)
					scoreboard.Multiply_Scoreboard_Entries[i].instruction.Qk = -1;
		  }
		}
		
		for(int i = 0; i < scoreboard.Nand_Scoreboard_Entries.length; i++){
		  if(scoreboard.Nand_Scoreboard_Entries[i] != null){
			if(scoreboard.Nand_Scoreboard_Entries[i].instruction.Qj == ROBId)
				scoreboard.Nand_Scoreboard_Entries[i].instruction.Qj = -1;
				
			if(scoreboard.Nand_Scoreboard_Entries[i].instruction.Qk == ROBId)
					scoreboard.Nand_Scoreboard_Entries[i].instruction.Qk = -1;
		  }
		}
		
		for(int i = 0; i < scoreboard.Load_Scoreboard_Entries.length; i++){
		  if(scoreboard.Load_Scoreboard_Entries[i] != null){	
			if(scoreboard.Load_Scoreboard_Entries[i].instruction.Qj == ROBId)
				scoreboard.Load_Scoreboard_Entries[i].instruction.Qj = -1;
			
			if(scoreboard.Load_Scoreboard_Entries[i].instruction.Qk == ROBId)
					scoreboard.Load_Scoreboard_Entries[i].instruction.Qk = -1;
		  }
		}
		
		for(int i = 0; i < scoreboard.Store_Scoreboard_Entries.length; i++){
		   if(scoreboard.Store_Scoreboard_Entries[i] != null){
			if(scoreboard.Store_Scoreboard_Entries[i].instruction.Qj == ROBId)
				scoreboard.Store_Scoreboard_Entries[i].instruction.Qj = -1;
			
			if(scoreboard.Store_Scoreboard_Entries[i].instruction.Qk == ROBId)
					scoreboard.Store_Scoreboard_Entries[i].instruction.Qk = -1;
		   }
		}
		
		ROB.ROBContent[ROBId].setReady(true);
		ROB.ROBContent[ROBId].setValue(instruction.Vi);
		instruction.status="written";
		}
		else
			if(instruction.type.equalsIgnoreCase("sw") 
					&& instruction.executeCount < instruction.executionCycles 
						&& instruction.status.equalsIgnoreCase("written"))
			{
			instruction.executeCount++;	
			}
		}
		
		instruction.No_of_cycles++;
		
	}
	
	public void commit(Instruction instruction){
		
		for (int i=0; i<ROB.ROBContent.length;i++){
			
			if(instruction.ROBIndex == ROB.ROBContent[i].getId()){
				if(ROB.ROBContent[i].getId() == ROB.HeadPointer && ROB.ROBContent[i].isReady()){
					
				  if(instruction.instID == 6000000)
					  endPipeline = true;
					
				  if(!(instruction.type.equalsIgnoreCase("beq") || instruction.type.equalsIgnoreCase("sw"))){
					  
					if(((int)regStatusTable.statusTable.get(instruction.Fi)) == ROB.ROBContent[i].getId()){
						regStatusTable.statusTable.remove(instruction.Fi);
						regStatusTable.statusTable.put(instruction.Fi,-1);
				    }
				    for(int k=0;k<registerFile.registers.length;k++){
				    	if(!registerFile.registers[k].name.equalsIgnoreCase("R0")){
				    		if(registerFile.registers[k].name.equals(instruction.Fi)){
				    			((normalRegister)registerFile.registers[k]).value = instruction.Vi;
				    		}
				    	}
				    }
				  }
				  else
					  if(instruction.type.equalsIgnoreCase("sw")){
						 memory.writeToMemory(instruction.calculatedAddress,instruction.Vj);
					  }
				    ROB.ROBContent[i].empty=true;
				    
				    if(ROB.HeadPointer == ROB.ROBContent.length -1)
					    ROB.HeadPointer = 0;
					else
						ROB.HeadPointer++;
				    
				    instruction.status="Commited";
				    commit = true;
				    instruction.No_of_cycles++;
				    if(instruction.type.equalsIgnoreCase("lw") || instruction.type.equalsIgnoreCase("sw"))
				    	loadAndStoreInstructions++;
				    else
				    	if(instruction.type.equalsIgnoreCase("beq"))
				    		branchesEncountered++;
				    
				    instructionsCompleted.add(instruction);
				    break;
					}
				}
			
		}
			
	}
	
	public void Flush(Instruction branchInstruction){
		
		int index = 0;
		for(int i = 0; i < ROB.ROBContent.length; i++)
		{
		 if(ROB.ROBContent[i].getId() == branchInstruction.ROBIndex)
		 {
		  if(index == ROB.ROBContent.length-1)
			  index = 0;
		  else
			  index = i+1;
		  break;
		 }	
		}
		
		ArrayList<Integer> IDs = new ArrayList<Integer>();
		for(int i = index; i != ROB.TailPointer; i++){
			ROB.ROBContent[i].empty = true;
			IDs.add(ROB.ROBContent[i].getId());
			PC.value--;
			if(i == ROB.ROBContent.length-1)
				i = -1;
		}
		
		ROB.TailPointer = index;
		
		for(int i = 0; i < scoreboard.Add_Scoreboard_Entries.length; i++)
			for(int j = 0; j < IDs.size(); j++)
			 if(scoreboard.Add_Scoreboard_Entries[i] != null)	
			  if(scoreboard.Add_Scoreboard_Entries[i].dest == IDs.get(i))
				 scoreboard.Add_Scoreboard_Entries[i].notEmpty = false;
		
		for(int i = 0; i < scoreboard.Subtract_Scoreboard_Entries.length; i++)
			for(int j = 0; j < IDs.size(); j++)
				if(scoreboard.Subtract_Scoreboard_Entries[i] != null)	
				  if(scoreboard.Subtract_Scoreboard_Entries[i].dest == IDs.get(i))
					  scoreboard.Subtract_Scoreboard_Entries[i].notEmpty = false;
		
		for(int i = 0; i < scoreboard.Multiply_Scoreboard_Entries.length; i++)
			for(int j = 0; j < IDs.size(); j++)
				if(scoreboard.Multiply_Scoreboard_Entries[i] != null)	
				  if(scoreboard.Multiply_Scoreboard_Entries[i].dest == IDs.get(i))
					  scoreboard.Multiply_Scoreboard_Entries[i].notEmpty = false;
		
		for(int i = 0; i < scoreboard.Nand_Scoreboard_Entries.length; i++)
			for(int j = 0; j < IDs.size(); j++)
				if(scoreboard.Nand_Scoreboard_Entries[i] != null)	
				  if(scoreboard.Nand_Scoreboard_Entries[i].dest == IDs.get(i))
					  scoreboard.Nand_Scoreboard_Entries[i].notEmpty = false;
		
		for(int i = 0; i < scoreboard.Load_Scoreboard_Entries.length; i++)
			for(int j = 0; j < IDs.size(); j++)
				if(scoreboard.Load_Scoreboard_Entries[i] != null)	
				  if(scoreboard.Load_Scoreboard_Entries[i].dest == IDs.get(i))
					  scoreboard.Load_Scoreboard_Entries[i].notEmpty = false;
		
		for(int i = 0; i < scoreboard.Store_Scoreboard_Entries.length; i++)
			for(int j = 0; j < IDs.size(); j++)
				if(scoreboard.Store_Scoreboard_Entries[i] != null)	
				  if(scoreboard.Store_Scoreboard_Entries[i].dest == IDs.get(i))
					  scoreboard.Store_Scoreboard_Entries[i].notEmpty = false;
		
		while(!instructionBuffer.isEmpty())
			instructionBuffer.remove(0);
		
		PC.value = (short) (PC.value + branchInstruction.immediate);
		branchMisprediction++;
	}
	
	public void Tomasulo(int Count){
		
	fetch(Count);
	System.out.print(instructionBuffer.get(0).toString());
		
	for(int j = 0;j < 6 && endPipeline == false;j++){
		
	    System.out.println("Cycle " + j); 
	    
		Scoreboard myScoreBoard = new Scoreboard(Add.length, Subtract.length, Mult.length, Nand.length, Load.length, Store.length);
		
		for(int i = 0; i < myScoreBoard.Add_Scoreboard_Entries.length; i++){
			if(this.scoreboard.Add_Scoreboard_Entries[i] != null){
			myScoreBoard.Add_Scoreboard_Entries[i] = new scoreboardEntry();
			myScoreBoard.Add_Scoreboard_Entries[i].address = this.scoreboard.Add_Scoreboard_Entries[i].address;
			myScoreBoard.Add_Scoreboard_Entries[i].dest = this.scoreboard.Add_Scoreboard_Entries[i].dest;
			myScoreBoard.Add_Scoreboard_Entries[i].instruction = new Instruction();
			myScoreBoard.Add_Scoreboard_Entries[i].instruction.type = this.scoreboard.Add_Scoreboard_Entries[i].instruction.type;
			myScoreBoard.Add_Scoreboard_Entries[i].instruction.Fi = this.scoreboard.Add_Scoreboard_Entries[i].instruction.Fi;
			myScoreBoard.Add_Scoreboard_Entries[i].instruction.Fj = this.scoreboard.Add_Scoreboard_Entries[i].instruction.Fj;
			myScoreBoard.Add_Scoreboard_Entries[i].instruction.Fk = this.scoreboard.Add_Scoreboard_Entries[i].instruction.Fk;
			myScoreBoard.Add_Scoreboard_Entries[i].instruction.Vi = this.scoreboard.Add_Scoreboard_Entries[i].instruction.Vi;
			myScoreBoard.Add_Scoreboard_Entries[i].instruction.Vj = this.scoreboard.Add_Scoreboard_Entries[i].instruction.Vj;
			myScoreBoard.Add_Scoreboard_Entries[i].instruction.Vk = this.scoreboard.Add_Scoreboard_Entries[i].instruction.Vk;
			myScoreBoard.Add_Scoreboard_Entries[i].instruction.status = this.scoreboard.Add_Scoreboard_Entries[i].instruction.status;
			myScoreBoard.Add_Scoreboard_Entries[i].instruction.Qj = this.scoreboard.Add_Scoreboard_Entries[i].instruction.Qj;
			myScoreBoard.Add_Scoreboard_Entries[i].instruction.Qk = this.scoreboard.Add_Scoreboard_Entries[i].instruction.Qk;
			myScoreBoard.Add_Scoreboard_Entries[i].instruction.No_of_cycles = this.scoreboard.Add_Scoreboard_Entries[i].instruction.No_of_cycles;
			myScoreBoard.Add_Scoreboard_Entries[i].instruction.immediate = this.scoreboard.Add_Scoreboard_Entries[i].instruction.immediate;
			myScoreBoard.Add_Scoreboard_Entries[i].instruction.FUindex = this.scoreboard.Add_Scoreboard_Entries[i].instruction.FUindex;
			myScoreBoard.Add_Scoreboard_Entries[i].instruction.branchTaken = this.scoreboard.Add_Scoreboard_Entries[i].instruction.branchTaken;
			myScoreBoard.Add_Scoreboard_Entries[i].instruction.calculatedAddress = this.scoreboard.Add_Scoreboard_Entries[i].instruction.calculatedAddress;
			myScoreBoard.Add_Scoreboard_Entries[i].instruction.address = this.scoreboard.Add_Scoreboard_Entries[i].instruction.address;
			myScoreBoard.Add_Scoreboard_Entries[i].instruction.executeCount = this.scoreboard.Add_Scoreboard_Entries[i].instruction.executeCount;
			myScoreBoard.Add_Scoreboard_Entries[i].instruction.executionCycles = this.scoreboard.Add_Scoreboard_Entries[i].instruction.executionCycles;
			myScoreBoard.Add_Scoreboard_Entries[i].instruction.instID = this.scoreboard.Add_Scoreboard_Entries[i].instruction.instID;
			myScoreBoard.Add_Scoreboard_Entries[i].instruction.ROBIndex = this.scoreboard.Add_Scoreboard_Entries[i].instruction.ROBIndex;
			myScoreBoard.Add_Scoreboard_Entries[i].notEmpty = this.scoreboard.Add_Scoreboard_Entries[i].notEmpty;
			}
		}
		
		for(int i = 0; i < myScoreBoard.Subtract_Scoreboard_Entries.length; i++){
		    if(this.scoreboard.Subtract_Scoreboard_Entries[i] != null){
		    myScoreBoard.Subtract_Scoreboard_Entries[i] = new scoreboardEntry();	
			myScoreBoard.Subtract_Scoreboard_Entries[i].address = this.scoreboard.Subtract_Scoreboard_Entries[i].address;
			myScoreBoard.Subtract_Scoreboard_Entries[i].dest = this.scoreboard.Subtract_Scoreboard_Entries[i].dest;
			myScoreBoard.Subtract_Scoreboard_Entries[i].instruction = new Instruction();
			myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.type = this.scoreboard.Subtract_Scoreboard_Entries[i].instruction.type;
			myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.Fi = this.scoreboard.Subtract_Scoreboard_Entries[i].instruction.Fi;
			myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.Fj = this.scoreboard.Subtract_Scoreboard_Entries[i].instruction.Fj;
			myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.Fk = this.scoreboard.Subtract_Scoreboard_Entries[i].instruction.Fk;
			myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.Vi = this.scoreboard.Subtract_Scoreboard_Entries[i].instruction.Vi;
			myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.Vj = this.scoreboard.Subtract_Scoreboard_Entries[i].instruction.Vj;
			myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.Vk = this.scoreboard.Subtract_Scoreboard_Entries[i].instruction.Vk;
			myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.status = this.scoreboard.Subtract_Scoreboard_Entries[i].instruction.status;
			myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.Qj = this.scoreboard.Subtract_Scoreboard_Entries[i].instruction.Qj;
			myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.Qk = this.scoreboard.Subtract_Scoreboard_Entries[i].instruction.Qk;
			myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.No_of_cycles = this.scoreboard.Subtract_Scoreboard_Entries[i].instruction.No_of_cycles;
			myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.immediate = this.scoreboard.Subtract_Scoreboard_Entries[i].instruction.immediate;
			myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.FUindex = this.scoreboard.Subtract_Scoreboard_Entries[i].instruction.FUindex;
			myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.branchTaken = this.scoreboard.Subtract_Scoreboard_Entries[i].instruction.branchTaken;
			myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.calculatedAddress = this.scoreboard.Subtract_Scoreboard_Entries[i].instruction.calculatedAddress;
			myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.address = this.scoreboard.Subtract_Scoreboard_Entries[i].instruction.address;
			myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.executeCount = this.scoreboard.Subtract_Scoreboard_Entries[i].instruction.executeCount;
			myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.executionCycles = this.scoreboard.Subtract_Scoreboard_Entries[i].instruction.executionCycles;
			myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.instID = this.scoreboard.Subtract_Scoreboard_Entries[i].instruction.instID;
			myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.ROBIndex = this.scoreboard.Subtract_Scoreboard_Entries[i].instruction.ROBIndex;
			myScoreBoard.Subtract_Scoreboard_Entries[i].notEmpty = this.scoreboard.Subtract_Scoreboard_Entries[i].notEmpty;
		    }
		}
		
		for(int i = 0; i < myScoreBoard.Multiply_Scoreboard_Entries.length; i++){
			if(this.scoreboard.Multiply_Scoreboard_Entries[i] != null){
			myScoreBoard.Multiply_Scoreboard_Entries[i] = new scoreboardEntry();	
			myScoreBoard.Multiply_Scoreboard_Entries[i].address = this.scoreboard.Multiply_Scoreboard_Entries[i].address;
			myScoreBoard.Multiply_Scoreboard_Entries[i].instruction = new Instruction();
			myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.type = this.scoreboard.Multiply_Scoreboard_Entries[i].instruction.type;
			myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.Fi = this.scoreboard.Multiply_Scoreboard_Entries[i].instruction.Fi;
			myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.Fj = this.scoreboard.Multiply_Scoreboard_Entries[i].instruction.Fj;
			myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.Fk = this.scoreboard.Multiply_Scoreboard_Entries[i].instruction.Fk;
			myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.Vi = this.scoreboard.Multiply_Scoreboard_Entries[i].instruction.Vi;
			myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.Vj = this.scoreboard.Multiply_Scoreboard_Entries[i].instruction.Vj;
			myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.Vk = this.scoreboard.Multiply_Scoreboard_Entries[i].instruction.Vk;
			myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.status = this.scoreboard.Multiply_Scoreboard_Entries[i].instruction.status;
			myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.Qj = this.scoreboard.Multiply_Scoreboard_Entries[i].instruction.Qj;
			myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.Qk = this.scoreboard.Multiply_Scoreboard_Entries[i].instruction.Qk;
			myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.No_of_cycles = this.scoreboard.Multiply_Scoreboard_Entries[i].instruction.No_of_cycles;
			myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.immediate = this.scoreboard.Multiply_Scoreboard_Entries[i].instruction.immediate;
			myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.FUindex = this.scoreboard.Multiply_Scoreboard_Entries[i].instruction.FUindex;
			myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.branchTaken = this.scoreboard.Multiply_Scoreboard_Entries[i].instruction.branchTaken;
			myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.calculatedAddress = this.scoreboard.Multiply_Scoreboard_Entries[i].instruction.calculatedAddress;
			myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.address = this.scoreboard.Multiply_Scoreboard_Entries[i].instruction.address;
			myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.executeCount = this.scoreboard.Multiply_Scoreboard_Entries[i].instruction.executeCount;
			myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.executionCycles = this.scoreboard.Multiply_Scoreboard_Entries[i].instruction.executionCycles;
			myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.instID = this.scoreboard.Multiply_Scoreboard_Entries[i].instruction.instID;
			myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.ROBIndex = this.scoreboard.Multiply_Scoreboard_Entries[i].instruction.ROBIndex;
			myScoreBoard.Multiply_Scoreboard_Entries[i].dest = this.scoreboard.Multiply_Scoreboard_Entries[i].dest;
			myScoreBoard.Multiply_Scoreboard_Entries[i].notEmpty = this.scoreboard.Multiply_Scoreboard_Entries[i].notEmpty;
			}
	    }
		
		for(int i = 0; i < myScoreBoard.Nand_Scoreboard_Entries.length; i++){
			if(this.scoreboard.Nand_Scoreboard_Entries[i] != null){
			myScoreBoard.Nand_Scoreboard_Entries[i] = new scoreboardEntry();
			myScoreBoard.Nand_Scoreboard_Entries[i].address = this.scoreboard.Nand_Scoreboard_Entries[i].address;
			myScoreBoard.Nand_Scoreboard_Entries[i].instruction = new Instruction();
			myScoreBoard.Nand_Scoreboard_Entries[i].instruction.type = this.scoreboard.Nand_Scoreboard_Entries[i].instruction.type;
			myScoreBoard.Nand_Scoreboard_Entries[i].instruction.Fi = this.scoreboard.Nand_Scoreboard_Entries[i].instruction.Fi;
			myScoreBoard.Nand_Scoreboard_Entries[i].instruction.Fj = this.scoreboard.Nand_Scoreboard_Entries[i].instruction.Fj;
			myScoreBoard.Nand_Scoreboard_Entries[i].instruction.Fk = this.scoreboard.Nand_Scoreboard_Entries[i].instruction.Fk;
			myScoreBoard.Nand_Scoreboard_Entries[i].instruction.Vi = this.scoreboard.Nand_Scoreboard_Entries[i].instruction.Vi;
			myScoreBoard.Nand_Scoreboard_Entries[i].instruction.Vj = this.scoreboard.Nand_Scoreboard_Entries[i].instruction.Vj;
			myScoreBoard.Nand_Scoreboard_Entries[i].instruction.Vk = this.scoreboard.Nand_Scoreboard_Entries[i].instruction.Vk;
			myScoreBoard.Nand_Scoreboard_Entries[i].instruction.status = this.scoreboard.Nand_Scoreboard_Entries[i].instruction.status;
			myScoreBoard.Nand_Scoreboard_Entries[i].instruction.Qj = this.scoreboard.Nand_Scoreboard_Entries[i].instruction.Qj;
			myScoreBoard.Nand_Scoreboard_Entries[i].instruction.Qk = this.scoreboard.Nand_Scoreboard_Entries[i].instruction.Qk;
			myScoreBoard.Nand_Scoreboard_Entries[i].instruction.No_of_cycles = this.scoreboard.Nand_Scoreboard_Entries[i].instruction.No_of_cycles;
			myScoreBoard.Nand_Scoreboard_Entries[i].instruction.immediate = this.scoreboard.Nand_Scoreboard_Entries[i].instruction.immediate;
			myScoreBoard.Nand_Scoreboard_Entries[i].instruction.FUindex = this.scoreboard.Nand_Scoreboard_Entries[i].instruction.FUindex;
			myScoreBoard.Nand_Scoreboard_Entries[i].instruction.branchTaken = this.scoreboard.Nand_Scoreboard_Entries[i].instruction.branchTaken;
			myScoreBoard.Nand_Scoreboard_Entries[i].instruction.calculatedAddress = this.scoreboard.Nand_Scoreboard_Entries[i].instruction.calculatedAddress;
			myScoreBoard.Nand_Scoreboard_Entries[i].instruction.address = this.scoreboard.Nand_Scoreboard_Entries[i].instruction.address;
			myScoreBoard.Nand_Scoreboard_Entries[i].instruction.executeCount = this.scoreboard.Nand_Scoreboard_Entries[i].instruction.executeCount;
			myScoreBoard.Nand_Scoreboard_Entries[i].instruction.executionCycles = this.scoreboard.Nand_Scoreboard_Entries[i].instruction.executionCycles;
			myScoreBoard.Nand_Scoreboard_Entries[i].instruction.instID = this.scoreboard.Nand_Scoreboard_Entries[i].instruction.instID;
			myScoreBoard.Nand_Scoreboard_Entries[i].instruction.ROBIndex = this.scoreboard.Nand_Scoreboard_Entries[i].instruction.ROBIndex;
			myScoreBoard.Nand_Scoreboard_Entries[i].dest = this.scoreboard.Nand_Scoreboard_Entries[i].dest;
			myScoreBoard.Nand_Scoreboard_Entries[i].notEmpty = this.scoreboard.Nand_Scoreboard_Entries[i].notEmpty;
			}
		}
		for(int i = 0; i < myScoreBoard.Load_Scoreboard_Entries.length; i++){
			if(this.scoreboard.Load_Scoreboard_Entries[i] != null){
			myScoreBoard.Load_Scoreboard_Entries[i] = new scoreboardEntry();
			myScoreBoard.Load_Scoreboard_Entries[i].address = this.scoreboard.Load_Scoreboard_Entries[i].address;
			myScoreBoard.Load_Scoreboard_Entries[i].instruction = new Instruction();
			myScoreBoard.Load_Scoreboard_Entries[i].instruction.type = this.scoreboard.Load_Scoreboard_Entries[i].instruction.type;
			myScoreBoard.Load_Scoreboard_Entries[i].instruction.Fi = this.scoreboard.Load_Scoreboard_Entries[i].instruction.Fi;
			myScoreBoard.Load_Scoreboard_Entries[i].instruction.Fj = this.scoreboard.Load_Scoreboard_Entries[i].instruction.Fj;
			myScoreBoard.Load_Scoreboard_Entries[i].instruction.Fk = this.scoreboard.Load_Scoreboard_Entries[i].instruction.Fk;
			myScoreBoard.Load_Scoreboard_Entries[i].instruction.Vi = this.scoreboard.Load_Scoreboard_Entries[i].instruction.Vi;
			myScoreBoard.Load_Scoreboard_Entries[i].instruction.Vj = this.scoreboard.Load_Scoreboard_Entries[i].instruction.Vj;
			myScoreBoard.Load_Scoreboard_Entries[i].instruction.Vk = this.scoreboard.Load_Scoreboard_Entries[i].instruction.Vk;
			myScoreBoard.Load_Scoreboard_Entries[i].instruction.status = this.scoreboard.Load_Scoreboard_Entries[i].instruction.status;
			myScoreBoard.Load_Scoreboard_Entries[i].instruction.Qj = this.scoreboard.Load_Scoreboard_Entries[i].instruction.Qj;
			myScoreBoard.Load_Scoreboard_Entries[i].instruction.Qk = this.scoreboard.Load_Scoreboard_Entries[i].instruction.Qk;
			myScoreBoard.Load_Scoreboard_Entries[i].instruction.No_of_cycles = this.scoreboard.Load_Scoreboard_Entries[i].instruction.No_of_cycles;
			myScoreBoard.Load_Scoreboard_Entries[i].instruction.immediate = this.scoreboard.Load_Scoreboard_Entries[i].instruction.immediate;
			myScoreBoard.Load_Scoreboard_Entries[i].instruction.FUindex = this.scoreboard.Load_Scoreboard_Entries[i].instruction.FUindex;
			myScoreBoard.Load_Scoreboard_Entries[i].instruction.branchTaken = this.scoreboard.Load_Scoreboard_Entries[i].instruction.branchTaken;
			myScoreBoard.Load_Scoreboard_Entries[i].instruction.calculatedAddress = this.scoreboard.Load_Scoreboard_Entries[i].instruction.calculatedAddress;
			myScoreBoard.Load_Scoreboard_Entries[i].instruction.address = this.scoreboard.Load_Scoreboard_Entries[i].instruction.address;
			myScoreBoard.Load_Scoreboard_Entries[i].instruction.executeCount = this.scoreboard.Load_Scoreboard_Entries[i].instruction.executeCount;
			myScoreBoard.Load_Scoreboard_Entries[i].instruction.executionCycles = this.scoreboard.Load_Scoreboard_Entries[i].instruction.executionCycles;
			myScoreBoard.Load_Scoreboard_Entries[i].instruction.instID = this.scoreboard.Load_Scoreboard_Entries[i].instruction.instID;
			myScoreBoard.Load_Scoreboard_Entries[i].instruction.ROBIndex = this.scoreboard.Load_Scoreboard_Entries[i].instruction.ROBIndex;
			myScoreBoard.Load_Scoreboard_Entries[i].dest = this.scoreboard.Load_Scoreboard_Entries[i].dest;
			myScoreBoard.Load_Scoreboard_Entries[i].notEmpty = this.scoreboard.Load_Scoreboard_Entries[i].notEmpty;
			}
		}
		for(int i = 0; i < myScoreBoard.Store_Scoreboard_Entries.length; i++){
			if(this.scoreboard.Store_Scoreboard_Entries[i] != null){
			myScoreBoard.Store_Scoreboard_Entries[i] = new scoreboardEntry();
			myScoreBoard.Store_Scoreboard_Entries[i].address = this.scoreboard.Store_Scoreboard_Entries[i].address;
			myScoreBoard.Store_Scoreboard_Entries[i].instruction = new Instruction();
			myScoreBoard.Store_Scoreboard_Entries[i].instruction.type = this.scoreboard.Store_Scoreboard_Entries[i].instruction.type;
			myScoreBoard.Store_Scoreboard_Entries[i].instruction.Fi = this.scoreboard.Store_Scoreboard_Entries[i].instruction.Fi;
			myScoreBoard.Store_Scoreboard_Entries[i].instruction.Fj = this.scoreboard.Store_Scoreboard_Entries[i].instruction.Fj;
			myScoreBoard.Store_Scoreboard_Entries[i].instruction.Fk = this.scoreboard.Store_Scoreboard_Entries[i].instruction.Fk;
			myScoreBoard.Store_Scoreboard_Entries[i].instruction.Vi = this.scoreboard.Store_Scoreboard_Entries[i].instruction.Vi;
			myScoreBoard.Store_Scoreboard_Entries[i].instruction.Vj = this.scoreboard.Store_Scoreboard_Entries[i].instruction.Vj;
			myScoreBoard.Store_Scoreboard_Entries[i].instruction.Vk = this.scoreboard.Store_Scoreboard_Entries[i].instruction.Vk;
			myScoreBoard.Store_Scoreboard_Entries[i].instruction.status = this.scoreboard.Store_Scoreboard_Entries[i].instruction.status;
			myScoreBoard.Store_Scoreboard_Entries[i].instruction.Qj = this.scoreboard.Store_Scoreboard_Entries[i].instruction.Qj;
			myScoreBoard.Store_Scoreboard_Entries[i].instruction.Qk = this.scoreboard.Store_Scoreboard_Entries[i].instruction.Qk;
			myScoreBoard.Store_Scoreboard_Entries[i].instruction.No_of_cycles = this.scoreboard.Store_Scoreboard_Entries[i].instruction.No_of_cycles;
			myScoreBoard.Store_Scoreboard_Entries[i].instruction.immediate = this.scoreboard.Store_Scoreboard_Entries[i].instruction.immediate;
			myScoreBoard.Store_Scoreboard_Entries[i].instruction.FUindex = this.scoreboard.Store_Scoreboard_Entries[i].instruction.FUindex;
			myScoreBoard.Store_Scoreboard_Entries[i].instruction.branchTaken = this.scoreboard.Store_Scoreboard_Entries[i].instruction.branchTaken;
			myScoreBoard.Store_Scoreboard_Entries[i].instruction.calculatedAddress = this.scoreboard.Store_Scoreboard_Entries[i].instruction.calculatedAddress;
			myScoreBoard.Store_Scoreboard_Entries[i].instruction.address = this.scoreboard.Store_Scoreboard_Entries[i].instruction.address;
			myScoreBoard.Store_Scoreboard_Entries[i].instruction.executeCount = this.scoreboard.Store_Scoreboard_Entries[i].instruction.executeCount;
			myScoreBoard.Store_Scoreboard_Entries[i].instruction.executionCycles = this.scoreboard.Store_Scoreboard_Entries[i].instruction.executionCycles;
			myScoreBoard.Store_Scoreboard_Entries[i].instruction.instID = this.scoreboard.Store_Scoreboard_Entries[i].instruction.instID;
			myScoreBoard.Store_Scoreboard_Entries[i].instruction.ROBIndex = this.scoreboard.Store_Scoreboard_Entries[i].instruction.ROBIndex;
			myScoreBoard.Store_Scoreboard_Entries[i].dest = this.scoreboard.Store_Scoreboard_Entries[i].dest;
			myScoreBoard.Store_Scoreboard_Entries[i].notEmpty = this.scoreboard.Store_Scoreboard_Entries[i].notEmpty;
			}
		}
		
		//System.out.println("Old Scoreboard " + myScoreBoard);
		//System.out.println("New Scoreboard " + this.scoreboard);
		

		int bufferSize = this.instructionBuffer.size(); 
		for(int i=0; i<Count && i < bufferSize;i++)
		{
		 if(!issue(this.instructionBuffer.get(0)))
			 break;
		}
		
		//System.out.println(myScoreBoard);
		//System.out.println(this.scoreboard.Add_Scoreboard_Entries);
		//System.out.println(myScoreBoard.Add_Scoreboard_Entries[0]);
		System.out.println(this.ROB);
	//	System.out.println(this.ROB.TailPointer);
		System.out.println("size of inst buffer: " + instructionBuffer.size());
//		System.out.println("Functional Unit instruction: " + Add[0].instruction);
//		System.out.println(myScoreBoard.Add_Scoreboard_Entries[0].instruction.status);
//		System.out.println("Count: " +myScoreBoard.Add_Scoreboard_Entries[0].instruction.executeCount);
//		System.out.println("Total " + myScoreBoard.Add_Scoreboard_Entries[0].instruction.executionCycles);
		
		for(int i=0;i < myScoreBoard.Add_Scoreboard_Entries.length;i++){
		  if(myScoreBoard.Add_Scoreboard_Entries[i] != null){	
			if(myScoreBoard.Add_Scoreboard_Entries[i].notEmpty==true){
				if(myScoreBoard.Add_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("issued")){
					if(myScoreBoard.Add_Scoreboard_Entries[i].instruction.executeCount < myScoreBoard.Add_Scoreboard_Entries[i].instruction.executionCycles)
						execute(this.scoreboard.Add_Scoreboard_Entries[i].instruction,myScoreBoard.Add_Scoreboard_Entries[i].instruction);
						
				}
				else
				if(myScoreBoard.Add_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("executed")){
				  if(myScoreBoard.Add_Scoreboard_Entries[i].instruction.executeCount < myScoreBoard.Add_Scoreboard_Entries[i].instruction.executionCycles)
					  execute(this.scoreboard.Add_Scoreboard_Entries[i].instruction, myScoreBoard.Add_Scoreboard_Entries[i].instruction);
				  else
					write(this.scoreboard.Add_Scoreboard_Entries[i].instruction, myScoreBoard);
				}
				
			}
			else
				if(myScoreBoard.Add_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("written")){
					commit(this.scoreboard.Add_Scoreboard_Entries[i].instruction);
				}
		  }
		}
		
		
		//System.out.println(this.ROB.TailPointer);
		
			for(int i=0;i < myScoreBoard.Subtract_Scoreboard_Entries.length;i++){
			  if(myScoreBoard.Subtract_Scoreboard_Entries[i] != null){
				if(myScoreBoard.Subtract_Scoreboard_Entries[i].notEmpty==true){
					if(myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("issued")){
						if(myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.executeCount < myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.executionCycles)
							execute(this.scoreboard.Subtract_Scoreboard_Entries[i].instruction, myScoreBoard.Subtract_Scoreboard_Entries[i].instruction);
					}
					else
					if(myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("executed")){
					  if(myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.executeCount < myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.executionCycles)
						  execute(this.scoreboard.Subtract_Scoreboard_Entries[i].instruction, myScoreBoard.Subtract_Scoreboard_Entries[i].instruction);
					  else
						write(this.scoreboard.Subtract_Scoreboard_Entries[i].instruction, myScoreBoard);
					}
				}
				else
					if(myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("written") && commit == false){
						commit(this.scoreboard.Subtract_Scoreboard_Entries[i].instruction);
					}
			  }
			}
				for(int i=0;i < myScoreBoard.Multiply_Scoreboard_Entries.length;i++){
				  if(myScoreBoard.Multiply_Scoreboard_Entries[i] != null){
					if(myScoreBoard.Multiply_Scoreboard_Entries[i].notEmpty==true){
						if(myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("issued")){
							if(myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.executeCount < myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.executionCycles)
								execute(this.scoreboard.Multiply_Scoreboard_Entries[i].instruction, myScoreBoard.Multiply_Scoreboard_Entries[i].instruction);
						}
						else
						if(myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("executed")){
							if(myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.executeCount < myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.executionCycles)
							    execute(this.scoreboard.Multiply_Scoreboard_Entries[i].instruction, myScoreBoard.Multiply_Scoreboard_Entries[i].instruction);
							else
							    write(this.scoreboard.Multiply_Scoreboard_Entries[i].instruction, myScoreBoard);
						}
					}
					else
						if(myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("written") && commit == false){
							commit(this.scoreboard.Multiply_Scoreboard_Entries[i].instruction);
						}
				  }
				}
			for(int i=0;i < myScoreBoard.Nand_Scoreboard_Entries.length;i++){
			  if(myScoreBoard.Nand_Scoreboard_Entries[i] != null){
				if(myScoreBoard.Nand_Scoreboard_Entries[i].notEmpty==true){
					if(myScoreBoard.Nand_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("issued")){
						if(myScoreBoard.Nand_Scoreboard_Entries[i].instruction.executeCount < myScoreBoard.Nand_Scoreboard_Entries[i].instruction.executionCycles)
							execute(this.scoreboard.Nand_Scoreboard_Entries[i].instruction, myScoreBoard.Nand_Scoreboard_Entries[i].instruction);
					}
					else
					if(myScoreBoard.Nand_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("executed")){
						if(myScoreBoard.Nand_Scoreboard_Entries[i].instruction.executeCount < myScoreBoard.Nand_Scoreboard_Entries[i].instruction.executionCycles)
							  execute(this.scoreboard.Nand_Scoreboard_Entries[i].instruction, myScoreBoard.Nand_Scoreboard_Entries[i].instruction);
						else
						write(this.scoreboard.Nand_Scoreboard_Entries[i].instruction, myScoreBoard);
					}
				}
				else
					if(myScoreBoard.Nand_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("written") && commit == false){
						commit(this.scoreboard.Nand_Scoreboard_Entries[i].instruction);
					}
			  }
			}
			
			for(int i=0;i < myScoreBoard.Load_Scoreboard_Entries.length;i++){
			  if(myScoreBoard.Load_Scoreboard_Entries[i] != null){
				if(myScoreBoard.Load_Scoreboard_Entries[i].notEmpty==true){
					if(myScoreBoard.Load_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("issued"))
						calculateAddress(this.scoreboard.Load_Scoreboard_Entries[i].instruction);
					else
					if(myScoreBoard.Load_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("Calculated Address")){
						if(myScoreBoard.Load_Scoreboard_Entries[i].instruction.executeCount < myScoreBoard.Load_Scoreboard_Entries[i].instruction.executionCycles)
							execute(this.scoreboard.Load_Scoreboard_Entries[i].instruction, myScoreBoard.Load_Scoreboard_Entries[i].instruction);
					}
					else
					if(myScoreBoard.Load_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("executed")){
						if(myScoreBoard.Load_Scoreboard_Entries[i].instruction.executeCount < myScoreBoard.Load_Scoreboard_Entries[i].instruction.executionCycles)
							execute(this.scoreboard.Load_Scoreboard_Entries[i].instruction, myScoreBoard.Load_Scoreboard_Entries[i].instruction);
						else
							write(this.scoreboard.Load_Scoreboard_Entries[i].instruction, myScoreBoard);
					}
				}
				else
					if(myScoreBoard.Load_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("written") && commit == false){
						commit(this.scoreboard.Load_Scoreboard_Entries[i].instruction);
					}
			  }
			}
			
			for(int i=0;i < myScoreBoard.Store_Scoreboard_Entries.length;i++){
				if(myScoreBoard.Store_Scoreboard_Entries[i] != null){
					if(myScoreBoard.Store_Scoreboard_Entries[i].notEmpty==true){
						if(myScoreBoard.Store_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("issued"))
							calculateAddress(this.scoreboard.Store_Scoreboard_Entries[i].instruction);
						else
						if(myScoreBoard.Store_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("Calculated Address")){
						   if(myScoreBoard.Store_Scoreboard_Entries[i].instruction.executeCount < myScoreBoard.Store_Scoreboard_Entries[i].instruction.executionCycles)
							 execute(this.scoreboard.Store_Scoreboard_Entries[i].instruction, myScoreBoard.Store_Scoreboard_Entries[i].instruction);
						}
						else
						if(myScoreBoard.Store_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("executed")){
							if(myScoreBoard.Store_Scoreboard_Entries[i].instruction.executeCount < myScoreBoard.Store_Scoreboard_Entries[i].instruction.executionCycles)
								execute(this.scoreboard.Store_Scoreboard_Entries[i].instruction, myScoreBoard.Store_Scoreboard_Entries[i].instruction);
							else{
								this.scoreboard.Store_Scoreboard_Entries[i].instruction.executeCount = 0;
								write(this.scoreboard.Store_Scoreboard_Entries[i].instruction, myScoreBoard);
								}
						}
					}
					else
						if(myScoreBoard.Store_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("written")){
							if(myScoreBoard.Store_Scoreboard_Entries[i].instruction.executeCount < myScoreBoard.Store_Scoreboard_Entries[i].instruction.executionCycles)
								write(this.scoreboard.Store_Scoreboard_Entries[i].instruction,myScoreBoard);
							else
							    if(commit == false)
							    	commit(this.scoreboard.Store_Scoreboard_Entries[i].instruction);
						}
				}
			}
			
			commit = false;
			//System.out.println("ScoreBoard Entries: " + myScoreBoard);
//			System.out.println("Functional Unit instruction: " + Nand[0].instruction);
//			System.out.println("Functional Unit instruction: " + Add[0].instruction);
			System.out.println("PC Value" + PC.value);
			System.out.println(this.ROB);
			
			//System.out.println("Old Scoreboard " + myScoreBoard);
			System.out.println("New Scoreboard " + this.scoreboard);
	  		}	
		}
	
		public static void main(String[]args){
			
			int [] FUs = {1,1,1,1,1,1};
			int [] cycles = {2,1,2,1,2,1};
			int ROBsize = 6;
			short startingAddress = 0;
			hardwareOrganization GreatTomasulo = new hardwareOrganization(FUs, cycles, ROBsize, startingAddress);
			((normalRegister) GreatTomasulo.registerFile.registers[2]).value = 3;
			((normalRegister) GreatTomasulo.registerFile.registers[3]).value = 3;
			
			GreatTomasulo.Tomasulo(3);
		}
		
	}
