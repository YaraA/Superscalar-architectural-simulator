import java.util.ArrayList;


public class mainMemory {
	
	int memoryCycles; //main memory access time 
	int capacity= (64/2 * 1024); //64KB //if word addressable /2 if byte addressable remove the division 
//	ArrayList<String> insMainMem;
//	ArrayList<Object> dataMainMem;
	String[] insMainMem;
	Object[] dataMainMem;
	
	public mainMemory(int memoryCycles) {
		this.memoryCycles= memoryCycles;
//		this.insMainMem= new ArrayList<String>(capacity);
//		this.dataMainMem= new ArrayList<Object>(capacity);
		this.insMainMem= new String[capacity];
		this.dataMainMem= new Object[capacity];		
	}

	public int getMemoryCycles() {
		return memoryCycles;
	}

	public void setMemoryCycles(int memoryCycles) {
		this.memoryCycles = memoryCycles;
	}

	public int getCapacity() {
		return capacity;
	}
	

//	public ArrayList<String> getInsMainMem() {
//		return insMainMem;
//	}
//
//	public void setInsMainMem(ArrayList<String> insMainMem) {
//		this.insMainMem = insMainMem;
//	}

//	public ArrayList<Object> getDataMainMem() {
//		return dataMainMem;
//	}
//
//	public void setDataMainMem(ArrayList<Object> dataMainMem) {
//		this.dataMainMem = dataMainMem;
//	}

	public String[] getInsMainMem() {
		return insMainMem;
	}

	public void setInsMainMem(String[] insMainMem) {
		this.insMainMem = insMainMem;
	}

	public Object[] getDataMainMem() {
		return dataMainMem;
	}

	public void setDataMainMem(Object[] dataMainMem) {
		this.dataMainMem = dataMainMem;
	}

	public void insertProgram(String assemblyProgram, int programStartingaddr) {
		String[] linesOfCode= assemblyProgram.split("/");
		int noOfLines= linesOfCode.length; //number of lines in assembly program
		for(int i=0; i<noOfLines; i++) {
			insMainMem[programStartingaddr +i] = linesOfCode[i]; 
			System.out.println("In mainMem: " + programStartingaddr +i +"code: " + linesOfCode[i]  );
		}
		
	}

	public void insertProgramData(Object[] programData, int[] dataAddr) {
		System.out.println(programData.length);
		System.out.println(dataAddr.length);
		System.out.println(dataMainMem.length);
		for(int i=0; i<programData.length;i++) {
			dataMainMem[dataAddr[i]] = programData[i]; 
		}		
	}
	
	public String findInstruction (int instructionAddr) { //retrieve a certain instruction from insMainMem
		return insMainMem[instructionAddr];	
	}
	
	public Object findData (int dataAddr) { //retrieve a certain data from dataMainMem
//		return dataMainMem.get(dataAddr);	
		return dataMainMem[dataAddr];	
	}
	
	public void updateDataMem(int dataAddr, Object newData) {
//		dataMainMem.set(dataAddr, newData);	
		dataMainMem[dataAddr] = newData;	
	}
	
	
	
	
	
}
