

public class ROB {

	int HeadPointer;
	int TailPointer;
	
	ROBEntry [] ROBContent;

	public ROB(int sizeOfROB) {
		ROBContent = new ROBEntry[sizeOfROB];
		
		for(int i = 0; i < ROBContent.length;i++){
			ROBContent[i] = new ROBEntry(i);
		}
	}
	
	public String toString(){
		String table = "";
		for(int i = 0; i < ROBContent.length; i++)
			table += ROBContent[i].toString() + "\n";
		
		return table;
	}

}
