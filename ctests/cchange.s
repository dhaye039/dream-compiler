.data

	# Line 6: Amt:int
	.comm	_Amt, 4, 4

	# Line 7: Quarters:int
	.comm	_Quarters, 4, 4

	# Line 8: Dimes:int
	.comm	_Dimes, 4, 4

	# Line 9: Nickels:int
	.comm	_Nickels, 4, 4
.text
.global ComputeChange
_ComputeChange:

	pushl	%ebp
	movl	%esp, %ebp
	pushl	$0

	# Line 13: ComputeChange:=amt/denom
	pushl	8(%ebp)
	pushl	12(%ebp)
	popl	%ebx
	popl	%eax
	movl	$0, %edx
	idivl	%ebx
	pushl	%eax
	popl	-4(%ebp)
		
	movl	-4(%ebp), %eax
	movl	%ebp, %esp
	popl	%ebp
	ret	 
.global ComputeRemain
_ComputeRemain:

	pushl	%ebp
	movl	%esp, %ebp
	pushl	$0

	# Line 18: ComputeRemain:=amt-denom*qtydenom
	pushl	8(%ebp)
	pushl	12(%ebp)
	pushl	16(%ebp)
	popl	%ebx
	popl	%eax
	imull	%ebx, %eax
	pushl	%eax
	popl	%ebx
	popl	%eax
	subl	%ebx, %eax
	pushl	%eax
	popl	-4(%ebp)
		
	movl	-4(%ebp), %eax
	movl	%ebp, %esp
	popl	%ebp
	ret	 
.global start
start:

	# Line 23: Amt:=in.readint()
	call	readint
	pushl	%eax
	popl	_Amt

	# Line 27: Quarters:=ComputeChange(Amt,25)
	pushl	$25
	pushl	_Amt
	call	_ComputeChange
	addl	$8, %esp
	pushl	%eax
	popl	_Quarters

	# Line 28: (Quarters>0)
	pushl	_Quarters
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

	# Line 29: out.writeint(Quarters)
	pushl	_Quarters
	call	writeint
	addl	$4, %esp
	jmp	_endif1
_else1:

	# Line 31: out.writeint(0)
	pushl	$0
	call	writeint
	addl	$4, %esp
_endif1:

	# Line 33: Amt:=ComputeRemain(Amt,25,Quarters)
	pushl	_Quarters
	pushl	$25
	pushl	_Amt
	call	_ComputeRemain
	addl	$12, %esp
	pushl	%eax
	popl	_Amt

	# Line 36: Dimes:=ComputeChange(Amt,10)
	pushl	$10
	pushl	_Amt
	call	_ComputeChange
	addl	$8, %esp
	pushl	%eax
	popl	_Dimes

	# Line 37: (Dimes>0)
	pushl	_Dimes
	pushl	$0
	popl	%ebx
	popl	%eax
	cmpl	%ebx, %eax
	setg	%al
	movzbl	%al, %eax
	pushl	%eax
	popl	%eax
	cmpl	$0, %eax
	jne	_doif2
	jmp	_else2
_doif2:

	# Line 38: out.writeint(Dimes)
	pushl	_Dimes
	call	writeint
	addl	$4, %esp
	jmp	_endif2
_else2:

	# Line 40: out.writeint(0)
	pushl	$0
	call	writeint
	addl	$4, %esp
_endif2:

	# Line 42: Amt:=ComputeRemain(Amt,10,Dimes)
	pushl	_Dimes
	pushl	$10
	pushl	_Amt
	call	_ComputeRemain
	addl	$12, %esp
	pushl	%eax
	popl	_Amt

	# Line 44: Nickels:=ComputeChange(Amt,5)
	pushl	$5
	pushl	_Amt
	call	_ComputeChange
	addl	$8, %esp
	pushl	%eax
	popl	_Nickels

	# Line 45: (Nickels>0)
	pushl	_Nickels
	pushl	$0
	popl	%ebx
	popl	%eax
	cmpl	%ebx, %eax
	setg	%al
	movzbl	%al, %eax
	pushl	%eax
	popl	%eax
	cmpl	$0, %eax
	jne	_doif3
	jmp	_else3
_doif3:

	# Line 46: out.writeint(Nickels)
	pushl	_Nickels
	call	writeint
	addl	$4, %esp
	jmp	_endif3
_else3:

	# Line 48: out.writeint(0)
	pushl	$0
	call	writeint
	addl	$4, %esp
_endif3:

	# Line 51: Amt:=ComputeRemain(Amt,5,Nickels)
	pushl	_Nickels
	pushl	$5
	pushl	_Amt
	call	_ComputeRemain
	addl	$12, %esp
	pushl	%eax
	popl	_Amt

	# Line 53: (Amt>0)
	pushl	_Amt
	pushl	$0
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

	# Line 54: out.writeint(Amt)
	pushl	_Amt
	call	writeint
	addl	$4, %esp
_else4:
_endif4:
	ret	 
.global main
main:
	call	start

	# Line 58: Exit call
	pushl	$0
	call	exit
