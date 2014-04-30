package AtmelProject.LedDriver;


public class LED {

	// static StorageParameters storageParameters;

	public static void main(String[] args) {

		Port DDRH = new Port(0x101);
		Port PORTH = new Port(0x102);
		DDRH.port = (byte) 0xFF;
		while (true) {
			PORTH.port = (byte) 0x10101010;
		}
	}

}
