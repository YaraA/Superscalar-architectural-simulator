
public class Scoreboard {

	scoreboardEntry [] Add_Scoreboard_Entries;
	scoreboardEntry [] Subtract_Scoreboard_Entries;
	scoreboardEntry [] Multiply_Scoreboard_Entries;
	scoreboardEntry [] Nand_Scoreboard_Entries;
	scoreboardEntry [] Load_Scoreboard_Entries;
	scoreboardEntry [] Store_Scoreboard_Entries;
	int count;
	
	public Scoreboard(int addSize, int subSize, int mulSize, int nandSize, int lwSize, int swSize){
		
		Add_Scoreboard_Entries = new scoreboardEntry[addSize];
		Subtract_Scoreboard_Entries = new scoreboardEntry[subSize];
		Multiply_Scoreboard_Entries = new scoreboardEntry[mulSize];
		Nand_Scoreboard_Entries = new scoreboardEntry[nandSize];
		Load_Scoreboard_Entries = new scoreboardEntry[lwSize];
		Store_Scoreboard_Entries = new scoreboardEntry[swSize];
	}
}
