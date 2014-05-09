package atmelProject.ledDriver;

import atmelProject.lEDBlink.Port;
import icecaptools.IcecapCompileMe;
import devices.AVR.ATMega2560.ATMega2560InterruptDispatcher;
import vm.InterruptDispatcher;
import vm.InterruptHandler;

public class DriverV2 {

	public native static void enableInterrupts();

	static Port DDRB = new Port(0x04 + 0x20);
	static Port DDRK = new Port(0x107);
	static Port DDRL = new Port(0x10A);
	static Port PORTB = new Port(0x05 + 0x20);
	static Port TCCR0B = new Port(0x25 + 0x20);
	static Port TIMSK0 = new Port(0x6E);
	static Port PORTK = new Port(0x108);
	static Port PORTL = new Port(0x10B);
	static Port SPDR = new Port(0X2E + 20);
	static Port SPCR = new Port(0x2C + 0x20);
	static Port DDRH = new Port(0x101);
	static Port PORTH = new Port(0x102);

	// const uint8_t DIGIT[10] = {0xFC, 0x60, 0xDA, 0xF2, 0x66, 0xB6, 0xBE,
	// 0xE0, 0xFE, 0xF6};
	static int DIGIT[] = { 0xFC, 0x60, 0xDA, 0xF2, 0x66, 0xB6, 0xBE, 0xE0,
			0xFE, 0xF6, 0x00 };

	static int current_display = 0;

	public static void main(String args[]) {
		disp_init();
		enableInterrupts();
		ATMega2560InterruptDispatcher.init();
		DDRH.port = (byte) 0xff;
		InterruptDispatcher.registerHandler(new TimerHandler(), (byte) 24);
		while (true) {
			display_value(4, 3, 2, 1, 1);
		}
	}

	public native static void enable_timer();

	public static void disp_init() {
		DDRB.port = (byte) 0b00010000;
		// Set needed ports as output.
		// DDRK |= _BV(DDK3); // STCP
		DDRK.port = (byte) 0b00010000;
		// DDRL |= _BV(DDL3); // Display A
		// DDRL |= _BV(DDL2); // Display B
		// DDRL |= _BV(DDL1); // Display C
		// DDRL |= _BV(DDL0); // Display D
		DDRL.port = (byte) 0b11110000;
		enable_timer();

	}

	static int i, a, b, c, d;
	static int numbers[] = { 10, 10, 10, 10 };

	static void display_value(int d1, int d2, int d3, int d4,
			int number_of_decimals) {
		d1 = DIGIT[d1];
		d2 = DIGIT[d2];
		d3 = DIGIT[d3];
		d4 = DIGIT[d4];
		if (number_of_decimals == 1) {
			d1 |= 1;
		} else if (number_of_decimals == 2) {
			d2 |= 1;
		} else if (number_of_decimals == 3) {
			d3 |= 1;
		} else if (number_of_decimals == 4) {
			d4 |= 1;
		}
		numbers[0] = d1;
		numbers[1] = d2;
		numbers[2] = d3;
		numbers[3] = d4;
	}

	// static void current_display() {
	//
	// // PORTK |= _BV(PK3);
	// // PORTK &= ~_BV(PK3);
	// PORTK.port = (byte) 0b00010000;
	// PORTK.port = (byte) 0b00000000;
	// if (current_display == 0) {
	// // PORTL |= _BV(PL0); // digit 0
	// // PORTL &= ~_BV(PL1);
	// // PORTL &= ~_BV(PL2);
	// // PORTL &= ~_BV(PL3);
	//
	// PORTL.port = (byte) 0b10000000;
	// current_display++;
	// } else if (current_display == 1) {
	// // PORTL |= _BV(PL1); // digit 1
	// // PORTL &= ~_BV(PL0);
	// // PORTL &= ~_BV(PL2);
	// // PORTL &= ~_BV(PL3);
	// PORTL.port = (byte) 0b01000000;
	// current_display++;
	// } else if (current_display == 2) {
	// // PORTL |= _BV(PL2); // digit 2
	// // PORTL &= ~_BV(PL1);
	// // PORTL &= ~_BV(PL0);
	// // PORTL &= ~_BV(PL3);
	// PORTL.port = (byte) 0b00100000;
	// current_display++;
	// } else {
	// // PORTL |= _BV(PL3); // digit 3
	// // PORTL &= ~_BV(PL1);
	// // PORTL &= ~_BV(PL2);
	// // PORTL &= ~_BV(PL0);
	// PORTL.port = (byte) 0b00010000;
	// current_display = 0;
	// }
	// }

	static native void native_supliments2();

	static void spi_init() {
		// Set needed ports as output.
		// DDRB |= _BV(DDB2); // MOSI
		// DDRB |= _BV(DDB1); // SCK
		// DDRB |= _BV(DDB0); // SS
		DDRB.port = (byte) 0b11100000;
		// Set values in the SPI Control Register.
		SPCR.port = (byte) 0b11001110;
		native_supliments2();
		// current_display();
	}

	static int value_digit;

	private static class TimerHandler implements InterruptHandler {

		public TimerHandler() {
		}

		@Override
		@IcecapCompileMe
		public void handle() {
			value_digit = numbers[current_display];
			// if (current_display != 3) {
			// current_display++;
			// } else {
			// current_display = 0;
			// }
			// PORTH.port = (byte) 0xDA;
			spi_init();
			SPDR.port = (byte) numbers[1];
			PORTH.port = (byte) numbers[1];
		}

		@Override
		public void register() {
		}

		@Override
		public void enable() {
		}

		@Override
		public void disable() {
		}
	}

}
