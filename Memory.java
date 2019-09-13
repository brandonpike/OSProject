import java.util.Scanner;

public class Memory {
	// memory block { 0-999 user; 1000-1999 system }
	private static int mem[];
	private static Scanner sc = new Scanner(System.in);
	
	// Constructors
	Memory() { 
		mem = new int[2000];
	}
	
	// Methods
	public static int read(int address) {
		if(address < 2000 && address >= 0)
			return mem[address];
		else
			return -1;
	}
	
	public boolean write(int address, int data) {
		if(address < 2000 && address >= 0) {
			mem[address] = data;
			return true;
		}else
			return false;
	}
	
	public static void main(String args[]){
		while(true){
			String line = null;
			if(sc.hasNext())
				line = sc.nextLine();
			if(line == "exit")
				break;
			else
				System.out.print(line);
		}
	}
	
	// testing purposes
	public void printMemory() {
		System.out.println("Full memory: ");
		for(int i=0; i<2000; i++) {
			System.out.print(i + ":" + mem[i] + "|");
			if(i != 0 &&i % 100 == 0)
				System.out.print("\n");
		}
		System.out.print("\n");
	}
	
	public void printUserStack() {
		System.out.println("User stack: ");
		for(int i=999; i>800; i--) {
			System.out.print(i + ":" + mem[i] + "|");
			if(i % 50 == 0)
				System.out.print("\n");
		}
		System.out.print("\n");
	}
	
	public void printSystemStack() {
		System.out.println("System stack: ");
		for(int i=1999; i>1800; i--) {
			System.out.print(i + ":" + mem[i] + "|");
			if(i % 50 == 0)
				System.out.print("\n");
		}
		System.out.print("\n");
	}
}
