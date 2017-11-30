@ Meet Arora

.equ SWI_Exit, 0x11

mov r3, #0
mov r5, #0 @array index
mov r1, #5 @array size + 1

loop:
	str r5, [r4, r3]
	add r3, r3, #4
	add r5, r5, #1
	cmp r5, r1
	ble loop

mov r5, #0
mov r2, #0
mov r3, #0
mov r0, #0 @sum
sum:
	ldr r2, [r4, r3]
	add r0, r0, r2
	add r3, r3, #4
	add r5, r5, #1
	cmp r5, r1
	ble sum

str r0, [r4, r3]
swi SWI_Exit
