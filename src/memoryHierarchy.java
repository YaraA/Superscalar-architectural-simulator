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

}
