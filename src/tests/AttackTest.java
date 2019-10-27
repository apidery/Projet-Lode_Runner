package tests;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import contracts.AttackContract;
import contracts.ScreenContract;
import contracts.errors.PreconditionError;
import data.Direction;
import impl.AttackImpl;
import impl.ScreenImpl;
import services.Attack;
import services.Screen;

public class AttackTest {
	private Attack testedAttack;
	private Screen screen;
	
	private int width = 7;
	private int height = 6;
	private int attackX = 1;
	private int attackY = 1;
	
	@Before
	public void beforeTests() {
		testedAttack = new AttackContract(new AttackImpl());
		screen = new ScreenContract(new ScreenImpl());
		screen.init(width, height);
	}
	
	@After
	public final void afterTests() {
		testedAttack = null;
	}
	
	@Test
	public void testInitPostif1() {
		try {
			testedAttack.init(screen, attackX, attackY, Direction.LEFT);
			assertTrue(true);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testInitPostif2() {
		try {
			testedAttack.init(screen, attackX, attackY, Direction.RIGHT);
			assertTrue(true);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void testStep1() {
		testedAttack.init(screen, attackX, attackY, Direction.LEFT);
		
		int wdtAt_pre = testedAttack.getWdt();
		int hgtAt_pre = testedAttack.getHgt();
		testedAttack.step();
		
		assertTrue(testedAttack.getWdt() == wdtAt_pre - 1);
		assertTrue(testedAttack.getHgt() == hgtAt_pre);
	}
	
	@Test
	public void testStep2() {
		testedAttack.init(screen, attackX, attackY, Direction.RIGHT);
		
		int wdtAt_pre = testedAttack.getWdt();
		int hgtAt_pre = testedAttack.getHgt();
		testedAttack.step();
		
		assertTrue(testedAttack.getWdt() == wdtAt_pre + 1);
		assertTrue(testedAttack.getHgt() == hgtAt_pre);
	}
}
