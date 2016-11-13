
public class Functional_Unit {
    
	UnitType type;
	boolean busy;
	int No_of_exec_cycles;
	Instruction instruction;
	int DestReg;
	int U;
	
	public Functional_Unit(UnitType type, int No_of_exec_cycles){
		this.type = type;
		this.No_of_exec_cycles = No_of_exec_cycles;
	}
	
	int execute(int srcReg1,int srcReg2){
		int result=0;
		
		if((instruction.type.equalsIgnoreCase("add") || instruction.type.equalsIgnoreCase("addi")) && type==UnitType.ADD)
			result=srcReg1+srcReg2;
		if(instruction.type.equalsIgnoreCase("sub") && type==UnitType.SUBTRACT)
			result=srcReg1-srcReg2;
		if(instruction.type.equalsIgnoreCase("mul") && type==UnitType.MULTIPLY)
			result=srcReg1*srcReg2;
		if(instruction.type.equalsIgnoreCase("nand")  && type==UnitType.NAND)
			result=~(srcReg1&srcReg2);
		
		return result;
	}
	
}
