.data

	# Line 6: a:int
	.comm	_a, 4, 4

	# Line 7: b:int
	.comm	_b, 4, 4

	# Line 8: num:int
	.comm	_num, 4, 4
.text
.global start
start:

	# Line 12: a:=10
	pushl	$10
	popl	_a

	# Line 13: b:=10
	pushl	$10
	popl	_b

	# Line 14: a>b
	pushl	_a
	pushl	_b
	popl	%ebx
	popl	%eax
	cmpl	%ebx, %eax
	setg	%al
	movzbl	%al, %eax
	pushl	%eax
	popl	%eax
	cmpl	$0, %eax
	jne	_doif1
	jmp	_else1
_doif1:

	# Line 15: out.writeint(1)
	pushl	$1
	call	writeint
	addl	$4, %esp
_else1:
_endif1:

	# Line 17: num:=5
	pushl	$5
	popl	_num

	# Line 18: not(num=0)
	pushl	_num
	pushl	$0
	popl	%ebx
	popl	%eax
	cmpl	%ebx, %eax
	sete	%al
	movzbl	%al, %eax
	pushl	%eax
	popl	%eax
	xorl	$1, %eax
	pushl	%eax
	popl	%eax
	cmpl	$0, %eax
	jne	_doif2
	jmp	_else2
_doif2:

	# Line 19: out.writeint(2)
	pushl	$2
	call	writeint
	addl	$4, %esp
_else2:
_endif2:

	# Line 21: num:=0
	pushl	$0
	popl	_num

	# Line 22: (num>0)andnot(num=0)
	pushl	_num
	pushl	$0
	popl	%ebx
	popl	%eax
	cmpl	%ebx, %eax
	setg	%al
	movzbl	%al, %eax
	pushl	%eax
	pushl	_num
	pushl	$0
	popl	%ebx
	popl	%eax
	cmpl	%ebx, %eax
	sete	%al
	movzbl	%al, %eax
	pushl	%eax
	popl	%eax
	xorl	$1, %eax
	pushl	%eax
	popl	%ebx
	popl	%eax
	andl	%ebx, %eax
	pushl	%eax
	popl	%eax
	cmpl	$0, %eax
	jne	_doif3
	jmp	_else3
_doif3:

	# Line 23: out.writeint(3)
	pushl	$3
	call	writeint
	addl	$4, %esp
_else3:
_endif3:

	# Line 25: a>b
	pushl	_a
	pushl	_b
	popl	%ebx
	popl	%eax
	cmpl	%ebx, %eax
	setg	%al
	movzbl	%al, %eax
	pushl	%eax
	popl	%eax
	cmpl	$0, %eax
	jne	_doif4
	jmp	_else4
_doif4:

	# Line 26: out.writeint(1)
	pushl	$1
	call	writeint
	addl	$4, %esp
	jmp	_endif4
_else4:

	# Line 28: a=b
	pushl	_a
	pushl	_b
	popl	%ebx
	popl	%eax
	cmpl	%ebx, %eax
	sete	%al
	movzbl	%al, %eax
	pushl	%eax
	popl	%eax
	cmpl	$0, %eax
	jne	_doif5
	jmp	_else5
_doif5:

	# Line 29: out.writeint(0)
	pushl	$0
	call	writeint
	addl	$4, %esp
	jmp	_endif5
_else5:

	# Line 31: not(a>=b)
	pushl	_a
	pushl	_b
	popl	%ebx
	popl	%eax
	cmpl	%ebx, %eax
	setge	%al
	movzbl	%al, %eax
	pushl	%eax
	popl	%eax
	xorl	$1, %eax
	pushl	%eax
	popl	%eax
	cmpl	$0, %eax
	jne	_doif6
	jmp	_else6
_doif6:

	# Line 32: out.writeint(-1)
	pushl	$-1
	call	writeint
	addl	$4, %esp
_else6:
_endif6:

	# Line 34: out.writeint(1)
	pushl	$1
	call	writeint
	addl	$4, %esp
_endif5:

	# Line 36: out.writeint(2)
	pushl	$2
	call	writeint
	addl	$4, %esp
_endif4:

	# Line 38: out.writeint(3)
	pushl	$3
	call	writeint
	addl	$4, %esp
	ret	 
.global main
main:
	call	start

	# Line 41: Exit call
	pushl	$0
	call	exit
