
package tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import contracts.CharacterContract;
import contracts.EditableScreenContract;
import contracts.EnvironnementContract;
import contracts.GuardContract;
import contracts.errors.PostconditionError;
import contracts.errors.PreconditionError;
import data.Cell;
import data.Item;
import impl.EditableScreenImpl;
import impl.EnvironnementImpl;
import impl.GuardImpl;
import impl.PlayerImpl;
import services.EditableScreen;
import services.Environnement;
import services.Guard;
import services.Player;

public class CharacterTest {

	private services.Character testedCharacterWithContent;
	private services.Character testedCharacterWithoutContent;
	private Player playerWithContent;
	private Player playerWithoutContent;
	private Guard g1;
	private Guard g2;
	private EditableScreen editable;
	private Environnement environnementWithContent;
	private Environnement environnementWithoutContent;
	private Cell[][] cells;
	private List<Guard> guards;
	private List<Item> treasures;
	private int width = 7;
	private int height = 6;
	private int playerX = 1;
	private int playerY = 1;
	private int wdtPortail = 2;
	private int hgtPortail = 3;

	@Before
	public void beforeTests() {
		editable = new EditableScreenContract(new EditableScreenImpl());
		editable.init(width, height);

		// Les gardes des tests
		guards = new ArrayList<>();
		g1 = new GuardContract(new GuardImpl());
		g2 = new GuardContract(new GuardImpl());
		guards.add(g1);
		guards.add(g2);

		// Les trésors des tests
		treasures = new ArrayList<>();

		// On initialise un plateau pour tester
		cells = new Cell[width][height];
		setUpEnvironnement();
		copyCellsFromEditable();

		// Nos environnements
		// On utilise un environnement vide et un autre avec des gardes/items
		environnementWithContent = new EnvironnementContract(new EnvironnementImpl());
		environnementWithoutContent = new EnvironnementContract(new EnvironnementImpl());

		// On a besoin d'une instance de Player pour pouvoir utiliser un environnement
		playerWithContent = new PlayerImpl();
		playerWithoutContent = new PlayerImpl();
		testedCharacterWithContent = new CharacterContract(playerWithContent);
		testedCharacterWithoutContent = new CharacterContract(playerWithoutContent);

		// Le garde est au bout du plateau sur la même ligne que le joueur
		g1.init(environnementWithContent, 4, playerY);

		// Le garde est juste à gauche du joueur
		g2.init(environnementWithContent, playerX - 1, playerY);

		environnementWithContent.init(width, height, playerWithContent, guards, treasures, cells, wdtPortail, hgtPortail);

		environnementWithoutContent.init(width, height, playerWithoutContent, Collections.emptyList(),
				Collections.emptyList(), cells, wdtPortail, hgtPortail);

		testedCharacterWithContent.init(environnementWithContent, playerX, playerY);
		testedCharacterWithoutContent.init(environnementWithoutContent, playerX, playerY);

		// Le plateau des tests est le suivant (pour celui sans content, les GRD sont
		// des EMP)
		// 5 |EMP|EMP|LAD|EMP|EMP|EMP|EMP|
		// 4 |HDR|HDR|LAD|HDR|HDR|EMP|EMP|
		// 3 |HDR|EMP|LAD|EMP|HDR|EMP|EMP|
		// 2 |PLT|PLT|LAD|PLT|HDR|HDR|PLT|
		// 1 |GRD|PLR|LAD|EMP|GRD|EMP|EMP|
		// 0 |MLT|MLT|MLT|MLT|MLT|MLT|MLT|
		//     0   1   2   3   4   5   6
		// GRD => Guard g1
		// PLR => Player
	}

	@After
	public final void afterTests() {
		testedCharacterWithContent = null;
		testedCharacterWithoutContent = null;
		environnementWithContent = null;
		environnementWithoutContent = null;
		editable = null;
		cells = null;
		guards = null;
		g1 = null;
		g2 = null;
		treasures = null;
		playerWithContent = null;
		playerWithoutContent = null;
	}

	@SuppressWarnings("unused")
	private void displayCells() {
		for (int i = height - 1; i >= 0; i--) {
			for (int j = 0; j < width; j++)
				System.out.print(cells[j][i] + " ");
			System.out.println();
		}
		System.out.println();
	}

	private void changeCell(services.Character testedCharacter, int x, int y, Cell c) {
		cells[x][y] = c;

		if (testedCharacter == this.testedCharacterWithContent) {
			environnementWithContent.init(width, height, playerWithContent, guards, treasures, cells, wdtPortail, hgtPortail);
			testedCharacterWithContent.init(environnementWithContent, testedCharacterWithContent.getWdt(),
					testedCharacterWithContent.getHgt());
		} else {
			environnementWithoutContent.init(width, height, playerWithoutContent, Collections.emptyList(),
					Collections.emptyList(), cells, wdtPortail, hgtPortail);
			testedCharacterWithoutContent.init(environnementWithoutContent, testedCharacterWithoutContent.getWdt(),
					testedCharacterWithoutContent.getHgt());
		}
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
		// 5 |EMP|EMP|LAD|EMP|EMP|EMP|EMP|
		// 4 |HDR|HDR|LAD|HDR|HDR|EMP|EMP|
		// 3 |HDR|EMP|LAD|EMP|HDR|EMP|EMP|
		// 2 |PLT|PLT|LAD|PLT|HDR|HDR|PLT|
		// 1 |EMP|EMP|LAD|EMP|EMP|EMP|EMP|
		// 0 |MLT|MLT|MLT|MLT|MLT|MLT|MLT|
		//     0   1   2   3   4   5   6

		// L'échelle
		editable.setNature(2, 1, Cell.LAD);
		editable.setNature(2, 2, Cell.LAD);
		editable.setNature(2, 3, Cell.LAD);
		editable.setNature(2, 4, Cell.LAD);
		editable.setNature(2, 5, Cell.LAD);

		// Les handrails
		editable.setNature(4, 2, Cell.HDR);
		editable.setNature(5, 2, Cell.HDR);

		editable.setNature(0, 3, Cell.HDR);
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
	}

