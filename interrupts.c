#include <avr/io.h>
#include <avr/interrupt.h>

#include "ostypes.h"

int16 vm_InterruptDispatcher_interrupt(int32 *fp, int8 n);
int32 isrMethodStack[128];
 
 int16 n_atmelProject_ledDriver_Driver_enableInterrupts(){
	sei();
	return -1;
 }

ISR(TIMER1_COMPA_vect)
{
	vm_InterruptDispatcher_interrupt(&isrMethodStack[0],17);
}
