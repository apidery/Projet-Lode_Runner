package tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import contracts.EditableScreenContract;
import contracts.errors.PostconditionError;
import contracts.errors.PreconditionError;
import data.Cell;
import impl.EditableScreenImpl;
import services.EditableScreen;

public class EditableScreenTest {

	private EditableScreen testedScreen;
	private int width = 7;
	private int height = 12;

	@Before
	public void beforeTests() {
		testedScreen = new EditableScreenContract(new EditableScreenImpl());
		testedScreen.init(width, height);
	}

	@After
	public final void afterTests() {
		testedScreen = null;
	}

	////////////////////////////////
	//////////// PLAYABLE //////////
	////////////////////////////////
	@Test
	public void testPlayablePositif() {
		// À l'initialisation, un EditableScreen est playable
		assertTrue(testedScreen.playable());
	}

	@Test
	public void testPlayableNegatif1() {
		testedScreen.setNature(0, 0, Cell.PLT);
		assertFalse(testedScreen.playable());
	}

	@Test
	public void testPlayableNegatif2() {
		testedScreen.setNature(testedScreen.getWidth() - 1, 0, Cell.PLT);
		assertFalse(testedScreen.playable());
	}

	@Test
	public void testPlayableNegatif3() {
		testedScreen.setNature(testedScreen.getWidth() - 1, testedScreen.getHeight() - 1, Cell.HOL);
		assertFalse(testedScreen.playable());
	}

	@Test
	public void testPlayableNegatif4() {
		testedScreen.setNature(0, 0, Cell.HOL);
		assertFalse(testedScreen.playable());
	}

	///////////////////////////////
	/////////// SETNATURE /////////
	///////////////////////////////
	@Test
	public void testSetNaturePostif1() {
		try {
			testedScreen.setNature(0, 0, Cell.PLT);
			assertTrue(true);
		} catch (PreconditionError | PostconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testSetNaturePostif2() {
		try {
			testedScreen.setNature(testedScreen.getWidth() - 1, testedScreen.getHeight() - 1, Cell.LAD);
			assertTrue(true);
		} catch (PreconditionError | PostconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testSetNatureNegatif1() {
		try {
			testedScreen.init(width, height);
			testedScreen.setNature(testedScreen.getWidth(), testedScreen.getHeight(), Cell.LAD);
			assertTrue(false);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}

	@Test
	public void testSetNatureNegatif2() {
		try {
			testedScreen.init(width, height);
			testedScreen.setNature(0, testedScreen.getHeight(), Cell.LAD);
			assertTrue(false);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}

	@Test
	public void testSetNatureNegatif3() {
		try {
			testedScreen.init(width, height);
			testedScreen.setNature(testedScreen.getWidth(), 0, Cell.LAD);
			assertTrue(false);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}

	@Test
	public void testSetNatureNegatif4() {
		try {
			testedScreen.init(width, height);
			testedScreen.setNature(-1, testedScreen.getHeight() - 1, Cell.LAD);
			assertTrue(false);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}

	@Test
	public void testSetNatureNegatif5() {
		try {
			testedScreen.init(width, height);
			testedScreen.setNature(0, -1, Cell.LAD);
			assertTrue(false);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}

	@Test
	public void testSetNatureNegatif6() {
		try {
			testedScreen.init(width, height);
			testedScreen.setNature(-1, -1, Cell.LAD);
			assertTrue(false);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}

	///////////////////////////////
	//////////// DIG //////////////
	///////////////////////////////
	@Test
	public void testDigPositif1() {
		try {
			testedScreen.init(width, height);
			testedScreen.setNature(1, 1, Cell.PLT);
			testedScreen.dig(1, 1);
			assertTrue(testedScreen.cellNature(1, 1) == Cell.HOL);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testDigPositif2() {
		try {
			testedScreen.init(width, height);
			testedScreen.setNature(width - 1, height - 1, Cell.PLT);
			testedScreen.dig(width - 1, height - 1);
			assertTrue(testedScreen.cellNature(width-1, height-1) == Cell.HOL);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testDigNegatif1() {
		testedScreen.init(width, height);
		testedScreen.dig(0, 0);
		assertTrue(testedScreen.cellNature(0, 0) == Cell.MTL);
	}

	///////////////////////////////
	//////////// FILL //////////////
	///////////////////////////////
	@Test
	public void testFillPositif1() {
		try {
			testedScreen.init(width, height);
			testedScreen.setNature(1, 1, Cell.HOL);
			testedScreen.fill(1, 1);
			assertTrue(testedScreen.cellNature(1, 1) == Cell.PLT);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testFillPositif2() {
		try {
			testedScreen.init(width, height);
			testedScreen.setNature(width - 1, height - 1, Cell.HOL);
			testedScreen.fill(width - 1, height - 1);
			assertTrue(testedScreen.cellNature(width-1, height-1) == Cell.PLT);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testFillNegatif1() {
		testedScreen.init(width, height);
		testedScreen.fill(0, 0);
		assertTrue(testedScreen.cellNature(0, 0) == Cell.MTL);
	}
}
