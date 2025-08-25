.data

	# Line 5: a:int
	.comm	_a, 4, 4

	# Line 6: b:int
	.comm	_b, 4, 4

	# Line 7: c:int
	.comm	_c, 4, 4

	# Line 8: d:int
	.comm	_d, 4, 4

	# Line 9: i:int
	.comm	_i, 4, 4

	# Line 10: j:int
	.comm	_j, 4, 4

	# Line 11: k:int
	.comm	_k, 4, 4
.text
.global start
start:

	# Line 15: b:=5
	pushl	$5
	popl	_b

	# Line 16: c:=2
	pushl	$2
	popl	_c

	# Line 17: d:=5
	pushl	$5
	popl	_d

	# Line 19: a:=1
	pushl	$1
	popl	_a

	# Line 20: not(a>=d)
_while1:
	pushl	_a
	pushl	_d
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
	jne	_startwhilebody1
	jmp	_endwhile1
_startwhilebody1:

	# Line 21: out.writeint(a)
	pushl	_a
	call	writeint
	addl	$4, %esp

	# Line 22: a:=a+1
	pushl	_a
	pushl	$1
	popl	%ebx
	popl	%eax
	addl	%ebx, %eax
	pushl	%eax
	popl	_a
	jmp	_while1
_endwhile1:

	# Line 25: i:=5
	pushl	$5
	popl	_i

	# Line 26: not(i>(c+d))
_while2:
	pushl	_i
	pushl	_c
	pushl	_d
	popl	%ebx
	popl	%eax
	addl	%ebx, %eax
	pushl	%eax
	popl	%ebx
	popl	%eax
	cmpl	%ebx, %eax
	setg	%al
	movzbl	%al, %eax
	pushl	%eax
	popl	%eax
	xorl	$1, %eax
	pushl	%eax
	popl	%eax
	cmpl	$0, %eax
	jne	_startwhilebody2
	jmp	_endwhile2
_startwhilebody2:

	# Line 28: j:=1
	pushl	$1
	popl	_j

	# Line 29: not(j>=4)
_while3:
	pushl	_j
	pushl	$4
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
	jne	_startwhilebody3
	jmp	_endwhile3
_startwhilebody3:

	# Line 31: k:=i
	pushl	_i
	popl	_k

	# Line 32: not(k>j)
_while4:
	pushl	_k
	pushl	_j
	popl	%ebx
	popl	%eax
	cmpl	%ebx, %eax
	setg	%al
	movzbl	%al, %eax
	pushl	%eax
	popl	%eax
	xorl	$1, %eax
	pushl	%eax
	popl	%eax
	cmpl	$0, %eax
	jne	_startwhilebody4
	jmp	_endwhile4
_startwhilebody4:

	# Line 33: out.writeint(0)
	pushl	$0
	call	writeint
	addl	$4, %esp

	# Line 34: k:=k+1
	pushl	_k
	pushl	$1
	popl	%ebx
	popl	%eax
	addl	%ebx, %eax
	pushl	%eax
	popl	_k
	jmp	_while4
_endwhile4:

	# Line 37: out.writeint(1)
	pushl	$1
	call	writeint
	addl	$4, %esp

	# Line 38: j:=j+1
	pushl	_j
	pushl	$1
	popl	%ebx
	popl	%eax
	addl	%ebx, %eax
	pushl	%eax
	popl	_j
	jmp	_while3
_endwhile3:

	# Line 41: out.writeint(2)
	pushl	$2
	call	writeint
	addl	$4, %esp

	# Line 42: i:=i+1
	pushl	_i
	pushl	$1
	popl	%ebx
	popl	%eax
	addl	%ebx, %eax
	pushl	%eax
	popl	_i
	jmp	_while2
_endwhile2:
	ret	 
.global main
main:
	call	start

	# Line 45: Exit call
	pushl	$0
	call	exit
