package impl;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JPanel;

import contracts.AttackContract;
import contracts.EditableScreenContract;
import contracts.EngineContract;
import contracts.EnvironnementContract;
import contracts.GuardContract;
import contracts.ItemContract;
import contracts.PathResolverContract;
import contracts.PlayerContract;
import data.AnsiColor;
import data.Cell;
import data.Command;
import data.Direction;
import data.Item;
import data.Status;
import services.Attack;
import services.Board;
import services.EditableScreen;
import services.Engine;
import services.Environnement;
import services.GameObject;
import services.Guard;
import services.PathResolver;
import services.Player;

public class EngineImpl implements Engine {

	private boolean useContract = false;
	private boolean testing = false;
	private int screenWdt, screenHgt, playerX, playerY;
	private Environnement envi;
	private EditableScreen editor;
	private Player player;
	private PathResolver resolver;
	private List<Guard> guards;
	private List<Item> treasures;
	private int nbTreasuresTotal;
	private int currentNbTreasures;
	private Status status;
	private Command nextCommand;

	// Il faut penser à protéger la map des accès concurrent (notamment entre le joueur et l'engine)
	// Pour se fait, il est conseille d'utiliser une ConcurrentHashMap
	private Map<GameObject, Integer> holes;
	private int score;
	private LodeRunnerFrame frame;
	private Board board;
	private int nbStep;
	private Map<Integer, Environnement> levels;
	private Map<Integer, EditableScreen> editors;
	private Map<Integer, PathResolver> resolvers;
	private Map<Integer, Boolean> playableLevels;
	private int currentLevel;
	private List<Attack> attacks;
	
