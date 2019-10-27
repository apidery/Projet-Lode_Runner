package contracts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import contracts.errors.InvariantError;
import contracts.errors.PostconditionError;
import contracts.errors.PreconditionError;
import data.Cell;
import data.Command;
import data.Direction;
import data.Item;
import data.Status;
import decorators.EngineDecorator;
import impl.AttackImpl;
import impl.GuardImpl;
import impl.TreasureImpl;
import services.Engine;
import services.Environnement;
import services.GameObject;
import services.Guard;
import services.PathResolver;
import services.Player;
import services.Attack;
import services.Character;
import services.EditableScreen;

public class EngineContract extends EngineDecorator {

	public boolean TESTING = false;
	
	public EngineContract(Engine delegate) {
		super(delegate);
	}

	public void checkInvariants() {
		if (getCurrentLevel() != -1) {
			for (Guard g : getGuards()) {
				Character c = getCurrentEnvironnement().cellContent(g.getWdt(), g.getHgt()).getCharacter();
	
				if (c == null || !(c instanceof Guard) || g != c)
					throw new InvariantError("ENGINE invariant n°2 : Un Garde n'est pas la ou il dit être)");
			}
	
			for (Item t : getTreasures()) {
				Item t2 = getCurrentEnvironnement().cellContent(t.getWdt(), t.getHgt()).getItem();
	
				if (t2 == null || !(t2 instanceof Item) || t != t2)
					throw new InvariantError("ENGINE invariant n°3 : Un Trésor n'est pas la ou il dit être)");
			}
	
			for (GameObject h : getHoles().keySet()) {
				Cell c = getCurrentEnvironnement().cellNature(h.getWdt(), h.getHgt());
				if (c != Cell.HOL)
					throw new InvariantError("ENGINE invariant n°4 : Un Trou n'est pas la ou il dit être)");
			}
		}
	}

	@Override
	public Environnement getCurrentEnvironnement() {
		return super.getCurrentEnvironnement();
	}

	@Override
	public Player getPlayer() {
		return super.getPlayer();
	}

	@Override
	public List<Guard> getGuards() {
		return super.getGuards();
	}

	@Override
	public List<Item> getTreasures() {
		return super.getTreasures();
	}

	@Override
	public Command getNextCommand() {
		return super.getNextCommand();
	}

	@Override
	public Status getStatus() {
		return super.getStatus();
	}

	@Override
	public int getNbStep() {
		return super.getNbStep();
	}
	
	@Override
	public Map<GameObject, Integer> getHoles() {
		return super.getHoles();
	}

	@Override
	public void addHole(int x, int y) {
		checkInvariants();

		// Capture des observers avant addHole
		int scoreAt_pre = getScore();
		int currentNbTreasuresAt_pre = getCurrentNbTreasures();
		int curretLevelAt_pre = getCurrentLevel();
		int nbStepAt_pre = getNbStep();
		Environnement currentEnviAt_pre = getCurrentEnvironnement();
		PathResolver currentResolverAt_pre = getCurrentPathResolver();
		Command cmdAt_pre = getNextCommand();
		Status statusAt_pre = getStatus();

		// Copie des holes
		Map<GameObject, Integer> holesAt_pre = new HashMap<>();
		for(Entry<GameObject, Integer> e : getHoles().entrySet()) {
			holesAt_pre.put(e.getKey(), e.getValue());
		}
		
		// Copie des gardes
		List<Guard> guardsAt_pre = new ArrayList<>();
		for(Guard g : getGuards()) {
			guardsAt_pre.add(g);
		}
		
		//Copie des trésors
		List<Item> treasuresAt_pre = new ArrayList<>();
		for(Item t : getTreasures()) {
			treasuresAt_pre.add(t);
		}
		
		// Copie des attaques
		List<Attack> attackAt_pre = new ArrayList<>();
		for(Attack a : getAttacks()) {
			attackAt_pre.add(a);
		}
		
		boolean containsHole = false;
		for (GameObject h : getHoles().keySet()) {
			if (h.getWdt() == x && h.getHgt() == y) {
				containsHole = true;
				break;
			}
		}

		if (!(0 <= y && y < getCurrentEnvironnement().getHeight() && 0 <= x && x < getCurrentEnvironnement().getWidth()))
			throw new PreconditionError("ENGINE addHole : 0 <= y < height(S) and 0 <= x < width(S)");

		if (containsHole)
			throw new PreconditionError("ENGINE addHole : il y a déjà un trou à ces coordonnées");

		super.addHole(x, y);

		for (GameObject h : getHoles().keySet()) {
			if (h.getWdt() == x && h.getHgt() == y)
				if (getHoles().get(h) != 0)
					throw new PostconditionError(
							"ENGINE addHole 1 : Le trou nouvellement fait n'as pas un paramêtre t égale à 0");
		}

		for (GameObject h : holesAt_pre.keySet()) {
			Integer t_h2 = getHoles().get(h);
			if (h.getWdt() == x && h.getHgt() == y)
				if (t_h2 != holesAt_pre.get(h))
					throw new PostconditionError("ENGINE addHole 2 : les durées des autres trous ont été modifiées");
		}

		if (getScore() != scoreAt_pre)
			throw new PreconditionError("ENGINE addHole 3: Le score a changé");

		if (getCurrentNbTreasures() != currentNbTreasuresAt_pre)
			throw new PreconditionError("ENGINE addHole 4 : Le nombre courant de trésors a changé");

		if (getCurrentLevel() != curretLevelAt_pre)
			throw new PreconditionError("ENGINE addHole 5 : Le niveau a changé");

		if (getCurrentEnvironnement() != currentEnviAt_pre)
			throw new PreconditionError("ENGINE addHole 6 : L'evénement courant à changé");
		
		if (getCurrentPathResolver() != currentResolverAt_pre)
			throw new PreconditionError("ENGINE addHole 7 : L'evénement courant à changé");

		if (getAttacks().size() != attackAt_pre.size())
			throw new PreconditionError("ENGINE addHole 8 : Les attaques ont changés");
		else {
			for (Attack a : attackAt_pre)
				if (!getAttacks().contains(a))
					throw new PreconditionError("ENGINE addHole 8 : Les attaques ont changés");
		}
		
		if (getNextCommand() != cmdAt_pre)
			throw new PreconditionError("ENGINE addHole 9 : La nextCommand a changé");
		
		if (getStatus() != statusAt_pre)
			throw new PreconditionError("ENGINE addHole 10 : Le status a changé");
		
		if (getNbStep() != nbStepAt_pre)
			throw new PreconditionError("ENGINE addHole 11 : Le nbStep a changé");

		List<Guard> guards = getGuards();
		if (guards.size() != guardsAt_pre.size())
			throw new PostconditionError("ENGINE addHole 12 : Les gardes ont changé.");
		else { 
			for (Guard g : guardsAt_pre) {
				if (!guards.contains(g))
					throw new PostconditionError("ENGINE addHole 12 : Les gardes ont changé.");
			}
		}

		List<Item> treasures = getTreasures();
		if (treasures.size() != treasuresAt_pre.size())
			throw new PostconditionError("ENGINE addHole 13 : Les tresors ont changés.");
		else {
			for (Item t : treasuresAt_pre) {
				if (!treasures.contains(t))
					throw new PostconditionError("ENGINE addHole 13 : Les tresors ont changés.");
			}
		}
		
		checkInvariants();
	}

