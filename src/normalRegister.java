
public class normalRegister extends Register {

	short value;
	
	public normalRegister(String name) {
		super(name);
	}

	public normalRegister(String name, short value) {
		super(name);
		this.value = value;
	}
	
	
}
