package atmelProject.ledDriver;

import vm.Address32Bit;
import vm.HardwareObject;

public class Port extends HardwareObject {
	byte port; 

	public Port(int address) {
		super(new Address32Bit(address));
		// TODO Auto-generated constructor stub
	}

}
