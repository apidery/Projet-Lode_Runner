package tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import contracts.EditableScreenContract;
import contracts.EnvironnementContract;
import contracts.GuardContract;
import contracts.PathResolverContract;
import contracts.errors.PreconditionError;
import data.Cell;
import data.Command;
import impl.EditableScreenImpl;
import impl.EnvironnementImpl;
import impl.GuardImpl;
import impl.PathResolverImpl;
import impl.PlayerImpl;
import services.EditableScreen;
import services.Environnement;
import services.Guard;
import services.PathResolver;
import services.Player;

public class GuardTest {

	private PathResolver resolver;
	private EditableScreen editable;
	private Environnement environnement;
	private Player player;
	private List<Guard> guards;
	private Guard testedGuard;
	private int guardX = 1;
	private int guardY = 3;
	private int playerX = 1;
	private int playerY = 1;
	private int wdtPortail = 2;
	private int hgtPortail = 3;
	
	private Cell[][] cells;
	private int width = 9;
	private int height = 6;

	@Before
	public void beforeTests() {
		editable = new EditableScreenContract(new EditableScreenImpl());
		editable.init(width, height);

		// Les gardes des tests
		guards = new ArrayList<>();
		testedGuard = new GuardContract(new GuardImpl());
		guards.add(testedGuard);

		// On initialise un plateau pour tester
		cells = new Cell[width][height];
		setUpEnvironnement();
		copyCellsFromEditable();

		environnement = new EnvironnementContract(new EnvironnementImpl());
		resolver = new PathResolverContract(new PathResolverImpl());

		// On a besoin d'une instance de Player pour le PathResolver
		player = new PlayerImpl();
		player.init(environnement, playerX, playerY);

		// Le garde est au bout du plateau sur la même ligne que le joueur
		testedGuard.init(environnement, guardX, guardY, 1, resolver);

		environnement.init(width, height, player, guards, Collections.emptyList(), cells, wdtPortail, hgtPortail);

		resolver.init(environnement, player);

		// Le plateau des tests est le suivant (pour celui sans content, les GRD sont
		// des EMP)
		// 5 |EMP|EMP|LAD|EMP|EMP|EMP|EMP|EMP|EMP|
		// 4 |HDR|HDR|LAD|HDR|HDR|EMP|EMP|EMP|EMP|
		// 3 |HOL|GRD|LAD|EMP|HDR|EMP|EMP|EMP|EMP|
		// 2 |PLT|PLT|LAD|PLT|HDR|HDR|PLT|HOL|LAD|
		// 1 |EMP|PLR|LAD|EMP|EMP|EMP|EMP|EMP|EMP|
		// 0 |MLT|MLT|MLT|MLT|MLT|MLT|MLT|MTL|MTL|
		//     0   1   2   3   4   5   6   7   8
	}

	@After
	public void afterTests() {
		resolver = null;
		editable = null;
		environnement = null;
		cells = null;
		player = null;
		guards = null;
		testedGuard = null;
	}

	private void setUpEnvironnement() {
		// L'échelle
		editable.setNature(2, 1, Cell.LAD);
		editable.setNature(2, 2, Cell.LAD);
		editable.setNature(2, 3, Cell.LAD);
		editable.setNature(2, 4, Cell.LAD);
		editable.setNature(2, 5, Cell.LAD);

		// Les handrails
		editable.setNature(4, 2, Cell.HDR);
		editable.setNature(5, 2, Cell.HDR);

		editable.setNature(0, 3, Cell.HOL);
		editable.setNature(0, 4, Cell.HDR);
		editable.setNature(1, 4, Cell.HDR);

		editable.setNature(4, 3, Cell.HDR);
		editable.setNature(3, 4, Cell.HDR);
		editable.setNature(4, 4, Cell.HDR);

		// Les plateformes sur le côté de l'echelle et du handrail
		editable.setNature(0, 2, Cell.PLT);
		editable.setNature(1, 2, Cell.PLT);
		editable.setNature(3, 2, Cell.PLT);
		editable.setNature(6, 2, Cell.PLT);

		// Le HOL avec le LAD
		editable.setNature(7, 2, Cell.HOL);
		editable.setNature(8, 2, Cell.LAD);
	}