	@Override
	public void step() {
		Map<Guard, Boolean> hasBeenReseted = new HashMap<>();
		player.step();

		List<Attack> copyAttacks = new ArrayList<>(attacks);
		for(Attack a : copyAttacks) {
			boolean hasBeenRemoved = false;
			for(Guard g : guards) {
				if(a.getDirection() == Direction.LEFT && a.getWdt() - 1 == g.getWdt() && a.getHgt() == g.getHgt()) {
					attacks.remove(a);
					hasBeenReseted.put(g, true);
					g.backInitialPosition();
					hasBeenRemoved = true;
					break;
				}else if(a.getDirection() == Direction.RIGHT && a.getWdt() + 1 == g.getWdt() && a.getHgt() == g.getHgt()) {
					attacks.remove(a);
					g.backInitialPosition();
					hasBeenReseted.put(g, true);
					hasBeenRemoved = true;
					break;
				}else if(a.getWdt() == -1 || a.getWdt() == envi.getWidth() || envi.cellNature(a.getWdt(), a.getHgt()) == Cell.PLT || envi.cellNature(a.getWdt(), a.getHgt()) == Cell.MTL){
					attacks.remove(a);
					hasBeenRemoved = true;
					break;
				}
			}
			if (!hasBeenRemoved)
				a.step();
		}
		
		Map<GameObject, Integer> copieHoles = holes;

		for (Entry<GameObject, Integer> e : copieHoles.entrySet()) {
			GameObject currentHole = e.getKey();
			// On incremente le i a chaque tour
			int oldVal = e.getValue();
			e.setValue(oldVal + 1);
			Integer currentI = e.getValue();

			if (currentI > 15) {
				envi.fill(currentHole.getWdt(), currentHole.getHgt());

				// Si le joueur est dans un trou qui se referme, il a perdu.
				if (player.getWdt() == currentHole.getWdt() && player.getHgt() == currentHole.getHgt()) {
					player.backInitialPosition();
					player.decreaseVie();
					if (player.getVie() == 0)
						status = Status.LOSS;
				}

				// Si un garde est dans un trou qui va se refermer, il revient à sa positions
				// initiale
				for (Guard g : guards) {
					if (g.getWdt() == currentHole.getWdt() && g.getHgt() == currentHole.getHgt()) {
						g.backInitialPosition();
						hasBeenReseted.put(g, true);
					}
				}
				holes.remove(e.getKey());
			}
		}
		
		nbStep++;
		if (nbStep % 3 == 0) {
			resolver.recomputeGraph();
			for (Guard g : guards) {
				// On exécute le step du garde uniquement si celui-ci n'est pas déjà revenu à sa position initiale
				if(!hasBeenReseted.containsKey(g))
					g.step();
			}
		}
		
		copyAttacks = new ArrayList<>(attacks);
		for(Attack a : copyAttacks) {
			for(Guard g : guards) {
				if(a.getWdt() == g.getWdt() && a.getHgt() == g.getHgt()) {
					attacks.remove(a);
					g.backInitialPosition();
					break;
				}
			}
		}

		// Si le joueur touche un tresor, on l'enleve, il le garde donc on l'enleve des
		// tresors present dans le jeu.
		for (int i = 0; i < treasures.size(); i++) {
			Item trs = treasures.get(i);
			if (player.getWdt() == trs.getWdt() && player.getHgt() == trs.getHgt()) {
				treasures.remove(i);
				score++;
			}
		}

		// Si le joueur touche un garde, il perd de la vie. Si sa vie tombe à 0, il a
		// perdu.
		for (Guard g : guards) {
			if (player.getWdt() == g.getWdt() && player.getHgt() == g.getHgt()) {
				player.decreaseVie();
				if (player.getVie() == 0)
					status = Status.LOSS;
			}
		}

		// Si on a atteint les 3 niveau, et qu'on a recupere tout les tresors, on a
		// gagne.
		if (currentLevel == 3 && score == nbTreasuresTotal 
				&& player.getWdt() == envi.getWdtPortail() && player.getHgt() == envi.getHgtPortail())
			this.status = Status.WIN;
		else if (score == currentNbTreasures
				&& (player.getWdt() == envi.getWdtPortail() && player.getHgt() == envi.getHgtPortail())) {// Verifie si le joueur peut passer au niveau suivant.
			if (useContract)
				new EngineContract(this).nextLevel();
			else
				nextLevel();
			}

		// Le joueur a recupere tout les tresors, on lui donne acces au portail.
		if (score == currentNbTreasures)
			board.displayPortal(true);
		
		// On a traite la nouvelle commmande, on la passe a neutre pour eviter de
		// l'effectuer plusieurs fois.
		nextCommand = Command.NEUTRAL;
	}

	@Override
	public void init(int wdtScreen, int hgtScreen, int playerX, int playerY) {
		this.playerX = playerX;
		this.playerY = playerY;
		this.screenWdt = wdtScreen;
		this.screenHgt = hgtScreen;
		this.status = Status.PLAYING;
		this.levels = new HashMap<Integer, Environnement>();
		this.editors = new HashMap<Integer, EditableScreen>();
		this.resolvers = new HashMap<Integer, PathResolver>();
		this.playableLevels = new HashMap<Integer, Boolean>();
		this.score = 0;
		this.nbStep = 0;
		this.attacks = new ArrayList<>();
		this.player = new PlayerImpl();
		
		// Creation des niveaux du jeu
		createEnvironnements(screenWdt, screenHgt);

		// On commence au premier niveau jouable
		this.currentLevel = getIndexOfNextPlayableLevel(0);
		if (currentLevel == -1) {
			System.out.println(AnsiColor.ERROR_RED + "[ERROR] : No Playable map on the game's ressources" + AnsiColor.RESET);
			this.status = Status.WIN;
			return;
		}
		else if (currentLevel != 1) {
			for(int i=1; i<currentLevel; i++)
				System.out.println(AnsiColor.WARNING_YELLOW + "[WARNING] : The map number " + i +" is not playable and has been skipped" + AnsiColor.RESET);
		}
			
		this.envi = levels.get(currentLevel);
		this.resolver = resolvers.get(currentLevel);
		
		// Initialisation du joueur
		player.init(envi, playerX, playerY, this);

		this.holes = new ConcurrentHashMap<>();

		this.treasures = envi.getTreasures();
		this.guards = envi.getGuards();
		
		// On calcule le nombre total de tresors de tout les niveaux.
		this.nbTreasuresTotal = 0;
		for(Entry<Integer, Environnement> e : levels.entrySet()) {
			if (getPlayableLevels().get(e.getKey()))
				this.nbTreasuresTotal += e.getValue().getTreasures().size();
		}
		
		this.currentNbTreasures = treasures.size();

		
		// Affichage de l'ecran de jeu
		frame = new LodeRunnerFrame();
		this.board = new BoardImpl();
		board.init(this, this.envi, frame);

		EventQueue.invokeLater(() -> {
			frame.init(board, new JPanel());
			frame.setVisible(true);
		});
	}

