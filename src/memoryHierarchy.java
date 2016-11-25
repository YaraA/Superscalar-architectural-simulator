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
		this.insertDataInAllCaches(address,ins);
		return (String)ins;
		
		
	}


	private void insertDataInAllCaches(int address, Object data) {
		for(int i=0; i<dataCaches.size();i++){
			dataCaches.get(i).insert(address, data);
		}
		
	}

}