	@Override
	public void step() {
		checkInvariants();
		
		if (getStatus() != Status.PLAYING)
			throw new PreconditionError("ENGINE step 1 : Le statut du jeu n'est pas 'PLAYING' ");

		// Capture des observer avant step.
		int nbStepAt_pre = getNbStep();
		int currentLevelAt_pre = getCurrentLevel();
		int currentNbTreasuresAt_pre = getCurrentNbTreasures();
		int scoreAt_pre = getScore();
		int vieAt_pre = getPlayer().getVie();
		
		// On copie les attaques
		Map<Attack, Boolean> hasCharacterAt_pre = new HashMap<>();
		Map<Attack, Cell> nextCellAt_pre = new HashMap<>();
		List<Attack> attackAt_pre = new ArrayList<>();
		for(Attack a : getAttacks()) {
			Attack at = new AttackImpl();
			at.init(a.getScreen(), a.getWdt(), a.getHgt(), at.getDirection());
			
			boolean hasCharacter = true;
			if (at.getDirection() == Direction.LEFT) {
				if (at.getWdt()-1 >=0) {
					hasCharacter = (getCurrentEnvironnement().cellContent(at.getWdt()-1, at.getHgt()).getCharacter() != null);
					nextCellAt_pre.put(a, getCurrentEnvironnement().cellNature(at.getWdt()-1, at.getHgt()));
				} else {
					hasCharacter = false;
				}
			}
			else if (at.getDirection() == Direction.RIGHT) {
				if (at.getWdt()+1 < getCurrentEnvironnement().getWidth()) {
					hasCharacter = (getCurrentEnvironnement().cellContent(at.getWdt()+1, at.getHgt()).getCharacter() != null);
					nextCellAt_pre.put(a, getCurrentEnvironnement().cellNature(at.getWdt()+1, at.getHgt()));
				} else {
					hasCharacter = false;
				}
			}
			hasCharacterAt_pre.put(at, hasCharacter);
			attackAt_pre.add(at);
		}
		
		// On copie les trous
		Map<GameObject, Integer> holesAt_pre = new HashMap<>();
		for (GameObject h : getHoles().keySet())
			holesAt_pre.put(h, getHoles().get(h));

		// On copie les trésors
		List<Item> itemsAt_pre = new ArrayList<>();
		for (Item i : getTreasures()) {
			Item i2 = new TreasureImpl();
			i2.init(i.getScreen(), i.getWdt(), i.getHgt());
			itemsAt_pre.add(i2);
		}

		// On copie les Wdt et Hgt des guards
		Map<Guard, Integer> wdtGuardAt_pre = new HashMap<>();
		Map<Guard, Integer> hgtGuardAt_pre = new HashMap<>();
		List<Guard> guardsAt_pre = new ArrayList<>();
		for (Guard g : getGuards()) {
			wdtGuardAt_pre.put(g, g.getWdt());
			hgtGuardAt_pre.put(g, g.getHgt());
			Guard g2 = new GuardImpl();
			g2.init(g.getEnvi(), g.getWdt(), g.getHgt(), g.getId(), g.getResolver());
			guardsAt_pre.add(g2);
		}

		super.step();

		for (GameObject h : holesAt_pre.keySet()) {
			int tAt_pre = holesAt_pre.get(h);

			if (tAt_pre == 15) {
				if (getCurrentEnvironnement().cellNature(h.getWdt(), h.getHgt()) != Cell.PLT)
					throw new PostconditionError("ENGINE step 1 : Le trou ne s'est pas rebouché");
				if (getPlayer().getWdt() == h.getWdt() && getPlayer().getHgt() == h.getHgt()) {
					if(!(getPlayer().getWdt() != getPlayer().getInitialWdt() && getPlayer().getHgt() != getPlayer().getInitialHgt()))
						throw new PostconditionError("ENGINE step 1 : Le trou s'est rebouché sur le joueur, et il n'est pas retourné à sa position initiale");
					if(!(getPlayer().getVie() != vieAt_pre - 1))
						throw new PostconditionError("ENGINE step 1 : Le trou s'est rebouché sur le joueur, et il n'a pas perdu de vie");
				}
				for (Guard g : wdtGuardAt_pre.keySet()) {
					int wdtAt_pre = wdtGuardAt_pre.get(g);
					int hgtAt_pre = hgtGuardAt_pre.get(g);
					if (wdtAt_pre == h.getWdt() && hgtAt_pre == h.getHgt()) {
						if (g.getWdt() != g.getInitialWdt() || g.getHgt() != g.getInitialHgt())
							throw new PostconditionError(
									"ENGINE step 1 : Un garde est sensé etre retourné à sa position initiale");
					}
				}
			} else {
				int t = getHoles().get(h);
				if (t != tAt_pre + 1)
					throw new PostconditionError(
							"ENGINE step 1 : Le paramètre t du trou n'a pas été incrémenté comme il se doit");
			}
		}

		for (Item trsCurrent : itemsAt_pre) {
			if (trsCurrent.getWdt() == getPlayer().getWdt() && trsCurrent.getHgt() == getPlayer().getHgt()) {
				if (getScore() != scoreAt_pre + 1)
					throw new PostconditionError("ENGINE step 2 : Le joueur aurait dû gagner un point");
				for (Item t2 : getTreasures()) {
					if (t2 != trsCurrent) {
						if (trsCurrent.getWdt() == t2.getWdt() && trsCurrent.getHgt() == t2.getHgt()) {
							throw new PostconditionError(
									"ENGINE step 2 : Le trésor aurait dû disparaitre après le passage du joueur");
						}
					}
				}
			}
		}

		if (getNextCommand() != Command.NEUTRAL)
			throw new PostconditionError("ENGINE step 3 : La commande du joueur aurait dû être réinitialisée");
		
		if (getNbStep() != nbStepAt_pre + 1)
			throw new PostconditionError("ENGINE step 4 : Le nombre de step aurait dû augmenter de 1");

		Player player = getPlayer();
		for (Guard g : getGuards()) {
			if (player.getWdt() == g.getWdt() && player.getHgt() == g.getHgt())
				if (player.getVie() != vieAt_pre - 1)
					throw new PostconditionError(
							"ENGINE step 5 : Un garde a touché le joueur et sa vie n'a pas diminuée");
		}
		
		if (currentLevelAt_pre < 3
			&& getScore() == currentNbTreasuresAt_pre
			&& getPlayer().getWdt() == getCurrentEnvironnement().getWdtPortail()
			&& getPlayer().getHgt() == getCurrentEnvironnement().getHgtPortail())
			if (getCurrentLevel() == currentLevelAt_pre)
				throw new PostconditionError("ENGINE step 6 : Le current level aurait dû changer");
		
		
		if (player.getVie() == 0)
			if (getStatus() != Status.LOSS)
				throw new PostconditionError(
						"ENGINE step 7 : Le joueur n'a plus de vie et le statut du jeu n'est pas en 'LOSS'.");

		if (getCurrentLevel() == 3
			&& getScore() == getNbTreasuresTotal()
			&& getPlayer().getWdt() == getCurrentEnvironnement().getWdtPortail()
			&& getPlayer().getHgt() == getCurrentEnvironnement().getHgtPortail()) {
			if (getStatus() != Status.WIN)
				throw new PostconditionError("ENGINE step 8 : Le joueur a finis tout les niveaux et a récupéré tout les trésors, mais le statut du jeu n'est pas en 'WIN'.");
		}

		for(Attack a : attackAt_pre) {
			if(a.getDirection() == Direction.LEFT) {
				int x_a = a.getWdt() - 1;
				int y_a = a.getHgt();
				
				if(	a.getWdt() - 1 < 0
					|| nextCellAt_pre.get(a) == Cell.PLT
					|| nextCellAt_pre.get(a) == Cell.MTL) {
					
					for(Attack a2 : getAttacks()) {
						if(a2.getWdt() == x_a && a2.getHgt() == y_a)
							throw new PostconditionError("ENGINE step 9: l'attaque ne devrait pour être dans la liste");
					}
					
				}
				else if(nextCellAt_pre.get(a) == Cell.EMP) {
					boolean exists = false;
					for(Attack a2 : getAttacks()) {
						if(a2.getWdt() == x_a && a2.getHgt() == y_a)
							exists = true;
					}
					
					if(!exists)
						throw new PostconditionError("ENGINE 9 : l'attaque n'a pas bougé.");
				}
			}else if(a.getDirection() == Direction.RIGHT) {
				int x_a = a.getWdt() + 1;
				int y_a = a.getHgt();
				
				if(	a.getWdt() + 1 >= getCurrentEnvironnement().getWidth()
					|| nextCellAt_pre.get(a) == Cell.PLT
					|| nextCellAt_pre.get(a) == Cell.MTL) {
					
					for(Attack a2 : getAttacks()) {
						if(a2.getWdt() == x_a && a2.getHgt() == y_a)
							throw new PostconditionError("ENGINE step 9: l'attaque ne devrait pour être dans la liste");
					}
				}
				else if(nextCellAt_pre.get(a) == Cell.EMP) {
					boolean exists = false;
					for(Attack a2 : getAttacks()) {
						if(a2.getWdt() == x_a && a2.getHgt() == y_a)
							exists = true;
					}
					
					if(!exists)
						throw new PostconditionError("ENGINE 9 : l'attaque n'a pas bougé.");
				}
			}
		}
		
		for(Attack a : attackAt_pre) {
			if (a.getDirection() == Direction.LEFT) {
				for (Guard g : wdtGuardAt_pre.keySet()) {
					int wdtAt_pre = wdtGuardAt_pre.get(g);
					int hgtAt_pre = hgtGuardAt_pre.get(g);
					if (wdtAt_pre == a.getWdt()-1 && hgtAt_pre == a.getHgt()) {
						if (g.getWdt() != g.getInitialWdt() || g.getHgt() != g.getInitialHgt())
							throw new PostconditionError(
									"ENGINE step 10 : Un garde est sensé etre retourné à sa position initiale après avoir été touché par une attaque.");
						for(Attack a2 : getAttacks()) {
							if (a2.getDirection() == a.getDirection()
							&& a2.getHgt() == a.getHgt()
							&& a2.getWdt() == a.getHgt()-1)
								throw new PostconditionError(
										"ENGINE step 10 : L'attaque aurait dû etre retirée");
						}
					}
				}
			} else if (a.getDirection() == Direction.RIGHT) {
				for (Guard g : wdtGuardAt_pre.keySet()) {
					int wdtAt_pre = wdtGuardAt_pre.get(g);
					int hgtAt_pre = hgtGuardAt_pre.get(g);
					if (wdtAt_pre == a.getWdt()+1 && hgtAt_pre == a.getHgt()) {
						if (g.getWdt() != g.getInitialWdt() || g.getHgt() != g.getInitialHgt())
							throw new PostconditionError(
									"ENGINE step 10 : Un garde est sensé etre retourné à sa position initiale après avoir été touché par une attaque.");
						for(Attack a2 : getAttacks()) {
							if (a2.getDirection() == a.getDirection()
							&& a2.getHgt() == a.getHgt()
							&& a2.getWdt() == a.getHgt()+1)
								throw new PostconditionError(
										"ENGINE step 10 : L'attaque aurait dû etre retirée");
						}
					}
				}
			}
		}
		
		for(Attack a : attackAt_pre) {
			int ajout = 0;;
			if (a.getDirection() == Direction.LEFT)
				ajout = -1;
			else if (a.getDirection() == Direction.RIGHT)
				ajout = 1;
			for (Guard g : guardsAt_pre) {
				g.step();
				if (g.getWdt() == a.getWdt() && g.getHgt() == a.getHgt()) {
					for(Guard g2 : getGuards()) {
						if (g2.getInitialWdt() == g.getInitialWdt() && g2.getInitialHgt() == g.getInitialHgt()) {
							if (g2.getWdt() != g2.getInitialWdt() || g2.getHgt() != g2.getInitialHgt())
								throw new PostconditionError(
										"ENGINE step 11 : Un garde est sensé etre retourné à sa position initiale après avoir été touché par une attaque.");
						}
					}
					for(Attack a2 : getAttacks()) {
						if (a2.getDirection() == a.getDirection()
						&& a2.getHgt() == a.getHgt()
						&& a2.getWdt() == a.getHgt() + ajout)
							throw new PostconditionError(
									"ENGINE step 11 : L'attaque aurait dû etre retirée");
					}
				}
			}
		}		
		checkInvariants();
	}
	
