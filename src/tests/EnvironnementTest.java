package tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import contracts.EditableScreenContract;
import contracts.EnvironnementContract;
import contracts.GuardContract;
import contracts.PlayerContract;
import contracts.errors.PreconditionError;
import data.Cell;
import data.Couple;
import data.Item;
import impl.EditableScreenImpl;
import impl.EnvironnementImpl;
import impl.GuardImpl;
import impl.PlayerImpl;
import impl.TreasureImpl;
import services.EditableScreen;
import services.Environnement;
import services.Guard;
import services.Player;

public class EnvironnementTest {

	private Environnement testedEnvironnement;
	private EditableScreen editable;
	private Cell[][] cells;
	private Player player;
	private List<Guard> guards;
	private Guard g1, g2;
	private List<Item> treasures;
	private Item t1, t2;
	private int width = 5;
	private int height = 6;
	private int playerX = 0;
	private int playerY = 1;
	private int guardX = 1;
	private int guardY = 1;
	private int wdtPortail = 2;
	private int hgtPortail = 3;
	private int treasureX = 0;
	private int treasureY = 3;
	private int commonX = 1;
	private int commonY = 3;

	@Before
	public void beforeTests() {
		editable = new EditableScreenContract(new EditableScreenImpl());
		editable.init(width, height);

		guards = new ArrayList<>();
		g1 = new GuardContract(new GuardImpl());
		g1.init(testedEnvironnement, guardX, guardY, 1, null);
		guards.add(g1);

		g2 = new GuardContract(new GuardImpl());
		g2.init(testedEnvironnement, commonX, commonY, 2, null);
		guards.add(g2);

		treasures = new ArrayList<>();
		t1 = new TreasureImpl();
		t1.init(testedEnvironnement, treasureX, treasureY);
		treasures.add(t1);

		t2 = new TreasureImpl();
		t2.init(testedEnvironnement, commonX, commonY);
		treasures.add(t2);

		cells = new Cell[width][height];
		setUpEnvironnement();
		copyCellsFromEditable();

		testedEnvironnement = new EnvironnementContract(new EnvironnementImpl());
		player = new PlayerContract(new PlayerImpl());

		player.init(testedEnvironnement, playerX, playerY);
		testedEnvironnement.init(width, height, player, guards, treasures, cells, wdtPortail, hgtPortail);
	}

	@After
	public final void afterTests() {
		testedEnvironnement = null;
		editable = null;
		cells = null;
		player = null;
		g1 = null;
		t1 = null;
		g2 = null;
		t2 = null;
		guards = null;
		treasures = null;
	}

	private void copyCellsFromEditable() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++)
				cells[i][j] = editable.cellNature(i, j);
		}
	}

	private void setUpEnvironnement() {
		// Déssine un plateau de la forme
		//
		// |EMP|EMP|LAD|EMP|EMP|
		// |EMP|EMP|EMP|EMP|EMP|
		// |PLT|PLT|LAD|PLT|PLT|
		// |EMP|EMP|LAD|EMP|EMP|
		// |MLT|MLT|MLT|MLT|MLT|

		// L'échelle
		editable.setNature(2, 1, Cell.LAD);
		editable.setNature(2, 2, Cell.LAD);

		// Les plateformes sur le côté de l'echelle
		editable.setNature(0, 2, Cell.PLT);
		editable.setNature(1, 2, Cell.PLT);
		editable.setNature(3, 2, Cell.PLT);
		editable.setNature(4, 2, Cell.PLT);
	}

	////////////////////////////////////////////
	////////////////// INIT 2 //////////////////
	////////////////////////////////////////////
	@Test
	public void testInit2Postif() {
		try {
			testedEnvironnement.init(width, height, player, guards, treasures, cells, wdtPortail, hgtPortail);
			assertTrue(true);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	////////////////////////////////////////////
	/////////////// CELLCONTENT ////////////////
	////////////////////////////////////////////
	@Test
	public void testCellContentPostif1() {
		try {
			testedEnvironnement.cellContent(0, 0);
			assertTrue(true);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testCellContentPostif2() {
		try {
			Couple c = testedEnvironnement.cellContent(testedEnvironnement.getWidth() - 1,
					testedEnvironnement.getHeight() - 1);
			assertTrue(c.getCharacter() == null);
			assertTrue(c.getItem() == null);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testCellContentPostif3() {
		try {
			// Test que l'on récupère bien uniquement le garde
			Couple c = testedEnvironnement.cellContent(guardX, guardY);
			assertTrue(c.getCharacter() == g1);
			assertTrue(c.getItem() == null);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testCellContentPostif4() {
		try {
			// Test que l'on récupère bien uniquement le trésor
			Couple c = testedEnvironnement.cellContent(treasureX, treasureY);
			assertTrue(c.getCharacter() == null);
			assertTrue(c.getItem() == t1);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testCellContentPostif5() {
		try {
			// Test que l'on récupère bien un trésor et un garde présent à la même position
			Couple c = testedEnvironnement.cellContent(commonX, commonY);
			assertTrue(c.getCharacter() == g2);
			assertTrue(c.getItem() == t2);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testCellContentNegatif1() {
		try {
			testedEnvironnement.cellContent(-1, -1);
			assertTrue(false);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}

	@Test
	public void testCellContentNegatif2() {
		try {
			testedEnvironnement.cellContent(testedEnvironnement.getWidth(), testedEnvironnement.getHeight());
			assertTrue(true);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}
}
