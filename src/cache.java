import java.util.ArrayList;


public class cache {
	int s; //the size of the cache
	int l; //the size of the cache block(line)
	int m; //associativity
	int c; //the number of cache blocks(number of lines)
	int hitWritingPolicy; // 1 for write back and 2 for write through
	int cacheCycles; //number of cycles required to access data
	//ArrayList<block> blocks;
	block[] blocks;
	int misses;
	
	public cache(int s, int l, int m, int hitWritingPolicy,  int cycles) {
		this.s= s;
		this.l=l;
		this.m =m; 
		this.c= s/l; 
		this.hitWritingPolicy= hitWritingPolicy;
		this.cacheCycles= cycles;
		//this.blocks= new ArrayList<block>(this.c);
		this.blocks= new block[c];
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


	public int getCacheCycles() {
		return cacheCycles;
	}

	public void setCacheCycles(int cacheCycles) {
		this.cacheCycles = cacheCycles;
	}

//	public ArrayList<block> getBlocks() {
//		return blocks;
//	}
//
//	public void setBlocks(ArrayList<block> blocks) {
//		this.blocks = blocks;
//	}
	
	
	public int calculateOffset() {
		return (int) (Math.log(this.l) / Math.log(2));
	}
	public block[] getBlocks() {
		return blocks;
	}

	public void setBlocks(block[] blocks) {
		this.blocks = blocks;
	}

	public int calculateIndex(int addr) {
		int index=0;
		if(this.m!=this.c) {
			//System.out.println("in calc index if");
			index=(addr/l)%(c/m);
		}
			//index = (int) (Math.log(this.c/this.m)/ Math.log(2));
		return index;	
	}
	public int calculateTag(int addr) {
		int offset= calculateOffset();
		int index= calculateIndex(addr);
		return (addr/l)/(c/m);
		//return 16 - index -offset;
	}
	
	public Object searchCache(int addr){
		boolean foundF = false;
		boolean foundS = false;
		int index= calculateIndex(addr);
		int tag= calculateTag(addr);
		if(m==1) { //direct mapped
			//check if index holds tag
			//System.out.println(this.blocks.length + " blocksLength");
			//System.out.println(index + " index in searchCache");
			if(this.blocks[index]!=null){
				//System.out.println(index + " index");
				//System.out.println(this.blocks[index] + "tag") ;
				if(this.blocks[index].getTag()==tag) //hit
					return this.blocks[index].getInsOrData();
				else{
					misses++;
					return null;
				}
			}
			
			
		}
		else if(m>1 && m<c){ //set associative 
			for(int k=0;(k/m)<=index;k=k+m){
				if(k/m == index){
				for(int i=0;i<m;i++){
					if(this.blocks[index*m+i]!=null){
						if(this.blocks[index*m+i].getTag()==tag){
							foundS = true;
							return this.blocks[index*m+i].getInsOrData();
						}
					}
					
				}
				if(!foundS)
					misses++;
				
				}
					
			}
			
		}
		if(m==c) {//fully associative
			for(int i=0; i<blocks.length; i++){
				if(this.blocks[i] != null) {
					if(this.blocks[i].getTag()==tag){
						foundF = true;
						return this.blocks[i].getInsOrData();
					}
				}
				
				
			}
			if(!foundF)
				misses++;
		}
		return null;
			
		
	}
	public Object[] insert(int addr, Object data){
		System.out.println("in insert in cache");
		int index = calculateIndex(addr);
		int offset = calculateOffset();
		int tag = calculateTag(addr);
		Object [] dirtyInfo= new Object [2];
		boolean space=false;
		//System.out.println("m " + m);
		if(m ==1){
			if(this.blocks[index]!=null) { //cache is empty
				if(this.blocks[index].isDirtyBit()) {
					dirtyInfo[0]=this.blocks[index].getInsOrData();
					dirtyInfo[1]=this.blocks[index].getMainMemoryAddr();
				}
				
			}
			this.blocks[index]= new block(false,true,addr,data,tag);
			
			//else: make an action accordingly 
			
		}
		if(m ==c){
			boolean empty = false;
			for (int i =0; i<blocks.length ; i++){ 
				if(this.blocks[i] == null){
					this.blocks[i]=new block(false,true,addr,data, tag);
					empty = true;
					break;
				}
			}
			if(!empty) {
				if(this.blocks[0] !=null) {
					if(this.blocks[0].isDirtyBit()) {
						dirtyInfo[0]=this.blocks[0].getInsOrData();
						dirtyInfo[1]=this.blocks[0].getMainMemoryAddr();
					}
				}
				this.blocks[0]= new block(false,true,addr,data,tag);
			}

		}
		if(m>1 && m<c) {
			for(int k=0;(k/m)<=index;k=k+m){
				if(k/m==index) {
					for(int i=0;i<m;i++){
						if(!(this.blocks.length==0)) {
							if(this.blocks[index*m+i] == null) {//get i or get tag??
								this.blocks[index*m+i]= new block(false,true,addr,data, tag);
								space=true;
								break;
							}
						}
						//else take action accordingly 
						
					}
				}
				if(space)
					break;
					
			}
			if(!space) {
				if(!(this.blocks.length==0)) {
					if(this.blocks[index*m].isDirtyBit()) {
						dirtyInfo[0]=this.blocks[index*m].getInsOrData();
						dirtyInfo[1]=this.blocks[index*m].getMainMemoryAddr();
					}
					
				}
				this.blocks[index*m]= new block(false,true,addr,data, tag);
			}
			//return false; //miss policy... law LRU
		}
		return dirtyInfo;
	}
	public String printCache(){
		System.out.println(blocks.length + "size of blocks in cache");
		String r= "";
		//System.out.println(blocks[1] + "size of blocks in cache");
		for(int i=0; i<blocks.length; i++) {
			if(blocks[i]!=null)
				r+= (short) new Integer ((int) blocks[i].insOrData).intValue() + ":" + blocks[i].getMainMemoryAddr() + " tag" + blocks[i].getTag() + " index" + i + " dirty" + blocks[i].dirtyBit + "\n"; 
		}
		return r;
		 
		
	}
}