	@Override
	public void init(int wdtScreen, int hgtScreen, int playerX, int playerY) {
		if (!(0 < playerY && playerY < hgtScreen && 0 < playerX && playerX < wdtScreen))
			throw new PreconditionError("ENGINE init 1 : 0 <= y < height(S) and 0 <= x < width(S)");

		super.init(wdtScreen, hgtScreen, playerX, playerY);

		int expectedLevel = -1;
		for(int i=1; i<=3; i++) {
			if (getPlayableLevels().get(i)) {
				expectedLevel = i;
				break;
			}
		}
		
		if (getCurrentLevel() != expectedLevel)
			throw new PostconditionError("ENGINE init 1 : le niveau à l'initialisation n'est pas le plus petit niveau jouable.");
		
		if (getCurrentLevel() == -1)
			if (getStatus() != Status.WIN)
				throw new PostconditionError("ENGINE init 1 : le statut du jeu n'est pas 'WIN' (aucun terrain jouable).");
		
		if (getCurrentLevel() != -1)
			if (getStatus() != Status.PLAYING)
				throw new PostconditionError("ENGINE init 2 : le statut du jeu n'est pas 'PLAYING'.");
		
		if (getCurrentLevel() != -1)
			if (getCurrentEnvironnement() != getLevels().get(getCurrentLevel()))
				throw new PostconditionError("ENGINE init 3 : Le niveau actuel n'est pas le premier.");
		
		if (getCurrentLevel() != -1)
			if (getBoard() == null)
				throw new PostconditionError("ENGINE init 4 : le Board n'a pas été affecté.");
		
		if (getCurrentLevel() != -1)
			if (getPlayer() == null)
				throw new PostconditionError("ENGINE init 5 : le Player n'a pas été affecté.");
		
		if (getCurrentLevel() != -1)
			if (getCurrentPathResolver() == null)
				throw new PostconditionError("ENGINE init 6 : le PathResolver n'a pas été affecté.");

		if (getCurrentLevel() != -1)
			for (Guard g : getGuards()) {
				if (getCurrentEnvironnement().cellNature(g.getWdt(), g.getHgt()) != Cell.EMP)
					throw new PostconditionError("ENGINE init 7 : Un garde n'est pas sur une case de type EMP");
			}

		if (getCurrentLevel() != -1)
			for (GameObject t : getTreasures()) {
				if (t.getHgt() - 1 >= 0) {
					Cell c = getCurrentEnvironnement().cellNature(t.getWdt(), t.getHgt());
					Cell c2 = getCurrentEnvironnement().cellNature(t.getWdt(), t.getHgt() - 1);
					if (c != Cell.EMP && (c2 == Cell.PLT || c2 == Cell.MTL))
						throw new PostconditionError(
								"ENGINE init 8 : Un trésor n'est pas sur une case de type EMP ou bien sa case en dessous n'est pas de type PLT ou MTL");
				}
			}

		// Vérifie que deux gardes n'ont pas la même position
		if (getCurrentLevel() != -1)
			for (Guard g1 : getGuards()) {
				for (Guard g2 : getGuards()) {
					if (g1 != g2)
						if (g1.getWdt() == g2.getWdt() && g1.getHgt() == g2.getHgt())
							throw new PostconditionError("ENGINE init 9 : Deux gardes ont la même position");
				}
			}

		// Vérifie que deux trésors n'ont pas la même position
		if (getCurrentLevel() != -1)
			for (GameObject t1 : getTreasures()) {
				for (GameObject t2 : getTreasures()) {
					if (t1 != t2)
						if (t1.getWdt() == t2.getWdt() && t1.getHgt() == t2.getHgt())
							throw new PostconditionError("ENGINE init 10 : Deux trésors ont la même position");
				}
			}
		
		if (getCurrentLevel() != -1)
			for (GameObject t : getTreasures())
				if (t.getWdt() == playerX && t.getHgt() == playerY)
					throw new PostconditionError(
							"ENGINE init 11 : un tresors est toujours en jeu, alors que le joueur est à la même position.");

		if (getCurrentLevel() != -1)
			for (Guard g : getGuards()) {
				if (g.getWdt() == playerX && g.getHgt() == playerY)
					if (getPlayer().getVie() == 3)
						throw new PostconditionError(
								"ENGINE init 12 : Un garde est sur un joueur, et pourtant le joueur ne semble pas avoir perdu de point de vie");
			}

		// On calcule le nombre total de tresors de tout les niveaux.
		int nbTreasuresTotal = 0;
		for (Entry<Integer, Environnement> e : getLevels().entrySet()) {
			if (getPlayableLevels().get(e.getKey()))
				nbTreasuresTotal += e.getValue().getTreasures().size();
		}
		if (getCurrentLevel() != -1)
			if (getNbTreasuresTotal() != nbTreasuresTotal)
				throw new PostconditionError("ENGINE init 13 : le nombre total de trésors n'est pas correct");
		
		if (getCurrentLevel() != -1)
			if (getCurrentNbTreasures() != getCurrentEnvironnement().getTreasures().size())
				throw new PostconditionError(
						"ENGINE init 14 : Le nombre de trésors courant, ne correspond pas à la taille de la liste des trésors fournie.");

		if (getCurrentLevel() != -1)
			if (getScore() != 0)
				throw new PostconditionError("ENGINE init 15 : Le score n'est pas initialisé à 0.");

		if (getCurrentLevel() != -1)
			if (getNbStep() != 0)
				throw new PostconditionError("ENGINE init 16 : Le nombre de step n'est pas initialisé à 0.");
		
		if (getCurrentLevel() != -1)
			if (getScreenWdt() != wdtScreen)
				throw new PostconditionError("ENGINE init 17 : La largeur de l'écran n'est pas affectée.");
		
		if (getCurrentLevel() != -1)
			if (getScreenHgt() != hgtScreen)
				throw new PostconditionError("ENGINE init 18 : La hauteur de l'écran n'est pas affectée.");

		if (getCurrentLevel() != -1)
			if(getAttacks().isEmpty() == false)
				throw new PostconditionError("ENGINE init 19 : la liste des attaques devrait être vide.");
		
		if (getCurrentLevel() != -1)
			if(getCurrentEnvironnement().getWidth() != wdtScreen)
				throw new PostconditionError("ENGINE init 20 : La largeur de l'écran n'est pas affectée.");
		
		if (getCurrentLevel() != -1)
			if(getCurrentEnvironnement().getHeight() != hgtScreen)
				throw new PostconditionError("ENGINE init 21 : La hauteur de l'écran n'est pas affectée.");
		
		if (getCurrentLevel() != -1)
			if(getPlayer().getWdt() != playerX)
				throw new PostconditionError("ENGINE init 22 : La coordonnée x du joueur n'est pas affectée.");
		
		if (getCurrentLevel() != -1)
			if(getPlayer().getHgt() != playerY)
				throw new PostconditionError("ENGINE init 23 : La coordonnée y du joueur n'est pas affectée.");
		
		checkInvariants();
	}

