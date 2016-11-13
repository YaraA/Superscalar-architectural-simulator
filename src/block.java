
public class block {
	
	boolean dirtyBit;
	boolean validBit;
	int mainMemoryAddr;
	Object insOrData;  //in case of dataCache this will hold and in case of insCache this will hold an instruction
	
	public block(boolean dirtyBit, boolean validBit, int mainMemoryAddr, Object insOrData){
		this.dirtyBit= dirtyBit;
		this.validBit= validBit;
		this.mainMemoryAddr= mainMemoryAddr;
		this.insOrData= insOrData;
	
	}

	public boolean isDirtyBit() {
		return dirtyBit;
	}

	public void setDirtyBit(boolean dirtyBit) {
		this.dirtyBit = dirtyBit;
	}

	public boolean isValidBit() {
		return validBit;
	}

	public void setValidBit(boolean validBit) {
		this.validBit = validBit;
	}

	public int getMainMemoryAddr() {
		return mainMemoryAddr;
	}

	public void setMainMemoryAddr(int mainMemoryAddr) {
		this.mainMemoryAddr = mainMemoryAddr;
	}

	public Object getInsOrData() {
		return insOrData;
	}

	public void setInsOrData(Object insOrData) {
		this.insOrData = insOrData;
	}
}
