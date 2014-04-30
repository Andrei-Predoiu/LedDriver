LedDriver
=========

Original C code by razius
https://github.com/razius/m1280-drivers


Todo: 
1. Fix error when trying to icecap
2. Implenet the native method needed to enable intrerupts( just call sei() )


Running:

1. Icecap the main method
2. Browse to the dir with the generated C files and run:

   $ avr-gcc -Wall -pedantic -Os -DJAVA_HEAP_SIZE=1024 -fpack-struct -fshort-enums -std=gnu99 -funsigned-char -funsigned-bitfields -mmcu=atmega1280 -DF_CPU=10000000UL classes.c icecapvm.c methodinterpreter.c methods.c gc.c print.c natives_allOS.c rom_heap.c allocation_point.c rom_access.c natives_avr.c interrupts.c -o a.elf
   
3. Load the elf file on the board
    For AVRDUDE users call:
      $ avrdude -p m1280 -c usbasp -U flash:w:"a.elf"   
