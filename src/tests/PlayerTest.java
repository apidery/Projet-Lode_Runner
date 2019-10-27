package tests;

import static org.junit.Assert.assertTrue;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import contracts.EngineContract;
import data.Command;
import impl.EngineImpl;
import services.Engine;
import services.Player;

public class PlayerTest {
	
	private Player testedPlayer;
	private Engine engine;
	
	private int playerX = 3;
	private int playerY = 2;
	
	private int width = 30;
	private int height = 23;
	
	@Before
	public void beforeTests() {
		engine = new EngineContract(new EngineImpl());
		((EngineContract) engine).TESTING = true;
		engine.initForTests(width, height, playerX, playerY);
		testedPlayer = engine.getPlayer();
	}

	@After
	public void afterTests() {
		engine = null;
		testedPlayer = null;
	}

	//////////////////////////////////
	////////////// INIT //////////////
	//////////////////////////////////
	
	@Test
	public void testInitPositif() {
		testedPlayer.init(engine.getCurrentEnvironnement(), playerX, playerY, null);
		assertTrue(true);
	}
	
	//////////////////////////////////
	////////// DeacreaseVie //////////
	//////////////////////////////////
	
	@Test
	public void testDecreaseViePositif() {
		int vieAt_pre = testedPlayer.getVie();
		testedPlayer.decreaseVie();
		assertTrue(testedPlayer.getVie() == vieAt_pre -1);
	}

	//////////////////////////////////
	////////////// Step //////////////
	//////////////////////////////////
	
	@Test
	public void testStepPositif1() {
		// Si le joueur est au dessus du vide, il tombe
		engine.initForTests(width, height, playerX, playerY+1);
		
		testedPlayer = engine.getPlayer();
		testedPlayer.step();		
		
		assertTrue(testedPlayer.getLastAction() == Command.DOWN);
	}
	
	@Test
	public void testStepPositif2() {
		// Si on demande au joueur de faire une commande DOWN, alors il doit l'executer
		engine.setNextCommand(Command.DOWN);
		testedPlayer.step();		
		
		assertTrue(testedPlayer.getLastAction() == Command.DOWN);
	}
	
	@Test
	public void testStepPositif3() {
		// Si on demande au joueur de faire une commande UP, alors il doit l'executer
		engine.setNextCommand(Command.UP);
		testedPlayer.step();		
		
		assertTrue(testedPlayer.getLastAction() == Command.UP);
	}
	
	@Test
	public void testStepPositif4() {
		// Si on demande au joueur de faire une commande LEFT, alors il doit l'executer
		engine.setNextCommand(Command.LEFT);
		testedPlayer.step();		
		
		assertTrue(testedPlayer.getLastAction() == Command.LEFT);
	}
	
	@Test
	public void testStepPositif5() {
		// Si on demande au joueur de faire une commande RIGHT, alors il doit l'executer
		engine.setNextCommand(Command.RIGHT);
		testedPlayer.step();		
		
		assertTrue(testedPlayer.getLastAction() == Command.RIGHT);
	}
	
	@Test
	public void testStepPositif6() {
		// Si on demande au joueur de faire une commande DIGL, alors il doit l'executer
		engine.setNextCommand(Command.DIGL);
		testedPlayer.step();		
		
		assertTrue(testedPlayer.getLastAction() == Command.DIGL);
	}
	
	@Test
	public void testStepPositif7() {
		// Si on demande au joueur de faire une commande DIGR, alors il doit l'executer
		engine.setNextCommand(Command.DIGR);
		testedPlayer.step();		
		
		assertTrue(testedPlayer.getLastAction() == Command.DIGR);
	}
	
	@Test
	public void testStepPositif8() {
		// Le joueur fait un trou et tombe dedans après etre aller dessus
		
		// Creuse le trou
		engine.setNextCommand(Command.DIGL);
		testedPlayer.step();
		
		// Va au dessus du trou
		engine.setNextCommand(Command.LEFT);
		testedPlayer.step();
		
		// Tombe
		testedPlayer.step();
		
		assertTrue(testedPlayer.getLastAction() == Command.DOWN);
	}
}
