import java.io.*;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class CPU {
	// Possible instruction codes
	private int instrCodes[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 
			16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 50 };
	// Instructions that have operands - INCOMPLETE
	private int operandCodes[] = { 1, 2, 3, 4, 5, 7, 9, 20, 21, 22, 23};
	private int counter = 0; //testing
	// Registers
	private int PC = 0, Sp = 999, IR = 0, AC = 0, X = 0, Y = 0,
		interruptCounter = 0,
		interruptTime = 10;
	private boolean kernelMode = false;
	private Memory memory = new Memory();
	private BufferedWriter writer;
	
	// Constructors
	CPU(){}
	CPU(int t) throws IOException{
		PC = IR = AC = X = Y = interruptCounter = 0;
		interruptTime = t;
		writer = new BufferedWriter(new FileWriter("output.txt"));
	}
	
	// Methods
	/*int memoryRead(int addr){
		pw.print(addr);
		return sc.nextInt();
	}
	
	void memoryWrite(int address, int data) {
		pw.printf("%d:%d", address, data);
	}*/
	
	void startUserProgram() throws IOException {
		
		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec("java Memory");
		
		int operand; // temp variable
		while(true) {
			operand = -1;
			IR = memory.read(PC);
			if(hasOperand(IR)) {
				operand = memory.read(++PC);
			}
			handleInstruction(IR, operand);
			if(counter == -1)
				break;
		}
		writer.close();
	}
	
	boolean hasOperand(int x) {
		for(int i=0; i<operandCodes.length; i++) {
			if(x == operandCodes[i])
				return true;
		}
		return false;
	}
	
	boolean handleInstruction(int code, int operand) throws IOException {
		
		if(isValidInstr(code)) { // if code is in instruction set
			exInstr(IR, operand);
			if(kernelMode == false)
				interruptCounter++;
			//write("Executed instruction [" + IR + "] with operand " + operand + ". PC Sp IR AC X Y IntCount -> " + PC + " " + Sp + " " + IR + " " + AC + " " + X + " " + Y + " " + interruptCounter + "\n");
			PC++;
		}else{
			System.out.println("Invalid Instruction Set.");
			return false;
		}
		
		if(interruptCounter >= interruptTime && kernelMode == false) {
			//write("[Timer interrupt]\n");
			kernelMode = true;
			int userPC = PC-1, userSp = Sp;
			// Switch to System stack
			PC = 1000; Sp = 1999;
			// Push PC
			memory.write(Sp--, userPC);
			// Push Sp
			memory.write(Sp--, userSp);
			interruptCounter = 0;
		}
		return true;
	}
	
	boolean isValidInstr(int code) {
		for(int i=0; i<31; i++) {
			if(code == instrCodes[i])
				return true;
		}
		return false;
	}
	
	boolean exInstr(int code, int parameter) throws IOException {
		
		//if(counter++ > interruptTime-2)//testing
			//System.exit(0);
		
		if(code == 1) { // Load value to AC
			this.AC = parameter;
			return true;
		}else if(code == 2) { // Load value at address to AC
			if(parameter > 999 && kernelMode == false)
				memoryViolation();
			this.AC = memory.read(parameter);
			return true;
		}else if(code == 3) { // Load value from Index from address to AC
			if((parameter > 999 || memory.read(parameter) > 999) && kernelMode == false)
				memoryViolation();
			this.AC = memory.read(memory.read(parameter));
			return true;
		}else if(code == 4) { // Load value at address+X to AC
			if(parameter > 999 && kernelMode == false)
				memoryViolation();
			this.AC = memory.read(parameter+X);
			return true;
		}else if(code == 5) { // Load value at address+Y to AC
			if(parameter > 999 && kernelMode == false)
				memoryViolation();
			this.AC = memory.read(parameter+Y);
			return true;
		}else if(code == 6) { // Load value at SP+X to AC
			if(memory.read(Sp+X+1) > 999 && kernelMode == false)
				memoryViolation();
			this.AC = memory.read(Sp+X+1);
			return true;
		}else if(code == 7) { // Store value at AC to address
			memory.write(parameter, AC);
			return true;
		}else if(code == 8) { // Get a random int from 1-100 to AC
			int r = ThreadLocalRandom.current().nextInt(1, 101);
			AC = r;
			return true;
		}else if(code == 9) {
			if(parameter == 1) // port1=write ac as int
				write("" + AC);
			if(parameter == 2) // port2=write ac as char
				write(""+(char)AC);
			return true;
		}else if(code == 10) { // Add value in X to AC
			AC += X;
			return true;
		}else if(code == 11) { // Add value in Y to AC
			AC += Y;
			return true;
		}else if(code == 12) { // Subtract value in X to AC
			AC -= X;
			return true;
		}else if(code == 13) { // Subtract value in Y to AC
			AC -= Y;
			return true;
		}else if(code == 14) { // Copy value in AC to X
			X = AC;
			return true;
		}else if(code == 15) { // Copy value in X to AC
			AC = X;
			return true;
		}else if(code == 16) { // Copy value in AC to Y
			Y = AC;
			return true;
		}else if(code == 17) { // Copy value in Y to AC
			AC = Y;
			return true;
		}else if(code == 18) { // Copy value in AC to Sp
			Sp = AC;
			return true;
		}else if(code == 19) { // Copy value in Sp to AC
			AC = Sp;
			return true;
		}else if(code == 20) { // Jump to address
			PC = parameter-1;
			return true;
		}else if(code == 21) { // Jump to address if AC == 0
			if(AC == 0) {
				PC = parameter-1;
				return true;
			}
			return false;
		}else if(code == 22) { // Jump to address if AC != 0
			if(AC != 0) {
				PC = parameter-1;
				return true;
			}
			return false;
		}else if(code == 23) { // Push return address onto stack, jump to address
			memory.write(Sp--, PC);
			PC = parameter-1;
			return true;
		}else if(code == 24) { // Pop return address onto stack, jump to address
			PC = memory.read(++Sp);
			memory.write(Sp, 0);
			return true;
		}else if(code == 25) { // Increment X
			X++;
			return true;
		}else if(code == 26) { // Decrement X
			X--;
			return true;
		}else if(code == 27) { // Push AC to stack
			memory.write(Sp--, AC);
			return true;
		}else if(code == 28) { // Pop from stack to AC
			AC = memory.read(++Sp);
			memory.write(Sp, 0);
			return true;
		}else if(code == 29) { // Perform system call
			kernelMode = true;
			int userPC = PC, userSp = Sp;
			// Switch to System stack
			PC = 1500-1; Sp = 1999;
			// Push PC
			memory.write(Sp--, userPC);
			// Push Sp
			memory.write(Sp--, userSp);
			return true;
		}else if(code == 30) { // Return from system call
			int userPC = -1, userSp = -1;
			// Pop Sp
			userSp = memory.read(++Sp);
			// Pop PC
			userPC = memory.read(++Sp);
			// Switch to User stack
			Sp = userSp; PC = userPC;
			kernelMode = false;
			return true;
		}else if(code == 50) { // End execution (including memory)
			System.exit(0);
			return true;
		}
		return false;
	}
	
	void memoryViolation() throws IOException{
		write("Memory violation: accessing out of permission.\n");
		System.exit(0);
	}
	
	void write(String data) throws IOException{
		writer.write(data);
		writer.flush();
	}
	
	public static void main(String args[]) throws IOException {
		int interruptTime;
		if(args.length < 1)
			System.exit(0);
		String FileName = args[0]; // Should be passed as argument to main
		if(args.length < 2)
			interruptTime = 15;
		else
			interruptTime = Integer.parseInt(args[1]);

		//int x = Memory.read(0);
		BufferedReader reader;
		CPU Processor = new CPU(15);
		int memIndex = 0;
		
		try {
			reader = new BufferedReader(new FileReader(FileName));
			String line = reader.readLine();
			while(line != null) {
				if(line.length() > 0) {
					String temp = "";
					for(int i=0; i<line.length(); i++) {
						if(line.charAt(i) != '.' && Character.digit(line.charAt(i), 10) < 0)
							break;
						else
							temp += line.charAt(i);
					}
					
					if(temp.length() > 0) {
						if(temp.charAt(0) == '.') {
							String newTemp = "";
							for(int i=1; i<temp.length(); i++) {
								newTemp += temp.charAt(i);
							}
							memIndex = Integer.parseInt(newTemp);
						}else
							Processor.memory.write(memIndex++, Integer.parseInt(temp));
					}
				}
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		Processor.startUserProgram();
	}
	
}
