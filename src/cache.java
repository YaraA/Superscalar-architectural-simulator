
public class cache {
	int s; //the size of the cache
	int l; //the size of the cache line
	int m; //associativity
	int c; //the number of cache lines
	String hitWritingPolicy;
	String missWritingPolicy;
	int cacheCycles; //number of cycles required to access data
	
	public cache(int s, int l, int m, String hitWritingPolicy, String missWritingPolicy, int cycles) {
		this.s= s;
		this.l=l;
		this.m =m; 
		this.c= s/l; 
		this.hitWritingPolicy= hitWritingPolicy;
		this.missWritingPolicy= missWritingPolicy;
		this.cacheCycles= cycles;
	}

}
