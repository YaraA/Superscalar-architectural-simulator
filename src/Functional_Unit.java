
public class Functional_Unit {
    
	UnitType type;
	boolean busy;
	int No_of_exec_cycles;
	Instruction instruction;
	String DestReg;
	
	public Functional_Unit(UnitType type, int No_of_exec_cycles){
		this.type = type;
		this.No_of_exec_cycles = No_of_exec_cycles;
	}
	
	public short execute(){
		short result=0;
		
		short srcReg1 = 0;
		short srcReg2 = 0; 
		short destReg = 0;
		
		if(!instruction.type.equalsIgnoreCase("jmp")){
		   if(instruction.type.equalsIgnoreCase("addi") 
				  || instruction.type.equalsIgnoreCase("beq") 
				  	|| instruction.type.equalsIgnoreCase("lw") 
				  		|| instruction.type.equalsIgnoreCase("sw")){
			 srcReg1 = instruction.Vj;
			 srcReg2 = instruction.immediate;
			 destReg = instruction.Vi;
		 }
		 else{
			 srcReg1 = instruction.Vj;
			 srcReg2 = instruction.Vk;
			 destReg= instruction.Vi;
		 }
		}
		else{
			if(instruction.type.equalsIgnoreCase("jmp"))
				srcReg1 = instruction.immediate;
		    }
		
		if((instruction.type.equalsIgnoreCase("add") 
				|| instruction.type.equalsIgnoreCase("addi") 
					|| instruction.type.equalsIgnoreCase("jmp")) && type==UnitType.ADD)
			result=(short) (srcReg1+srcReg2);
		if((instruction.type.equalsIgnoreCase("sub") 
				|| instruction.type.equalsIgnoreCase("beq")) && type==UnitType.SUBTRACT)
			result=(short) (srcReg1-srcReg2);
		if(instruction.type.equalsIgnoreCase("mul") && type==UnitType.MULTIPLY)
			result=(short) (srcReg1*srcReg2);
		if(instruction.type.equalsIgnoreCase("nand")  && type==UnitType.NAND)
			result=(short) ~(srcReg1&srcReg2);
		if(instruction.type.equalsIgnoreCase("LW")  && type==UnitType.LOAD)
		{
		 short address=(short) (srcReg1+srcReg2);
		 result= readFromMemory(address);
		}
		if(instruction.type.equalsIgnoreCase("SW")  && type==UnitType.STORE)
		{
		short address=(short) (srcReg1+srcReg2);
		writeToMemory(address,destReg);
		}
		
		
		return result;
	}
	
	short readFromMemory(short address){
		return 0;
	}
	
	void writeToMemory(short address, short value){
		
	}
	
}
