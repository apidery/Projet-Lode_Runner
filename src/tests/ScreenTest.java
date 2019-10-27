package tests;


import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import contracts.ScreenContract;
import impl.ScreenImpl;
import services.Screen;
import contracts.errors.PreconditionError;

public class ScreenTest {

	private Screen testedScreen;
	private int width = 7;
	private int height = 12;
	
	@Before
	public void beforeTests() {
		testedScreen = new ScreenContract(new ScreenImpl());
		testedScreen.init(width, height);
	}
	
	@After
	public final void afterTests() {
		testedScreen = null;
	}
	
	////////////////////////////////////////////
	/////////////////// INIT ///////////////////
	////////////////////////////////////////////
	@Test
	public void testInitPostif1() {
		try {
			testedScreen.init(width, height);
			assertTrue(true);
		}catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void testInitPostif2() {
		try {
			testedScreen.init(1, 1);
			assertTrue(true);
		}catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void testInitNegatif1() {
		try {
			testedScreen.init(width, 0);
			assertTrue(false);
		}catch (PreconditionError e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testInitNegatif2() {
		try {
			testedScreen.init(0, height);
			assertTrue(false);
		}catch (PreconditionError e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testInitNegatif3() {
		try {
			testedScreen.init(-1, -1);
			assertTrue(false);
		}catch (PreconditionError e) {
			assertTrue(true);
		}
	}
	
	/////////////////////////////////////////
	/////////////// CELLNATURE //////////////
	/////////////////////////////////////////
	@Test
	public void testCellNaturePostif1() {
		try {
			testedScreen.cellNature(width/2, height/2);
			assertTrue(true);
		}catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void testCellNaturePostif2() {
		try {
			testedScreen.cellNature(width-1, height-1);
			assertTrue(true);
		}catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void testCellNaturePostif3() {
		try {
			testedScreen.cellNature(0, 0);
			assertTrue(true);
		}catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void testCellNatureNegatif1() {
		try {
			testedScreen.cellNature(-1, 0);
			assertTrue(false);
		}catch (PreconditionError e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testCellNatureNegatif2() {
		try {
			testedScreen.cellNature(0, -1);
			assertTrue(false);
		}catch (PreconditionError e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testCellNatureNegatif3() {
		try {
			testedScreen.cellNature(-1, -1);
			assertTrue(false);
		}catch (PreconditionError e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testCellNatureNegatif4() {
		try {
			testedScreen.cellNature(width, 0);
			assertTrue(false);
		}catch (PreconditionError e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testCellNatureNegatif5() {
		try {
			testedScreen.cellNature(0, height);
			assertTrue(false);
		}catch (PreconditionError e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testCellNatureNegatif6() {
		try {
			testedScreen.cellNature(width, height);
			assertTrue(false);
		}catch (PreconditionError e) {
			assertTrue(true);
		}
	}
	
	/////////////////////////////////////////////
	//////////////// DIG ET FILL ////////////////
	/////////////////////////////////////////////
	
	// Pour dig et fill, il est impossible de faire des tests positifs
	// On ne peut modifier la nature d'une case d'un Screen.
	// Et un Screen et initialement remplis avec des cases EMP
	// Il faut donc reporter ces tests à EditableScreen
	
	@Test
	public void testDigNegatif() {
		try {
			testedScreen.init(width, height);
			testedScreen.dig(0, 0);
			assertTrue(false);
		}catch (PreconditionError e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testFillNegatif() {
		try {
			testedScreen.init(width, height);
			testedScreen.fill(0, 0);
			assertTrue(false);
		}catch (PreconditionError e) {
			assertTrue(true);
		}
	}
}
