import java.util.ArrayList;


public class hardwareOrganization {

	int [] reservationStations;
	normalRegister PC;
	ArrayList<Instruction> instructionBuffer;
	RegisterStatusTable regStatusTable;
	Register_File registerFile;
	memory memory;
	ROB ROB;
	Scoreboard scoreboard;
	
	int [] cycles;
	Functional_Unit [] Add;
	Functional_Unit [] Subtract;
	Functional_Unit [] Nand;
	Functional_Unit [] Mult;
	Functional_Unit [] Load;
	Functional_Unit [] Store;
	
	int branchesEncountered;
	int branchMispredictions;
	ArrayList<Instruction> instructionsCompleted;
	
	
	public hardwareOrganization(int [] FUs,int ROBentries, int [] cycles, int ROBsize, short startingAddress)
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
		PC.value = startingAddress;
	}
	
	public void fetch(int fetchCount){
		String[] instr;
		instr = memory.fetchInstructions(PC.value,fetchCount);
		 
		for(int i = 0; i < instr.length; i++){
		  
		  String[] part1=instr[i].split(" ");
		  String[] part2 = part1[1].split(",");
		
		  Instruction instruction = new Instruction();
		  instruction.type = part1[0];
		  instruction.Fi = part2[0];
		  instruction.address = PC.value;
		  if(!(instruction.type.equalsIgnoreCase("jmp") && instruction.type.equalsIgnoreCase("jalr")))
		  {
		  instruction.Fj = part2[1];
		  if(instruction.type.equalsIgnoreCase("addi") 
				  || instruction.type.equalsIgnoreCase("beq") 
				  	|| instruction.type.equalsIgnoreCase("lw") 
				  		|| instruction.type.equalsIgnoreCase("sw")){
			  instruction.immediate = (byte) Integer.parseInt(part2[2]);
		  }
		  else{
			  instruction.Fk = part2[2];
		  }
		  }
		  else{
			  if(instruction.type.equalsIgnoreCase("jmp"))
				  instruction.immediate = (byte) Integer.parseInt(part2[1]);
			  else{
				  instruction.Fj = part2[1];
			  }
		  }
		  instruction.status = "Fetched";
		  PC.value++;
		
		  instruction.No_of_cycles++;
		  instructionBuffer.add(instruction);
		  
		  if(!(regStatusTable.statusTable.contains(instruction.Fj)))
			  regStatusTable.statusTable.put(instruction.Fi, null);
		}
	}
	
	public boolean issue(Instruction instruction){
		int freeFU = 0;
		boolean freeROB = false;
		int index = 0;
		boolean issued=false;
		
		if(regStatusTable.statusTable.get(instruction.Fi) != null){
			System.out.println("Stalled due to WAW hazard");
		}
		else{
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
			
			for(int i=0;i<ROB.ROBContent.length;i++){
			   if(ROB.ROBContent[i].empty==true){
				    ROB.ROBContent[i].setDestination(instruction.Fi);
					ROB.ROBContent[i].setType(instruction.type);
					ROB.ROBContent[i].setReady(false);
					ROB.ROBContent[i].empty=false;
					issued=true;
			     }
			   }
			if(issued==false){
				System.out.println("Stalled due to ROB being full");
			}
			else
			{
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
								if(registerFile.registers[i].name.equalsIgnoreCase(instruction.Fi))
								{
								 	if(instruction.Fi.equalsIgnoreCase("R0"))
								 		instruction.Vi =((zeroRegister)registerFile.registers[i]).value;
								 	else
								 		instruction.Vi =((normalRegister)registerFile.registers[i]).value;
								}
							}
								 
						}
					 if(!(instruction.type.equalsIgnoreCase("jalr") 
							 	&& instruction.type.equalsIgnoreCase("ret")))
						instruction.Vk = instruction.immediate;
					 
					}
					
				}
				
				if(instruction.type.equalsIgnoreCase("BEQ"))
				{
				 if(instruction.immediate >= 0)
					 instruction.branchTaken = false;
				 else
					 instruction.branchTaken = true;
				}
				
				switch(freeFU)
				{
					case 1: {   Add[index].busy = true;
								Add[index].instruction = instruction;
								Add[index].DestReg = instruction.Fi;
								instruction.FUindex = index;
								scoreboard.Add_Scoreboard_Entries[index].instruction = instruction;
								scoreboard.Add_Scoreboard_Entries[index].dest = ROB.TailPointer;
								scoreboard.Add_Scoreboard_Entries[index].notEmpty = true;
								break;
							}
					case 2: {	Subtract[index].busy = true;
								Subtract[index].instruction = instruction;
								Subtract[index].DestReg = instruction.Fi;
								instruction.FUindex = index;
								scoreboard.Subtract_Scoreboard_Entries[index].instruction = instruction;
								scoreboard.Subtract_Scoreboard_Entries[index].dest = ROB.TailPointer;
								scoreboard.Subtract_Scoreboard_Entries[index].notEmpty = true;
								break;
							}
					case 3: {	
								Mult[index].busy = true;
								Mult[index].instruction = instruction;
								Mult[index].DestReg = instruction.Fi;
								instruction.FUindex = index;
								scoreboard.Multiply_Scoreboard_Entries[index].instruction = instruction;
								scoreboard.Multiply_Scoreboard_Entries[index].dest = ROB.TailPointer;
								scoreboard.Multiply_Scoreboard_Entries[index].notEmpty = true;
								break;
							}
					case 4: {	Nand[index].busy = true;
								Nand[index].instruction = instruction;
								Nand[index].DestReg = instruction.Fi;
								instruction.FUindex = index;
								scoreboard.Nand_Scoreboard_Entries[index].instruction = instruction;
								scoreboard.Nand_Scoreboard_Entries[index].dest = ROB.TailPointer;
								scoreboard.Nand_Scoreboard_Entries[index].notEmpty = true;
								break;
							}
					case 5: {	Load[index].busy = true;
								Load[index].instruction = instruction;
								Load[index].DestReg = instruction.Fi;
								instruction.FUindex = index;
								scoreboard.Load_Scoreboard_Entries[index].instruction = instruction;
								scoreboard.Load_Scoreboard_Entries[index].dest = ROB.TailPointer;
								scoreboard.Load_Scoreboard_Entries[index].notEmpty = true;
								break;
							}
					case 6: {	Store[index].busy = true;
								Store[index].instruction = instruction;
								instruction.FUindex = index;
								scoreboard.Store_Scoreboard_Entries[index].instruction = instruction;
								scoreboard.Store_Scoreboard_Entries[index].dest = ROB.TailPointer;
								scoreboard.Store_Scoreboard_Entries[index].notEmpty = true;
								break;
							}
				}
				
				regStatusTable.statusTable.put(instruction.Fi,ROB.TailPointer);
				
				if(regStatusTable.statusTable.containsKey(instruction.Fj) && regStatusTable.statusTable.get(instruction.Fj) != null){
					instruction.Qj=(int) regStatusTable.statusTable.get(instruction.Fj);
				}
				if(regStatusTable.statusTable.containsKey(instruction.Fk) && regStatusTable.statusTable.get(instruction.Fk) != null){
					instruction.Qk=(int) regStatusTable.statusTable.get(instruction.Fk);
				}
				
				if(regStatusTable.statusTable.containsKey(instruction.Fi) && regStatusTable.statusTable.get(instruction.Fi) != null){
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
		}
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
			instruction.calculatedAddress = (short) (instruction.Vj + instruction.immediate);
			scoreboard.Store_Scoreboard_Entries[instruction.FUindex].address = instruction.calculatedAddress;
			}
		
		instruction.No_of_cycles++;
	}
	
	public void execute(Instruction instruction){
		if(instruction.executeCount == 0){
		if(instruction.Qj != -1 || instruction.Qk != -1){
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
				instruction.No_of_cycles+= Add[instruction.FUindex].No_of_exec_cycles;
			}
			else{
				if(instruction.type.equalsIgnoreCase("sub") || instruction.type.equalsIgnoreCase("beq")){
					
					instruction.Vi = Subtract[instruction.FUindex].execute();
					if(instruction.type.equalsIgnoreCase("beq")){
						branchesEncountered++;
						short address = 0;
						if(instruction.Vi == 0)
						{
						 address = (short) (PC.value + 1 + instruction.immediate); 
						}
						else{
							address = (short) (PC.value + 1);
							}
					}
					instruction.No_of_cycles+= Subtract[instruction.FUindex].No_of_exec_cycles;
				}
				else{
					if(instruction.type.equalsIgnoreCase("mul")){
						instruction.Vi = Mult[instruction.FUindex].execute();
						instruction.No_of_cycles+= Mult[instruction.FUindex].No_of_exec_cycles;
					}
					else{
						if(instruction.type.equalsIgnoreCase("nand")){
							instruction.Vi = Nand[instruction.FUindex].execute();
							instruction.No_of_cycles+= Nand[instruction.FUindex].No_of_exec_cycles;
						}
						else{
							if(instruction.type.equalsIgnoreCase("lw")){
								instruction.Vi = Load[instruction.FUindex].execute();
								instruction.No_of_cycles+= Load[instruction.FUindex].No_of_exec_cycles;
							}
							else{
								if(instruction.type.equalsIgnoreCase("sw")){
									//no destination
									Store[instruction.FUindex].execute();
									instruction.No_of_cycles+= Store[instruction.FUindex].No_of_exec_cycles;
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
		}
		}
		else{
			if(instruction.executeCount < instruction.executionCycles && instruction.status.equalsIgnoreCase("Executed"))
				instruction.executeCount++;
		}
	}
	
	public void write(Instruction instruction){
		
		int ROBId = -1;
		
		if(instruction.type.equalsIgnoreCase("add") || instruction.type.equalsIgnoreCase("addi")){
			ROBId = scoreboard.Add_Scoreboard_Entries[instruction.FUindex].dest;
			scoreboard.Add_Scoreboard_Entries[instruction.FUindex].notEmpty = false;
			}
		else
			if(instruction.type.equalsIgnoreCase("sub")){
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
			if(scoreboard.Add_Scoreboard_Entries[i].instruction.Qj == ROBId)
				scoreboard.Add_Scoreboard_Entries[i].instruction.Qj = -1;
				
			if(scoreboard.Add_Scoreboard_Entries[i].instruction.Qk == ROBId)
					scoreboard.Add_Scoreboard_Entries[i].instruction.Qk = -1;
		}
		
		for(int i = 0; i < scoreboard.Subtract_Scoreboard_Entries.length; i++){
			if(scoreboard.Subtract_Scoreboard_Entries[i].instruction.Qj == ROBId)
				scoreboard.Subtract_Scoreboard_Entries[i].instruction.Qj = -1;
				
			if(scoreboard.Subtract_Scoreboard_Entries[i].instruction.Qk == ROBId)
					scoreboard.Subtract_Scoreboard_Entries[i].instruction.Qk = -1;
		}
		
		for(int i = 0; i < scoreboard.Multiply_Scoreboard_Entries.length; i++){
			if(scoreboard.Multiply_Scoreboard_Entries[i].instruction.Qj == ROBId)
				scoreboard.Multiply_Scoreboard_Entries[i].instruction.Qj = -1;
				
			if(scoreboard.Multiply_Scoreboard_Entries[i].instruction.Qk == ROBId)
					scoreboard.Multiply_Scoreboard_Entries[i].instruction.Qk = -1;
		}
		
		for(int i = 0; i < scoreboard.Nand_Scoreboard_Entries.length; i++){
			if(scoreboard.Nand_Scoreboard_Entries[i].instruction.Qj == ROBId)
				scoreboard.Nand_Scoreboard_Entries[i].instruction.Qj = -1;
				
			if(scoreboard.Nand_Scoreboard_Entries[i].instruction.Qk == ROBId)
					scoreboard.Nand_Scoreboard_Entries[i].instruction.Qk = -1;
		}
		
		for(int i = 0; i < scoreboard.Load_Scoreboard_Entries.length; i++){
			if(scoreboard.Load_Scoreboard_Entries[i].instruction.Qj == ROBId)
				scoreboard.Load_Scoreboard_Entries[i].instruction.Qj = -1;
			
			if(scoreboard.Load_Scoreboard_Entries[i].instruction.Qk == ROBId)
					scoreboard.Load_Scoreboard_Entries[i].instruction.Qk = -1;
		}
		
		for(int i = 0; i < scoreboard.Store_Scoreboard_Entries.length; i++){
			if(scoreboard.Store_Scoreboard_Entries[i].instruction.Qj == ROBId)
				scoreboard.Store_Scoreboard_Entries[i].instruction.Qj = -1;
			
			if(scoreboard.Store_Scoreboard_Entries[i].instruction.Qk == ROBId)
					scoreboard.Store_Scoreboard_Entries[i].instruction.Qk = -1;
		}
		
		ROB.ROBContent[ROBId].setReady(true);
		ROB.ROBContent[ROBId].setValue(instruction.Vi);
		instruction.status="written";
		instruction.No_of_cycles++;
		
	}
	
	public void commit(Instruction instruction){
		
		for (int i=0; i<ROB.ROBContent.length;i++){
			if(instruction.Fi == ROB.ROBContent[i].getDestination()){
				if(ROB.ROBContent[i].getId() == ROB.HeadPointer){
					
					if(((int)regStatusTable.statusTable.get(instruction.Fi)) == ROB.ROBContent[i].getId()){
						regStatusTable.statusTable.remove(instruction.Fi);
						regStatusTable.statusTable.put(instruction.Fi,null);
				    }
				    for(int k=0;k<registerFile.registers.length;k++){
				    	if(!registerFile.registers[k].name.equalsIgnoreCase("R0")){
				    		if(registerFile.registers[k].name.equals(instruction.Fi)){
				    			((normalRegister)registerFile.registers[k]).value = instruction.Vi;
				    		}
				    	}
				    	
				    }
				    ROB.ROBContent[i].empty=true;
				    
				    if(ROB.HeadPointer == ROB.ROBContent.length -1)
						  ROB.HeadPointer = 0;
					else
						ROB.HeadPointer++;
				    
				    instruction.status="Commited";
				    instructionsCompleted.add(instruction);
				    break;
					}
				}
			
		}
			
	}
	
	public void Tomasulo(int Count){
		
		//Scoreboard myScoreBoard = new Scoreboard(this.scoreboard.Add_Scoreboard_Entries.length,this.scoreboard.Subtract_Scoreboard_Entries.length,this.scoreboard.Multiply_Scoreboard_Entries.length,this.scoreboard.Nand_Scoreboard_Entries.length,this.scoreboard.Load_Scoreboard_Entries.length,this.scoreboard.Store_Scoreboard_Entries.length);
		//myScoreBoard.Add_Scoreboard_Entries = this.scoreboard.Add_Scoreboard_Entries;
		
		Scoreboard myScoreBoard = this.scoreboard;
		fetch(Count);
		
		for(int i=0; i<Count && i < this.instructionBuffer.size();i++)
		{
		 if(!issue(this.instructionBuffer.get(i)))
			 break;
		}
		
		for(int i=0;i < myScoreBoard.Add_Scoreboard_Entries.length;i++){
			if(myScoreBoard.Add_Scoreboard_Entries[i].notEmpty==true){
				if(myScoreBoard.Add_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("isssued")){
					while(myScoreBoard.Add_Scoreboard_Entries[i].instruction.executeCount < myScoreBoard.Add_Scoreboard_Entries[i].instruction.executionCycles)
						execute(myScoreBoard.Add_Scoreboard_Entries[i].instruction);
				}
				if(myScoreBoard.Add_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("executed")){
					write(myScoreBoard.Add_Scoreboard_Entries[i].instruction);
				}
				if(myScoreBoard.Add_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("written")){
					commit(myScoreBoard.Add_Scoreboard_Entries[i].instruction);
				}
			}
		}
			for(int i=0;i < myScoreBoard.Subtract_Scoreboard_Entries.length;i++){
				if(myScoreBoard.Subtract_Scoreboard_Entries[i].notEmpty==true){
					if(myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("isssued")){
						while(myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.executeCount < myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.executionCycles)
							execute(myScoreBoard.Subtract_Scoreboard_Entries[i].instruction);
					}
					if(myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("executed")){
						write(myScoreBoard.Subtract_Scoreboard_Entries[i].instruction);
					}
					if(myScoreBoard.Subtract_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("written")){
						commit(myScoreBoard.Subtract_Scoreboard_Entries[i].instruction);
					}
				}
			}
				for(int i=0;i < myScoreBoard.Multiply_Scoreboard_Entries.length;i++){
					if(myScoreBoard.Multiply_Scoreboard_Entries[i].notEmpty==true){
						if(myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("isssued")){
							while(myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.executeCount < myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.executionCycles)
								execute(myScoreBoard.Multiply_Scoreboard_Entries[i].instruction);
						}
						if(myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("executed")){
							write(myScoreBoard.Multiply_Scoreboard_Entries[i].instruction);
						}
						if(myScoreBoard.Multiply_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("written")){
							commit(myScoreBoard.Multiply_Scoreboard_Entries[i].instruction);
						}
					}
				}
			for(int i=0;i < myScoreBoard.Nand_Scoreboard_Entries.length;i++){
				if(myScoreBoard.Nand_Scoreboard_Entries[i].notEmpty==true){
					if(myScoreBoard.Nand_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("isssued")){
						while(myScoreBoard.Nand_Scoreboard_Entries[i].instruction.executeCount < myScoreBoard.Nand_Scoreboard_Entries[i].instruction.executionCycles)
							execute(myScoreBoard.Nand_Scoreboard_Entries[i].instruction);
					}
					if(myScoreBoard.Nand_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("executed")){
						write(myScoreBoard.Nand_Scoreboard_Entries[i].instruction);
					}
					if(myScoreBoard.Nand_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("written")){
						commit(myScoreBoard.Nand_Scoreboard_Entries[i].instruction);
					}
				}
			}
			for(int i=0;i < myScoreBoard.Load_Scoreboard_Entries.length;i++){
				if(myScoreBoard.Load_Scoreboard_Entries[i].notEmpty==true){
					if(myScoreBoard.Load_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("isssued")){
						while(myScoreBoard.Load_Scoreboard_Entries[i].instruction.executeCount < myScoreBoard.Load_Scoreboard_Entries[i].instruction.executionCycles)
							execute(myScoreBoard.Load_Scoreboard_Entries[i].instruction);
					}
					if(myScoreBoard.Load_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("executed")){
						write(myScoreBoard.Load_Scoreboard_Entries[i].instruction);
					}
					if(myScoreBoard.Load_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("written")){
						commit(myScoreBoard.Load_Scoreboard_Entries[i].instruction);
					}
				}
			}
			for(int i=0;i < myScoreBoard.Store_Scoreboard_Entries.length;i++){
					if(myScoreBoard.Store_Scoreboard_Entries[i].notEmpty==true){
						if(myScoreBoard.Store_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("isssued")){
							while(myScoreBoard.Store_Scoreboard_Entries[i].instruction.executeCount < myScoreBoard.Store_Scoreboard_Entries[i].instruction.executionCycles)
							execute(myScoreBoard.Store_Scoreboard_Entries[i].instruction);
						}
						if(myScoreBoard.Store_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("executed")){
							write(myScoreBoard.Store_Scoreboard_Entries[i].instruction);
						}
						if(myScoreBoard.Store_Scoreboard_Entries[i].instruction.status.equalsIgnoreCase("written")){
							commit(myScoreBoard.Store_Scoreboard_Entries[i].instruction);
						}
					}
			}
			
		}
		
	}