	@Override
	public void initWithContract(int wdtScreen, int hgtScreen, int playerX, int playerY) {
		this.useContract = true;
		this.playerX = playerX;
		this.playerY = playerY;
		this.screenWdt = wdtScreen;
		this.screenHgt = hgtScreen;
		this.score = 0;
		this.nbStep = 0;
		this.status = Status.PLAYING;
		this.levels = new HashMap<Integer, Environnement>();
		this.editors = new HashMap<Integer, EditableScreen>();
		this.resolvers = new HashMap<Integer, PathResolver>();
		this.playableLevels = new HashMap<Integer, Boolean>();
		this.attacks = new ArrayList<>();
		this.player = new PlayerContract(new PlayerImpl());
		
		// Creation des niveaux du jeu
		new EngineContract(this).createEnvironnementsWithContract(screenWdt, screenHgt);

		// On commence au niveau 1
		this.currentLevel = getIndexOfNextPlayableLevel(0);
		if (currentLevel == -1) {
			System.out.println(AnsiColor.ERROR_RED + "[ERROR] : No Playable map on the game's ressources" + AnsiColor.RESET);
			this.status = Status.WIN;
			return;
		}
		else if (currentLevel != 1) {
			for(int i=1; i<currentLevel; i++)
				System.out.println(AnsiColor.WARNING_YELLOW + "[WARNING] : The map number " + i +" is not playable and has been skipped" + AnsiColor.RESET);
		}
		
		this.envi = levels.get(currentLevel);
		this.resolver = resolvers.get(currentLevel);

		// Initialisation du joueur
		player.init(envi, playerX, playerY, new EngineContract(this));

		this.holes = new ConcurrentHashMap<>();

		this.treasures = envi.getTreasures();
		this.guards = envi.getGuards();

		// On calcule le nombre total de tresors de tout les niveaux.
		this.nbTreasuresTotal = 0;
		for(Entry<Integer, Environnement> e : levels.entrySet()) {
			if (getPlayableLevels().get(e.getKey()))
				this.nbTreasuresTotal += e.getValue().getTreasures().size();
		}
		
		this.currentNbTreasures = treasures.size();
		
		// Affichage de l'ecran de jeu
		frame = new LodeRunnerFrame();
		this.board = new BoardImpl();
		board.init(new EngineContract(this), this.envi, frame);

		EventQueue.invokeLater(() -> {
			frame.init(board, new JPanel());
			frame.setVisible(true);
		});
	}
	
