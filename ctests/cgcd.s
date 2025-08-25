.data

	# Line 6: x:int
	.comm	_x, 4, 4

	# Line 7: y:int
	.comm	_y, 4, 4

	# Line 8: ans:int
	.comm	_ans, 4, 4
.text
.global gcd
_gcd:

	pushl	%ebp
	movl	%esp, %ebp
	pushl	$0

	# Line 12: b=0
	pushl	12(%ebp)
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

	# Line 13: gcd:=a
	pushl	8(%ebp)
	popl	-4(%ebp)
	jmp	_endif1
_else1:

	# Line 15: a:=gcd(b,a-(a/b)*b)
	pushl	8(%ebp)
	pushl	8(%ebp)
	pushl	12(%ebp)
	popl	%ebx
	popl	%eax
	movl	$0, %edx
	idivl	%ebx
	pushl	%eax
	pushl	12(%ebp)
	popl	%ebx
	popl	%eax
	imull	%ebx, %eax
	pushl	%eax
	popl	%ebx
	popl	%eax
	subl	%ebx, %eax
	pushl	%eax
	pushl	12(%ebp)
	call	_gcd
	addl	$8, %esp
	pushl	%eax
	popl	%eax
	movl	%eax, 8(%ebp)

	# Line 16: gcd:=a
	pushl	8(%ebp)
	popl	-4(%ebp)
_endif1:
		
	movl	-4(%ebp), %eax
	movl	%ebp, %esp
	popl	%ebp
	ret	 
.global displayres
_displayres:

	pushl	%ebp
	movl	%esp, %ebp

	# Line 22: out.writeint(ans)
	pushl	8(%ebp)
	call	writeint
	addl	$4, %esp
		
	movl	-4(%ebp), %eax
	movl	%ebp, %esp
	popl	%ebp
	ret	 
.global start
start:

	# Line 27: x:=in.readint()
	call	readint
	pushl	%eax
	popl	_x

	# Line 28: y:=in.readint()
	call	readint
	pushl	%eax
	popl	_y

	# Line 29: ans:=gcd(x,y)
	pushl	_y
	pushl	_x
	call	_gcd
	addl	$8, %esp
	pushl	%eax
	popl	_ans

	# Line 30: displayres(ans)
	pushl	_ans
	call	_displayres
	addl	$4, %esp
	ret	 
.global main
main:
	call	start

	# Line 33: Exit call
	pushl	$0
	call	exit
