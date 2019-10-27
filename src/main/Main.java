package main;

import data.AnsiColor;
import impl.EngineImpl;
import services.Engine;

public class Main {
	public static void main(String[] args) {
		System.out.print(AnsiColor.INTEL_BLUE + "[INFORMATION] Game's initialisation..." + AnsiColor.RESET);
		System.out.flush();

		// L'ENGINE
		Engine engine = new EngineImpl();
		engine.init(30, 23, 3, 2);
		
		System.out.println(AnsiColor.INTEL_BLUE + "Done" + AnsiColor.RESET);
	}
}
