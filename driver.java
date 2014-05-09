package atmelProject.ledDriver;

import atmelProject.lEDBlink.Port;
import devices.AVR.ATMega2560.ATMega2560InterruptDispatcher;
import icecaptools.IcecapCompileMe;
import vm.InterruptDispatcher;
import vm.InterruptHandler;

public class Driver {
	// const uint8_t DISPLAY[4] = {PL0, PL1, PL2, PL3};
	int DISPLAY[] = { 0b1, 0b10, 0b11, 0b110 };
	// const uint8_t DIGIT[10] = {0xFC, 0x60, 0xDA, 0xF2, 0x66, 0xB6, 0xBE,
	// 0xE0, 0xFE, 0xF6};
	static int DIGIT[] = { 0xFC, 0x60, 0xDA, 0xF2, 0x66, 0xB6, 0xBE, 0xE0,
			0xFE, 0xF6 };

	// Holds the value to display separated into digits.
	// int8_t value_by_digits[4] = {-1, -1, -1, -1};
	static int value_by_digits[] = { -1, -1, -1, -1 };
	// // The current active display.
	// uint8_t current_display = 0;
	static int current_display = 0;
	// uint8_t previous_display = -1;
	static int previous_display = -1;

	// initializing ports

	public native static void enableInterrupts();

	static Port DDRB = new Port(0x04);
	static Port DDRK = new Port(0x107);
	static Port DDRL = new Port(0x10A);
	static Port PORTB = new Port(0x05);
	static Port TCCR0B = new Port(0x25);
	static Port TIMSK0 = new Port(0x6E);
	static Port PORTK = new Port(0x108);
	static Port PORTL = new Port(0x10B);
	static Port SPDR = new Port(0X2E);
	static Port SPCR = new Port(0x2C);
	static Port DDRH = new Port(0x101);
	static Port PORTH = new Port(0x102);

	/**
	 * @ingroup display
	 * @brief Initializes the 7 segment display driver.
	 * @note Must be called first, before any other method can be used.
	 **/
	public static void display_init() {
		// // Set needed ports as output.
		// DDRB |= _BV(DDB3); // MR
		DDRB.port = (byte) 0b00010000;
		// DDRK |= _BV(DDK3); // STCP
		DDRK.port = (byte) 0b00010000;
		// DDRL |= _BV(DDL3); // Display A
		// DDRL |= _BV(DDL2); // Display B
		// DDRL |= _BV(DDL1); // Display C
		// DDRL |= _BV(DDL0); // Display D
		DDRL.port = (byte) 0b11110000;
		// Clear any values that we currently have in the shift
		// register and set the master reset pin to HIGH.
		// PORTB &= ~_BV(PB3);
		PORTB.port = (byte) 0b00000000;
		// PORTB |= _BV(PB3);
		PORTB.port = (byte) 0b10000000;
		// Set the source to CLK/64 (from pre-scaler).
		// TCCR0B |= _BV(CS01) | _BV(CS00);
		TCCR0B.port = (byte) 0b11000000;
		// // Enable Timer/Counter0 overflow interrupt.
		// TIMSK0 = _BV(TOIE0);
		TIMSK0.port = (byte) 0b100;
		DDRH.port = (byte) 0xff;
		PORTH.port = (byte) 0xff;
	}

	/**
	 * @ingroup display
	 * @brief Display a value on the 7 segment display.
	 * @param value_to_display
	 *            The value to display on the 7 segment display. If the value
	 *            has more than 4 digits it will be truncated.
	 * @param numbers_of_decimals
	 *            The number of decimals to be displayed.
	 * @todo Figure out a way to indicate an overflow.
	 **/
	// void display_value(float value_to_display, uint8_t numbers_of_decimals){
	static void display_value(float value_to_display, int numbers_of_decimals) {
		int pow_ten[] = { 1, 10, 100, 1000, 10000 };
		int int_value = (int) (value_to_display * pow_ten[numbers_of_decimals]);
		for (int i = 0; i < 4; i++) {
			int digit = (int) ((int_value / pow_ten[i]) % 10);
			value_by_digits[i] = DIGIT[digit];
			// Add the decimal point.2
			if (i == numbers_of_decimals && numbers_of_decimals != 0) {
				value_by_digits[i] = 1;
			}
		}
	}

	static void _turn_display_on() {
		// Send the value to the storage register.
		// CLOCK_PIN(PORTK, PK3);
		PORTK.port = (byte) 0b00010000;
		PORTK.port = (byte) 0b00000000;
		// Turn on the current active display.
		// SET_BIT(PORTL, current_display);
		PORTL.port = (byte) current_display;
		// Turn off previous display, save position
		// and move to next display.
		// if (previous_display != -1) {
		// PORTL.port = (byte) DISPLAY[previous_display];
		// }
		previous_display = current_display;
		if (current_display == 3) {
			current_display = 0;
		} else {
			current_display++;
		}
	}

	static void spi_init() {
		// Set needed ports as output.
		// DDRB |= _BV(DDB2); // MOSI
		// DDRB |= _BV(DDB1); // SCK
		// DDRB |= _BV(DDB0); // SS
		DDRB.port = (byte) 0b11100000;

		// Set values in the SPI Control Register.
		SPCR.port = (byte) 0b11001111;
	}

	static int value_digit;

	private static class TimerHandler implements InterruptHandler {

		public TimerHandler() {
		}

		@Override
		@IcecapCompileMe
		public void handle() {
			PORTH.port = (byte) 0x00;
			// value_digit = value_by_digits[current_display];
			// if (value_digit != -1) {
			// spi_init();
			// SPDR.port = (byte) value_digit;
			// }
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

	public static void main(String args[]) {
		enableInterrupts();
		ATMega2560InterruptDispatcher.init();
		InterruptDispatcher.registerHandler(new TimerHandler(), (byte) 17);
		display_init();
		display_value(25, 1);
		while (true) {
			;
		}
	}
}
