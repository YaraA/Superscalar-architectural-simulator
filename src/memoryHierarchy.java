import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class memoryHierarchy {
	mainMemory mainMemory;
	// mainMemory insMainMemory;
	ArrayList<cache> dataCaches;
	ArrayList<cache> insCaches;
	int noOfLevels;

	public memoryHierarchy(int noOfLevels, int[] s, int[] l, int[] m,
			int[] hitWritingPolicy, int[] cacheCycles, int memoryCycles,
			String assemblyProgram, int programStartingaddr,
			Object[] programData, int[] dataAddr) {
		this.mainMemory = new mainMemory(memoryCycles);
		// this.insMainMemory= new mainMemory(memoryCycles);
		this.dataCaches = new ArrayList<cache>();
		this.insCaches = new ArrayList<cache>();
		this.noOfLevels = noOfLevels;
		for (int i = 0; i < noOfLevels; i++) {
			dataCaches.add(new cache(s[i], l[i], m[i], hitWritingPolicy[i],
					cacheCycles[i]));
			insCaches.add(new cache(s[i], l[i], m[i], hitWritingPolicy[i],
					cacheCycles[i]));
		}
		mainMemory.insertProgram(assemblyProgram, programStartingaddr);
		mainMemory.insertProgramData(programData, dataAddr);
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

	public short getData(int address) {
		System.out.println("in getData + address" + address);
		Object data = null;
		boolean found = false;
		int count = 0;
		System.out.println("in getData + dataCaches.size" + dataCaches.size());
		System.out.println("size of block: " + dataCaches.get(0).blocks.length);
		
		for (int i = 0; i < dataCaches.size(); i++) {
			
			if (!(dataCaches.get(i).blocks.length == 0)) {
				data = dataCaches.get(i).searchCache(address);
				if (data != null) {
					// return (short) data;
					found = true;
					break;
				} else {
					count++;
				}

			} else
				// comes here only if first cache is empty
				count++;

		}
		System.out.println("found "+ found + " count " + count);
		if (count == 0 && !found)
			insertIn(dataCaches.size(), address, data);
		else if (count==dataCaches.size()) {
			
			data = mainMemory.findData(address);
			// this.insertDataInAllCaches(address,data);
			this.insertIn(dataCaches.size(), address, data);
		}
		else {
			System.out.println("in else");
			this.insertIn(count, address, data);
		}
		
		return (short) new Integer ((int) data).intValue();

	}

	private void insertIn(int count, int address, Object data) {
		for (int i = 0; i < count; i++) {
			Object[] o = dataCaches.get(i).insert(address, data); //o[1]: address; o[0]: data
			System.out.println("insertt");
			System.out.println(o[0] + "data being replaced");
			System.out.println(o[1] + "address being replaced");
			if (o[0]!= null && o[1]!=null) { // if insert caused replacement of dirty block
				
				
				mainMemory.updateDataMem((int) o[1], o[0]);
				for (int j = i + 1; j < noOfLevels; j++) {
					block[] blocks = dataCaches.get(j).getBlocks();
					int m = dataCaches.get(j).getM();
					int c = dataCaches.get(j).getC();
					int index = dataCaches.get(j).calculateIndex((int) o[1]);
					if (m == 1) {
						blocks[index].setInsOrData(o[0]);
					}
					if (m > 1 && m < c) {
						for (int k = index * m; k < index * m + m; k++) {
							if ((int) o[1] == blocks[k].getMainMemoryAddr()) {
								blocks[k].setInsOrData(o[0]);
							}
						}
					}
					if (m == c) {
						for (int k = 0; k < blocks.length; k++) {
							if ((int) o[1] == blocks[k].getMainMemoryAddr()) {
								blocks[k].setInsOrData(o[0]);
							}
						}
					}

				}
			}
		}

	}

	public String getInstruction(int address) {

		Object ins;
		for (int i = 0; i < insCaches.size(); i++) {
			if (!(insCaches.get(i).blocks.length == 0)) {
				ins = insCaches.get(i).searchCache(address);
				if (ins != null) {
					return (String) ins;
				}
			}
		}
		ins = mainMemory.findInstruction(address);
		this.insertInsInAllCaches(address, ins);
		return (String) ins;

	}

	// private void insertDataInAllCaches(int address, Object data) {
	// for(int i=0; i<dataCaches.size();i++){
	// Object[] o=dataCaches.get(i).insert(address, data);
	// if(o!=null){
	// mainMemory.insertProgramData(o[0], (int) o[1]);
	// }
	// }
	//
	// }

	private void insertInsInAllCaches(int address, Object ins) {
		for (int i = 0; i < insCaches.size(); i++) {
			Object[] o = insCaches.get(i).insert(address, ins);
			if (o[0] != null && o[1] != null) {
				mainMemory.insertProgram((String) o[0], (int) o[1]);
			}

		}

	}

	public void Write(int addr, Object data) {
		Object o;
		int count = 0;
		for (int i = 0; i < dataCaches.size(); i++) {
			o = dataCaches.get(i).searchCache(addr);
			if (o == null) {
				count++;
			} else {
				if (dataCaches.get(i).hitWritingPolicy == 1)
					writeBack(i, addr, data);
				else
					writeThrough(addr, data);
				break;

			}
		}
		if (count == dataCaches.size()) { // not in any of the caches, update in
											// mem then cache in all levels
			writeMiss(count, addr, data);
		}
	}

	public void writeMiss(int count, int addr, Object data) {
		this.mainMemory.updateDataMem(addr, data);
		this.insertIn(count, addr, data);

	}

	public void writeBack(int cacheLevel, int addr, Object data) {
		block [] blocks = dataCaches.get(cacheLevel).getBlocks();
		int m = dataCaches.get(cacheLevel).getM();
		int c = dataCaches.get(cacheLevel).getC();
		int index = dataCaches.get(cacheLevel).calculateIndex(addr);
		if (m == 1) {
			if (addr == blocks[index].getMainMemoryAddr()) {
			blocks[index].setInsOrData(data);
			blocks[index].setDirtyBit(true);
			}
		}
		if (m > 1 && m < c) {
			for (int i = index * m; i < index * m + m; i++) {
				if (addr == blocks[i].getMainMemoryAddr()) {
					blocks[i].setInsOrData(data);
					blocks[i].setDirtyBit(true);
				}
			}
		}
		if (m == c) {
			for (int i = 0; i < blocks.length; i++) {
				if (addr == blocks[i].getMainMemoryAddr()) {
					blocks[i].setInsOrData(data);
					blocks[i].setDirtyBit(true);
				}
			}
		}
		insertIn(cacheLevel, addr, data);
	}

	public void writeThrough(int addr, Object data) {
		insertIn(dataCaches.size(), addr, data);
		// insert in main memory
		mainMemory.updateDataMem(addr, data);

	}

	public static void main(String[] args) throws Exception {
		// 11 inputs
		// System.out.println("Please enter the Number of Cache Levels, the size of the cache in the form of array, the size of the cache block(line) in the form of array , associativity number of sets, the Hit Write Policy(1 for write back and 2 for write through) in the form of array"
		// +
		// ",the cache Cycles in the form of arrays, the memory cycles, the assembly program, the program starting address, the program data, the data address:");
		// System.out.println("Note: seperate your input with a '/' and written in that order. Thank you!");
		// System.out.println("Please enter the Number of Cache Levels");
		// BufferedReader addr = new BufferedReader (new
		// InputStreamReader(System.in));
		// String sAddr= addr.readLine();
		// int levels=Integer.parseInt(sAddr);
		// int[] m= new int[9];
		// int[] associativity= new int[levels];
		int levels = 2;
		int[] associativity = { 1,1};
		int[] sizeOfCache = { 32,64};
		int[] sizeOfCacheLine = { 16,16};
		int[] hitPolicy = { 2,2};
		int[] cyclesNumber = { 1,1};
		int memoryCycles = 30;
		String programAssembly = "ADD R1,R2/ ADD R1,R2/ ADD R1,R2/ ADD R1,R2/SUB R1,R2/ADD R1,R2/ADD R1,R2/ADD R1,R2";
		int programStartingAddress = 0;
		
		int[] programDataAddress = { 320, 500, 160};
		Object[] programData =     { 5, 6, 7};
		// int[] sizeOfCache = new int[levels];
		// int [] sizeOfCacheLine = new int [levels];
		// int [] hitPolicy = new int [levels];
		// int [] cyclesNumber = new int [levels];
		// System.out.println("Please enter the size of the caches separated by commas.");
		// String[] sAddr1 = sAddr.split(",");
		// for(int i=0; i<levels;i++){
		// sizeOfCache[i]=Integer.parseInt(sAddr1[i]);
		// }
		// System.out.println("Please enter the size of the cache block(line) separated by commas.");
		// String[] sAddr2 = sAddr.split(",");
		// for(int i=0; i<levels;i++){
		// sizeOfCacheLine[i]=Integer.parseInt(sAddr2[i]);
		// }
		// System.out.println("Please enter the associativity number of sets separated by commas.");
		// String[] sAddr3 = sAddr.split(",");
		// for(int i=0; i<levels;i++){
		// associativity[i]=Integer.parseInt(sAddr3[i]);
		// }
		// System.out.println("Please enter the cache Cycles separated by commas.");
		// String[] sAddr4 = sAddr.split(",");
		// for(int i=0; i<levels;i++){
		// cyclesNumber[i]=Integer.parseInt(sAddr4[i]);
		// }
		// System.out.println("Please enter the Hit Write Policy(1 for write back and 2 for write through) for each cache separated by commas.");
		// String[] sAddr5 = sAddr.split(",");
		// for(int i=0; i<levels;i++){
		// hitPolicy[i]=Integer.parseInt(sAddr5[i]);
		// }
		// System.out.println("Please enter the memory cycles");
		// int memoryCycles= Integer.parseInt(sAddr);
		//
		// System.out.println("Please enter assembly program separated by /");
		// String programAssembly= sAddr;
		//
		// System.out.println("Please enter program starting address");
		// int programStartingAddress = Integer.parseInt(sAddr);

		// System.out.println("Please enter all program data separated by commas");
		// String[] sAddr7 = sAddr.split(",");
		// Object [] programData = new Object [sAddr7.length];
		// for(int i=0; i<sAddr7.length;i++){
		// programData[i]=(sAddr7[i]);
		// }
		// System.out.println("Please enter all data's addresses respectively separated by commas");
		// String[] sAddr8 = sAddr.split(",");
		// int [] programDataAddress = new int [sAddr8.length];
		// for(int i=0; i<sAddr8.length;i++){
		// programDataAddress[i]=(Integer.parseInt(sAddr8[i]));
		// }
		//
		// m[0]=Integer.parseInt(sAddr1[0]);//number of cache levels
		// m[1]=Integer.parseInt(sAddr1[1]);//size of cache
		// m[2]=Integer.parseInt(sAddr1[2]);//size of cache block/line
		// m[3]=Integer.parseInt(sAddr1[3]);//associativity of sets
		// m[4]=Integer.parseInt(sAddr1[4]);//hit write policy
		// m[5]=Integer.parseInt(sAddr1[5]);//cache cycles
		// m[6]=//memory cycles
		// m[7]=Integer.parseInt(sAddr1[8]);//program starting address
		// m[8]=Integer.parseInt(sAddr1[10]);//data address
		// String[] s= new String[2];
		// s[0]=sAddr1[7];//assembly program
		// s[1]=sAddr1[9];//program data
		// memoryHierarchy(int noOfLevels, int [] s, int [] l, int[] m, int[]
		// hitWritingPolicy, int[] cacheCycles, int memoryCycles, String
		// assemblyProgram, int programStartingaddr, Object programData, int
		// dataAddr);
		memoryHierarchy mH = new memoryHierarchy(levels, sizeOfCache,
				sizeOfCacheLine, associativity, hitPolicy, cyclesNumber,
				memoryCycles, programAssembly, programStartingAddress,
				programData, programDataAddress);
		//mH.getData(400);
		
		//System.out.println(mH.getData(400) + "hi");
		System.out.println(mH.getData(500) + "hi2");
		System.out.println(mH.getData(320) + "hi");
		System.out.println(mH.getData(160) + "hi");
		mH.Write(320, 80);
		//mH.getData(320);
		System.out.println(mH.mainMemory.findData(160) + "data in mem");
		
		//System.out.println(mH.getInstruction(4));
		System.out.println(mH.dataCaches.size() + "number of caches");
		System.out.println(programDataAddress[1] + "address in mem");
		System.out.println(mH.dataCaches.get(0).calculateIndex((int) programDataAddress[1]) + "index in mem");
		System.out.println(mH.dataCaches.get(0).calculateTag((int) programDataAddress[1])+ "tag in mem");
		System.out.println(mH.dataCaches.get(0).printCache() + "cache: " + 1);
		System.out.println(mH.dataCaches.get(1).printCache() + "cache: " + 2);
		
		// mH.getInstruction(m[7]);
		// mH.getInstruction(m[7]);
		// mH.insertDataInAllCaches(m[8], s[1]);
		// mH.Write(m[8], s[1]);//for data
		// mH.Write(m[7], s[0]);// for instruction

		// System.out.println(sAddr);
		// System.out.println(sAddr1[0]);
		// System.out.println(m[0]);
		// System.out.println(s[0]);

	}

}