	@Override
	public void initWithContract(int wdtScreen, int hgtScreen, int playerX, int playerY) {
		if (!(0 < playerY && playerY < hgtScreen && 0 < playerX && playerX < wdtScreen))
			throw new PreconditionError("ENGINE initWithContract 1 : 0 <= y < height(S) and 0 <= x < width(S)");

		super.initWithContract(wdtScreen, hgtScreen, playerX, playerY);

		int expectedLevel = -1;
		for(int i=1; i<=3; i++) {
			if (getPlayableLevels().get(i)) {
				expectedLevel = i;
				break;
			}
		}
		
		if (getCurrentLevel() != expectedLevel)
			throw new PostconditionError("ENGINE initWithContract 1 : le niveau à l'initialisation n'est pas le plus petit niveau jouable.");
		
		if (getCurrentLevel() == -1)
			if (getStatus() != Status.WIN)
				throw new PostconditionError("ENGINE initWithContract 1 : le statut du jeu n'est pas 'WIN' (aucun terrain jouable).");
		
		if (getCurrentLevel() != -1)
			if (getStatus() != Status.PLAYING)
				throw new PostconditionError("ENGINE initWithContract 2 : le statut du jeu n'est pas 'PLAYING'.");
		
		if (getCurrentLevel() != -1)
			if (getCurrentEnvironnement() != getLevels().get(getCurrentLevel()))
				throw new PostconditionError("ENGINE initWithContract 3 : Le niveau actuel n'est pas le premier.");
		
		if (getCurrentLevel() != -1)
			if (getBoard() == null)
				throw new PostconditionError("ENGINE initWithContract 4 : le Board n'a pas été affecté.");
		
		if (getCurrentLevel() != -1)
			if (getPlayer() == null)
				throw new PostconditionError("ENGINE initWithContract 5 : le Player n'a pas été affecté.");
		
		if (getCurrentLevel() != -1)
			if (getCurrentPathResolver() == null)
				throw new PostconditionError("ENGINE initWithContract 6 : le PathResolver n'a pas été affecté.");

		if (getCurrentLevel() != -1)
			for (Guard g : getGuards()) {
				if (getCurrentEnvironnement().cellNature(g.getWdt(), g.getHgt()) != Cell.EMP)
					throw new PostconditionError("ENGINE initWithContract 7 : Un garde n'est pas sur une case de type EMP");
			}

		if (getCurrentLevel() != -1)
			for (GameObject t : getTreasures()) {
				if (t.getHgt() - 1 >= 0) {
					Cell c = getCurrentEnvironnement().cellNature(t.getWdt(), t.getHgt());
					Cell c2 = getCurrentEnvironnement().cellNature(t.getWdt(), t.getHgt() - 1);
					if (c != Cell.EMP && (c2 == Cell.PLT || c2 == Cell.MTL))
						throw new PostconditionError(
								"ENGINE initWithContract 8 : Un trésor n'est pas sur une case de type EMP ou bien sa case en dessous n'est pas de type PLT ou MTL");
				}
			}

		// Vérifie que deux gardes n'ont pas la même position
		if (getCurrentLevel() != -1)
			for (Guard g1 : getGuards()) {
				for (Guard g2 : getGuards()) {
					if (g1 != g2)
						if (g1.getWdt() == g2.getWdt() && g1.getHgt() == g2.getHgt())
							throw new PostconditionError("ENGINE initWithContract 9 : Deux gardes ont la même position");
				}
			}

		// Vérifie que deux trésors n'ont pas la même position
		if (getCurrentLevel() != -1)
			for (GameObject t1 : getTreasures()) {
				for (GameObject t2 : getTreasures()) {
					if (t1 != t2)
						if (t1.getWdt() == t2.getWdt() && t1.getHgt() == t2.getHgt())
							throw new PostconditionError("ENGINE initWithContract 10 : Deux trésors ont la même position");
				}
			}
		
		if (getCurrentLevel() != -1)
			for (GameObject t : getTreasures())
				if (t.getWdt() == playerX && t.getHgt() == playerY)
					throw new PostconditionError(
							"ENGINE initWithContract 11 : un tresors est toujours en jeu, alors que le joueur est à la même position.");

		if (getCurrentLevel() != -1)
			for (Guard g : getGuards()) {
				if (g.getWdt() == playerX && g.getHgt() == playerY)
					if (getPlayer().getVie() == 3)
						throw new PostconditionError(
								"ENGINE initWithContract 12 : Un garde est sur un joueur, et pourtant le joueur ne semble pas avoir perdu de point de vie");
			}

		// On calcule le nombre total de tresors de tout les niveaux.
		int nbTreasuresTotal = 0;
		for (Entry<Integer, Environnement> e : getLevels().entrySet()) {
			if (getPlayableLevels().get(e.getKey()))
				nbTreasuresTotal += e.getValue().getTreasures().size();
		}
		if (getCurrentLevel() != -1)
			if (getNbTreasuresTotal() != nbTreasuresTotal)
				throw new PostconditionError("ENGINE initWithContract 13 : le nombre total de trésors n'est pas correct");
		
		if (getCurrentLevel() != -1)
			if (getCurrentNbTreasures() != getCurrentEnvironnement().getTreasures().size())
				throw new PostconditionError(
						"ENGINE initWithContract 14 : Le nombre de trésors courant, ne correspond pas à la taille de la liste des trésors fournie.");

		if (getCurrentLevel() != -1)
			if (getScore() != 0)
				throw new PostconditionError("ENGINE initWithContract 15 : Le score n'est pas initialisé à 0.");

		if (getCurrentLevel() != -1)
			if (getNbStep() != 0)
				throw new PostconditionError("ENGINE initWithContract 16 : Le nombre de step n'est pas initialisé à 0.");
		
		if (getCurrentLevel() != -1)
			if (getScreenWdt() != wdtScreen)
				throw new PostconditionError("ENGINE initWithContract 17 : La largeur de l'écran n'est pas affectée.");
		
		if (getCurrentLevel() != -1)
			if (getScreenHgt() != hgtScreen)
				throw new PostconditionError("ENGINE initWithContract 18 : La hauteur de l'écran n'est pas affectée.");

		if (getCurrentLevel() != -1)
			if(getAttacks().isEmpty() == false)
				throw new PostconditionError("ENGINE initWithContract 19 : la liste des attaques devrait être vide.");
			
		if (getCurrentLevel() != -1)
			if(getCurrentEnvironnement().getWidth() != wdtScreen)
				throw new PostconditionError("ENGINE initWithContract 20 : La largeur de l'écran n'est pas affectée.");
		
		if (getCurrentLevel() != -1)
			if(getCurrentEnvironnement().getHeight() != hgtScreen)
				throw new PostconditionError("ENGINE initWithContract 21 : La hauteur de l'écran n'est pas affectée.");
		
		if (getCurrentLevel() != -1)
			if(getPlayer().getWdt() != playerX)
				throw new PostconditionError("ENGINE initWithContract 22 : La coordonnée x du joueur n'est pas affectée.");
		
		if (getCurrentLevel() != -1)
			if(getPlayer().getHgt() != playerY)
				throw new PostconditionError("ENGINE initWithContract 23 : La coordonnée y du joueur n'est pas affectée.");
		
		// LES NOUVELLES POST CONDITIONS
		if (getCurrentLevel() != -1)
			if (!(getCurrentEnvironnement() instanceof EnvironnementContract))
				throw new PostconditionError(
						"ENGINE initWithContract 24 : L'environnement devrait etre une instance de EnvironnementContract");

		if (getCurrentLevel() != -1)
			if (!(getCurrentPathResolver() instanceof PathResolverContract))
				throw new PostconditionError(
						"ENGINE initWithContract 25 : Le resolver devrait etre une instance de PathResolverContract");
		
		if (getCurrentLevel() != -1)
			if (!(getPlayer() instanceof PlayerContract))
				throw new PostconditionError(
						"ENGINE initWithContract 26 : Le joueur devrait etre une instance de PlayerContract");

		if (getCurrentLevel() != -1)
			for (Guard g : getGuards()) {
				if (!(g instanceof GuardContract))
					throw new PostconditionError(
							"ENGINE initWithContract 27 : Au moins un garde n'est pas une instance de GuardContract");
			}

		checkInvariants();
	}
	
