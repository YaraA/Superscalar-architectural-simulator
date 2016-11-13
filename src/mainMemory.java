import java.util.ArrayList;


public class mainMemory {
	int noOfLevels;
	ArrayList<cache> caches = new ArrayList<cache>(); 
	int memoryCycles; //main memory access time 
	int capacity= 64 * 1024; //64KB
	
	public mainMemory(int noOfLevels, int memoryCycles, int s, int l, int m, String hitWritingPolicy, String missWritingPolicy, int cacheCycles) {
		this.noOfLevels= noOfLevels;
		this.memoryCycles= memoryCycles;
		for(int i=0; i<noOfLevels; i++) {
			caches.add(new cache(s,l,m,hitWritingPolicy, missWritingPolicy, cacheCycles));
		}
		
	}
	
}