	private void copyCellsFromEditable() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++)
				cells[i][j] = editable.cellNature(i, j);
		}
	}

	//////////////////////////////////
	////////////// INIT //////////////
	//////////////////////////////////
	
	@Test
	public void testInitPositif() {
		testedGuard.init(environnement, guardX, guardY, 1, resolver);
		assertTrue(true);
	}
	
	
	//////////////////////////////////
	/////////// CLIMB LEFT ///////////
	//////////////////////////////////
	@Test
	public void testClimbLeftPositif1() {
		try {
			// On vérifie que le joueur peut faire climbRight en étant dans un trou
			testedGuard.goLeft();
			Cell c = environnement.cellNature(testedGuard.getWdt(), testedGuard.getHgt());

			assertTrue(c == Cell.HOL);
			testedGuard.climbLeft();
			assertTrue(true);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testClimbLeftPositif2() {
		try {
			// On vérifie que le joueur est bien sortie du trou après un goLeft
			testedGuard.goRight();
			testedGuard.goRight();
			testedGuard.goRight();
			testedGuard.goRight();
			testedGuard.goRight();
			testedGuard.goRight();
			testedGuard.goDown();
			Cell c = environnement.cellNature(testedGuard.getWdt(), testedGuard.getHgt());

			assertTrue(c == Cell.HOL);
			testedGuard.climbLeft();
			assertTrue(testedGuard.getWdt() == 6);
			assertTrue(testedGuard.getHgt() == 3);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testClimbLeftNegatif() {
		try {
			// Le joueur fait climbLeft en etant pas dans un HOL
			testedGuard.climbLeft();
			assertTrue(false);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}

	//////////////////////////////////
	/////////// CLIMB RIGHT //////////
	//////////////////////////////////
	@Test
	public void testClimbRightPositif1() {
		try {
			// On vérifie que le joueur peut faire climbRight en étant dans un trou
			testedGuard.goLeft();
			Cell c = environnement.cellNature(testedGuard.getWdt(), testedGuard.getHgt());

			assertTrue(c == Cell.HOL);
			testedGuard.climbRight();
			assertTrue(true);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testClimbRightPositif2() {
		try {
			// On vérifie que le joueur est bien sortie du trou après un goRight
			testedGuard.goRight();
			testedGuard.goRight();
			testedGuard.goRight();
			testedGuard.goRight();
			testedGuard.goRight();
			testedGuard.goRight();
			testedGuard.goDown();
			Cell c = environnement.cellNature(testedGuard.getWdt(), testedGuard.getHgt());

			assertTrue(c == Cell.HOL);
			testedGuard.climbRight();
			assertTrue(testedGuard.getWdt() == 8);
			assertTrue(testedGuard.getHgt() == 3);
			assertTrue(true);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testClimbRightNegatif() {
		try {
			// Le joueur fait climbRight en etant pas dans un HOL
			testedGuard.climbRight();
			assertTrue(false);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}

	/////////////////////////////////
	////////////// STEP /////////////
	/////////////////////////////////
	@Test
	public void TestStepPositif1() {
		try {
			// Vérifie que le garde tombe (il est au dessus du vide)
			testedGuard.init(environnement, 8, 5, 1, resolver);
			int wdtAt_pre = testedGuard.getWdt();
			int hgtAt_pre = testedGuard.getHgt();
			testedGuard.step();
			assertTrue(testedGuard.getLastAction() == Command.DOWN);
			assertTrue(testedGuard.getWdt() == wdtAt_pre);
			assertTrue(testedGuard.getHgt() == hgtAt_pre - 1);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void TestStepPositif2() {
		try {
			// Vérifie que le garde tombe (il est au dessus d'un HOL)
			testedGuard.init(environnement, 7, 3, 1, resolver);
			int wdtAt_pre = testedGuard.getWdt();
			int hgtAt_pre = testedGuard.getHgt();
			testedGuard.step();
			assertTrue(testedGuard.getLastAction() == Command.DOWN);
			assertTrue(testedGuard.getWdt() == wdtAt_pre);
			assertTrue(testedGuard.getHgt() == hgtAt_pre - 1);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void TestStepPositif3() {
		try {
			// Vérifie le TimeInHole du garde lorsqu'il est dans un HOL
			testedGuard.init(environnement, 7, 2, 1, resolver);
			int wdtAt_pre = testedGuard.getWdt();
			int hgtAt_pre = testedGuard.getHgt();
			int timeInHoleAt_pre = testedGuard.getTimeInHole();
			testedGuard.step();
			assertTrue(testedGuard.getWdt() == wdtAt_pre);
			assertTrue(testedGuard.getHgt() == hgtAt_pre);
			assertTrue(testedGuard.getTimeInHole() == timeInHoleAt_pre + 1);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void TestStepPositif4() {
		try {
			// Vérifie le TimeInHole du garde lorsqu'il est dans un HOL
			// Il est sensé sortir après trois steps (sortir par la gauche pour se
			// rappprocher du joueur)
			testedGuard.init(environnement, 7, 2, 1, resolver);
			int wdtAt_pre = testedGuard.getWdt();
			int hgtAt_pre = testedGuard.getHgt();
			int timeInHoleAt_pre = testedGuard.getTimeInHole();
			testedGuard.step();
			testedGuard.step();
			assertTrue(testedGuard.getWdt() == wdtAt_pre);
			assertTrue(testedGuard.getHgt() == hgtAt_pre);
			assertTrue(testedGuard.getTimeInHole() == timeInHoleAt_pre + 2);

			// Troisième step, le garde doit sortir par la gauche pour se rapprcher du
			// joueur
			wdtAt_pre = testedGuard.getWdt();
			hgtAt_pre = testedGuard.getHgt();
			testedGuard.step();
			assertTrue(testedGuard.getLastAction() == Command.LEFT);
			assertTrue(testedGuard.getWdt() == wdtAt_pre - 1);
			assertTrue(testedGuard.getHgt() == hgtAt_pre + 1);
			assertTrue(testedGuard.getTimeInHole() == 0);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void TestStepPositif5() {
		try {
			// Test de la commande NEUTRAL (fait notamment quand le guard est deja sur le
			// joueur)
			testedGuard.init(environnement, playerX, playerY, 1, resolver);
			int wdtAt_pre = testedGuard.getWdt();
			int hgtAt_pre = testedGuard.getHgt();
			testedGuard.step();

			assertTrue(testedGuard.getLastAction() == Command.NEUTRAL);
			assertTrue(testedGuard.getWdt() == wdtAt_pre);
			assertTrue(testedGuard.getHgt() == hgtAt_pre);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