	@Override
	public void initForTests(int wdtScreen, int hgtScreen, int playerX, int playerY) {
		if (!(0 < playerY && playerY < hgtScreen && 0 < playerX && playerX < wdtScreen))
			throw new PreconditionError("ENGINE initForTests 1 : 0 <= y < height(S) and 0 <= x < width(S)");

		super.initForTests(wdtScreen, hgtScreen, playerX, playerY);

		int expectedLevel = -1;
		for(int i=1; i<=3; i++) {
			if (getPlayableLevels().get(i)) {
				expectedLevel = i;
				break;
			}
		}
		
		if (getCurrentLevel() != expectedLevel)
			throw new PostconditionError("ENGINE initForTests 1 : le niveau à l'initialisation n'est pas le plus petit niveau jouable.");
		
		if (getCurrentLevel() == -1)
			if (getStatus() != Status.WIN)
				throw new PostconditionError("ENGINE initForTests 1 : le statut du jeu n'est pas 'WIN' (aucun terrain jouable).");
		
		if (getCurrentLevel() != -1)
			if (getStatus() != Status.PLAYING)
				throw new PostconditionError("ENGINE initForTests 2 : le statut du jeu n'est pas 'PLAYING'.");
		
		if (getCurrentLevel() != -1)
			if (getCurrentEnvironnement() != getLevels().get(getCurrentLevel()))
				throw new PostconditionError("ENGINE initForTests 3 : Le niveau actuel n'est pas le premier.");
		
		if (getCurrentLevel() != -1)
			if (getBoard() != null)
				throw new PostconditionError("ENGINE initForTests 4 : le Board ne doit pas être affecté.");
		
		if (getCurrentLevel() != -1)
			if (getPlayer() == null)
				throw new PostconditionError("ENGINE initForTests 5 : le Player n'a pas été affecté.");
		
		if (getCurrentLevel() != -1)
			if (getCurrentPathResolver() == null)
				throw new PostconditionError("ENGINE initForTests 6 : le PathResolver n'a pas été affecté.");

		if (getCurrentLevel() != -1)
			for (Guard g : getGuards()) {
				if (getCurrentEnvironnement().cellNature(g.getWdt(), g.getHgt()) != Cell.EMP)
					throw new PostconditionError("ENGINE initForTests 7 : Un garde n'est pas sur une case de type EMP");
			}

		if (getCurrentLevel() != -1)
			for (GameObject t : getTreasures()) {
				if (t.getHgt() - 1 >= 0) {
					Cell c = getCurrentEnvironnement().cellNature(t.getWdt(), t.getHgt());
					Cell c2 = getCurrentEnvironnement().cellNature(t.getWdt(), t.getHgt() - 1);
					if (c != Cell.EMP && (c2 == Cell.PLT || c2 == Cell.MTL))
						throw new PostconditionError(
								"ENGINE initForTests 8 : Un trésor n'est pas sur une case de type EMP ou bien sa case en dessous n'est pas de type PLT ou MTL");
				}
			}

		// Vérifie que deux gardes n'ont pas la même position
		if (getCurrentLevel() != -1)
			for (Guard g1 : getGuards()) {
				for (Guard g2 : getGuards()) {
					if (g1 != g2)
						if (g1.getWdt() == g2.getWdt() && g1.getHgt() == g2.getHgt())
							throw new PostconditionError("ENGINE initForTests 9 : Deux gardes ont la même position");
				}
			}

		// Vérifie que deux trésors n'ont pas la même position
		if (getCurrentLevel() != -1)
			for (GameObject t1 : getTreasures()) {
				for (GameObject t2 : getTreasures()) {
					if (t1 != t2)
						if (t1.getWdt() == t2.getWdt() && t1.getHgt() == t2.getHgt())
							throw new PostconditionError("ENGINE initForTests 10 : Deux trésors ont la même position");
				}
			}
		
		if (getCurrentLevel() != -1)
			for (GameObject t : getTreasures())
				if (t.getWdt() == playerX && t.getHgt() == playerY)
					throw new PostconditionError(
							"ENGINE initForTests 11 : un tresors est toujours en jeu, alors que le joueur est à la même position.");

		if (getCurrentLevel() != -1)
			for (Guard g : getGuards()) {
				if (g.getWdt() == playerX && g.getHgt() == playerY)
					if (getPlayer().getVie() == 3)
						throw new PostconditionError(
								"ENGINE initForTests 12 : Un garde est sur un joueur, et pourtant le joueur ne semble pas avoir perdu de point de vie");
			}

		// On calcule le nombre total de tresors de tout les niveaux.
		int nbTreasuresTotal = 0;
		for (Entry<Integer, Environnement> e : getLevels().entrySet()) {
			if (getPlayableLevels().get(e.getKey()))
				nbTreasuresTotal += e.getValue().getTreasures().size();
		}
		if (getCurrentLevel() != -1)
			if (getNbTreasuresTotal() != nbTreasuresTotal)
				throw new PostconditionError("ENGINE initForTests 13 : le nombre total de trésors n'est pas correct");
		
		if (getCurrentLevel() != -1)
			if (getCurrentNbTreasures() != getCurrentEnvironnement().getTreasures().size())
				throw new PostconditionError(
						"ENGINE initForTests 14 : Le nombre de trésors courant, ne correspond pas à la taille de la liste des trésors fournie.");

		if (getCurrentLevel() != -1)
			if (getScore() != 0)
				throw new PostconditionError("ENGINE initForTests 15 : Le score n'est pas initialisé à 0.");

		if (getCurrentLevel() != -1)
			if (getNbStep() != 0)
				throw new PostconditionError("ENGINE initForTests 16 : Le nombre de step n'est pas initialisé à 0.");
		
		if (getCurrentLevel() != -1)
			if (getScreenWdt() != wdtScreen)
				throw new PostconditionError("ENGINE initForTests 17 : La largeur de l'écran n'est pas affectée.");
		
		if (getCurrentLevel() != -1)
			if (getScreenHgt() != hgtScreen)
				throw new PostconditionError("ENGINE initForTests 18 : La hauteur de l'écran n'est pas affectée.");

		if (getCurrentLevel() != -1)
			if(getAttacks().isEmpty() == false)
				throw new PostconditionError("ENGINE initForTests 19 : la liste des attaques devrait être vide.");

		if (getCurrentLevel() != -1)
			if(getCurrentEnvironnement().getWidth() != wdtScreen)
				throw new PostconditionError("ENGINE initForTests 20 : La largeur de l'écran n'est pas affectée.");
		
		if (getCurrentLevel() != -1)
			if(getCurrentEnvironnement().getHeight() != hgtScreen)
				throw new PostconditionError("ENGINE initForTests 21 : La hauteur de l'écran n'est pas affectée.");
		
		if (getCurrentLevel() != -1)
			if(getPlayer().getWdt() != playerX)
				throw new PostconditionError("ENGINE initForTests 22 : La coordonnée x du joueur n'est pas affectée.");
		
		if (getCurrentLevel() != -1)
			if(getPlayer().getHgt() != playerY)
				throw new PostconditionError("ENGINE initForTests 23 : La coordonnée y du joueur n'est pas affectée.");
		
		// LES NOUVELLES POST CONDITIONS
		if (getCurrentLevel() != -1)
			if (!(getCurrentEnvironnement() instanceof EnvironnementContract))
				throw new PostconditionError(
						"ENGINE initForTests 24 : L'environnement devrait etre une instance de EnvironnementContract");

		if (getCurrentLevel() != -1)
			if (!(getCurrentPathResolver() instanceof PathResolverContract))
				throw new PostconditionError(
						"ENGINE initForTests 25 : Le resolver devrait etre une instance de PathResolverContract");
		
		if (getCurrentLevel() != -1)
			if (!(getPlayer() instanceof PlayerContract))
				throw new PostconditionError(
						"ENGINE initForTests 26 : Le joueur devrait etre une instance de PlayerContract");

		if (getCurrentLevel() != -1)
			for (Guard g : getGuards()) {
				if (!(g instanceof GuardContract))
					throw new PostconditionError(
							"ENGINE initForTests 27 : Au moins un garde n'est pas une instance de GuardContract");
			}

		checkInvariants();
	}
	
