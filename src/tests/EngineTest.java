package tests;

import static org.junit.Assert.assertTrue;

import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import contracts.EngineContract;
import contracts.errors.PreconditionError;
import data.Cell;
import data.Command;
import data.Direction;
import data.Status;
import impl.EngineImpl;
import services.Attack;
import services.Engine;
import services.GameObject;

public class EngineTest {
	// Dans les terrain utilisé pour tester, le premier terrain est injouable
	// Les tests sont donc sensé se dérouler sur le second terrain
	
	private Engine testedEngine;

	private int playerX = 3;
	private int playerY = 2;

	private int width = 30;
	private int height = 23;

	@Before
	public void beforeTests() {
		testedEngine = new EngineContract(new EngineImpl());
		((EngineContract) testedEngine).TESTING = true;
		testedEngine.initForTests(width, height, playerX, playerY);
	}

	@After
	public void afterTests() {
		testedEngine = null;
	}

	//////////////////////////////////
	////////////// INIT //////////////
	//////////////////////////////////
	@Test
	public void testInitPositif() {
		// Le init ne lève pas d'exception
		try {
			testedEngine.initForTests(width, height, playerX, playerY);
			assertTrue(testedEngine.getCurrentLevel() == 2);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testInitNegatif1() {
		// La width est nulle
		try {
			testedEngine.initForTests(0, height, playerX, playerY);
			assertTrue(false);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}

	@Test
	public void testInitNegatif2() {
		// La height est négative
		try {
			testedEngine.initForTests(width, -8, playerX, playerY);
			assertTrue(false);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}

	@Test
	public void testInitNegatif3() {
		// Le x du joueur est supérieur à la width du terrain
		try {
			testedEngine.initForTests(width, height, width + 1, playerY);
			assertTrue(false);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}

	@Test
	public void testInitNegatif4() {
		// Le y du joueur est égale à la height du terrain
		try {
			testedEngine.initForTests(width, height, playerX, height);
			assertTrue(false);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}

	//////////////////////////////////
	///////////// ADDHOLE ////////////
	//////////////////////////////////
	@Test
	public void testAddHolePositif1() {
		// Un trou en bas à droite du joueur
		try {
			testedEngine.addHole(playerX + 1, playerY - 1);
			assertTrue(true);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testAddHolePositif2() {
		// Un trou en bas à gauche du joueur
		try {
			testedEngine.addHole(playerX - 1, playerY - 1);
			assertTrue(true);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testAddHolePositif3() {
		// Un trou dans un non PLT (ne lève pas d'erreur mais la case ne change pas de
		// nature)
		// Ici la cible est un MTL
		try {
			int targetX = 0;
			int targetY = 0;
			testedEngine.addHole(targetX, targetY);
			assertTrue(testedEngine.getCurrentEnvironnement().cellNature(targetX, targetY) == Cell.MTL);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testAddHoleNegatif1() {
		// Ajoute un trou la ou un trou est deja présent
		try {
			int targetX = playerX + 1;
			int targetY = playerY - 1;
			testedEngine.addHole(targetX, targetY);

			testedEngine.addHole(targetX, targetY);
			assertTrue(false);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}

	@Test
	public void testAddHoleNegatif2() {
		// x du trou négatif
		try {
			testedEngine.addHole(-5, playerY - 1);
			assertTrue(false);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}

	@Test
	public void testAddHoleNegatif3() {
		// y du trou supérieur à la limite
		try {
			testedEngine.addHole(playerX - 1, height);
			assertTrue(false);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}

	
	//////////////////////////////////
	///////////// ADDATTACK ////////////
	//////////////////////////////////
	@Test
	public void testAddAttackPositif1() {
		// Une attaque à droite du joueur (qui part à gauche)
		try {
			testedEngine.addAttack(playerX + 1, playerY, Direction.LEFT);
			assertTrue(true);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testAddAttackPositif2() {
		// Une attaque en 0:0
		try {
			testedEngine.addAttack(0, 0, Direction.RIGHT);
			assertTrue(true);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testAddAttackNegatif1() {
		// Ajoute une attaque la ou une attaque est deja présente
		try {
			int targetX = playerX + 1;
			int targetY = playerY;
			Direction targetDirection = Direction.RIGHT;
			testedEngine.addAttack(targetX, targetY, targetDirection);

			testedEngine.addAttack(targetX, targetY, targetDirection);
			assertTrue(false);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}

	@Test
	public void testAddAttackNegatif2() {
		// x de l'attaque négatif
		try {
			testedEngine.addAttack(-5, playerY - 1, Direction.LEFT);
			assertTrue(false);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}

	@Test
	public void testAddAttackNegatif3() {
		// y de l'attaque supérieur à la limite
		try {
			testedEngine.addAttack(playerX - 1, height, Direction.RIGHT);
			assertTrue(false);
		} catch (PreconditionError e) {
			assertTrue(true);
		}
	}

	
	//////////////////////////////////
	///////// SETNEXTCOMMAND /////////
	//////////////////////////////////
	@Test
	public void testSetNextCommandPositif() {
		// La nouvelle commande est la bonne
		try {
			testedEngine.setNextCommand(Command.RIGHT);
			assertTrue(testedEngine.getNextCommand() == Command.RIGHT);
			
			testedEngine.setNextCommand(Command.NEUTRAL);
			assertTrue(testedEngine.getNextCommand() == Command.NEUTRAL);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	//////////////////////////////////
	////// CREATEENVIRONNEMENT ///////
	//////////////////////////////////
	@Test
	public void testCreateEnvironnementPositif() {
		// Ne lève pas d'erreur
		try {
			testedEngine.createEnvironnements(testedEngine.getScreenWdt(), testedEngine.getScreenHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	/////////////////////////////////////
	// CREATEENVIRONNEMENTWITHCONTRACT //
	/////////////////////////////////////
	@Test
	public void testCreateEnvironnementWithContractPositif() {
		// Ne lève pas d'erreur
		try {
			testedEngine.createEnvironnementsWithContract(testedEngine.getScreenWdt(), testedEngine.getScreenHgt());
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	//////////////////////////////////
	/////////// NEXTLEVEL ////////////
	//////////////////////////////////
	@Test
	public void testNextLevelPositif() {
		// Le terrain numéro 3 est injouable, il n'y a donc pas de prochain niveau
		// Le joueur à donc gagné par défaut
		try {
			testedEngine.nextLevel();
			assertTrue(testedEngine.getCurrentLevel() == -1);
			assertTrue(testedEngine.getStatus() == Status.WIN);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	//////////////////////////////////
	////////////// STEP //////////////
	//////////////////////////////////
	
	@Test
	public void testStepPositif1() {
		// Ne lève pas d'erreur te augmente le nombre de step
		try {
			testedEngine.step();
			assertTrue(testedEngine.getNbStep() == 1);
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void testStepPositif2() {
		// Ne lève pas d'erreur après avoir ajouté des attaques et des trous
		// Des attaques sont sensées sortir de l'écran après le step
		try {
			testedEngine.addHole(4, 21);
			testedEngine.addHole(23, 18);
			testedEngine.addHole(17, 12);
			
			testedEngine.addAttack(10, 8, Direction.LEFT);
			testedEngine.addAttack(29, 5, Direction.RIGHT);
			testedEngine.addAttack(0, 13, Direction.LEFT);
			testedEngine.step();
			testedEngine.step();
			
			// On vérifie les paramètre t des trous
			for(Entry<GameObject, Integer> e : testedEngine.getHoles().entrySet())
				assertTrue(e.getValue() == 2);
			
			// Deux attaques sont sensées avoir disparu après les steps (elles sont sorties de l'écran)
			assertTrue(testedEngine.getAttacks().size() == 1);
			
			// On vérifie le bon positionnement de l'attaque restante
			for(Attack a : testedEngine.getAttacks())
				assertTrue(a.getWdt() == 8 && a.getHgt() == 8);
				
			
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void testStepPositif3() {
		// Un trésor est présent trois cases à gauche du joueur
		// On va s'assurer qu'en déplancant le joueur dessus, le trésor disparait
		try {
			int nbTreasuresAt_pre = testedEngine.getCurrentEnvironnement().getTreasures().size();
			testedEngine.setNextCommand(Command.LEFT);
			testedEngine.step();
			testedEngine.setNextCommand(Command.LEFT);
			testedEngine.step();
			testedEngine.setNextCommand(Command.LEFT);
			testedEngine.step();
			
			// Le nombre de trésor à diminué
			assertTrue(testedEngine.getCurrentEnvironnement().getTreasures().size() == nbTreasuresAt_pre - 1);
			// Le score du joueur est passé à 1
			assertTrue(testedEngine.getScore() == 1);
				
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testStepPositif4() {
		// On tue le joueur, le status est sensé passer à LOSS
		try {
			// EN faisant des steps, les gardes s'approcheront du joueur et le tueront
			while(testedEngine.getPlayer().getVie() > 0)
				testedEngine.step();
			
			assertTrue(testedEngine.getStatus() == Status.LOSS);
				
		} catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
}
