
public class ROBEntry {

	private int id;
	private String type;
	private String destination;
	private short value;
	private boolean ready;
	
	public ROBEntry(int id, String type, String destination, short value, boolean ready) {
		
		this.id = id;
		this.type = type;
		this.destination = destination;
		this.value = value;
		this.ready = ready;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public short getValue() {
		return value;
	}
	public void setValue(short value) {
		this.value = value;
	}
	public boolean isReady() {
		return ready;
	}
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	
	
}
