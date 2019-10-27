package tests;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import contracts.GameObjectContract;
import contracts.ScreenContract;
import contracts.errors.PreconditionError;
import impl.ScreenImpl;
import impl.TreasureImpl;
import services.GameObject;
import services.Screen;

public class GameObjectTest {
	
	private GameObject testedGameObject;
	private Screen screen;
	
	private int width = 7;
	private int height = 6;
	private int objectX = 1;
	private int objectY = 1;
	
	@Before
	public void beforeTests() {
		testedGameObject = new GameObjectContract(new TreasureImpl());
		screen = new ScreenContract(new ScreenImpl());
		screen.init(width, height);
	}
	
	@After
	public final void afterTests() {
		testedGameObject = null;
	}
	
	@Test
	public void testInitPostif1() {
		try {
			testedGameObject.init(screen, objectX, objectY);
			assertTrue(true);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testInitPostif2() {
		try {
			testedGameObject.init(screen, screen.getWidth() - 1, screen.getHeight() - 1);
			assertTrue(true);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testInitNegatif1() {
		try {
			testedGameObject.init(screen, -1, 0);
			assertTrue(false);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}

	@Test
	public void testInitNegatif2() {
		try {
			testedGameObject.init(screen, 0, -1);
			assertTrue(false);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}
}
