.data

	# Line 6: num:int
	.comm	_num, 4, 4

	# Line 7: num2:int
	.comm	_num2, 4, 4
.text
.global Fact
_Fact:

	pushl	%ebp
	movl	%esp, %ebp
	pushl	$0
	pushl	$0

	# Line 12: num=0
	pushl	8(%ebp)
	pushl	$0
	popl	%ebx
	popl	%eax
	cmpl	%ebx, %eax
	sete	%al
	movzbl	%al, %eax
	pushl	%eax
	popl	%eax
	cmpl	$0, %eax
	jne	_doif1
	jmp	_else1
_doif1:

	# Line 13: answer:=1
	pushl	$1
	popl	%eax
	movl	%eax, -8(%ebp)
	jmp	_endif1
_else1:

	# Line 15: answer:=num*Fact(num-1)
	pushl	8(%ebp)
	pushl	8(%ebp)
	pushl	$1
	popl	%ebx
	popl	%eax
	subl	%ebx, %eax
	pushl	%eax
	call	_Fact
	addl	$4, %esp
	pushl	%eax
	popl	%ebx
	popl	%eax
	imull	%ebx, %eax
	pushl	%eax
	popl	%eax
	movl	%eax, -8(%ebp)
_endif1:

	# Line 17: Fact:=answer
	pushl	-8(%ebp)
	popl	-4(%ebp)
		
	movl	-4(%ebp), %eax
	movl	%ebp, %esp
	popl	%ebp
	ret	 
.global Go
_Go:

	pushl	%ebp
	movl	%esp, %ebp
	pushl	$0

	# Line 24: isOk:=false
	pushl	$0
	popl	-4(%ebp)

	# Line 25: notisOk
_while1:
	pushl	-4(%ebp)
	popl	%eax
	xorl	$1, %eax
	pushl	%eax
	popl	%eax
	cmpl	$0, %eax
	jne	_startwhilebody1
	jmp	_endwhile1
_startwhilebody1:

	# Line 26: num:=in.readint()
	call	readint
	pushl	%eax
	popl	_num

	# Line 27: isOk:=(num>=1)
	pushl	_num
	pushl	$1
	popl	%ebx
	popl	%eax
	cmpl	%ebx, %eax
	setge	%al
	movzbl	%al, %eax
	pushl	%eax
	popl	-4(%ebp)
	jmp	_while1
_endwhile1:
		
	movl	-4(%ebp), %eax
	movl	%ebp, %esp
	popl	%ebp
	ret	 
.global start
start:

	# Line 33: Go()
	call	_Go

	# Line 34: num2:=Fact(num)
	pushl	_num
	call	_Fact
	addl	$4, %esp
	pushl	%eax
	popl	_num2

	# Line 35: out.writeint(num)
	pushl	_num
	call	writeint
	addl	$4, %esp

	# Line 36: out.writeint(num2)
	pushl	_num2
	call	writeint
	addl	$4, %esp
	ret	 
.global main
main:
	call	start

	# Line 39: Exit call
	pushl	$0
	call	exit
