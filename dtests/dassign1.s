.data

	# Line 6: x:int
	.comm	_x, 4, 4

	# Line 7: y:int
	.comm	_y, 4, 4

	# Line 8: z:int
	.comm	_z, 4, 4

	# Line 9: b1:boolean
	.comm	_b1, 4, 4

	# Line 10: b2:boolean
	.comm	_b2, 4, 4
.text
.global start
start:

	# Line 14: x:=5
	pushl	$5
	popl	_x

	# Line 15: y:=x*(8-x)
	pushl	_x
	pushl	$8
	pushl	_x
	popl	%ebx
	popl	%eax
	subl	%ebx, %eax
	pushl	%eax
	popl	%ebx
	popl	%eax
	imull	%ebx, %eax
	pushl	%eax
	popl	_y

	# Line 17: b1:=true
	pushl	$1
	popl	_b1

	# Line 18: b1
	pushl	_b1
	popl	%eax
	cmpl	$0, %eax
	jne	_doif1
	jmp	_else1
_doif1:

	# Line 19: out.writeint(1)
	pushl	$1
	call	writeint
	addl	$4, %esp
	jmp	_endif1
_else1:

	# Line 21: out.writeint(0)
	pushl	$0
	call	writeint
	addl	$4, %esp
_endif1:

	# Line 24: b2:=notfalse
	pushl	$0
	popl	%eax
	xorl	$1, %eax
	pushl	%eax
	popl	_b2

	# Line 25: b2
	pushl	_b2
	popl	%eax
	cmpl	$0, %eax
	jne	_doif2
	jmp	_else2
_doif2:

	# Line 26: out.writeint(1)
	pushl	$1
	call	writeint
	addl	$4, %esp
	jmp	_endif2
_else2:

	# Line 28: out.writeint(0)
	pushl	$0
	call	writeint
	addl	$4, %esp
_endif2:

	# Line 31: b2:=not(not(y>x)or(y=15))
	pushl	_y
	pushl	_x
	popl	%ebx
	popl	%eax
	cmpl	%ebx, %eax
	setg	%al
	movzbl	%al, %eax
	pushl	%eax
	popl	%eax
	xorl	$1, %eax
	pushl	%eax
	pushl	_y
	pushl	$15
	popl	%ebx
	popl	%eax
	cmpl	%ebx, %eax
	sete	%al
	movzbl	%al, %eax
	pushl	%eax
	popl	%ebx
	popl	%eax
	orl 	%ebx, %eax
	pushl	%eax
	popl	%eax
	xorl	$1, %eax
	pushl	%eax
	popl	_b2

	# Line 32: b2
	pushl	_b2
	popl	%eax
	cmpl	$0, %eax
	jne	_doif3
	jmp	_else3
_doif3:

	# Line 33: out.writeint(1)
	pushl	$1
	call	writeint
	addl	$4, %esp
	jmp	_endif3
_else3:

	# Line 35: out.writeint(0)
	pushl	$0
	call	writeint
	addl	$4, %esp
_endif3:

	# Line 38: b1:=true
	pushl	$1
	popl	_b1

	# Line 39: b2:=false
	pushl	$0
	popl	_b2

	# Line 40: b1andb2
	pushl	_b1
	pushl	_b2
	popl	%ebx
	popl	%eax
	andl	%ebx, %eax
	pushl	%eax
	popl	%eax
	cmpl	$0, %eax
	jne	_doif4
	jmp	_else4
_doif4:

	# Line 41: out.writeint(1)
	pushl	$1
	call	writeint
	addl	$4, %esp
	jmp	_endif4
_else4:

	# Line 43: out.writeint(0)
	pushl	$0
	call	writeint
	addl	$4, %esp
_endif4:

	# Line 46: b1andb1
	pushl	_b1
	pushl	_b1
	popl	%ebx
	popl	%eax
	andl	%ebx, %eax
	pushl	%eax
	popl	%eax
	cmpl	$0, %eax
	jne	_doif5
	jmp	_else5
_doif5:

	# Line 47: out.writeint(1)
	pushl	$1
	call	writeint
	addl	$4, %esp
	jmp	_endif5
_else5:

	# Line 49: out.writeint(0)
	pushl	$0
	call	writeint
	addl	$4, %esp
_endif5:

	# Line 52: b1orb2
	pushl	_b1
	pushl	_b2
	popl	%ebx
	popl	%eax
	orl 	%ebx, %eax
	pushl	%eax
	popl	%eax
	cmpl	$0, %eax
	jne	_doif6
	jmp	_else6
_doif6:

	# Line 53: out.writeint(1)
	pushl	$1
	call	writeint
	addl	$4, %esp
	jmp	_endif6
_else6:

	# Line 55: out.writeint(0)
	pushl	$0
	call	writeint
	addl	$4, %esp
_endif6:

	# Line 58: b2orb2
	pushl	_b2
	pushl	_b2
	popl	%ebx
	popl	%eax
	orl 	%ebx, %eax
	pushl	%eax
	popl	%eax
	cmpl	$0, %eax
	jne	_doif7
	jmp	_else7
_doif7:

	# Line 59: out.writeint(1)
	pushl	$1
	call	writeint
	addl	$4, %esp
	jmp	_endif7
_else7:

	# Line 61: out.writeint(0)
	pushl	$0
	call	writeint
	addl	$4, %esp
_endif7:

	# Line 64: out.writeint(y)
	pushl	_y
	call	writeint
	addl	$4, %esp

	# Line 65: out.writeint(x/2)
	pushl	_x
	pushl	$2
	popl	%ebx
	popl	%eax
	movl	$0, %edx
	idivl	%ebx
	pushl	%eax
	call	writeint
	addl	$4, %esp

	# Line 67: y:=y-x
	pushl	_y
	pushl	_x
	popl	%ebx
	popl	%eax
	subl	%ebx, %eax
	pushl	%eax
	popl	_y

	# Line 68: out.writeint(y+4)
	pushl	_y
	pushl	$4
	popl	%ebx
	popl	%eax
	addl	%ebx, %eax
	pushl	%eax
	call	writeint
	addl	$4, %esp

	# Line 69: out.writeint(-(9-5))
	pushl	$9
	pushl	$5
	popl	%ebx
	popl	%eax
	subl	%ebx, %eax
	pushl	%eax
	popl	%eax
	negl	%eax
	pushl	%eax
	call	writeint
	addl	$4, %esp

	# Line 70: out.writeint(5-2*3+1)
	pushl	$5
	pushl	$2
	pushl	$3
	popl	%ebx
	popl	%eax
	imull	%ebx, %eax
	pushl	%eax
	popl	%ebx
	popl	%eax
	subl	%ebx, %eax
	pushl	%eax
	pushl	$1
	popl	%ebx
	popl	%eax
	addl	%ebx, %eax
	pushl	%eax
	call	writeint
	addl	$4, %esp
	ret	 
.global main
main:
	call	start

	# Line 72: Exit call
	pushl	$0
	call	exit
