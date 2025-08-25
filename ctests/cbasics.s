.data

	# Line 6: d:int
	.comm	_d, 4, 4
.text
.global meth2
_meth2:

	pushl	%ebp
	movl	%esp, %ebp
	pushl	$0
	pushl	$0

	# Line 11: meth2:=a+b
	pushl	8(%ebp)
	pushl	12(%ebp)
	popl	%ebx
	popl	%eax
	addl	%ebx, %eax
	pushl	%eax
	popl	-4(%ebp)

	# Line 12: a:=10
	pushl	$10
	popl	%eax
	movl	%eax, 8(%ebp)

	# Line 13: out.writeint(a)
	pushl	8(%ebp)
	call	writeint
	addl	$4, %esp

	# Line 14: out.writeint(b)
	pushl	12(%ebp)
	call	writeint
	addl	$4, %esp

	# Line 15: out.writeint(c)
	pushl	-8(%ebp)
	call	writeint
	addl	$4, %esp
		
	movl	-4(%ebp), %eax
	movl	%ebp, %esp
	popl	%ebp
	ret	 
.global meth1
_meth1:

	pushl	%ebp
	movl	%esp, %ebp
	pushl	$0
	pushl	$0

	# Line 22: d:=-5
	pushl	$-5
	popl	_d

	# Line 23: meth1:=meth2(a+b,d)
	pushl	_d
	pushl	8(%ebp)
	pushl	12(%ebp)
	popl	%ebx
	popl	%eax
	addl	%ebx, %eax
	pushl	%eax
	call	_meth2
	addl	$8, %esp
	pushl	%eax
	popl	-4(%ebp)

	# Line 24: out.writeint(a)
	pushl	8(%ebp)
	call	writeint
	addl	$4, %esp

	# Line 25: out.writeint(b)
	pushl	12(%ebp)
	call	writeint
	addl	$4, %esp
		
	movl	-4(%ebp), %eax
	movl	%ebp, %esp
	popl	%ebp
	ret	 
.global start
start:

	pushl	%ebp
	movl	%esp, %ebp
	pushl	$0
	pushl	$0
	pushl	$0

	# Line 33: x:=10
	pushl	$10
	popl	-8(%ebp)

	# Line 34: y:=20
	pushl	$20
	popl	-12(%ebp)

	# Line 35: out.writeint(num)
	pushl	-4(%ebp)
	call	writeint
	addl	$4, %esp

	# Line 36: out.writeint(x)
	pushl	-8(%ebp)
	call	writeint
	addl	$4, %esp

	# Line 37: out.writeint(y)
	pushl	-12(%ebp)
	call	writeint
	addl	$4, %esp

	# Line 38: out.writeint(d)
	pushl	_d
	call	writeint
	addl	$4, %esp

	# Line 39: num:=meth1(x,y)
	pushl	-12(%ebp)
	pushl	-8(%ebp)
	call	_meth1
	addl	$8, %esp
	pushl	%eax
	popl	-4(%ebp)

	# Line 40: out.writeint(d)
	pushl	_d
	call	writeint
	addl	$4, %esp

	# Line 41: out.writeint(num)
	pushl	-4(%ebp)
	call	writeint
	addl	$4, %esp
		
	movl	-4(%ebp), %eax
	movl	%ebp, %esp
	popl	%ebp
	ret	 
.global main
main:
	call	start

	# Line 45: Exit call
	pushl	$0
	call	exit