	@Override
	public void initForTests(int wdtScreen, int hgtScreen, int playerX, int playerY) {
		useContract = true;
		testing = true;
		this.playerX = playerX;
		this.playerY = playerY;
		this.screenWdt = wdtScreen;
		this.screenHgt = hgtScreen;
		this.score = 0;
		this.nbStep = 0;
		this.status = Status.PLAYING;
		this.levels = new HashMap<Integer, Environnement>();
		this.editors = new HashMap<Integer, EditableScreen>();
		this.resolvers = new HashMap<Integer, PathResolver>();
		this.playableLevels = new HashMap<Integer, Boolean>();
		this.attacks = new ArrayList<>();
		this.player = new PlayerContract(new PlayerImpl());

		// Creation des niveaux du jeu (indique que l'on prend ceux des tests)
		EngineContract tmp = new EngineContract(this);
		tmp.TESTING = testing;
		tmp.createEnvironnementsWithContract(screenWdt, screenHgt);

		// On commence au niveau 1
		this.currentLevel = getIndexOfNextPlayableLevel(0);
		if (currentLevel == -1) {
			System.out.println(AnsiColor.ERROR_RED + "[ERROR] : No Playable map on the game's ressources" + AnsiColor.RESET);
			this.status = Status.WIN;
			return;
		}
		else if (currentLevel != 1) {
			for(int i=1; i<currentLevel; i++)
				System.out.println(AnsiColor.WARNING_YELLOW + "[WARNING] : The map number " + i +" is not playable and has been skipped" + AnsiColor.RESET);
		}
		
		this.envi = levels.get(currentLevel);
		this.resolver = resolvers.get(currentLevel);

		// Initialisation du joueur
		player.init(envi, playerX, playerY, this);

		this.holes = new ConcurrentHashMap<>();

		this.treasures = envi.getTreasures();
		this.guards = envi.getGuards();

		// On calcule le nombre total de tresors de tout les niveaux.
		this.nbTreasuresTotal = 0;
		for(Entry<Integer, Environnement> e : levels.entrySet()) {
			if (getPlayableLevels().get(e.getKey()))
				this.nbTreasuresTotal += e.getValue().getTreasures().size();
		}
		
		this.currentNbTreasures = treasures.size();
		
		// Affichage de l'ecran de jeu
		// TODO Utiliser un BoardContract quand on en aura un
		this.board = null;
	}

