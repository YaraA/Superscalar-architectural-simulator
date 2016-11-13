import java.util.ArrayList;


public class cache {
	int s; //the size of the cache
	int l; //the size of the cache block
	int m; //associativity
	int c; //the number of cache blocks
	int hitWritingPolicy; // 1 for write back and 2 for write through
	int missWritingPolicy;// 1 for write allocate and 2 for write around
	int cacheCycles; //number of cycles required to access data
	ArrayList<block> blocks;
	
	public cache(int s, int l, int m, int hitWritingPolicy, int missWritingPolicy, int cycles) {
		this.s= s;
		this.l=l;
		this.m =m; 
		this.c= s/l; 
		this.hitWritingPolicy= hitWritingPolicy;
		this.missWritingPolicy= missWritingPolicy;
		this.cacheCycles= cycles;
		this.blocks= new ArrayList<block>(this.c);
	}
	
	public int getS() {
		return s;
	}

	public void setS(int s) {
		this.s = s;
	}

	public int getL() {
		return l;
	}

	public void setL(int l) {
		this.l = l;
	}

	public int getM() {
		return m;
	}

	public void setM(int m) {
		this.m = m;
	}

	public int getC() {
		return c;
	}

	public void setC(int c) {
		this.c = c;
	}

	public int getHitWritingPolicy() {
		return hitWritingPolicy;
	}

	public void setHitWritingPolicy(int hitWritingPolicy) {
		this.hitWritingPolicy = hitWritingPolicy;
	}

	public int getMissWritingPolicy() {
		return missWritingPolicy;
	}

	public void setMissWritingPolicy(int missWritingPolicy) {
		this.missWritingPolicy = missWritingPolicy;
	}

	public int getCacheCycles() {
		return cacheCycles;
	}

	public void setCacheCycles(int cacheCycles) {
		this.cacheCycles = cacheCycles;
	}

	public ArrayList<block> getBlocks() {
		return blocks;
	}

	public void setBlocks(ArrayList<block> blocks) {
		this.blocks = blocks;
	}


}
