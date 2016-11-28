

public class Register_File {

  Register [] registers;
  
  public Register_File() {
	this.registers = new Register[8];
	
	for(int i = 0; i < registers.length; i++){
	   if(i == 0)	
		   registers[i] = new zeroRegister("R0");
	   else
		   registers[i] = new normalRegister("R"+i);
	}
	
  }
}
