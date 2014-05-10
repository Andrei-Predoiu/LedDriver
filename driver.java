package atmelProject.ledDriver;

import atmelProject.lEDBlink.Port;
import icecaptools.IcecapCompileMe;
import devices.AVR.ATMega2560.ATMega2560InterruptDispatcher;
import vm.InterruptDispatcher;
import vm.InterruptHandler;

public class Driver {

	public native static void enableInterrupts();

	/**
	 * Seven segment display driver java implementation for the ATMega1280.
	 * Using IceCap, see www.Icelab.dk
	 * 
	 * @see IceCap, www.Icelab.dk
	 * @author Andrei Predoiu, Thorsten Aksel Roy, Alex Patriche
	 */
	static Port DDRB = new Port(0x04 + 0x20);
	static Port DDRK = new Port(0x107);
	static Port DDRL = new Port(0x10A);
	static Port PORTB = new Port(0x05 + 0x20);
	static Port TCCR0B = new Port(0x25 + 0x20);
	static Port TIMSK0 = new Port(0x6E);
	static Port PORTK = new Port(0x108);
	static Port PORTL = new Port(0x10B);

	// Hex values for numbers 0-9 + empty display
	static int DIGIT[] = { 0xFC, 0x60, 0xDA, 0xF2, 0x66, 0xB6, 0xBE, 0xE0,
			0xFE, 0xF6, 0x00 };

	static int current_display = 1;
	static int mask_result = 0b00000000;

	public static void main(String args[]) {
		disp_init();
		enableInterrupts();
		ATMega2560InterruptDispatcher.init();
		InterruptDispatcher.registerHandler(new TimerHandler(), (byte) 24);
		while (true) {
			display_value(1, 3, 4, 7, 2);
		}
	}

	public static void disp_init() {
		DDRB.port |= (1 << (1));// SHCP
		DDRB.port |= (1 << (2));// DS
		DDRB.port |= (1 << (3));// MR

		DDRK.port |= (1 << (3)); // STCP

		DDRL.port |= (1 << (3));// Display A
		DDRL.port |= (1 << (2));// Display B
		DDRL.port |= (1 << (1));// Display C
		DDRL.port |= (1 << (0));// Display D
		// enable_timer();
		TCCR0B.port |= (1 << (1)) | (1 << (0)); // Set the source to CLK/64
												// (from prescaler).
		TIMSK0.port = (1 << (0)); // Enable Timer/Counter0 overflow interrupt.

		PORTB.port &= ~(1 << (3)); // Reset the shift register once
		PORTB.port |= (1 << (3)); // Disable reset again so we can input

	}

	static int numbers[] = { 10, 10, 10, 10 };

	/**
	 * @ingroup display
	 * @brief Display a value on the 7 segment display.
	 * @param d1
	 *            The value of the first digit.
	 * @param d2
	 *            The value of the 2nd digit.
	 * @param d3
	 *            The value of the 3rd digit.
	 * @param d4
	 *            The value of the 4th digit.
	 * @param numbers_of_decimals
	 *            The number of decimals to be displayed.
	 **/
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
		numbers[3] = d1;
		numbers[2] = d2;
		numbers[1] = d3;
		numbers[0] = d4;
	}

	static int value_digit, i = 0, segment_mask;

	private static class TimerHandler implements InterruptHandler {

		public TimerHandler() {
		}

		@Override
		@IcecapCompileMe
		public void handle() {
			segment_mask = 0b00000001;
			value_digit = numbers[current_display];

			PORTB.port &= ~(1 << (1));
			// make sure SHCP is low before putting new value on DS

			for (i = 0; i < 8; i++) {
				// & current segment_mask with digit we want
				mask_result = segment_mask & numbers[current_display];

				if (mask_result != 0)
				// check if the current DS value needs to be one or 0
				{
					PORTB.port |= (1 << (2));
				} else {
					PORTB.port &= ~(1 << (2));
				}

				// pulse SHCP to shift value at DS into the register
				PORTB.port |= (1 << (1));
				// make sure SHCP is low before putting new value on DS
				PORTB.port &= ~(1 << (1));
				// shuffle mask bit for next segment digit
			}
			segment_mask = segment_mask << 1;

			// STCP pulse to shift bits to SSD segments
			PORTK.port |= (1 << (3));
			// STCP low while we input new digit bits
			PORTK.port &= ~(1 << (3));

			if (current_display == 0) {
				PORTL.port |= (1 << (0)); // digit 0
				PORTL.port &= ~(1 << (1));
				PORTL.port &= ~(1 << (2));
				PORTL.port &= ~(1 << (3));
				current_display++;
			} else if (current_display == 1) {
				PORTL.port |= (1 << (1)); // digit 1
				PORTL.port &= ~(1 << (0));
				PORTL.port &= ~(1 << (2));
				PORTL.port &= ~(1 << (3));
				current_display++;
			} else if (current_display == 2) {
				PORTL.port |= (1 << (2)); // digit 2
				PORTL.port &= ~(1 << (0));
				PORTL.port &= ~(1 << (1));
				PORTL.port &= ~(1 << (3));
				current_display++;
			} else {
				PORTL.port |= (1 << (3)); // digit 3
				PORTL.port &= ~(1 << (0));
				PORTL.port &= ~(1 << (1));
				PORTL.port &= ~(1 << (2));
				current_display = 0;
			}
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
