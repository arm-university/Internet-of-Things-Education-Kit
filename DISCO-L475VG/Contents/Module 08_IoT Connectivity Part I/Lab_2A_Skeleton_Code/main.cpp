/*
 * Copyright (c) 2022 Arm Limited and affiliates
 *
 * Please see license at: https://github.com/arm-university/Internet-of-Things-Education-Kit/blob/main/License/LICENSE.md
 

LAB EXERCISE - Low Level LED blinky
 ----------------------------------------
	In this exercise you will have to write a program which reads the state of
	the on board button and turns on the on board LED.
	
	GOOD LUCK!
 */

#include "stm32l475xx.h"

void init_button(void) {

    /* your code here */
	
}


void init_led(void) {
	
	/* your code here */

}

// Set output pin PA_5 to high
void led_on(void) {

	 /* your code here */

}

// Set output pin PA_5 to low
void led_off(void) {

   /* your code here */

}

/*----------------------------------------------------------------------------
 MAIN function
 *----------------------------------------------------------------------------*/

int main() {

	// Initialise LEDs and buttons
	init_button();
    init_led();
	while(1){

		if((GPIOC->IDR & GPIO_IDR_IDR_13)==0) { 		// If the button is pressed turn on the LED 
			led_on();
		}
		else{ // Otherwise turn off the LED
			led_off();
		}
	}
}