	@Override
	public void setNextCommand(Command command) {
		checkInvariants();
		
		if (command != Command.LEFT && command != Command.RIGHT && command != Command.UP && command != Command.DOWN
				&& command != Command.DIGL && command != Command.DIGR && command != Command.NEUTRAL)
			throw new PreconditionError("ENGINE setNextCommand : La commande ne correspond pas au commande disponible");

		// Capture des observers
		int scoreAt_pre = getScore();
		int nbTreasuresTotalAt_pre = getNbTreasuresTotal();
		int currentNbTreasuresAt_pre = getCurrentNbTreasures();
		int nbStepAt_pre = getNbStep();
		Environnement enviAt_pre = getCurrentEnvironnement();
		PathResolver resolverAt_pre = getCurrentPathResolver();
		Status statusAt_pre = getStatus();
		int currentLevelAt_pre = getCurrentLevel();
		
		// Copie des holes
		Map<GameObject, Integer> holesAt_pre = new HashMap<>();
		for(Entry<GameObject, Integer> e : getHoles().entrySet()) {
			holesAt_pre.put(e.getKey(), e.getValue());
		}
		
		// Copie des gardes
		List<Guard> guardsAt_pre = new ArrayList<>();
		for(Guard g : getGuards()) {
			guardsAt_pre.add(g);
		}
		
		//Copie des trésors
		List<Item> treasuresAt_pre = new ArrayList<>();
		for(Item t : getTreasures()) {
			treasuresAt_pre.add(t);
		}
		
		// On copie les attaques
		List<Attack> attackAt_pre = new ArrayList<>();
		for(Attack a : getAttacks()) {
			attackAt_pre.add(a);
		}
	
		super.setNextCommand(command);

		if (getNextCommand() != command)
			throw new PostconditionError(
					"ENGINE setNextCommand 1 : La nouvelle commande actuelle ne correspond pas à la commande fournie.");

		if (scoreAt_pre != getScore())
			throw new PostconditionError("ENGINE setNextCommand 2 : Le score a changé.");
		
		if (nbStepAt_pre != getNbStep())
			throw new PostconditionError("ENGINE setNextCommand 3 : Le nombre de step a changé.");

		if (nbTreasuresTotalAt_pre != getNbTreasuresTotal())
			throw new PostconditionError(
					"ENGINE setNextCommand 4 : Le nombre total de tresors présent en jeu a changé.");

		List<Guard> guards = getGuards();
		if (guards.size() != guardsAt_pre.size())
			throw new PostconditionError("ENGINE setNextCommand 5 : Les gardes ont changé.");
		else { 
			for (Guard g : guardsAt_pre) {
				if (!guards.contains(g))
					throw new PostconditionError("ENGINE setNextCommand 5 : Les gardes ont changé.");
			}
		}

		List<Item> treasures = getTreasures();
		if (treasures.size() != treasuresAt_pre.size())
			throw new PostconditionError("ENGINE setNextCommand 6 : Les tresors ont changés.");
		else {
			for (Item t : treasuresAt_pre) {
				if (!treasures.contains(t))
					throw new PostconditionError("ENGINE setNextCommand 6 : Les tresors ont changés.");
			}
		}
		
		if (enviAt_pre != getCurrentEnvironnement())
			throw new PostconditionError("ENGINE setNextCommand 7 : L'environnement a changé.");
		
		if (resolverAt_pre != getCurrentPathResolver())
			throw new PostconditionError("ENGINE setNextCommand 8 : L'environnement a changé.");

		if (statusAt_pre != getStatus())
			throw new PostconditionError("ENGINE setNextCommand 9 : Le statut a changé.");

		Map<GameObject, Integer> holes = getHoles();
		for (Entry<GameObject, Integer> e : holesAt_pre.entrySet())
			if (!holes.containsKey(e.getKey())
					|| (holes.containsKey(e.getKey()) && holes.get(e.getKey()) != e.getValue()))
				throw new PostconditionError("ENGINE setNextCommand 10 : Les trous ont changé.");

		if (getCurrentNbTreasures() != currentNbTreasuresAt_pre)
			throw new PostconditionError("ENGINE setNextCommand 11 : le nombre de tresors courant a changé");

		if (getCurrentLevel() != currentLevelAt_pre)
			throw new PostconditionError("ENGINE setNextCommand 12 : le niveau courant a changé");

		if (getAttacks().size() != attackAt_pre.size())
			throw new PostconditionError("ENGINE setNextCommand 13 : les attaques ont changés");
		else {
			for(Attack a : attackAt_pre) {
				if(!getAttacks().contains(a))
					throw new PostconditionError("ENGINE setNextCommand 13 : les attaques ont changés");
			}
		}
		
		checkInvariants();
	}
	
	@Override
	public void nextLevel() {
		checkInvariants();
		// Capture des observers
		int scoreAt_pre = getScore();
		int nbStepAt_pre = getNbStep();
		int currentNbTreasuresAt_pre = getCurrentNbTreasures();

		int expectedLevel = -1;
		for(int i=getCurrentLevel()+1; i<=3; i++) {
			if (getPlayableLevels().get(i)) {
				expectedLevel = i;
				break;
			}
		}
		
		super.nextLevel();

		if (getCurrentLevel() != expectedLevel)
			throw new PostconditionError("ENGINE nextLevel 1 : on est pas passé au prochain niveau jouable");
		
		if (getCurrentLevel() == -1)
			if (getStatus() != Status.WIN)
				throw new PostconditionError("ENGINE nextLevel 2 : Aucun prochain niveau jouable, le joueur aurait du gagner");
		
		if (getCurrentLevel() != -1)
			if (getCurrentEnvironnement() != getLevels().get(getCurrentLevel()))
				throw new PostconditionError(
						"ENGINE nextLevel 3 : le niveau ne correspond pas à celui associé dans l'ensemble des niveaux.");
		
		if (getCurrentLevel() != -1)
			if (getCurrentPathResolver() != getPathResolvers().get(getCurrentLevel()))
				throw new PostconditionError(
						"ENGINE nextLevel 4 : le resolver ne correspond pas à celui associé dans l'ensemble des niveaux.");
		
		if (getCurrentLevel() != -1)
			if (!getHoles().isEmpty())
				throw new PostconditionError("ENGINE nextLevel 5 : la liste des trous n'est pas vide.");

		if (getCurrentLevel() != -1)
			if (getBoard() != null && getBoard().getEnvi() != getCurrentEnvironnement())
				throw new PostconditionError(
						"ENGINE nextLevel 6 : l'environnement du board ne correspond pas à celui de l'engine.");

		if (getCurrentLevel() != -1)
			if (getCurrentEnvironnement().getTreasures() != getTreasures())
				throw new PostconditionError(
						"ENGINE nextLevel 7 : la liste des trésors de l'environnement ne correspond pas à celui de l'engine.");

		if (getCurrentLevel() != -1)
			if (getCurrentEnvironnement().getGuards() != getGuards())
				throw new PostconditionError(
						"ENGINE nextLevel 8 : la liste des gars de l'environnement ne correspond pas à celui de l'engine.");

		if (getCurrentLevel() != -1)
			if (getCurrentNbTreasures() != getTreasures().size() + currentNbTreasuresAt_pre)
				throw new PostconditionError(
						"ENGINE nextLevel 9 : le nombre courant de trésors ne correspond pas à la taille de la liste actuelle + la valeur précédente.");

		if (getCurrentLevel() != -1) {
			if (getPlayer().getWdt() != getCurrentEnvironnement().getWdtPortail())
				throw new PostconditionError("ENGINE nextLevel 10 : La Wdt du joueur n'est pas celle du portail.");
			if (getPlayer().getHgt() != getCurrentEnvironnement().getHgtPortail())
				throw new PostconditionError("ENGINE nextLevel 10 : La Hgt du joueur n'est pas celle du portail.");
		}
			
		if (getScore() != scoreAt_pre)
			throw new PostconditionError("ENGINE nextLevel 11 : le score a changé.");

		if (getNbStep() != nbStepAt_pre)
			throw new PostconditionError("ENGINE nextLevel 12 : le nombre de step a changé.");

		if (getCurrentLevel() != -1)
			if (!getAttacks().isEmpty())
				throw new PostconditionError("ENGINE nextLevel 13 : la liste des attaques n'est pas renouvelée.");
		
		if (getCurrentLevel() != -1)
			if (getPlayer().getVie() != 3)
				throw new PostconditionError("ENGINE nextLevel 14 : Lejoueur auraît dû regénérer sa vie entièrement.");
		
		if (getCurrentLevel() != -1)
			if (getPlayer().getNbAttacks() != 3)
				throw new PostconditionError("ENGINE nextLevel 15 : Lejoueur auraît dû regénérer son nombre d'attaque.");
		
		checkInvariants();
	}
	
