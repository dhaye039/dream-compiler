.text
.global start
start:

	pushl	%ebp
	movl	%esp, %ebp
	pushl	$0
	pushl	$0

	# Line 10: x:=in.readint()
	call	readint
	pushl	%eax
	popl	-4(%ebp)

	# Line 11: y:=in.readint()
	call	readint
	pushl	%eax
	popl	-8(%ebp)

	# Line 12: out.writeint(x)
	pushl	-4(%ebp)
	call	writeint
	addl	$4, %esp

	# Line 13: out.writeint(y)
	pushl	-8(%ebp)
	call	writeint
	addl	$4, %esp
		
	movl	-4(%ebp), %eax
	movl	%ebp, %esp
	popl	%ebp
	ret	 
.global main
main:
	call	start

	# Line 17: Exit call
	pushl	$0
	call	exit
