
public class Scoreboard {

	scoreboardEntry [] Add_Scoreboard_Entries;
	scoreboardEntry [] Subtract_Scoreboard_Entries;
	scoreboardEntry [] Multiply_Scoreboard_Entries;
	scoreboardEntry [] Nand_Scoreboard_Entries;
	scoreboardEntry [] Load_Scoreboard_Entries;
	scoreboardEntry [] Store_Scoreboard_Entries;
	
	public Scoreboard(int addSize, int subSize, int mulSize, int nandSize, int lwSize, int swSize){
		
		Add_Scoreboard_Entries = new scoreboardEntry[addSize];
		Subtract_Scoreboard_Entries = new scoreboardEntry[subSize];
		Multiply_Scoreboard_Entries = new scoreboardEntry[mulSize];
		Nand_Scoreboard_Entries = new scoreboardEntry[nandSize];
		Load_Scoreboard_Entries = new scoreboardEntry[lwSize];
		Store_Scoreboard_Entries = new scoreboardEntry[swSize];
	}
	
	public String toString(){
		String add = "\nAdd Entries: \n";

		for(int i = 0; i < Add_Scoreboard_Entries.length; i++)
		  if(Add_Scoreboard_Entries[i] != null)	
			add += Add_Scoreboard_Entries[i].toString() + "\n";
		
		String sub = "\nSubtract Entries: \n";
		
		for(int i = 0; i < Subtract_Scoreboard_Entries.length; i++)
		  if(Subtract_Scoreboard_Entries[i] != null)
			sub += Subtract_Scoreboard_Entries[i].toString() + "\n";
		
		String mul = "\nMultiply Entries: \n";
		
		for(int i = 0; i < Multiply_Scoreboard_Entries.length; i++)
		  if(Multiply_Scoreboard_Entries[i] != null)	
			mul += Multiply_Scoreboard_Entries[i].toString() + "\n";
		
		String Nand = "\nNand Entries: \n";
		
		for(int i = 0; i < Nand_Scoreboard_Entries.length; i++)
		  if(Nand_Scoreboard_Entries[i] != null)	
			Nand += Nand_Scoreboard_Entries[i].toString() + "\n";
		
		String load = "\nLoad Entries: \n";
		
		for(int i = 0; i < Load_Scoreboard_Entries.length; i++)
		  if(Load_Scoreboard_Entries[i] != null)
			load += Load_Scoreboard_Entries[i].toString() + "\n";
		
		String store= "\nStore Entries: \n";
		
		for(int i = 0; i < Store_Scoreboard_Entries.length; i++)
		  if(Store_Scoreboard_Entries[i] != null)	
			store += Store_Scoreboard_Entries[i].toString() + "\n";
		
		return add + sub + mul + Nand + load + store;
	}
}