	@Override
	public void createEnvironnements(int wdt, int hgt) {
		// createEnvironnement est appelé avant la fin du init (dans ce cas la on ne peut pas encore respecter les invariants)
		if (getGuards() != null && getTreasures() != null && getHoles() != null)
			checkInvariants();

		if (!(wdt == getScreenWdt() && hgt == getScreenHgt()))
			throw new PreconditionError("ENGINE createEnvironnement 1 : taille de ne correspond pas à celle de l'editeur.");

		super.createEnvironnements(wdt, hgt);
		
		BufferedReader br = null;
		try {
			for (int nbLevel = 1; nbLevel <= 3; nbLevel++) {

				br = new BufferedReader(new FileReader(new File("src/resources/niveaux/niveau" + nbLevel + ".txt")));
				Environnement e = getLevels().get(nbLevel);
				EditableScreen edit = getEditors().get(nbLevel);
				PathResolver r = getPathResolvers().get(nbLevel);
				
				if (r.playerCanReachAllTreasuresAndPortal(e.getTreasures(), e.getWdtPortail(), e.getHgtPortail()) && edit.playable()) {
					if (getPlayableLevels().get(nbLevel) == false)
						throw new PostconditionError("ENGINE createEnvironnement 1: Le terrain est playable est devrait etre marqué comme tel");
				}
				else {
					if (getPlayableLevels().get(nbLevel) == true)
						throw new PostconditionError("ENGINE createEnvironnement 1: Le terrain n'est pas playable est devrait etre marqué comme tel");
				}
				for (int i = hgt-1; i >= 1; i--) {
					String lineCells = br.readLine();
					String[] cells = lineCells.split(",");

					for (int j = 0; j < cells.length; j++) {
						if (cells[j].equals("GRD")) {
							if (e.cellContent(j, i).getCharacter() == null)
								throw new PostconditionError("ENGINE createEnvironnement 2: il devrait y avoir un garde");
							if (e.cellNature(j, i) != Cell.EMP)
								throw new PostconditionError("ENGINE createEnvironnement 2: la cellule devrait etre EMP");
							if (edit.cellNature(j, i) != Cell.EMP)
								throw new PostconditionError("ENGINE createEnvironnement 2: la cellule devrait etre EMP");

							boolean exist = false;
							for (Guard g : e.getGuards())
								if (g.getWdt() == j && g.getHgt() == i)
									exist = true;

							if (!exist)
								throw new PostconditionError("ENGINE createEnvironnement 3: il n'y a pas de garde dans la liste des gardes de l'environnement crée.");
						} else if (cells[j].equals("TRS")) {
							if (e.cellContent(j, i).getItem() == null)
								throw new PostconditionError("ENGINE createEnvironnement 3: il devrait y avoir un tresor");
							if (e.cellNature(j, i) != Cell.EMP)
								throw new PostconditionError("ENGINE createEnvironnement 3: la nature de la cellule devrait etre EMP");
							if (edit.cellNature(j, i) != Cell.EMP)
								throw new PostconditionError("ENGINE createEnvironnement 3: la nature de la cellule devrait etre EMP");

							boolean exist = false;
							for (Item t : e.getTreasures())
								if (t.getWdt() == j && t.getHgt() == i)
									exist = true;

							if (!exist)
								throw new PostconditionError("ENGINE createEnvironnement 3: il n'y a pas de trésor dans la liste des trésors de l'environnement crée.");
						} else if (cells[j].equals("PRT")) {
							if (e.cellNature(j, i) != Cell.PRT)
								throw new PostconditionError("ENGINE createEnvironnement 4: la nature de la cellule devrait etre PRT");
							if (edit.cellNature(j, i) != Cell.PRT)
								throw new PostconditionError("ENGINE createEnvironnement 4: la nature de la cellule devrait etre PRT");
							if (e.getWdtPortail() != j || e.getHgtPortail() != i)
								throw new PostconditionError("ENGINE createEnvironnement 4: les coordonnées du portail ne correspondent pas");
						} else {
							if (e.cellNature(j, i) != Cell.valueOf(cells[j]))
								throw new PostconditionError("ENGINE createEnvironnement 4: la nature de la cellule devrait être "+ cells[j]);
							if (edit.cellNature(j, i) != Cell.valueOf(cells[j]))
								throw new PostconditionError("ENGINE createEnvironnement 4: la nature de la cellule devrait être "+ cells[j]);
						}
					}
				}
			}
			
			if (getGuards() != null && getTreasures() != null && getHoles() != null)
				checkInvariants();

		} catch (IOException e) {
			// Nous ne savons pas comment spécifier dans les specs formelle.
			throw new PreconditionError("ENGINE createEnvironnement 2: aucun fichier de ce type.");
		}
		finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void createEnvironnementsWithContract(int wdt, int hgt) {
		// createEnvironnementsWithContract est appelé avant la fin du init (dans ce cas la on ne peut pas encore respecter les invariants)
		if (getGuards() != null && getTreasures() != null && getHoles() != null)
			checkInvariants();

		if (!(wdt == getScreenWdt() && hgt == getScreenHgt()))
			throw new PreconditionError("ENGINE createEnvironnementsWithContract 1 : taille de ne correspond pas à celle de l'editeur.");

		super.createEnvironnementsWithContract(wdt, hgt);
		
		BufferedReader br = null;
		try {
			for (int nbLevel = 1; nbLevel <= 3; nbLevel++) {
				if (TESTING)
					br = new BufferedReader(new FileReader(new File("src/resources/niveaux/test_niveau" + nbLevel + ".txt")));
				else
					br = new BufferedReader(new FileReader(new File("src/resources/niveaux/niveau" + nbLevel + ".txt")));
				Environnement e = getLevels().get(nbLevel);
				EditableScreen edit = getEditors().get(nbLevel);
				PathResolver r = getPathResolvers().get(nbLevel);
				
				if (r.playerCanReachAllTreasuresAndPortal(e.getTreasures(), e.getWdtPortail(), e.getHgtPortail()) && edit.playable()) {
					if (getPlayableLevels().get(nbLevel) == false)
						throw new PostconditionError("ENGINE createEnvironnementsWithContract 1: Le terrain est playable est devrait etre marqué comme tel");
				}
				else {
					if (getPlayableLevels().get(nbLevel) == true)
						throw new PostconditionError("ENGINE createEnvironnementsWithContract 1: Le terrain n'est pas playable est devrait etre marqué comme tel");
				}
				
				if(e.getClass() != EnvironnementContract.class)
					throw new PostconditionError("ENGINE createEnvironnementsWithContract 1: il n'y a pas de contrat pour l'environnement.");
				
				if(edit.getClass() != EditableScreenContract.class)
					throw new PostconditionError("ENGINE createEnvironnementsWithContract 1: il n'y a pas de contrat pour l'éditeur.");
				
				if(r.getClass() != PathResolverContract.class)
					throw new PostconditionError("ENGINE createEnvironnementsWithContract 1: il n'y a pas de contrat pour le resolver.");
				
				for (int i = hgt-1; i >= 1; i--) {
					String lineCells = br.readLine();
					String[] cells = lineCells.split(",");

					for (int j = 0; j < cells.length; j++) {
						if (cells[j].equals("GRD")) {
							if (e.cellContent(j, i).getCharacter() == null)
								throw new PostconditionError("ENGINE createEnvironnementsWithContract 2: il devrait y avoir un garde");
							if (e.cellNature(j, i) != Cell.EMP)
								throw new PostconditionError("ENGINE createEnvironnementsWithContract 2: la cellule devrait etre EMP");
							if (edit.cellNature(j, i) != Cell.EMP)
								throw new PostconditionError("ENGINE createEnvironnementsWithContract 2: la cellule devrait etre EMP");

							boolean exist = false;
							for (Guard g : e.getGuards()) {
								if(g.getClass() != GuardContract.class)
									throw new PostconditionError("ENGINE createEnvironnementsWithContract 2: il n'y a pas de contrat pour les gardes.");
								if (g.getWdt() == j && g.getHgt() == i)
									exist = true;
							}
							
							if (!exist)
								throw new PostconditionError("ENGINE createEnvironnementsWithContract 3: il n'y a pas de garde dans la liste des gardes de l'environnement crée.");
						} else if (cells[j].equals("TRS")) {
							if (e.cellContent(j, i).getItem() == null)
								throw new PostconditionError("ENGINE createEnvironnementsWithContract 3: il devrait y avoir un tresor");
							if (e.cellNature(j, i) != Cell.EMP)
								throw new PostconditionError("ENGINE createEnvironnementsWithContract 3: la nature de la cellule devrait etre EMP");
							if (edit.cellNature(j, i) != Cell.EMP)
								throw new PostconditionError("ENGINE createEnvironnementsWithContract 3: la nature de la cellule devrait etre EMP");

							boolean exist = false;
							for (Item t : e.getTreasures()) {
								if(t.getClass() != ItemContract.class)
									throw new PostconditionError("ENGINE createEnvironnementsWithContract 3: il n'y a pas de contrat pour les trésors.");
								if (t.getWdt() == j && t.getHgt() == i)
									exist = true;
							}

							if (!exist)
								throw new PostconditionError("ENGINE createEnvironnementsWithContract 3: il n'y a pas de trésor dans la liste des trésors de l'environnement crée.");
						} else if (cells[j].equals("PRT")) {
							if (e.cellNature(j, i) != Cell.PRT)
								throw new PostconditionError("ENGINE createEnvironnementsWithContract 4: la nature de la cellule devrait etre PRT");
							if (edit.cellNature(j, i) != Cell.PRT)
								throw new PostconditionError("ENGINE createEnvironnementsWithContract 4: la nature de la cellule devrait etre PRT");
							if (e.getWdtPortail() != j || e.getHgtPortail() != i)
								throw new PostconditionError("ENGINE createEnvironnementsWithContract 4: les coordonnées du portail ne correspondent pas");
						} else {
							if (e.cellNature(j, i) != Cell.valueOf(cells[j]))
								throw new PostconditionError("ENGINE createEnvironnementsWithContract 4: la nature de la cellule devrait être "+ cells[j]);
							if (edit.cellNature(j, i) != Cell.valueOf(cells[j]))
								throw new PostconditionError("ENGINE createEnvironnementsWithContract 4: la nature de la cellule devrait être "+ cells[j]);
						}
					}
				}
			}
			
			if (getGuards() != null && getTreasures() != null && getHoles() != null)
				checkInvariants();

		} catch (IOException e) {
			// Nous ne savons pas comment spécifier dans les specs formelle.
			throw new PreconditionError("ENGINE createEnvironnementsWithContract 2: aucun fichier de ce type.");
		}
		finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void addAttack(int x, int y, Direction direction) {
		checkInvariants();

		if (!(0 <= y && y < getCurrentEnvironnement().getHeight() && 0 <= x && x < getCurrentEnvironnement().getWidth()))
			throw new PreconditionError("ENGINE addAttack : 0 <= y < height(S) and 0 <= x < width(S)");
		
		for(Attack a : getAttacks())
			if(a.getWdt() == x && a.getHgt() == y && a.getDirection() == direction)
				throw new PreconditionError("ENGINE addAttack : il y a déjà une attaque à cette position ayant la même direction.");
		
		// Capture des observers
		int scoreAt_pre = getScore();
		int currentNbTreasuresAt_pre = getCurrentNbTreasures();
		int nbStepAt_pre = getNbStep();
		int nbAttackAt_pre = getPlayer().getNbAttacks();
		
		Environnement enviAt_pre = getCurrentEnvironnement();
		PathResolver resolverAt_pre = getCurrentPathResolver();
		
		Status statusAt_pre = getStatus();
		int currentLevelAt_pre = getCurrentLevel();
		Command commandAt_pre = getNextCommand();
		
		// Copie des holes
		Map<GameObject, Integer> holesAt_pre = new HashMap<>();
		for(Entry<GameObject, Integer> e : getHoles().entrySet()) {
			holesAt_pre.put(e.getKey(), e.getValue());
		}
		
		// Copie des gardes
		List<Guard> guardsAt_pre = new ArrayList<>();
		for(Guard g : getGuards()) {
			guardsAt_pre.add(g);
		}
		
		//Copie des trésors
		List<Item> treasuresAt_pre = new ArrayList<>();
		for(Item t : getTreasures()) {
			treasuresAt_pre.add(t);
		}
		
		// On copie les attaques
		List<Attack> attackAt_pre = new ArrayList<>();
		for(Attack a : getAttacks()) {
			attackAt_pre.add(a);
		}

		super.addAttack(x, y, direction);
		
		if (getNextCommand() != commandAt_pre)
			throw new PostconditionError("ENGINE addAttack 1 : la commande a changé.");
		
		if (scoreAt_pre != getScore())
			throw new PostconditionError("ENGINE addAttack 2 : Le score a changé.");
		
		if (nbStepAt_pre != getNbStep())
			throw new PostconditionError("ENGINE addAttack 3 : Le nombre de step a changé.");
		
		List<Item> treasures = getTreasures();
		if (treasures.size() != treasuresAt_pre.size())
			throw new PostconditionError("ENGINE addAttack 4 : Les tresors ont changés.");
		else {
			for (Item t : treasuresAt_pre) {
				if (!treasures.contains(t))
					throw new PostconditionError("ENGINE addAttack 4 : Les tresors ont changés.");
			}
		}
		
		List<Guard> guards = getGuards();
		if (guards.size() != guardsAt_pre.size())
			throw new PostconditionError("ENGINE addAttack 5 : Les gardes ont changé.");
		else { 
			for (Guard g : guardsAt_pre) {
				if (!guards.contains(g))
					throw new PostconditionError("ENGINE addAttack 5 : Les gardes ont changé.");
			}
		}
		
		if (enviAt_pre != getCurrentEnvironnement())
			throw new PostconditionError("ENGINE addAttack 6 : L'environnement a changé.");
		
		if (resolverAt_pre != getCurrentPathResolver())
			throw new PostconditionError("ENGINE addAttack 7 : Le resolver a changé.");

		if (statusAt_pre != getStatus())
			throw new PostconditionError("ENGINE addAttack 8 : Le statut a changé.");
		
		Map<GameObject, Integer> holes = getHoles();
		for (Entry<GameObject, Integer> e : holesAt_pre.entrySet())
			if (!holes.containsKey(e.getKey())
					|| (holes.containsKey(e.getKey()) && holes.get(e.getKey()) != e.getValue()))
				throw new PostconditionError("ENGINE addAttack 9 : Les trous ont changé.");
		
		if (getCurrentNbTreasures() != currentNbTreasuresAt_pre)
			throw new PostconditionError("ENGINE addAttack 10 : le nombre de tresors courant a changé");
		
		if (getCurrentLevel() != currentLevelAt_pre)
			throw new PostconditionError("ENGINE addAttack 11 : le niveau courant a changé");
		
		if(nbAttackAt_pre > 0 && getPlayer().getNbAttacks() != nbAttackAt_pre - 1)
			throw new PostconditionError("ENGINE addAttack 12 : le nombre d'attaque du joueur n'a pas diminué.");
		
		boolean exists = false;
		if (nbAttackAt_pre > 0) {
			for(Attack a : getAttacks())
				if(a.getWdt() == x && a.getHgt() == y && a.getDirection() == direction)
					exists = true;
	
			if(!exists)
				throw new PostconditionError("ENGINE addAttack 12 : L'attaque n'a pas été ajoutée.");
		}
		checkInvariants();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
