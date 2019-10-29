2 objects: CPU and Memory
Read input file into memory
Traverse through memory array executing each instruction

CPU:
	Registers ; PC, Sp, IR, AC, X, Y
	HashSet of instruction codes
	HashSet of instruction codes with operands
	Kernel Mode
	Functions:
		handleInstruction(instr, operand)
			if instr is in HashSet
				executeInstruction(instr, operand)
				interruptCounter++
				PC++
			if interruptCounter reaches interrupt interval
				kernel mode = true
				switch from user stack to system stack
		executeInstruction(instr, operand)
			31 if statements:
				loading into and out of AC register
				getting a random number between 1-100
				outputting AC to console as int or char
				adding or subtracting to AC
				jump address (moving PC)
					jump if equals
					jump if not equal
					Call addr (push return address to stack, jump to new address)
					Return addr (pop return address from stack, jump to that address)
				Push AC onto stack
				Pop from stack to AC
				Interrupt (perform system call)
					Move to system stack for system call handling
				Interrupt Return (return from system call)
					Move back to User stack
				End program
				
Memory:
	Stored as an int array of size 2000
	User memory ; 0-999
		User stack starts at 999 grows downwards
	System memory ; 1000-1999
		System stack starts at 1999 grows downwards
	Functions:
		int read(address)
			return mem[address]
		boolean write(address, data)
			mem[address] = data;
			return true