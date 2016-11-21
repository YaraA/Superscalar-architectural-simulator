import java.util.ArrayList;


public class hardwareOrganization {

	int [] reservationStations;
	normalRegister PC;
	InstructionBuffer instructionBuffer;
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
		int scoreboard_size = Add.length + Subtract.length + Nand.length + Mult.length + Load.length + Store.length;
		scoreboard = new Scoreboard(scoreboard_size);
		
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
		
		this.instructionBuffer = new InstructionBuffer();
		PC.value = startingAddress;
	}
	
	public void fetch(int address,int fetchCount){
		String[] instr;
		instr = memory.fetchInstructions(address,fetchCount);
		 
		for(int i = 0; i < instr.length; i++){
		  
		  String[] part1=instr[i].split(" ");
		  String[] part2 = part1[1].split(",");
		
		  Instruction instruction = new Instruction();
		  instruction.type = part1[0];
		  instruction.Fi = part2[0];
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
		
		  instruction.No_of_cycles++;
		  instructionBuffer.fetchedInstructions.add(instruction);
		  instructionBuffer.count++;
		  
		  if(!(regStatusTable.statusTable.contains(instruction.Fj)))
			  regStatusTable.statusTable.put(instruction.Fi, null);
		}
	}
	
	public void issue(Instruction instruction){
		int freeFU = 0;
		boolean freeROB = false;
		int index = 0;
		
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
			boolean issued=false;
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
				
				
				switch(freeFU)
				{
					case 1: {   Add[index].busy = true;
								Add[index].instruction = instruction;
								Add[index].DestReg = instruction.Fi;
								instruction.FUindex = index;
								break;
							}
					case 2: {	Subtract[index].busy = true;
								Subtract[index].instruction = instruction;
								Subtract[index].DestReg = instruction.Fi;
								instruction.FUindex = index;
								break;
							}
					case 3: {	Mult[index].busy = true;
								Mult[index].instruction = instruction;
								Mult[index].DestReg = instruction.Fi;
								instruction.FUindex = index;
								break;
							}
					case 4: {	Nand[index].busy = true;
								Nand[index].instruction = instruction;
								Nand[index].DestReg = instruction.Fi;
								instruction.FUindex = index;
								break;
							}
					case 5: {	Load[index].busy = true;
								Load[index].instruction = instruction;
								Load[index].DestReg = instruction.Fi;
								instruction.FUindex = index;
								break;
							}
					case 6: {	Store[index].busy = true;
								Store[index].instruction = instruction;
								instruction.FUindex = index;
								break;
							}
				}
				
				instruction.status = "Issued";
				
				regStatusTable.statusTable.put(instruction.Fi,ROB.ROBContent[ROB.TailPointer].getId());
				
				if(regStatusTable.statusTable.containsKey(instruction.Fj) && regStatusTable.statusTable.get(instruction.Fj) != null){
					instruction.Qj=(int) regStatusTable.statusTable.get(instruction.Fj);
				}
				if(regStatusTable.statusTable.containsKey(instruction.Fk) && regStatusTable.statusTable.get(instruction.Fk) != null){
					instruction.Qk=(int) regStatusTable.statusTable.get(instruction.Fk);
				}
				
				if(ROB.TailPointer == ROB.ROBContent.length -1)
				  ROB.TailPointer = 0;
				else
					ROB.TailPointer++;
				
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
			}
			//else System.out.println("There is no free ROB entries");	
		  }
		  else System.out.println("There is no free functional unit");
		}
		instruction.No_of_cycles++;
	}
	
	public void execute(Instruction instruction){
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
						 //prediction part
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
	
	public void write(Instruction instruction){
		boolean stalled=false;
		for(int i = 0; i < scoreboard.Scoreboard_Entries.length; i++){
			if(scoreboard.Scoreboard_Entries[i].instruction != null 
					&& (scoreboard.Scoreboard_Entries[i].instruction.Fj.equalsIgnoreCase(instruction.Fi)
							|| scoreboard.Scoreboard_Entries[i].instruction.Fk.equalsIgnoreCase(instruction.Fi)))
			{ 
				stalled=true;
				System.out.println("Stalled due to WAR hazard");
				break;
			}
		}
		if(stalled==false){
		int ROBId=(int) regStatusTable.statusTable.get(instruction.Fi);
		for(int i = 0; i < scoreboard.Scoreboard_Entries.length; i++){
			if( scoreboard.Scoreboard_Entries[i].instruction != null 
					&&  scoreboard.Scoreboard_Entries[i].dest==ROBId){
				 scoreboard.Scoreboard_Entries[i].instruction=null;
				 scoreboard.Scoreboard_Entries[i].address=-1;
				 scoreboard.Scoreboard_Entries[i].dest=-1;
				 
				 
			}
			
			
		}
		for(int i=0; i<ROB.ROBContent.length;i++){
			if(ROB.ROBContent[i].getId()==ROBId){
				ROB.ROBContent[i].setReady(true);
				ROB.ROBContent[i].setValue(instruction.Vi);
				
			}
			
			
		}
		instruction.status="written";
		
		}
		
		
		
		instruction.No_of_cycles++;
	}
	
	public void commit(Instruction instruction){
		
		for (int i=0; i<ROB.ROBContent.length;i++){
			if(instruction.Fi==ROB.ROBContent[i].getDestination()){
				if(ROB.ROBContent[i].getId()==ROB.HeadPointer){
					ROB.ROBContent[i].empty=true;
				    regStatusTable.statusTable.remove(instruction.Fi);
				    regStatusTable.statusTable.put(instruction.Fi,null);
				    for(int k=0;k<registerFile.registers.length;k++){
				    	if(!registerFile.registers[k].name.equalsIgnoreCase("R0")){
				    		if(registerFile.registers[k].name.equals(instruction.Fi)){
				    			((normalRegister)registerFile.registers[k]).value = instruction.Vi;
				    		}
				    	}
				    	
				    }
				    
				    if(ROB.HeadPointer == ROB.ROBContent.length -1)
						  ROB.HeadPointer = 0;
						else
							ROB.HeadPointer++;
			       }
				}
		}
		instruction.status="Commit";
		
	}
	
	
	
	
}