	@Override
	public void createEnvironnements(int wdt, int hgt) {
		try {
			BufferedReader br;

			for (int nbLevel = 1; nbLevel <= 3; nbLevel++) {

				br = new BufferedReader(new FileReader(new File("src/resources/niveaux/niveau" + nbLevel + ".txt")));

				this.editor = new EditableScreenImpl();
				PathResolver resolv = new PathResolverImpl();
				editor.init(wdt, hgt);

				List<Guard> guards = new ArrayList<>();
				List<Item> treasures = new ArrayList<>();
				Environnement envi = new EnvironnementImpl();

				int wdtPortail = 0;
				int hgtPortail = 0;

				int idGuard = 0;

				for (int i = hgt-1; i >= 1; i--) {
					String lineCells = br.readLine();
					String[] cells = lineCells.split(",");

					for (int j = 0; j < cells.length; j++) {
						if (cells[j].equals("GRD")) {
							Guard g = new GuardImpl();
							g.init(envi, j, i, idGuard, resolv);
							idGuard++;

							guards.add(g);

							// Les gardes sont initialises sur des cellules EMP
							editor.setNature(j, i, Cell.EMP);
						} else if (cells[j].equals("TRS")) {
							Item t = new TreasureImpl();
							t.init(envi, j, i);
							treasures.add(t);

							// Les tresors sont poses sur des cellules EMP
							editor.setNature(j, i, Cell.EMP);
						} else if (cells[j].equals("PRT")) {
							wdtPortail = j;
							hgtPortail = i;

							editor.setNature(j, i, Cell.PRT);
						} else {
							editor.setNature(j, i, Cell.valueOf(cells[j]));
						}
					}
				}

				// On recupere les cellules modifiees
				Cell[][] cells = editor.getPlayableEnvi();

				// Initialisation de l'environnement
				envi.init(wdt, hgt, player, guards, treasures, cells, wdtPortail, hgtPortail);
				levels.put(nbLevel, envi);
				
				editors.put(nbLevel, editor);
				
				player.init(envi, playerX, playerY, this);

				// Création du resolver
				resolv.init(envi, player);
				resolvers.put(nbLevel, resolv);
				
				// Le terrain est jouable ou non
				playableLevels.put(nbLevel, 
						resolv.playerCanReachAllTreasuresAndPortal(envi.getTreasures(), envi.getWdtPortail(), envi.getHgtPortail())
						&& editor.playable());

				br.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void createEnvironnementsWithContract(int wdt, int hgt) {
		try {
			BufferedReader br;

			for (int nbLevel = 1; nbLevel <= 3; nbLevel++) {
				if (testing)
					br = new BufferedReader(new FileReader(new File("src/resources/niveaux/test_niveau" + nbLevel + ".txt")));
				else
					br = new BufferedReader(new FileReader(new File("src/resources/niveaux/niveau" + nbLevel + ".txt")));
				
				this.editor = new EditableScreenContract(new EditableScreenImpl());
				editor.init(wdt, hgt);
				
				PathResolver resolv = new PathResolverContract(new PathResolverImpl());

				List<Guard> guards = new ArrayList<>();
				List<Item> treasures = new ArrayList<>();
				Environnement envi = new EnvironnementContract(new EnvironnementImpl());

				int wdtPortail = 0;
				int hgtPortail = 0;

				int idGuard = 0;

				for (int i = hgt-1; i >= 1; i--) {
					String lineCells = br.readLine();
					String[] cells = lineCells.split(",");

					for (int j = 0; j < cells.length; j++) {
						if (cells[j].equals("GRD")) {
							Guard g = new GuardContract(new GuardImpl());
							g.init(envi, j, i, idGuard, resolv);
							idGuard++;

							guards.add(g);

							// Les gardes sont initialisé sur des cellules EMP
							editor.setNature(j, i, Cell.EMP);
						} else if (cells[j].equals("TRS")) {
							Item t =  new ItemContract(new TreasureImpl());
							t.init(envi, j, i);
							treasures.add(t);

							// Les tresors sont posés sur des cellules EMP
							editor.setNature(j, i, Cell.EMP);
						} else if (cells[j].equals("PRT")) {
							wdtPortail = j;
							hgtPortail = i;

							editor.setNature(j, i, Cell.PRT);
						} else {
							editor.setNature(j, i, Cell.valueOf(cells[j]));
						}
					}
				}
				// On recupere les cellules modifiees
				Cell[][] cells = editor.getPlayableEnvi();

				// Initialisation de l'environnement
				envi.init(wdt, hgt, player, guards, treasures, cells, wdtPortail, hgtPortail);
				levels.put(nbLevel, envi);
				
				editors.put(nbLevel, editor);
				
				player.init(envi, playerX, playerY, this);
				
				// Création du resolver
				resolv.init(envi, player);
				resolvers.put(nbLevel, resolv);
				
				// Le terrain est jouable ou non
				playableLevels.put(nbLevel,
						resolv.playerCanReachAllTreasuresAndPortal(envi.getTreasures(), envi.getWdtPortail(), envi.getHgtPortail())
						&& editor.playable());

				br.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void nextLevel() {
		int previousCurrentLevel = currentLevel;
		currentLevel = getIndexOfNextPlayableLevel(currentLevel);
		// Aucun prochain terrain, le joueur a gagné
		if (currentLevel == -1) {
			this.status = Status.WIN;
			System.out.println(AnsiColor.WARNING_YELLOW + "[WARNING] : You have won because there were not any playable map left" + AnsiColor.RESET);
			return;
		}
		// On a sauté un niveau
		else if (previousCurrentLevel != currentLevel -1) {
			for(int i=previousCurrentLevel+1; i<currentLevel; i++)
				System.out.println(AnsiColor.WARNING_YELLOW + "[WARNING] : The map number " + i +" is not playable and has been skipped" + AnsiColor.RESET);
		}
		this.envi = levels.get(currentLevel);
		this.resolver = resolvers.get(currentLevel);

		// Recuperation des coordonnee du portail pour l'apparition du joueur dans le nouveau niviveau.
		int xPortail = envi.getWdtPortail();
		int yPortail = envi.getHgtPortail();

		// Modification des variables utilisees.
		this.holes = new ConcurrentHashMap<>();
		
		// Les attaques sont réinitialisé pour le nouveau niveau.
		this.attacks = new ArrayList<>();
		
		// Ajout du resolver
		resolver.init(this.envi, player);

		// Lorsque l'on fait les tests junits, on utilise pas le board
		if (board != null)
			board.nextLevel(this.envi);

		// Le joueur apparait dans la nouvelle map, au niveau du portail de celle-ci.
		if (useContract)
			player.init(envi, xPortail, yPortail, new EngineContract(this));
		else
			player.init(envi, xPortail, yPortail, this);

		// Recuperer la nouvelle liste des tresors
		treasures = envi.getTreasures();
		
		// Le nombre de tresors a recuperer apres le changmenet de niveau est egale au :
		// NbTreasures(niveauPrecedent) + NbTreasures(niveauSuivant)
		currentNbTreasures += treasures.size();

		// Recuperer la nouvelle liste des gardes
		guards = envi.getGuards();		
	}

	@Override
	public void addAttack(int x, int y, Direction direction) {
		if(player.getNbAttacks() > 0) {
			Attack a;
			if (useContract)
				a = new AttackContract(new AttackImpl());
			else
				a = new AttackImpl();
			a.init(envi, x, y, direction);
			attacks.add(a);
			player.decreaseNbAttacks();
		}
	}
	
	@Override
	public void addHole(int x, int y) {
		if (getCurrentEnvironnement().cellNature(x, y) == Cell.PLT) {
			GameObject hole = new HoleImpl();
			hole.init(envi, x, y);
			holes.put(hole, 0);
			getCurrentEnvironnement().dig(x, y);
		}
	}

	@Override
	public void setNextCommand(Command c) {
		this.nextCommand = c;
	}

	@Override
	public Environnement getCurrentEnvironnement() {
		return envi;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public PathResolver getCurrentPathResolver() {
		return resolver;
	}

	@Override
	public List<Guard> getGuards() {
		return guards;
	}

	@Override
	public List<Item> getTreasures() {
		return treasures;
	}

	@Override
	public Command getNextCommand() {
		return nextCommand;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public Map<GameObject, Integer> getHoles() {
		return holes;
	}

	@Override
	public Board getBoard() {
		return board;
	}

	@Override
	public LodeRunnerFrame getFrame() {
		return frame;
	}

	@Override
	public int getScore() {
		return score;
	}

	@Override
	public int getNbStep() {
		return nbStep;
	}

	@Override
	public Map<Integer, Environnement> getLevels() {
		return this.levels;
	}

	@Override
	public int getCurrentLevel() {
		return this.currentLevel;
	}

	@Override
	public int getNbTreasuresTotal() {
		return this.nbTreasuresTotal;
	}
	
	@Override
	public int getCurrentNbTreasures() {
		return this.currentNbTreasures;
	}

	@Override
	public int getScreenWdt() {
		return this.screenWdt;
	}

	@Override
	public int getScreenHgt() {
		return this.screenHgt;
	}

	@Override
	public List<Attack> getAttacks() {
		return this.attacks;
	}

	@Override
	public Map<Integer, Boolean> getPlayableLevels() {
		return playableLevels;
	}
	
	@Override
	public Map<Integer, PathResolver> getPathResolvers() {
		return resolvers;
	}

	private int getIndexOfNextPlayableLevel(int from) {
		boolean hasPlayable = false;
		int i;
		for(i=from+1; i<=3; i++) {
			if (getPlayableLevels().get(i)) {
				hasPlayable = true;
				break;
			}
		}
		if (hasPlayable)
			return i;
		else
			return -1;
	}

	@Override
	public boolean hasAttack(int wdt, int hgt, Direction direction) {
		for(Attack a : getAttacks()) {
			if (a.getWdt() == wdt && a.getHgt() == hgt && a.getDirection() == direction)
				return true;
		}
		return false;
	}

	@Override
	public Map<Integer, EditableScreen> getEditors() {
		return editors;
	}
}
