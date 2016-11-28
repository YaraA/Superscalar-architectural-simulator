import java.util.ArrayList;
import java.util.Hashtable;


public class RegisterStatusTable {

	Hashtable statusTable;

	public RegisterStatusTable() {
		statusTable = new Hashtable<String,Integer>();
	}
	
	public String toString(){
		
		String RegStatusTable = null;
		if(!statusTable.isEmpty()){
		ArrayList keys  = (ArrayList) statusTable.keys();
		ArrayList values  = (ArrayList) statusTable.values();
		
		for(int i = 0; i < keys.size(); i++){
			RegStatusTable += "Register: " + keys.get(i)+ " , Value: " + values.get(i);
		}
		}
		else 
			RegStatusTable = "No Entries Yet";
		
		return RegStatusTable;
	}
	

	
}
