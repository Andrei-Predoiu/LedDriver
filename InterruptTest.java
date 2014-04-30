package AtmelProject.LedDriver;

import icecaptools.IcecapCompileMe;
import devices.AVR.ATMega2560.ATMega2560InterruptDispatcher;
import vm.InterruptDispatcher;
import vm.InterruptHandler;

public class InterruptTest {

	public native static void setupInterrupts();

	private static Port PORTH = new Port(0x102);
	private static Port DDRH = new Port(0x101);
	private static Port TCCR1B = new Port(0x81);
	private static Port TIMSK1 = new Port(0x6F);
	private static Port OCR1AH = new Port(0x88);
	private static Port OCR1AL = new Port(0x89);

	public static void main(String args[]) {
		ATMega2560InterruptDispatcher.init();
		InterruptDispatcher.registerHandler(new TimerHandler(PORTH), (byte) 17);
		setupInterrupt();
		// PORTH.port = (byte) 0b00000111;
		while (true) {
			;
		}
	}

	private static class TimerHandler implements InterruptHandler {
		private Port PORTH;

		public TimerHandler(Port PORTH) {
			this.PORTH = PORTH;
		}

		@Override
		@IcecapCompileMe
		public void handle() {
			PORTH.port ^= (byte)0xff;
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

	private static void setupInterrupt() {
		// 0x101 00000010
		// DDRH |= _BV(DDH1);
		DDRH.port = (byte) 0xff;
		// PORTH.port = (byte) 0xff;

		// 0x81 00001000
		// TCCR1B |= _BV(WGM12);
		TCCR1B.port = (byte) 0b00001000;

		// 0x6F 000010
		// TIMSK1 |= _BV(OCIE1A);
		TIMSK1.port = (byte) 0b000010;

		// sei();
		setupInterrupts();

		// 0x88
		// OCR1A = 7199;
		OCR1AH.port = 0x1f;
		OCR1AL.port = 0x1c;

		// 0x81 00000101
		// TCCR1B |= _BV(CS12) | _BV(CS10);
		TCCR1B.port = 0b0000010;
	}
}
