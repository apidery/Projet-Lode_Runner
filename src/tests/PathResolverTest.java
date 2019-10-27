package tests;

import static org.junit.Assert.assertFalse;
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
import data.Item;
import data.Move;
import impl.EditableScreenImpl;
import impl.EnvironnementImpl;
import impl.GuardImpl;
import impl.PathResolverImpl;
import impl.PlayerImpl;
import impl.TreasureImpl;
import services.EditableScreen;
import services.Environnement;
import services.Guard;
import services.PathResolver;
import services.Player;

public class PathResolverTest {
	
	private PathResolver testedResolver;
	private EditableScreen editable;
	private Environnement environnement;
	private Player player;
	private List<Guard> guards;
	private Guard g1;
	private Guard g2;
	private int playerX = 1;
	private int playerY = 1;
	
	private Cell[][] cells;
	private int width = 9;
	private int height = 6;
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

		// On initialise un plateau pour tester
		cells = new Cell[width][height];
		setUpEnvironnement();
		copyCellsFromEditable();

		environnement = new EnvironnementContract(new EnvironnementImpl());

		// On a besoin d'une instance de Player pour le PathResolver
		player = new PlayerImpl();
		player.init(environnement, playerX, playerY);

		// Le garde est au bout du plateau sur la même ligne que le joueur
		g1.init(environnement, 4, playerY);

		g2.init(environnement, 1, 3);

		environnement.init(width, height, player, guards, Collections.emptyList(), cells, wdtPortail, hgtPortail);

		testedResolver = new PathResolverContract(new PathResolverImpl());
		testedResolver.init(environnement, player);
		