	////////////////////////////////////////////
	/////////////////// INIT ///////////////////
	////////////////////////////////////////////
	@Test
	public void testInitPostif1() {
		try {
			testedCharacterWithContent.init(environnementWithContent, playerX, playerY);
			testedCharacterWithoutContent.init(environnementWithoutContent, playerX, playerY);
			assertTrue(true);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	public void testInitPostif2() {
		try {
			testedCharacterWithContent.init(environnementWithContent, environnementWithContent.getWidth() - 1,
					environnementWithContent.getHeight() - 1);
			testedCharacterWithoutContent.init(environnementWithoutContent, environnementWithoutContent.getWidth() - 1,
					environnementWithoutContent.getHeight() - 1);
			assertTrue(true);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testInitNegatif1() {
		try {
			testedCharacterWithContent.init(environnementWithContent, -1, 0);
			assertTrue(false);
		} catch (PreconditionError e) {
			try {
				testedCharacterWithoutContent.init(environnementWithoutContent, -1, 0);
				assertTrue(false);
			} catch (PreconditionError e2) {
				assertTrue(true);
			}
		}
	}

	@Test
	public void testInitNegatif2() {
		try {
			testedCharacterWithContent.init(environnementWithContent, 0, -1);
			assertTrue(false);
		} catch (PreconditionError e) {
			try {
				testedCharacterWithoutContent.init(environnementWithoutContent, 0, -1);
				assertTrue(false);
			} catch (PreconditionError e2) {
				assertTrue(true);
			}
		}
	}

	@Test
	public void testInitNegatif3() {
		try {
			testedCharacterWithContent.init(environnementWithContent, -1, -1);
			assertTrue(false);
		} catch (PreconditionError e) {
			try {
				testedCharacterWithoutContent.init(environnementWithoutContent, -1, -1);
				assertTrue(false);
			} catch (PreconditionError e2) {
				assertTrue(true);
			}
		}
	}

	@Test
	public void testInitNegatif4() {
		try {
			testedCharacterWithContent.init(environnementWithContent, environnementWithContent.getWidth(), 0);
			assertTrue(false);
		} catch (PreconditionError e) {
			try {
				testedCharacterWithoutContent.init(environnementWithoutContent, environnementWithoutContent.getWidth(),
						0);
				assertTrue(false);
			} catch (PreconditionError e2) {
				assertTrue(true);
			}
		}
	}

	@Test
	public void testInitNegatif5() {
		try {
			testedCharacterWithContent.init(environnementWithContent, 0, environnementWithContent.getHeight());
			assertTrue(false);
		} catch (PreconditionError e) {
			try {
				testedCharacterWithoutContent.init(environnementWithoutContent, 0,
						environnementWithoutContent.getHeight());
				assertTrue(false);
			} catch (PreconditionError e2) {
				assertTrue(true);
			}
		}
	}

	@Test
	public void testInitNegatif6() {
		try {
			testedCharacterWithContent.init(environnementWithContent, environnementWithContent.getWidth(),
					environnementWithContent.getHeight());
			assertTrue(false);
		} catch (PreconditionError e) {
			try {
				testedCharacterWithoutContent.init(environnementWithoutContent, environnementWithoutContent.getWidth(),
						environnementWithoutContent.getHeight());
				assertTrue(false);
			} catch (PreconditionError e2) {
				assertTrue(true);
			}
		}
	}

	@Test
	public void testInitNegatif7() {
		try {
			testedCharacterWithContent.init(environnementWithContent, 0, 0);
			assertTrue(false);
		} catch (PreconditionError e) {
			try {
				testedCharacterWithoutContent.init(environnementWithoutContent, 0, 0);
				assertTrue(false);
			} catch (PreconditionError e2) {
				assertTrue(true);
			}
		}
	}

	///////////////////////////////////////////
	///////////////// GOLEFT /////////////////
	///////////////////////////////////////////
	public void testGoLeftSansEnvironnement1() {
		try {
			testedCharacterWithoutContent.goLeft();
			assertTrue(true);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoLeftSansEnvironnement2() {
		try {
			// Avance de deux cases puis revient
			int wdtAt_pre = testedCharacterWithoutContent.getWdt();
			int hgtAt_pre = testedCharacterWithoutContent.getHgt();

			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goLeft();
			testedCharacterWithoutContent.goLeft();
			assertTrue(testedCharacterWithoutContent.getWdt() == wdtAt_pre);
			assertTrue(testedCharacterWithoutContent.getHgt() == hgtAt_pre);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoLeftSansEnvironnement3() {
		try {
			// Force le mur à gauche
			int wdtAt_pre = testedCharacterWithoutContent.getWdt();
			int hgtAt_pre = testedCharacterWithoutContent.getHgt();
			testedCharacterWithoutContent.goLeft();
			testedCharacterWithoutContent.goLeft();
			testedCharacterWithoutContent.goLeft();
			testedCharacterWithoutContent.goLeft();
			testedCharacterWithoutContent.goLeft();
			testedCharacterWithoutContent.goLeft();
			testedCharacterWithoutContent.goLeft();
			testedCharacterWithoutContent.goLeft();
			assertTrue(wdtAt_pre - 1 == testedCharacterWithoutContent.getWdt());
			assertTrue(hgtAt_pre == testedCharacterWithoutContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoLeftSansEnvironnement4() {
		try {
			// On place un obstacle à gauche du joueur pour le bloquer après son premier
			// déplacement
			int old = testedCharacterWithoutContent.getWdt();
			int hgtAt_pre = testedCharacterWithoutContent.getHgt();

			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goRight();
			changeCell(testedCharacterWithoutContent, old, testedCharacterWithoutContent.getHgt(), Cell.MTL);
			testedCharacterWithoutContent.goLeft();
			testedCharacterWithoutContent.goLeft();
			testedCharacterWithoutContent.goLeft();

			assertTrue(testedCharacterWithoutContent.getWdt() == old + 1);
			assertTrue(testedCharacterWithoutContent.getHgt() == hgtAt_pre);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	public void testGoLeftSansEnvironnement5() {
		try {
			// Ici on va vérifier que le joueur peut bien se déplacer sur un HDR
			// On vérifie aussi que une fois au bout du HDR, le joueur monte sur la
			// plateforme à gauche

			// Le joueur se place sur le handrail
			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goRight();

			int oldWdt = testedCharacterWithoutContent.getWdt();
			int oldHgt = testedCharacterWithoutContent.getHgt();

			// Le joueur tombe sur le handrail
			testedCharacterWithoutContent.goRight();

			Cell c = environnementWithoutContent.cellNature(testedCharacterWithoutContent.getWdt(),
					testedCharacterWithoutContent.getHgt());

			// Le joueur est bien sur le handrail
			assertTrue(c == Cell.HDR);
			assertTrue(oldWdt + 1 == testedCharacterWithoutContent.getWdt());
			assertTrue(oldHgt - 1 == testedCharacterWithoutContent.getHgt());

			// Le joueur remonte sur une plateforme
			oldWdt = testedCharacterWithoutContent.getWdt();
			oldHgt = testedCharacterWithoutContent.getHgt();

			testedCharacterWithoutContent.goLeft();

			// Le joueur est monté sur la plateforme ?
			assertTrue(oldWdt - 1 == testedCharacterWithoutContent.getWdt());
			assertTrue(oldHgt + 1 == testedCharacterWithoutContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoLeftSansEnvironnement6() {
		// On veut s'assurer que si le joueur est deja sur un HDR et que, si un autre
		// HDR est present en dessous
		// Il reste sur le HDR présent à sa hauteur en faisant un goLeft

		testedCharacterWithoutContent.goRight();
		testedCharacterWithoutContent.goUp();
		testedCharacterWithoutContent.goUp();
		testedCharacterWithoutContent.goUp();
		testedCharacterWithoutContent.goUp();

		testedCharacterWithoutContent.goLeft();
		testedCharacterWithoutContent.goDown();

		Cell c = testedCharacterWithoutContent.getEnvi().cellNature(testedCharacterWithoutContent.getWdt(),
				testedCharacterWithoutContent.getHgt());

		// Le joueur est bien sur le premier HDR
		assertTrue(c == Cell.HDR);
		assertTrue(testedCharacterWithoutContent.getWdt() == 1);
		assertTrue(testedCharacterWithoutContent.getHgt() == 4);

		int wdtAt_pre = testedCharacterWithoutContent.getWdt();
		int hgtAt_pre = testedCharacterWithoutContent.getHgt();

		testedCharacterWithoutContent.goLeft();

		c = testedCharacterWithoutContent.getEnvi().cellNature(testedCharacterWithoutContent.getWdt(),
				testedCharacterWithoutContent.getHgt());

		// Le joueur est toujours sur un HDR (cette fois-ci, celui de gauche)
		assertTrue(c == Cell.HDR);
		assertTrue(testedCharacterWithoutContent.getWdt() == wdtAt_pre - 1);
		assertTrue(testedCharacterWithoutContent.getHgt() == hgtAt_pre);
	}
	
	@Test
	public void testGoLeftSansEnvironnement7() {
		// Ici, on va vérifier que le joueur peut tomber dans un HOL
		// Il peut remonter avec un goLeft
		changeCell(testedCharacterWithoutContent, 1, 2, Cell.HOL);

		try {
			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goUp();

			testedCharacterWithoutContent.goLeft();

			// Le joueur tombe dans le HOL
			testedCharacterWithoutContent.goDown();

			Cell c = environnementWithoutContent.cellNature(testedCharacterWithoutContent.getWdt(),
					testedCharacterWithoutContent.getHgt());

			assertTrue(c == Cell.HOL);

			int oldWdt = testedCharacterWithoutContent.getWdt();
			int oldHgt = testedCharacterWithoutContent.getHgt();

			// Le joueur peut remonter par la gauche
			testedCharacterWithoutContent.goLeft();
			assertTrue(oldWdt - 1 == testedCharacterWithoutContent.getWdt());
			assertTrue(oldHgt + 1 == testedCharacterWithoutContent.getHgt());

		} catch (PostconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}

	@Test
	public void testGoLeftAvecEnvironnement1() {
		try {
			testedCharacterWithContent.goLeft();
			assertTrue(true);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoLeftAvecEnvironnement2() {
		try {
			// Avance de deux cases puis reviens
			int wdtAt_pre = testedCharacterWithContent.getWdt();
			int hgtAt_pre = testedCharacterWithoutContent.getHgt();

			testedCharacterWithContent.goRight();
			testedCharacterWithContent.goRight();
			testedCharacterWithContent.goLeft();
			testedCharacterWithContent.goLeft();
			assertTrue(testedCharacterWithContent.getWdt() == wdtAt_pre);
			assertTrue(testedCharacterWithContent.getHgt() == hgtAt_pre);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoLeftAvecEnvironnement3() {
		try {
			// Force le garde à gauche
			int wdtAt_pre = testedCharacterWithContent.getWdt();
			int hgtAt_pre = testedCharacterWithoutContent.getHgt();
			testedCharacterWithContent.goLeft();
			testedCharacterWithContent.goLeft();
			testedCharacterWithContent.goLeft();
			testedCharacterWithContent.goLeft();
			testedCharacterWithContent.goLeft();
			testedCharacterWithContent.goLeft();
			testedCharacterWithContent.goLeft();
			testedCharacterWithContent.goLeft();
			assertTrue(wdtAt_pre == testedCharacterWithContent.getWdt());
			assertTrue(hgtAt_pre == testedCharacterWithContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoLeftAvecEnvironnement4() {
		try {
			// On place un obstacle à gauche du joueur pour le bloquer après ses deux
			// premiers déplacements
			int old = testedCharacterWithContent.getWdt();
			int hgtAt_pre = testedCharacterWithoutContent.getHgt();

			testedCharacterWithContent.goRight();
			testedCharacterWithContent.goRight();
			changeCell(testedCharacterWithContent, old, testedCharacterWithContent.getHgt(), Cell.MTL);
			testedCharacterWithContent.goLeft();
			testedCharacterWithContent.goLeft();
			testedCharacterWithContent.goLeft();

			assertTrue(testedCharacterWithContent.getWdt() == old + 1);
			assertTrue(testedCharacterWithContent.getHgt() == hgtAt_pre);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoLeftAvecEnvironnement5() {
		try {
			// Ici on va vérifier que le joueur est bloqué par un garde présent dans un HDR
			// Le joueur se place sur le handrail
			testedCharacterWithContent.goRight();
			testedCharacterWithContent.goUp();
			testedCharacterWithContent.goUp();
			testedCharacterWithContent.goRight();

			int oldWdt = testedCharacterWithContent.getWdt();
			int oldHgt = testedCharacterWithContent.getHgt();

			// Le joueur tombe sur le handrail
			testedCharacterWithContent.goRight();

			Cell c = environnementWithContent.cellNature(testedCharacterWithContent.getWdt(),
					testedCharacterWithContent.getHgt());

			// Le joueur est bien sur le handrail
			assertTrue(c == Cell.HDR);
			assertTrue(oldWdt + 1 == testedCharacterWithContent.getWdt());
			assertTrue(oldHgt - 1 == testedCharacterWithContent.getHgt());

			// Il va sur le handrail suivant
			testedCharacterWithContent.goRight();

			// On déplace un garde sur le handrail
			g1.goLeft();
			g1.goLeft();
			g1.goUp();
			g1.goUp();
			g1.goRight();

			oldWdt = g1.getWdt();
			oldHgt = g1.getHgt();

			g1.goRight();

			c = environnementWithContent.cellNature(g1.getWdt(), g1.getHgt());

			// On vérifie que le garde est bien sur le HDR
			assertTrue(c == Cell.HDR);
			assertTrue(oldWdt + 1 == g1.getWdt());
			assertTrue(oldHgt - 1 == g1.getHgt());

			/////////////////////////////////////////
			// g1 est en place pour bloquer le joueur
			/////////////////////////////////////////

			oldWdt = testedCharacterWithContent.getWdt();
			oldHgt = testedCharacterWithContent.getHgt();
			testedCharacterWithContent.goLeft();

			// g1 est censé avoir bloqué le joueur
			assertTrue(oldWdt == testedCharacterWithContent.getWdt());
			assertTrue(oldHgt == testedCharacterWithContent.getHgt());

			// g1 monte sur la plateforme de gauche afin d'empecher le joueur de remonter
			g1.goLeft();
			
			oldWdt = testedCharacterWithContent.getWdt();
			oldHgt = testedCharacterWithContent.getHgt();

			// Le joueur force pour remonter à gauche
			testedCharacterWithContent.goLeft();
			testedCharacterWithContent.goLeft();
			testedCharacterWithContent.goLeft();
			testedCharacterWithContent.goLeft();

			assertTrue(oldWdt == testedCharacterWithContent.getWdt() + 1);
			assertTrue(oldHgt == testedCharacterWithContent.getHgt());
			
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	///////////////////////////////////////////
	///////////////// GORIGHT /////////////////
	///////////////////////////////////////////

	@Test
	public void testGoRightSansEnvironnement1() {
		try {
			testedCharacterWithoutContent.goRight();
			assertTrue(true);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoRightSansEnvironnement2() {
		try {
			// Avance jusqu'au bout du plateau
			int hgtAt_pre = testedCharacterWithoutContent.getHgt();

			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goRight();
			assertTrue(
					testedCharacterWithoutContent.getWdt() == testedCharacterWithoutContent.getEnvi().getWidth() - 1);
			assertTrue(hgtAt_pre == testedCharacterWithoutContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoRightSansEnvironnement3() {
		try {
			// Force le mur à droite
			int hgtAt_pre = testedCharacterWithoutContent.getHgt();

			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goRight();

			assertTrue(
					testedCharacterWithoutContent.getWdt() == testedCharacterWithoutContent.getEnvi().getWidth() - 1);
			assertTrue(hgtAt_pre == testedCharacterWithoutContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoRightSansEnvironnement4() {
		try {
			// On place un obstacle à droite du joueur pour le bloquer
			int old = testedCharacterWithoutContent.getWdt();
			int hgtAt_pre = testedCharacterWithoutContent.getHgt();
			changeCell(testedCharacterWithoutContent, testedCharacterWithoutContent.getWdt() + 1,
					testedCharacterWithoutContent.getHgt(), Cell.MTL);
			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goRight();

			assertTrue(old == testedCharacterWithoutContent.getWdt());
			assertTrue(hgtAt_pre == testedCharacterWithoutContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoRightSansEnvironnement5() {
		try {
			// Ici on va vérifier que le joueur peut bien se déplacer sur un HDR
			// On vérifie aussi que une fois au bout du HDR, le joueur monte sur la
			// plateforme à droite

			// Le joueur se place sur le handrail
			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goRight();

			int oldWdt = testedCharacterWithoutContent.getWdt();
			int oldHgt = testedCharacterWithoutContent.getHgt();

			// Le joueur tombe sur le handrail
			testedCharacterWithoutContent.goRight();

			Cell c = environnementWithoutContent.cellNature(testedCharacterWithoutContent.getWdt(),
					testedCharacterWithoutContent.getHgt());

			// Le joueur est bien sur le handrail
			assertTrue(c == Cell.HDR);
			assertTrue(oldWdt + 1 == testedCharacterWithoutContent.getWdt());
			assertTrue(oldHgt - 1 == testedCharacterWithoutContent.getHgt());

			// Il va sur le handrail suivant
			oldWdt = testedCharacterWithoutContent.getWdt();
			testedCharacterWithoutContent.goRight();

			// Le joueur peut se déplacer sur le handrail suivant
			assertTrue(oldWdt + 1 == testedCharacterWithoutContent.getWdt());

			// Le joueur remonte sur une plateforme
			oldWdt = testedCharacterWithoutContent.getWdt();
			oldHgt = testedCharacterWithoutContent.getHgt();

			testedCharacterWithoutContent.goRight();

			// Le joueur est monté sur la plateforme ?
			assertTrue(oldWdt + 1 == testedCharacterWithoutContent.getWdt());
			assertTrue(oldHgt + 1 == testedCharacterWithoutContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoRightSansEnvironnement6() {
		// On veut s'assurer que si le joueur est deja sur un HDR et que, si un autre
		// HDR est present en dessous
		// Il reste sur le HDR présent à sa hauteur en faisant un goRight

		testedCharacterWithoutContent.goRight();
		testedCharacterWithoutContent.goUp();
		testedCharacterWithoutContent.goUp();
		testedCharacterWithoutContent.goUp();
		testedCharacterWithoutContent.goUp();

		testedCharacterWithoutContent.goRight();
		testedCharacterWithoutContent.goDown();

		Cell c = testedCharacterWithoutContent.getEnvi().cellNature(testedCharacterWithoutContent.getWdt(),
				testedCharacterWithoutContent.getHgt());

		// Le joueur est bien sur le premier HDR
		assertTrue(c == Cell.HDR);
		assertTrue(testedCharacterWithoutContent.getWdt() == 3);
		assertTrue(testedCharacterWithoutContent.getHgt() == 4);

		int wdtAt_pre = testedCharacterWithoutContent.getWdt();
		int hgtAt_pre = testedCharacterWithoutContent.getHgt();

		testedCharacterWithoutContent.goRight();

		c = testedCharacterWithoutContent.getEnvi().cellNature(testedCharacterWithoutContent.getWdt(),
				testedCharacterWithoutContent.getHgt());

		// Le joueur est toujours sur un HDR (cette fois-ci, celui de droite)
		assertTrue(c == Cell.HDR);
		assertTrue(testedCharacterWithoutContent.getWdt() == wdtAt_pre + 1);
		assertTrue(testedCharacterWithoutContent.getHgt() == hgtAt_pre);
	}

	@Test
	public void testGoRightSansEnvironnement7() {
		// Ici, on va vérifier que le joueur peut tomber dans un HOL
		// Il peut remonter avec un goRight
		changeCell(testedCharacterWithoutContent, 0, 2, Cell.HOL);

		try {
			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goUp();

			testedCharacterWithoutContent.goLeft();
			testedCharacterWithoutContent.goLeft();

			// Le joueur tombe dans le HOL
			testedCharacterWithoutContent.goDown();
			testedCharacterWithoutContent.goDown();

			Cell c = environnementWithoutContent.cellNature(testedCharacterWithoutContent.getWdt(),
					testedCharacterWithoutContent.getHgt());

			assertTrue(c == Cell.HOL);

			int oldWdt = testedCharacterWithoutContent.getWdt();
			int oldHgt = testedCharacterWithoutContent.getHgt();

			// Le joueur peut remonter par la droite
			testedCharacterWithoutContent.goRight();
			assertTrue(oldWdt + 1 == testedCharacterWithoutContent.getWdt());
			assertTrue(oldHgt + 1 == testedCharacterWithoutContent.getHgt());

		} catch (PostconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}
	
	@Test
	public void testGoRightAvecEnvironnement1() {
		try {
			testedCharacterWithContent.goRight();
			assertTrue(true);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoRightAvecEnvironnement2() {
		try {
			// Avance jusqu'au garde
			int hgtAt_pre = testedCharacterWithoutContent.getHgt();
			testedCharacterWithContent.goRight();
			testedCharacterWithContent.goRight();
			assertTrue(testedCharacterWithContent.getWdt() == g1.getWdt() - 1);
			assertTrue(testedCharacterWithContent.getHgt() == hgtAt_pre);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoRightAvecEnvironnement3() {
		try {
			// Force le garde à droite
			int hgtAt_pre = testedCharacterWithoutContent.getHgt();
			testedCharacterWithContent.goRight();
			testedCharacterWithContent.goRight();
			testedCharacterWithContent.goRight();
			testedCharacterWithContent.goRight();
			testedCharacterWithContent.goRight();
			testedCharacterWithContent.goRight();
			testedCharacterWithContent.goRight();
			testedCharacterWithContent.goRight();
			assertTrue(testedCharacterWithContent.getWdt() == g1.getWdt() - 1);
			assertTrue(testedCharacterWithContent.getHgt() == hgtAt_pre);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoRightAvecEnvironnement4() {
		try {
			// On place un obstacle à droite du joueur pour le bloquer
			int old = testedCharacterWithContent.getWdt();
			int hgtAt_pre = testedCharacterWithoutContent.getHgt();
			changeCell(testedCharacterWithContent, testedCharacterWithContent.getWdt() + 1,
					testedCharacterWithContent.getHgt(), Cell.MTL);
			testedCharacterWithContent.goRight();
			testedCharacterWithContent.goRight();

			assertTrue(old == testedCharacterWithContent.getWdt());
			assertTrue(testedCharacterWithContent.getHgt() == hgtAt_pre);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoRightAvecEnvironnement5() {
		try {
			// Ici on va vérifier que le joueur est bloqué par un garde présent dans un HDR
			// On déplace un garde sur le handrail
			g1.goLeft();
			g1.goLeft();
			g1.goUp();
			g1.goUp();
			g1.goRight();

			int oldWdt = g1.getWdt();
			int oldHgt = g1.getHgt();

			g1.goRight();

			Cell c = environnementWithContent.cellNature(g1.getWdt(), g1.getHgt());

			// On vérifie que le garde est bien sur le HDR
			assertTrue(c == Cell.HDR);
			assertTrue(oldWdt + 1 == g1.getWdt());
			assertTrue(oldHgt - 1 == g1.getHgt());

			g1.goRight();

			/////////////////////////////////////////
			// g1 est en place pour bloquer le joueur
			/////////////////////////////////////////

			// Le joueur se place sur le handrail
			testedCharacterWithContent.goRight();
			testedCharacterWithContent.goUp();
			testedCharacterWithContent.goUp();
			testedCharacterWithContent.goRight();

			oldWdt = testedCharacterWithContent.getWdt();
			oldHgt = testedCharacterWithContent.getHgt();

			// Le joueur tombe sur le handrail
			testedCharacterWithContent.goRight();

			c = environnementWithContent.cellNature(testedCharacterWithContent.getWdt(),
					testedCharacterWithContent.getHgt());

			// Le joueur est bien sur le handrail
			assertTrue(c == Cell.HDR);
			assertTrue(oldWdt + 1 == testedCharacterWithContent.getWdt());
			assertTrue(oldHgt - 1 == testedCharacterWithContent.getHgt());

			// Il va essayer d'aller sur le handrail suivant
			oldWdt = testedCharacterWithContent.getWdt();
			testedCharacterWithContent.goRight();

			// g1 est censé avoir bloqué le joueur
			assertTrue(oldWdt == testedCharacterWithContent.getWdt());

			// On va vérifier que le g1 bloque le joueur s'il est sur la plateforme de fin
			g1.goRight();

			oldWdt = testedCharacterWithContent.getWdt();
			oldHgt = testedCharacterWithContent.getHgt();

			testedCharacterWithContent.goRight();
			testedCharacterWithContent.goRight();
			// testedCharacterWithContent.goRight();

			assertTrue(oldWdt + 1 == testedCharacterWithContent.getWdt());
			assertTrue(oldHgt == testedCharacterWithContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	//////////////////////////////////////////
	////////////////// GOUP //////////////////
	//////////////////////////////////////////
	@Test
	public void testGoUpSansEnvironnement1() {
		try {
			// Va vers l'echelle
			testedCharacterWithoutContent.goRight();

			// Le character peut prendre l'echelle
			int htgAt_pre = testedCharacterWithoutContent.getHgt();
			testedCharacterWithoutContent.goUp();
			assertTrue(htgAt_pre + 1 == testedCharacterWithoutContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoUpSansEnvironnement2() {
		try {
			// Va vers l'echelle
			testedCharacterWithoutContent.goRight();

			// Le character peut prendre l'echelle et monter seulement de trois cases (Il
			// peut etre sur l'échelle)
			int htgAt_pre = testedCharacterWithoutContent.getHgt();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goUp();
			assertTrue(htgAt_pre + 3 == testedCharacterWithoutContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoUpSansEnvironnement3() {
		try {
			// Essaie de monter alors qu'aucune echelle n'est présente

			int htgAt_pre = testedCharacterWithoutContent.getHgt();
			testedCharacterWithoutContent.goUp();
			assertTrue(htgAt_pre == testedCharacterWithoutContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoUpSansEnvironnement4() {
		try {
			// Va vers l'echelle
			testedCharacterWithoutContent.goRight();

			// Le character peut prendre l'echelle et monter seulement de quatre cases
			// (bordure du plateau)
			// Ici il essaie de forcer et aller plus haut que possible
			int htgAt_pre = testedCharacterWithoutContent.getHgt();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goUp();
			assertTrue(htgAt_pre + 4 == testedCharacterWithoutContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoUpSansEnvironnement5() {
		// Ici, on va vérifier que le joueur peut tomber dans un HOL
		// Il ne peut remonter avec un goUp
		changeCell(testedCharacterWithoutContent, 1, 2, Cell.HOL);

		try {
			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goUp();

			testedCharacterWithoutContent.goLeft();

			// Le joueur tombe dans le HOL
			testedCharacterWithoutContent.goDown();

			Cell c = environnementWithoutContent.cellNature(testedCharacterWithoutContent.getWdt(),
					testedCharacterWithoutContent.getHgt());

			assertTrue(c == Cell.HOL);

			int oldWdt = testedCharacterWithoutContent.getWdt();
			int oldHgt = testedCharacterWithoutContent.getHgt();

			testedCharacterWithoutContent.goUp();
			assertTrue(oldWdt == testedCharacterWithoutContent.getWdt());
			assertTrue(oldHgt == testedCharacterWithoutContent.getHgt());

		} catch (PostconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void testGoUpAvecEnvironnement1() {
		try {
			// Un garde va bloquer l'echelle en montant dessus
			g1.goLeft();
			g1.goLeft();
			g1.goUp();

			// Le character se met en bat de l'echelle et essaie de monter
			int htgAt_pre = testedCharacterWithContent.getHgt();
			testedCharacterWithContent.goRight();
			testedCharacterWithContent.goUp();
			assertTrue(htgAt_pre == testedCharacterWithContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoUpAvecEnvironnement2() {
		try {
			// Un garde va bloquer l'echelle en montant dessus
			g1.goLeft();
			g1.goLeft();
			g1.goUp();
			g1.goUp();

			// Le character se met en bat de l'echelle et essaie de monter
			int htgAt_pre = testedCharacterWithContent.getHgt();
			testedCharacterWithContent.goRight();
			testedCharacterWithContent.goUp();
			testedCharacterWithContent.goUp();
			testedCharacterWithContent.goUp();
			assertTrue(htgAt_pre + 1 == testedCharacterWithContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoUpAvecEnvironnement3() {
		try {
			// Essaie de monter alors qu'aucune echelle n'est présente

			int htgAt_pre = testedCharacterWithContent.getHgt();
			testedCharacterWithContent.goUp();
			assertTrue(htgAt_pre == testedCharacterWithContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoUpAvecEnvironnement4() {
		try {
			// Va vers l'echelle
			testedCharacterWithContent.goRight();

			// Le character peut prendre l'echelle et monter seulement de quatre cases
			// (bordure du plateau)
			// Ici il essaie de forcer et aller plus haut que possible
			int htgAt_pre = testedCharacterWithContent.getHgt();
			testedCharacterWithContent.goUp();
			testedCharacterWithContent.goUp();
			testedCharacterWithContent.goUp();
			testedCharacterWithContent.goUp();
			testedCharacterWithContent.goUp();
			testedCharacterWithContent.goUp();
			assertTrue(htgAt_pre + 4 == testedCharacterWithContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	//////////////////////////////////////////
	///////////////// GODOWN /////////////////
	//////////////////////////////////////////
	@Test
	public void testGoDownSansEnvironnement1() {
		try {
			// Va vers l'echelle
			testedCharacterWithoutContent.goRight();

			// Le character peut prendre l'echelle
			int htgAt_pre = testedCharacterWithoutContent.getHgt();
			testedCharacterWithoutContent.goUp();
			assertTrue(htgAt_pre + 1 == testedCharacterWithoutContent.getHgt());

			testedCharacterWithoutContent.goDown();
			assertTrue(htgAt_pre == testedCharacterWithoutContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoDownSansEnvironnement2() {
		try {
			// Va vers l'echelle
			testedCharacterWithoutContent.goRight();

			// Le character peut prendre l'echelle et monter seulement de trois cases (Il
			// peut etre sur l'échelle)
			int htgAt_pre = testedCharacterWithoutContent.getHgt();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goUp();
			assertTrue(htgAt_pre + 3 == testedCharacterWithoutContent.getHgt());

			// Il descend des trois cases de l'echelle
			testedCharacterWithoutContent.goDown();
			testedCharacterWithoutContent.goDown();
			testedCharacterWithoutContent.goDown();
			assertTrue(htgAt_pre == testedCharacterWithoutContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoDownSansEnvironnement3() {
		try {
			// Essaie de descendre alors qu'un MTL est en dessous

			int htgAt_pre = testedCharacterWithoutContent.getHgt();
			testedCharacterWithoutContent.goDown();
			assertTrue(htgAt_pre == testedCharacterWithoutContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoDownSansEnvironnement4() {
		try {
			// Va vers l'echelle
			testedCharacterWithoutContent.goRight();

			// Le character peut prendre l'echelle et essaie de ne faire que descendre
			int htgAt_pre = testedCharacterWithoutContent.getHgt();
			testedCharacterWithoutContent.goDown();
			testedCharacterWithoutContent.goDown();
			testedCharacterWithoutContent.goDown();
			testedCharacterWithoutContent.goDown();
			testedCharacterWithoutContent.goDown();
			testedCharacterWithoutContent.goDown();
			assertTrue(htgAt_pre == testedCharacterWithoutContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoDownSansEnvironnement5() {
		try {
			// On va vérifier que si le joueur est sur un HDR et qu'il peut descendre

			// Le joueur se place sur le handrail
			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goRight();

			int oldWdt = testedCharacterWithoutContent.getWdt();
			int oldHgt = testedCharacterWithoutContent.getHgt();

			// Le joueur tombe sur le handrail
			testedCharacterWithoutContent.goRight();

			Cell c = environnementWithoutContent.cellNature(testedCharacterWithoutContent.getWdt(),
					testedCharacterWithoutContent.getHgt());

			// Le joueur est bien sur le handrail
			assertTrue(c == Cell.HDR);
			assertTrue(oldWdt + 1 == testedCharacterWithoutContent.getWdt());
			assertTrue(oldHgt - 1 == testedCharacterWithoutContent.getHgt());

			// Le joueur va essayer de descendre
			oldWdt = testedCharacterWithoutContent.getWdt();
			oldHgt = testedCharacterWithoutContent.getHgt();

			testedCharacterWithoutContent.goDown();

			assertTrue(oldWdt == testedCharacterWithoutContent.getWdt());
			assertTrue(oldHgt - 1 == testedCharacterWithoutContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoDownSansEnvironnement6() {
		// Ici, on va vérifier que le joueur peut tomber dans un HOL
		// Il ne tombera pas plus en faisant goDown
		changeCell(testedCharacterWithoutContent, 1, 2, Cell.HOL);

		try {
			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goUp();

			testedCharacterWithoutContent.goLeft();

			// Le joueur tombe dans le HOL
			testedCharacterWithoutContent.goDown();

			Cell c = environnementWithoutContent.cellNature(testedCharacterWithoutContent.getWdt(),
					testedCharacterWithoutContent.getHgt());

			assertTrue(c == Cell.HOL);

			int oldWdt = testedCharacterWithoutContent.getWdt();
			int oldHgt = testedCharacterWithoutContent.getHgt();

			testedCharacterWithoutContent.goDown();
			assertTrue(oldWdt == testedCharacterWithoutContent.getWdt());
			assertTrue(oldHgt == testedCharacterWithoutContent.getHgt());

		} catch (PostconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoDownAvecEnvironnement1() {
		try {
			// Le character se met sur l'echelle et monte d'une case
			testedCharacterWithContent.goRight();
			testedCharacterWithContent.goUp();
			testedCharacterWithContent.goUp();

			// Un garde va bloquer l'echelle en montant dessus après qu le joueur aie deja
			// monté d'une case sur l'echelle
			g1.goLeft();
			g1.goLeft();
			g1.goUp();

			int htgAt_pre = testedCharacterWithContent.getHgt();

			// Essaie de descendre alors qu'un garde est en dessous
			testedCharacterWithContent.goDown();

			assertTrue(htgAt_pre == testedCharacterWithContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoDownAvecEnvironnement2() {
		try {
			// Le character se met sur l'echelle et monte d'une case
			testedCharacterWithContent.goRight();
			testedCharacterWithContent.goUp();
			testedCharacterWithContent.goUp();

			// Un garde va bloquer l'echelle en montant dessus après qu le joueur aie deja
			// monté d'une case sur l'echelle
			g1.goLeft();
			g1.goLeft();
			g1.goUp();

			int htgAt_pre = testedCharacterWithContent.getHgt();

			// Force pour essayer de descendre
			testedCharacterWithContent.goDown();
			testedCharacterWithContent.goDown();
			testedCharacterWithContent.goDown();

			assertTrue(htgAt_pre == testedCharacterWithContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoDownAvecEnvironnement3() {
		try {
			// Essaie de descendre alors qu'aucune echelle n'est présente

			int htgAt_pre = testedCharacterWithContent.getHgt();
			testedCharacterWithContent.goDown();
			assertTrue(htgAt_pre == testedCharacterWithContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoDownAvecEnvironnement4() {
		try {
			// Va vers l'echelle
			testedCharacterWithContent.goRight();

			// Ici il essaie de forcer et aller plus bas que possible
			int htgAt_pre = testedCharacterWithContent.getHgt();
			testedCharacterWithContent.goDown();
			testedCharacterWithContent.goDown();
			testedCharacterWithContent.goDown();
			testedCharacterWithContent.goDown();
			testedCharacterWithContent.goDown();
			testedCharacterWithContent.goDown();
			assertTrue(htgAt_pre == testedCharacterWithContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGoDownAvecEnvironnement5() {
		try {
			// On va vérifier que si le joueur est sur un HDR et qu'un garde est dessous,
			// Le joueur sera bloqué en faisant goDown

			// Le joueur se place sur le handrail
			testedCharacterWithContent.goRight();
			testedCharacterWithContent.goUp();
			testedCharacterWithContent.goUp();
			testedCharacterWithContent.goRight();

			int oldWdt = testedCharacterWithContent.getWdt();
			int oldHgt = testedCharacterWithContent.getHgt();

			// Le joueur tombe sur le handrail
			testedCharacterWithContent.goRight();

			Cell c = environnementWithContent.cellNature(testedCharacterWithContent.getWdt(),
					testedCharacterWithContent.getHgt());

			// Le joueur est bien sur le handrail
			assertTrue(c == Cell.HDR);
			assertTrue(oldWdt + 1 == testedCharacterWithContent.getWdt());
			assertTrue(oldHgt - 1 == testedCharacterWithContent.getHgt());

			// Le joueur va essayer de descendre, il sera bloqué par le garde
			oldWdt = testedCharacterWithContent.getWdt();
			oldHgt = testedCharacterWithContent.getHgt();

			testedCharacterWithContent.goDown();

			assertTrue(c == Cell.HDR);
			assertTrue(oldWdt == testedCharacterWithContent.getWdt());
			assertTrue(oldHgt == testedCharacterWithContent.getHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	/////////////////////////////////
	////// BackInitialPosition //////
	/////////////////////////////////
	@Test
	public void TestInitialPositionPositif1() {
		try {
			// Vérifie que le garde retourne bien à sa position initiale
			assertTrue(testedCharacterWithoutContent.getInitialWdt() == playerX);
			assertTrue(testedCharacterWithoutContent.getInitialHgt() == playerY);
		
			// Retour à la position initial du garde
			testedCharacterWithoutContent.backInitialPosition();
			assertTrue(testedCharacterWithoutContent.getWdt() == testedCharacterWithoutContent.getInitialWdt());
			assertTrue(testedCharacterWithoutContent.getHgt() == testedCharacterWithoutContent.getInitialHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void TestInitialPositionPositif2() {
		try {
			// Vérifie que le personnage retourne bien à sa position initiale après avoir fait quelque mouvements
			assertTrue(testedCharacterWithoutContent.getInitialWdt() == playerX);
			assertTrue(testedCharacterWithoutContent.getInitialHgt() == playerY);
			
			// On fait bouger le personnage
			testedCharacterWithoutContent.goRight();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goUp();
			testedCharacterWithoutContent.goRight();
			
			assertTrue(testedCharacterWithoutContent.getWdt() != testedCharacterWithoutContent.getInitialWdt());
			assertTrue(testedCharacterWithoutContent.getHgt() != testedCharacterWithoutContent.getInitialHgt());
			
			// Retour à la position initial du garde
			testedCharacterWithoutContent.backInitialPosition();
			assertTrue(testedCharacterWithoutContent.getWdt() == testedCharacterWithoutContent.getInitialWdt());
			assertTrue(testedCharacterWithoutContent.getHgt() == testedCharacterWithoutContent.getInitialHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
