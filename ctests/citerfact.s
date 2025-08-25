.data

	# Line 5: num:int
	.comm	_num, 4, 4

	# Line 6: num2:int
	.comm	_num2, 4, 4

	# Line 7: isOk:boolean
	.comm	_isOk, 4, 4
.text
.global Fact
_Fact:

	pushl	%ebp
	movl	%esp, %ebp
	pushl	$0
	pushl	$0

	# Line 12: answer:=1
	pushl	$1
	popl	%eax
	movl	%eax, -8(%ebp)

	# Line 13: num>0
_while1:
	pushl	8(%ebp)
	pushl	$0
	popl	%ebx
	popl	%eax
	cmpl	%ebx, %eax
	setg	%al
	movzbl	%al, %eax
	pushl	%eax
	popl	%eax
	cmpl	$0, %eax
	jne	_startwhilebody1
	jmp	_endwhile1
_startwhilebody1:

	# Line 14: answer:=answer*num
	pushl	-8(%ebp)
	pushl	8(%ebp)
	popl	%ebx
	popl	%eax
	imull	%ebx, %eax
	pushl	%eax
	popl	%eax
	movl	%eax, -8(%ebp)

	# Line 15: num:=num-1
	pushl	8(%ebp)
	pushl	$1
	popl	%ebx
	popl	%eax
	subl	%ebx, %eax
	pushl	%eax
	popl	%eax
	movl	%eax, 8(%ebp)
	jmp	_while1
_endwhile1:

	# Line 17: Fact:=answer
	pushl	-8(%ebp)
	popl	-4(%ebp)

	# Line 18: out.writeint(0+answer-answer)
	pushl	$0
	pushl	-8(%ebp)
	popl	%ebx
	popl	%eax
	addl	%ebx, %eax
	pushl	%eax
	pushl	-8(%ebp)
	popl	%ebx
	popl	%eax
	subl	%ebx, %eax
	pushl	%eax
	call	writeint
	addl	$4, %esp
		
	movl	-4(%ebp), %eax
	movl	%ebp, %esp
	popl	%ebp
	ret	 
.global start
start:

	# Line 23: num:=in.readint()
	call	readint
	pushl	%eax
	popl	_num

	# Line 24: num>0
	pushl	_num
	pushl	$0
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

	# Line 25: num:=Fact(num)
	pushl	_num
	call	_Fact
	addl	$4, %esp
	pushl	%eax
	popl	_num

	# Line 26: out.writeint(num)
	pushl	_num
	call	writeint
	addl	$4, %esp
_else1:
_endif1:
	ret	 
.global main
main:
	call	start

	# Line 29: Exit call
	pushl	$0
	call	exit
