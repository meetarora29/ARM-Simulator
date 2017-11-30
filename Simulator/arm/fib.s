@Meet 2016055

.equ SWI_PrInt, 0x6b
.equ SWI_PrChr, 0x0
.equ SWI_Exit, 0x11
.equ SWI_RdInt, 0x6c

mov r0, #16
mov r4, r0 @n
sub	r4, r4, #1
mov r0, #0 @p_prev
mov r1, #1 @prev
mov	r5, r4

@fibonacci
cmp	r5, r1
ble end @if n<=1
mov r7, #1 @i=1
loop:
	add	r5, r0, r1 @r5 is fib
	add	r7, r7, #1 @i++
	mov	r0, r1 @p_prev=prev
	mov	r1, r5 @prev=fib
	cmp	r7, r4
	blt loop

mov	r7, r5
cmp	r5, #10
blt end @if fib<10

end:
	mov	r0, #1
	mov r1, r5
	swi SWI_PrInt

swi SWI_Exit