		// Le plateau des tests est le suivant (pour celui sans content, les GRD sont
		// des EMP)
		// 5 |EMP|EMP|LAD|EMP|EMP|EMP|EMP|PLT|EMP|
		// 4 |HDR|HDR|LAD|HDR|HDR|EMP|EMP|PLT|PLT|
		// 3 |HDR|GRD|LAD|EMP|HDR|EMP|EMP|EMP|EMP|
		// 2 |PLT|PLT|LAD|PLT|HDR|HDR|PLT|HOL|LAD|
		// 1 |EMP|PLR|LAD|EMP|GRD|EMP|EMP|EMP|EMP|
		// 0 |MLT|MLT|MLT|MLT|MLT|MLT|MLT|MTL|MTL|
		//     0   1   2   3   4   5   6   7   8
	}
	
	@After
	public void afterTests() {
		testedResolver = null;
		editable = null;
		environnement = null;
		cells = null;
		player = null;
		guards = null;
		g1 = null;
		g2 = null;
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
		
		// Le HOL avec le LAD
		editable.setNature(7, 2, Cell.HOL);
		editable.setNature(8, 2, Cell.LAD);
		
		// Les PLT pour bloquer l'accès au trésor
		editable.setNature(7, 5, Cell.PLT);
		editable.setNature(7, 4, Cell.PLT);
		editable.setNature(8, 4, Cell.PLT);
	}
	
	private void copyCellsFromEditable() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++)
				cells[i][j] = editable.cellNature(i, j);
		}
	}
	
	private void changeCell(int x, int y, Cell c) {
		cells[x][y] = c;

		environnement.init(width, height, player, guards, Collections.emptyList(), cells, wdtPortail, hgtPortail);
		testedResolver.init(environnement, player);
	}
	
	@SuppressWarnings("unused")
	private void printNeighboursOfNode(int nodeId) {
		List<Integer>[] adjancencyList = testedResolver.getGraphe();
		System.out.print(testedResolver.getWdtOfNode(nodeId) + ":" + testedResolver.getHgtOfNode(nodeId) + " ==> [");
		
		for(Integer n : adjancencyList[nodeId])
			System.out.print(" " + testedResolver.getWdtOfNode(n) + ":" + testedResolver.getHgtOfNode(n));
		
		System.out.println(" ]");
	}
	
	private boolean checkNeighboursOfNode(int nodeId, List<Pair> neighbours) {
		if (neighbours.size() != testedResolver.getGraphe()[nodeId].size())
			return false;
		
		boolean flag = false;
		for(Pair neighbour : neighbours) {
			flag = false;
			for(Integer neighbour2 : testedResolver.getGraphe()[nodeId]) {
				int neighbourInt = neighbour2.intValue();
				if (testedResolver.getWdtOfNode(neighbourInt) == neighbour.x && testedResolver.getHgtOfNode(neighbourInt) == neighbour.y) {
					flag = true;
					break;
				}
			}
			if (!flag)
				return false;
		}
		return flag;
	}
	
	@Test
	public void testNodeIdOfPositionPositif1() {
		try {
			testedResolver.getNodeIdOfPosition(0, 0);
			assertTrue(true);
		}
		catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void testNodeIdOfPositionPositif2() {
		try {
			testedResolver.getNodeIdOfPosition(width-1, height-1);
			assertTrue(true);
		}
		catch (PreconditionError e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void testNodeIdOfPositionNegatif1() {
		try {
			testedResolver.getNodeIdOfPosition(0, -1);
			assertTrue(false);
		}
		catch (PreconditionError e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testNodeIdOfPositionNegatif2() {
		try {
			testedResolver.getNodeIdOfPosition(-1, 0);
			assertTrue(false);
		}
		catch (PreconditionError e) {
			assertTrue(true);
		}
	}

	////////////////////////////////////////
	///////////////// INIT /////////////////
	////////////////////////////////////////
	@Test
	public void testInitPositif() {
		testedResolver.init(environnement, player);
	}
	
	////////////////////////////////////////
	////////////// PLT ET MTL //////////////
	////////////////////////////////////////
	@Test
	public void testConversionPlateauToGraphPLTMTL() {
		// Tous les noeuds de types PLT/MTL ne sont pas sensés avoir de voisin
		List<Integer>[] adjancencyList = testedResolver.getGraphe();
		for (int node=0; node<adjancencyList.length; node++) {
			if (testedResolver.getNatureOfNode(node) == Cell.PLT || testedResolver.getNatureOfNode(node) == Cell.MTL)
				assertTrue(adjancencyList[node].isEmpty());
		}
	}
	
	///////////////////////////////////////
	///////////////// LAD /////////////////
	///////////////////////////////////////
	@Test
	public void testConversionPlateauToGraphLAD1() {
		// Ce noeud la est sensé avoir trois voisins (1:1, 3:1, 2:2)
		
		List<Integer>[] adjancencyList = testedResolver.getGraphe();
		boolean flag = false;
		int searchedWdt = 2;
		int searchedHgt = 1;
		int node;
		for (node = 0; node < adjancencyList.length; node++) {
			if (testedResolver.getWdtOfNode(node) == searchedWdt && testedResolver.getHgtOfNode(node) == searchedHgt) {
				flag = true;
				break;
			}
		}
		// On a bien trouvé le noeud
		assertTrue(flag);
		
		List<Pair> expected = new ArrayList<>();
		expected.add(new Pair(1, 1));
		expected.add(new Pair(3, 1));
		expected.add(new Pair(2, 2));
		assertTrue(checkNeighboursOfNode(node, expected));
	}

	@Test
	public void testConversionPlateauToGraphLAD2() {
		// Ce noeud la est sensé avoir deux voisins (2:1, 2:3)
				
		List<Integer>[] adjancencyList = testedResolver.getGraphe();
		boolean flag = false;
		int searchedWdt = 2;
		int searchedHgt = 2;
		int node;
		for (node = 0; node < adjancencyList.length; node++) {
			if (testedResolver.getWdtOfNode(node) == searchedWdt && testedResolver.getHgtOfNode(node) == searchedHgt) {
				flag = true;
				break;
			}
		}
		assertTrue(flag);
		
		List<Pair> expected = new ArrayList<>();
		expected.add(new Pair(2, 1));
		expected.add(new Pair(2, 3));
		assertTrue(checkNeighboursOfNode(node, expected));
	}

	@Test
	public void testConversionPlateauToGraphLAD3() {
		// Ce noeud la est sensé avoir quatres voisins (1:3, 3:3, 2:2, 2:4)
		List<Integer>[] adjancencyList = testedResolver.getGraphe();
		boolean flag = false;
		int searchedWdt = 2;
		int searchedHgt = 3;
		int node;
		for (node = 0; node < adjancencyList.length; node++) {
			if (testedResolver.getWdtOfNode(node) == searchedWdt && testedResolver.getHgtOfNode(node) == searchedHgt) {
				flag = true;
				break;
			}
		}
		// On a bien trouvé le noeud
		assertTrue(flag);
		
		List<Pair> expected = new ArrayList<>();
		expected.add(new Pair(1, 3));
		expected.add(new Pair(3, 3));
		expected.add(new Pair(2, 2));
		expected.add(new Pair(2, 4));
		assertTrue(checkNeighboursOfNode(node, expected));
	}
	
	@Test
	public void testConversionPlateauToGraphLAD4() {
		// Ce noeud la est sensé avoir quatres voisins (1:4, 3:4, 2:3, 2:5)
		
		List<Integer>[] adjancencyList = testedResolver.getGraphe();
		boolean flag = false;
		int searchedWdt = 2;
		int searchedHgt = 4;
		int node;
		for (node = 0; node < adjancencyList.length; node++) {
			if (testedResolver.getWdtOfNode(node) == searchedWdt && testedResolver.getHgtOfNode(node) == searchedHgt) {
				flag = true;
				break;
			}
		}
		// On a bien trouvé le noeud
		assertTrue(flag);
		
		List<Pair> expected = new ArrayList<>();
		expected.add(new Pair(1, 4));
		expected.add(new Pair(3, 4));
		expected.add(new Pair(2, 3));
		expected.add(new Pair(2, 5));
		assertTrue(checkNeighboursOfNode(node, expected));
	}
	
	@Test
	public void testConversionPlateauToGraphLAD5() {
		// Ce noeud la est sensé avoir trois voisins (2:4, 1:5, 3:5)
		
		List<Integer>[] adjancencyList = testedResolver.getGraphe();
		boolean flag = false;
		int searchedWdt = 2;
		int searchedHgt = 5;
		int node;
		for (node = 0; node < adjancencyList.length; node++) {
			if (testedResolver.getWdtOfNode(node) == searchedWdt && testedResolver.getHgtOfNode(node) == searchedHgt) {
				flag = true;
				break;
			}
		}
		// On a bien trouvé le noeud
		assertTrue(flag);
		
		List<Pair> expected = new ArrayList<>();
		expected.add(new Pair(2, 4));
		expected.add(new Pair(1, 5));
		expected.add(new Pair(3, 5));
		assertTrue(checkNeighboursOfNode(node, expected));		
	}
	
	///////////////////////////////////////
	///////////////// HDR /////////////////
	///////////////////////////////////////
	@Test
	public void testConversionPlateauToGraphHDR1() {
		// Ce noeud la est sensé avoir un voisin (1:3)
		
		List<Integer>[] adjancencyList = testedResolver.getGraphe();
		boolean flag = false;
		int searchedWdt = 0;
		int searchedHgt = 3;
		int node;
		for (node = 0; node < adjancencyList.length; node++) {
			if (testedResolver.getWdtOfNode(node) == searchedWdt && testedResolver.getHgtOfNode(node) == searchedHgt) {
				flag = true;
				break;
			}
		}
		// On a bien trouvé le noeud
		assertTrue(flag);
		
		List<Pair> expected = new ArrayList<>();
		expected.add(new Pair(1, 3));
		assertTrue(checkNeighboursOfNode(node, expected));
	}
	
	@Test
	public void testConversionPlateauToGraphHDR2() {
		// Ce noeud la est sensé avoir deux voisins (1:4, 0:3)
		
		List<Integer>[] adjancencyList = testedResolver.getGraphe();
		boolean flag = false;
		int searchedWdt = 0;
		int searchedHgt = 4;
		int node;
		for (node = 0; node < adjancencyList.length; node++) {
			if (testedResolver.getWdtOfNode(node) == searchedWdt && testedResolver.getHgtOfNode(node) == searchedHgt) {
				flag = true;
				break;
			}
		}
		// On a bien trouvé le noeud
		assertTrue(flag);
				
		List<Pair> expected = new ArrayList<>();
		expected.add(new Pair(1, 4));
		expected.add(new Pair(0, 3));
		assertTrue(checkNeighboursOfNode(node, expected));
	}

	@Test
	public void testConversionPlateauToGraphHDR3() {
		// Ce noeud la est sensé avoir trois voisins (0:4, 2:4, 1:3)
		
		List<Integer>[] adjancencyList = testedResolver.getGraphe();
		boolean flag = false;
		int searchedWdt = 1;
		int searchedHgt = 4;
		int node;
		for (node = 0; node < adjancencyList.length; node++) {
			if (testedResolver.getWdtOfNode(node) == searchedWdt && testedResolver.getHgtOfNode(node) == searchedHgt) {
				flag = true;
				break;
			}
		}
		// On a bien trouvé le noeud
		assertTrue(flag);
				
		List<Pair> expected = new ArrayList<>();
		expected.add(new Pair(0, 4));
		expected.add(new Pair(2, 4));
		expected.add(new Pair(1, 3));
		assertTrue(checkNeighboursOfNode(node, expected));
	}
	
	@Test
	public void testConversionPlateauToGraphHDR4() {
		// On teste qu'il y a un bien un lien entre un noeuds en w-1:h+1 à la fin d'une HDR
		// Ce noeud la est sensé avoir trois voisins (3:3, 5:2, 4:1)
		
		List<Integer>[] adjancencyList = testedResolver.getGraphe();
		boolean flag = false;
		int searchedWdt = 4;
		int searchedHgt = 2;
		int node;
		for (node = 0; node < adjancencyList.length; node++) {
			if (testedResolver.getWdtOfNode(node) == searchedWdt && testedResolver.getHgtOfNode(node) == searchedHgt) {
				flag = true;
				break;
			}
		}
		// On a bien trouvé le noeud
		assertTrue(flag);
				
		List<Pair> expected = new ArrayList<>();
		expected.add(new Pair(3, 3));
		expected.add(new Pair(5, 2));
		expected.add(new Pair(4, 1));
		assertTrue(checkNeighboursOfNode(node, expected));
	}
	
	@Test
	public void testConversionPlateauToGraphHDR5() {
		// On teste qu'il y a un bien un lien entre un noeuds en w+1:h+1 à la fin d'une HDR
		// Ce noeud la est sensé avoir trois voisins (6:3, 4:2, 5:1)
		
		List<Integer>[] adjancencyList = testedResolver.getGraphe();
		boolean flag = false;
		int searchedWdt = 5;
		int searchedHgt = 2;
		int node;
		for (node = 0; node < adjancencyList.length; node++) {
			if (testedResolver.getWdtOfNode(node) == searchedWdt && testedResolver.getHgtOfNode(node) == searchedHgt) {
				flag = true;
				break;
			}
		}
		// On a bien trouvé le noeud
		assertTrue(flag);
				
		List<Pair> expected = new ArrayList<>();
		expected.add(new Pair(6, 3));
		expected.add(new Pair(4, 2));
		expected.add(new Pair(5, 1));
		assertTrue(checkNeighboursOfNode(node, expected));
	}
	
	///////////////////////////////////////
	///////////////// HOL /////////////////
	///////////////////////////////////////
	@Test
	public void testConversionPlateauToGraphHOL1() {
		// Ici le noeud est sensé avoir deux voisins (5:2, 7:3)
		
		List<Integer>[] adjancencyList = testedResolver.getGraphe();
		boolean flag = false;
		int searchedWdt = 6;
		int searchedHgt = 3;
		int node;
		for (node = 0; node < adjancencyList.length; node++) {
			if (testedResolver.getWdtOfNode(node) == searchedWdt && testedResolver.getHgtOfNode(node) == searchedHgt) {
				flag = true;
				break;
			}
		}
		// On a bien trouvé le noeud
		assertTrue(flag);
				
		List<Pair> expected = new ArrayList<>();
		expected.add(new Pair(5, 2));
		expected.add(new Pair(7, 3));
		assertTrue(checkNeighboursOfNode(node, expected));
	} 
	
	@Test
	public void testConversionPlateauToGraphHOL2() {
		// Ici le noeud est sensé avoir deux voisins (6:3, 8:2)
		
		List<Integer>[] adjancencyList = testedResolver.getGraphe();
		boolean flag = false;
		int searchedWdt = 7;
		int searchedHgt = 2;
		int node;
		for (node = 0; node < adjancencyList.length; node++) {
			if (testedResolver.getWdtOfNode(node) == searchedWdt && testedResolver.getHgtOfNode(node) == searchedHgt) {
				flag = true;
				break;
			}
		}
		// On a bien trouvé le noeud
		assertTrue(flag);
				
		List<Pair> expected = new ArrayList<>();
		expected.add(new Pair(6, 3));
		expected.add(new Pair(8, 2));
		assertTrue(checkNeighboursOfNode(node, expected));
	}
	
	@Test
	public void testConversionPlateauToGraphHOL3() {
		// Ici le noeud est sensé avoir trois voisins (7:2, 8:3, 8:1)
		
		List<Integer>[] adjancencyList = testedResolver.getGraphe();
		boolean flag = false;
		int searchedWdt = 8;
		int searchedHgt = 2;
		int node;
		for (node = 0; node < adjancencyList.length; node++) {
			if (testedResolver.getWdtOfNode(node) == searchedWdt && testedResolver.getHgtOfNode(node) == searchedHgt) {
				flag = true;
				break;
			}
		}
		// On a bien trouvé le noeud
		assertTrue(flag);
				
		List<Pair> expected = new ArrayList<>();
		expected.add(new Pair(7, 2));
		expected.add(new Pair(8, 3));
		expected.add(new Pair(8, 1));
		assertTrue(checkNeighboursOfNode(node, expected));
	}
	
	///////////////////////////////////////
	/////////////// Bordure ///////////////
	///////////////////////////////////////
	@Test
	public void testConversionPlateauToGraphBorder1() {
		// On test les noeuds en bordure
		// Ici le noeud est sensé avoir un voisin (w-2:1)
		
		List<Integer>[] adjancencyList = testedResolver.getGraphe();
		boolean flag = false;
		int searchedWdt = width-1;
		int searchedHgt = 1;
		int node;
		for (node = 0; node < adjancencyList.length; node++) {
			if (testedResolver.getWdtOfNode(node) == searchedWdt && testedResolver.getHgtOfNode(node) == searchedHgt) {
				flag = true;
				break;
			}
		}
		// On a bien trouvé le noeud
		assertTrue(flag);
				
		List<Pair> expected = new ArrayList<>();
		expected.add(new Pair(width-2, 1));
		assertTrue(checkNeighboursOfNode(node, expected));
	}
	
	@Test
	public void testConversionPlateauToGraphBorder2() {
		// On test les noeuds en bordure
		// Ici le noeud est sensé avoir un voisin (1:1)
		
		List<Integer>[] adjancencyList = testedResolver.getGraphe();
		boolean flag = false;
		int searchedWdt = 0;
		int searchedHgt = 1;
		int node;
		for (node = 0; node < adjancencyList.length; node++) {
			if (testedResolver.getWdtOfNode(node) == searchedWdt && testedResolver.getHgtOfNode(node) == searchedHgt) {
				flag = true;
				break;
			}
		}
		// On a bien trouvé le noeud
		assertTrue(flag);
				
		List<Pair> expected = new ArrayList<>();
		expected.add(new Pair(1, 1));
		assertTrue(checkNeighboursOfNode(node, expected));
	}
	
	@Test
	public void testConversionPlateauToGraphBorder3() {
		// Ce noeud la est sensé avoir deux voisins (0:h-2, 1:h-2)
		
		List<Integer>[] adjancencyList = testedResolver.getGraphe();
		boolean flag = false;
		int searchedWdt = 0;
		int searchedHgt = height-1;
		int node;
		for (node = 0; node < adjancencyList.length; node++) {
			if (testedResolver.getWdtOfNode(node) == searchedWdt && testedResolver.getHgtOfNode(node) == searchedHgt) {
				flag = true;
				break;
			}
		}
		// On a bien trouvé le noeud
		assertTrue(flag);
				
		List<Pair> expected = new ArrayList<>();
		expected.add(new Pair(0, height-2));
		expected.add(new Pair(1, height-2));
		assertTrue(checkNeighboursOfNode(node, expected));
	}
	
	///////////////////////////////////////
	///////// ShortestPathToPlayer ////////
	///////////////////////////////////////
	@Test
	public void testShortestPathToPlayer1() {
		// Test que le ShortestPath pour aller du garde g1 au joueur est correct
		int pred[] = testedResolver.getShortestPathToPlayer(g1);
		int idPlayer = testedResolver.getNodeIdOfPosition(playerX, playerY);
		int idGuard = testedResolver.getNodeIdOfPosition(g1.getWdt(), g1.getHgt());
		
		Pair expected[] = {new Pair(1, 1), new Pair(2, 1), new Pair(3, 1), new Pair(4, 1)};
		Pair currentExpected;
		int expectedCpt = 0;
		int currentId = idPlayer;
		int currentWdt, currentHgt;
		while (currentId != idGuard) {
			currentExpected = expected[expectedCpt];
			currentWdt = testedResolver.getWdtOfNode(currentId);
			currentHgt = testedResolver.getHgtOfNode(currentId);
			assertTrue(currentWdt == currentExpected.x && currentHgt == currentExpected.y);
			currentId = pred[currentId];
			expectedCpt++;
		}
		currentExpected = expected[expectedCpt];
		currentWdt = testedResolver.getWdtOfNode(currentId);
		currentHgt = testedResolver.getHgtOfNode(currentId);
		assertTrue(currentWdt == currentExpected.x && currentHgt == currentExpected.y);
	}
	
	@Test
	public void testShortestPathToPlayer2() {
		// Test que le ShortestPath pour aller du garde g2 au joueur est correct
		int pred[] = testedResolver.getShortestPathToPlayer(g2);
		int idPlayer = testedResolver.getNodeIdOfPosition(playerX, playerY);
		int idGuard = testedResolver.getNodeIdOfPosition(g2.getWdt(), g2.getHgt());
		
		Pair expected[] = {new Pair(1, 1), new Pair(2, 1), new Pair(2, 2), new Pair(2, 3), new Pair(1, 3)};
		Pair currentExpected;
		int expectedCpt = 0;
		int currentId = idPlayer;
		int currentWdt, currentHgt;
		while (currentId != idGuard) {
			currentExpected = expected[expectedCpt];
			currentWdt = testedResolver.getWdtOfNode(currentId);
			currentHgt = testedResolver.getHgtOfNode(currentId);
			assertTrue(currentWdt == currentExpected.x && currentHgt == currentExpected.y);
			currentId = pred[currentId];
			expectedCpt++;
		}
		currentExpected = expected[expectedCpt];
		currentWdt = testedResolver.getWdtOfNode(currentId);
		currentHgt = testedResolver.getHgtOfNode(currentId);
		assertTrue(currentWdt == currentExpected.x && currentHgt == currentExpected.y);
	}
	
	///////////////////////////////////////
	//////////// RecomputeGraph ///////////
	///////////////////////////////////////
	@Test
	public void testRecomputeGraphPositif() {
		testedResolver.recomputeGraph();
		assertTrue(true);
	}
	
	///////////////////////////////////////
	//////// NextMoveToReachPlayer ////////
	///////////////////////////////////////
	@Test
	public void testNextMoveToReachPlayer1() {
		// On vérifie que la suite de mouvement renvoyé par le Resolver pour que le garde
		// g1 atteigne le joueur est bien LEFT, LEFT, LEFT
		
		Move move = testedResolver.getNextMoveToReachPlayer(g1);
		assertTrue(move == Move.LEFT);
		g1.goLeft();
		
		move = testedResolver.getNextMoveToReachPlayer(g1);
		assertTrue(move == Move.LEFT);
		g1.goLeft();
		
		move = testedResolver.getNextMoveToReachPlayer(g1);
		assertTrue(move == Move.LEFT);
		g1.goLeft();
		
		move = testedResolver.getNextMoveToReachPlayer(g1);
		assertTrue(move == Move.NEUTRAL);
	}
	
	@Test
	public void testNextMoveToReachPlayer2() {
		// On vérifie que la suite de mouvement renvoyé par le Resolver pour que le garde
		// g1 atteigne le joueur est bien RIGHT, DOWN, DOWN, LEFT
		
		Move move = testedResolver.getNextMoveToReachPlayer(g2);
		assertTrue(move == Move.RIGHT);
		g2.goRight();
		
		move = testedResolver.getNextMoveToReachPlayer(g2);
		assertTrue(move == Move.DOWN);
		g2.goDown();
		
		move = testedResolver.getNextMoveToReachPlayer(g2);
		assertTrue(move == Move.DOWN);
		g2.goDown();
		
		move = testedResolver.getNextMoveToReachPlayer(g2);
		assertTrue(move == Move.LEFT);
		g2.goLeft();
		
		move = testedResolver.getNextMoveToReachPlayer(g2);
		assertTrue(move == Move.NEUTRAL);
	}
	
	@Test
	public void testNextMoveToReachPlayer3() {
		// On vérifie que si le joueur est innateignable par le garde, on retourne bien NEUTRAL
		g2.init(environnement, 8, 5);
		Move move = testedResolver.getNextMoveToReachPlayer(g2);
		assertTrue(move == Move.NEUTRAL);
	}
	
	///////////////////////////////////////
	////////// PlayerCanReachPos //////////
	///////////////////////////////////////
	@Test
	public void testPlayerCanReachPosPositif1() {
		// Vérifie que le joueur peut bien attraper objectif atteignable
		assertTrue(testedResolver.playerCanReachPos(3, 5));
	}
	
	@Test
	public void testPlayerCanReachPosPositif2() {
		// Vérifie que le joueur peut bien attraper objectif atteignable
		// Ici un garde est sur le trésor (il est cependant considéré comme atteignable)
		assertTrue(testedResolver.playerCanReachPos(1, 3));
	}
	
	@Test
	public void testPlayerCanReachPosNegatif1() {
		// Vérifie que le joueur ne peut pas attraper objectif inatteignable
		// Ici l'objectif est entouré par des PLT
		assertFalse(testedResolver.playerCanReachPos(width-1, height-1));
	}
	
	@Test
	public void testPlayerCanReachPosNegatif2() {
		// Vérifie que le joueur ne peut pas attraper objectif inatteignable
		// Ici l'objectif est dans une case MTL
		assertFalse(testedResolver.playerCanReachPos(1, 0));
	}
	
	
	/////////////////////////////////////////
	// PlayerCanReachAllTreasuresAndPortal //
	/////////////////////////////////////////
	@Test
	public void testPlayerCanReachAllTreasuresAndPortalPositif1() {
		// Tout est joignable
		int portalX = 3;
		int portalY = 5;
		
		List<Item> treasures = new ArrayList<>();
		
		Item t1 = new TreasureImpl();
		t1.init(environnement, 0, 1);
		treasures.add(t1);
		
		Item t2 = new TreasureImpl();
		t2.init(environnement, 8, 1);
		treasures.add(t2);
		
		Item t3 = new TreasureImpl();
		t3.init(environnement, 6, 3);
		treasures.add(t3);
		
		assertTrue(testedResolver.playerCanReachAllTreasuresAndPortal(treasures, portalX, portalY));
	}
	
	@Test
	public void testPlayerCanReachAllTreasuresAndPortalPositif2() {
		// Tout est joignable
		int portalX = 2;
		int portalY = 3;
		
		List<Item> treasures = new ArrayList<>();
		
		Item t1 = new TreasureImpl();
		t1.init(environnement, 1, 3);
		treasures.add(t1);
		
		Item t2 = new TreasureImpl();
		t2.init(environnement, 4, 1);
		treasures.add(t2);
		
		Item t3 = new TreasureImpl();
		t3.init(environnement, 0, 3);
		treasures.add(t3);
		
		assertTrue(testedResolver.playerCanReachAllTreasuresAndPortal(treasures, portalX, portalY));
	}
	
	@Test
	public void testPlayerCanReachAllTreasuresAndPortalNegatif1() {
		// Le joueur ne peut pas atteindre un trésor
		int portalX = 2;
		int portalY = 3;
		
		changeCell(6, 1, Cell.PLT);
		changeCell(8, 2, Cell.PLT);
		
		List<Item> treasures = new ArrayList<>();
		
		Item t1 = new TreasureImpl();
		t1.init(environnement, 7, 1);
		treasures.add(t1);
		
		Item t2 = new TreasureImpl();
		t2.init(environnement, 1, 5);
		treasures.add(t2);
		
		Item t3 = new TreasureImpl();
		t3.init(environnement, 0, 1);
		treasures.add(t3);
		
		assertFalse(testedResolver.playerCanReachAllTreasuresAndPortal(treasures, portalX, portalY));
	}
	
	@Test
	public void testPlayerCanReachAllTreasuresAndPortalNegatif2() {
		// Le joueur ne peut pas atteindre le portail
		int portalX = 0;
		int portalY = 5;
		
		List<Item> treasures = new ArrayList<>();
		
		Item t1 = new TreasureImpl();
		t1.init(environnement, 7, 1);
		treasures.add(t1);
		
		Item t2 = new TreasureImpl();
		t2.init(environnement, 1, 5);
		treasures.add(t2);
		
		Item t3 = new TreasureImpl();
		t3.init(environnement, 0, 1);
		treasures.add(t3);
		
		assertFalse(testedResolver.playerCanReachAllTreasuresAndPortal(treasures, portalX, portalY));
	}
	
	@Test
	public void testPlayerCanReachAllTreasuresAndPortalNegatif3() {
		// Le joueur ne peut rien atteindre
		int portalX = 1;
		int portalY = 3;
		
		List<Item> treasures = new ArrayList<>();
		changeCell(playerX+1, playerY, Cell.MTL);
		
		Item t1 = new TreasureImpl();
		t1.init(environnement, 3, 4);
		treasures.add(t1);
		
		Item t2 = new TreasureImpl();
		t2.init(environnement, 6, 4);
		treasures.add(t2);
		
		Item t3 = new TreasureImpl();
		t3.init(environnement, 0, 2);
		treasures.add(t3);
		
		assertFalse(testedResolver.playerCanReachAllTreasuresAndPortal(treasures, portalX, portalY));
	}
	
	@Test
	public void testPlayerCanReachAllTreasuresAndPortalNegatif4() {
		// Le joueur peut juste atteindre le portail
		int portalX = 0;
		int portalY = 1;
		
		List<Item> treasures = new ArrayList<>();
		changeCell(playerX+1, playerY, Cell.MTL);
		
		Item t1 = new TreasureImpl();
		t1.init(environnement, 3, 4);
		treasures.add(t1);
		
		Item t2 = new TreasureImpl();
		t2.init(environnement, 6, 4);
		treasures.add(t2);
		
		Item t3 = new TreasureImpl();
		t3.init(environnement, 0, 2);
		treasures.add(t3);
		
		assertFalse(testedResolver.playerCanReachAllTreasuresAndPortal(treasures, portalX, portalY));
	}
	
	class Pair {
		private int x, y;
		public Pair(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
