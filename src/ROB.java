import java.util.Queue;


public class ROB {

	int HeadPointer;
	int TailPointer;
	
	ROBEntry [] ROBContent;

	public ROB(int sizeOfROB) {
		ROBContent = new ROBEntry[sizeOfROB];
	}

}
