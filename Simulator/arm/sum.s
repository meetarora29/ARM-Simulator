mov r2,#1
mov r3, #0
mov r5,#0

str r2,[r4,#0]

add r2,r2,#1
str r2,[r4,#4]

add r2,r2,#1
str r2,[r4,#8]

add r2,r2,#1
str r2,[r4,#12]

add r2,r2,#1
str r2,[r4,#16]

mov r2,#0
ldr r2,[r4,#0]
add r5,r5,r2

ldr r2,[r4,#4]
add r5,r5,r2

ldr r2,[r4,#8]
add r5,r5,r2

ldr r2,[r4,#12]
add r5,r5,r2

ldr r2,[r4,#16]
add r5,r5,r2

str r5,[r4,#20]

swi 0x11
