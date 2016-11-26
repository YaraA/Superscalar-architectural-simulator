import java.util.ArrayList;


public class cache {
	int s; //the size of the cache
	int l; //the size of the cache block(line)
	int m; //associativity
	int c; //the number of cache blocks(number of lines)
	int hitWritingPolicy; // 1 for write back and 2 for write through
	int missWritingPolicy;// 1 for write allocate and 2 for write around
	int cacheCycles; //number of cycles required to access data
	ArrayList<block> blocks;
	int misses;
	
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
	public int calculateOffset() {
		return (int) (Math.log(this.l) / Math.log(2));
	}
	public int calculateIndex(int addr) {
		int index=0;
		if(this.m!=this.c)
			index=(addr/l)%m;
			//index = (int) (Math.log(this.c/this.m)/ Math.log(2));
		return index;	
	}
	public int calculateTag(int addr) {
		int offset= calculateOffset();
		int index= calculateIndex(addr);
		return (addr/l)/m;
		//return 16 - index -offset;
	}
	
	public Object searchCache(int addr){
		boolean foundF = false;
		boolean foundS = false;
		int index= calculateIndex(addr);
		int tag= calculateTag(addr);
		if(m==1) { //direct mapped
			//check if index holds tag
			if(this.blocks.get(index).getTag()==tag) //hit
				return this.blocks.get(index).getInsOrData();
			else{
				misses++;
				return null;
			}
		}
		else if(m>1 && m<c){ //set associative 
			for(int k=0;(k/m)<=index;k=k+m){
				if(k/m == index){
				for(int i=0;i<m;i++){
					if(this.blocks.get(index*m+i).getTag()==tag){
						foundS = true;
						return this.blocks.get(index*m+i).getInsOrData();
					}
				}
				if(!foundS)
					misses++;
				
				}
					
			}
			
		}
		if(m==c) {//fully associative
			for(int i=0; i<blocks.size(); i++){
				if(this.blocks.get(i).getTag()==tag){
					foundF = true;
					return this.blocks.get(i).getInsOrData();
				}
				
			}
			if(!foundF)
				misses++;
		}
		return null;
			
		
	}
	public Object[] insert(int addr, Object data){
		
		int index = calculateIndex(addr);
		int offset = calculateOffset();
		int tag = calculateTag(addr);
		Object [] dirtyInfo= new Object [2];
		boolean space=false;
		if(m ==1){
			if(this.blocks.get(index).isDirtyBit()) {
				dirtyInfo[0]=this.blocks.get(index).getInsOrData();
				dirtyInfo[1]=addr;
			}
			this.blocks.add(index, new block(false,true,addr,data)); 
		}
		if(m ==c){
			if(blocks.size()==c) {
				if(this.blocks.get(0).isDirtyBit()) {
					dirtyInfo[0]=this.blocks.get(0).getInsOrData();
					dirtyInfo[1]=addr;
				}
				this.blocks.add(0, new block(false,true,addr,data));
				//return false;//insert at position 0
			}
			else {
				this.blocks.add(new block(false,true,addr,data)); 			
				//return true;
			}
		}
		if(m>1 && m<c) {
			for(int k=0;(k/m)<=index;k=k+m){
				if(k/m==index) {
					for(int i=0;i<m;i++){
						if(this.blocks.get(i) == null) {//get i or get tag??
							this.blocks.add(index*m+i, new block(false,true,addr,data));
							space=true;
							//return true;
						}
					}
				}
				if(space)
					break;
					
			}
			if(!space) {
				if(this.blocks.get(index*m).isDirtyBit()) {
					dirtyInfo[0]=this.blocks.get(index*m).getInsOrData();
					dirtyInfo[1]=addr;
				}
				this.blocks.add(index*m,new block(false,true,addr,data));
			}
			//return false; //miss policy... law LRU
		}
		return dirtyInfo;
	}
}
