import java.util.ArrayList;


public class memoryHierarchy {
	mainMemory mainMemory;
	//mainMemory insMainMemory;
	ArrayList<cache> dataCaches;
	ArrayList<cache> insCaches;
	int noOfLevels;
	
	public memoryHierarchy(int noOfLevels, int s, int l, int m, int hitWritingPolicy, int missWritingPolicy, int cacheCycles, int memoryCycles, String assemblyProgram, int programStartingaddr, Object programData, int dataAddr){
		this.mainMemory= new mainMemory(memoryCycles);
		//this.insMainMemory= new mainMemory(memoryCycles);
		this.dataCaches= new ArrayList <cache>();
		this.insCaches= new ArrayList <cache>();
		this.noOfLevels= noOfLevels;
		for(int i=0; i<noOfLevels; i++) {
			dataCaches.add(new cache(s,l,m,hitWritingPolicy, missWritingPolicy, cacheCycles));
			insCaches.add(new cache(s,l,m,hitWritingPolicy, missWritingPolicy, cacheCycles));
		}
		mainMemory.insertProgram(assemblyProgram,programStartingaddr);
		mainMemory.insertProgramData(programData,dataAddr);
	}

	
	public mainMemory getMainMemory() {
		return mainMemory;
	}


	public void setMainMemory(mainMemory mainMemory) {
		this.mainMemory = mainMemory;
	}


	public ArrayList<cache> getDataCaches() {
		return dataCaches;
	}

	public void setDataCaches(ArrayList<cache> dataCaches) {
		this.dataCaches = dataCaches;
	}

	public ArrayList<cache> getInsCaches() {
		return insCaches;
	}

	public void setInsCaches(ArrayList<cache> insCaches) {
		this.insCaches = insCaches;
	}

	public int getNoOfLevels() {
		return noOfLevels;
	}

	public void setNoOfLevels(int noOfLevels) {
		this.noOfLevels = noOfLevels;
	}
	
	public short getData(int address){
		
		for(int i=0; i<dataCaches.size();i++){
			if(!dataCaches.get(i).blocks.isEmpty()) {
				Object data= dataCaches.get(i).searchCache(address);
				if(data != null){
					return (short) data;
				}
			}
		}
		Object data= mainMemory.findData(address);
		this.insertDataInAllCaches(address,data);
		return (short)data;
		
		
	}
	public String getInstruction(int address){
		
		Object ins;
		for(int i=0; i<insCaches.size();i++){
			if(!insCaches.get(i).blocks.isEmpty()) {
				ins= insCaches.get(i).searchCache(address);
				if(ins != null){
					return (String) ins;
				}
			}
		}
		ins= mainMemory.findInstruction(address);
		this.insertInsInAllCaches(address,ins);
		return (String)ins;
		
		
	}


	private void insertDataInAllCaches(int address, Object data) {
		for(int i=0; i<dataCaches.size();i++){
			Object[] o=dataCaches.get(i).insert(address, data);
			if(o!=null){
				mainMemory.insertProgramData(o[0], (int) o[1]);
			}
		}
		
	}
	
	private void insertInsInAllCaches(int address, Object ins) {
		for(int i=0; i<insCaches.size();i++){
			Object[] o=insCaches.get(i).insert(address, ins);
			if(o!=null){
				mainMemory.insertProgramData(o[0], (int) o[1]);
			}
		}
		
	}
	public void Write(int addr, Object data){
		Object o;
		int count=0;
		for(int i=0;i<dataCaches.size();i++){
			o=dataCaches.get(i).searchCache(addr);
			if(o == null){
				//writeMiss(i,addr,data);
				//break;
				count++;
			}else{
				if(dataCaches.get(i).hitWritingPolicy==1)
					writeBack(i,addr,data);
				else
					writeThrough(addr,data);
				break;
				
			}
		}
		if(count==dataCaches.size()) {
			writeMiss(addr,data);
		}
	}
	
public void writeMiss(int addr, Object data) {
//	for(int i=0;i<cacheLevel;i++){
//		dataCaches.get(i).insert(addr,data);
//	}
	this.insertDataInAllCaches(addr,data);
	this.mainMemory.insertProgramData(data, addr);
	
}


public void writeBack(int cacheLevel,int addr,Object data) {
	ArrayList<block> blocks= dataCaches.get(cacheLevel).getBlocks();
	int m= dataCaches.get(cacheLevel).getM();
	int c=dataCaches.get(cacheLevel).getC();
	int index= dataCaches.get(cacheLevel).calculateIndex(addr);
	if(m==1){
		blocks.get(index).setInsOrData(data);
		blocks.get(index).setDirtyBit(true);
	}
	if(m>1 && m<c) {
		for(int i=index*m; i<index*m+m; i++) {
			if(addr==blocks.get(i).getMainMemoryAddr()) {
				blocks.get(i).setInsOrData(data);
				blocks.get(i).setDirtyBit(true);
			}
		}
	}
	if(m==c){
		for(int i=0; i<blocks.size();i++) {
			if(addr==blocks.get(i).getMainMemoryAddr()) {
				blocks.get(i).setInsOrData(data);
				blocks.get(i).setDirtyBit(true);
			}		
		}		
	}
	
	
}

public void writeThrough(int addr,Object data) {
	for(int i=0;i<dataCaches.size();i++){
		dataCaches.get(i).insert(addr,data);
		
	}
	//insert in main memory
	mainMemory.insertProgramData(data, addr);
}

}
