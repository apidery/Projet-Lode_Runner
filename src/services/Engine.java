package services;

import java.util.List;
import java.util.Map;

import data.Command;
import data.Direction;
import data.Item;
import data.Status;
import impl.LodeRunnerFrame;

/**
 * inv: forall g in Guards(E)
 *			Screen::CellContent(Character::Wdt(g), Character::Hdt(g)) == g
 * 
 * inv: forall t in Treasures(E)
 * 			Screen::CellContent(GameObject::Wdt(t), GameObject::Hgt(t)) == t
 * 
 * inv: forall h in Holes(E)
 * 			Screen::CellNature(GameObject::Wdt(h), GameObject::Hgt(h)) == h
 */
public interface Engine {	
	///////////////////////////////
	///////// OBSERVATORS /////////
	///////////////////////////////
	// CONST
	public Player getPlayer();

	// CONST
	public int getNbTreasuresTotal();
	
	// CONST
	public Board getBoard();
	
	// CONST
	public Map<Integer, Environnement> getLevels();
	
	// CONST
	public Map<Integer, EditableScreen> getEditors();
	
	// CONST
	public Map<Integer, PathResolver> getPathResolvers();
	
	// CONST
	public int getScreenWdt();
	
	// CONST
	public int getScreenHgt();
	
	// CONST
	public LodeRunnerFrame getFrame();
	
	// CONST
	public Map<Integer, Boolean> getPlayableLevels();
	
	public List<Guard> getGuards();
	
	public List<Item> getTreasures();
	
	public Environnement getCurrentEnvironnement();
	
	public PathResolver getCurrentPathResolver();
	
	public int getCurrentNbTreasures();
	
	public Command getNextCommand();
	
	public Status getStatus();
	
	public Map<GameObject, Integer> getHoles();
	
	public int getScore();
	
	public int getNbStep();
	
	public int getCurrentLevel();
	
	public List<Attack> getAttacks();
	
	/**
	 * inv de minimisation: 
	 * 		forall a in Attacks(E)
	 * 			GameObject::Wdt(a) == wdt && GameObject::Hgt(a) == hgt && Attack::Direction(a) == direction
	 */
	public boolean hasAttack(int wdt, int hgt, Direction direction);
	
	/**
	 * pre: AddHole(E, x, y) require 0 <= x < Environnnement::Wdt(CurrentEnvironnement(E)) 
	 * 							&& 0 <= y < Environnnement::Hgt(CurrentEnvironnement(E)) 
	 * 							&& ∄ (hole) in Holes(E) with GameObject::Wdt(hole) == x && GameObject::Hgt(hole) == y 
	 * 
	 * post: forall <h, t> in KeySet(Holes(E, x, y))
	 * 		if GameObject::Wdt(h) == x && GameObject::Hgt(h) == y then t == 0
	 * 
	 * post: forall <h, t> in KeySet(Holes(E, x, y))
	 * 		if GameObject::Wdt(h) != x && GameObject::Hgt(h) != y then t of Holes(AddHole(E, x, y)) == t of Holes(E)
	 * 
	 * post: Score(AddHole(E, x, y)) == Score(E)
	 *  
	 * post: CurrentNbTreasures(AddHole(E, x, y)) == CurrentNbTreasures(E)
	 * 
	 * post: CurrentLevel(AddHole(E, x, y)) == CurrentLevel(E)
	 * 
	 * post: CurrentEnvironnement(AddHole(E, x, y)) == CurrentEnvironnement(E)
	 * 
	 * post: CurrentPathResolver(AddHole(E, x, y)) == CurrentPathResolver(E)
	 * 
	 * post: Attack(AddHole(E, x, y)) == Attack(E)
	 * 
	 * post: NextCommand(AddHole(E, x, y)) == NextCommand(E)
	 * 
	 * post: Status(AddHole(E, x, y)) == Status(E)
	 *  
	 * post: NbStep(AddHole(E, x, y)) == NbStep(E)
	 * 
	 * post: Guards(AddHole(E, x, y)) == Guards(E)
	 * 
	 * post: Treasures(AddHole(E, x, y)) == Treasures(E)
	 */
	public void addHole(int x, int y);
	
	/**
	 * pre: Step(E) require Status(E) == PLAYING
	 * 
	 * post: forall (hole, I) in Holes(E) 
	 * 			if I == 15 then
	 *				Screen::CellNature(step(E), GameObject::Wdt(hole), GameObject::Hgt(hole)) = PLT
	 *				if Character::Wdt(Player(E)) == GameObject::Wdt(hole) && Character::Hgt(Player(E)) == GameObject::Hgt(hole) then 
	 *					Character::Wdt(Player(E)) == Character::InitialWdt(Player(E)) 
	 *					&& Character::Hgt(Player(E)) == Character::InitialHgt(Player(E))
	 *					&& Vie(Player(Step(E)) == Vie(Player(E)) - 1
	 *				forall g in Guards(Step(E))
	 *					if Character::Wdt(g) == GameObject::Wdt(hole) && Character::Hgt(g) == GameObject::Hgt(hole) then
	 *						Character::Wdt(g) = Character::InitialWdt(g)
	 *						&& Character::Hgt(g) = Character::initialHgt(g)
	 *			else
	 *				I(Step(E)) = I(E) + 1
	 *
	 * post: forall trsCurrent in Treasures(E)
	 * 			if(GameObject::Wdt(trsCurrent) == Character::Wdt(Player(E)) && GameObject::Hgt(trsCurrent) == Character::Hdt(Player(E)))
	 * 			then 
	 * 				Score(step(E)) = Score(E) + 1
	 * 				forall t2 in Treasures(step(E))
	 * 					(GameObject::Wgt(t2) == GameObject::Wgt(trsCurrent) && GameObject::Hgt(t2) == GameObject::Hgt(trsCurrent)) == false  
	 *
	 * post: nextCommand(step(E)) == NEUTRAL
	 *
	 * post: NbStep(Step(E)) == NbStep(E) + 1
	 *
	 * post: forall g in Guards(Step(E))
	 *			if Character::Wdt(g) == Character::Wdt(Player(Step(E))) && Character::Hgt(g) == Character::Hgt(Player(Step(E))) then
	 * 				Vie(Player(Step(E)) == Vie(Player(E)) - 1 
	 * 
	 * post: 	if CurrentLevel(Step(E)) < 3 
	 * 			&& Score(Step(E)) == CurrentNbTreasures(E)
	 * 			&& Character::Wdt(Player(Step(E))) == Environnement::WdtPortal(CurrentEnvironnement(Step(E)))
	 * 			&& Character::Hgt(Player(Step(E))) == Environnement::HgtPortal(CurrentEnvironnement(Step(E))) then 
	 * 				E = NextLevel(E)
	 * 
	 * post: if Vie(Player(Step(E))) == 0 then Status(Step(E)) = LOSS
	 * 
	 * post: 	if CurrentLevel(Step(E)) == 3
	 * 			&& Score(Step(E)) == NbTreasuresTotal(Step(E))
	 * 			&& Character::Wdt(Player(Step(E))) == Environnement::WdtPortal(CurrentEnvironnement(Step(E)))
	 * 			&& Character::Hgt(Player(Step(E))) == Environnement::HgtPortal(CurrentEnvironnement(Step(E))) then 
	 * 				Status(Step(E)) = WIN 
	 * 
	 * post: forall a in Attacks(E)
	 *		   if Attack::Direction(a) == LEFT then
	 *				if  GameObject::Wdt(a) - 1 < 0
	 *					|| Screen::CellNature(CurrentEnvironnement(E), GameObject::Wdt(a) - 1, GameObject::Hgt(a)) ∈ {PLT, MTL} then
	 *					List::Contains(Attacks(Step(E), a)) == false
	 *
	 *				else if Screen::CellNature(CurrentEnvironnement(E), GameObject::Wdt(a) - 1, GameObject::Hgt(a)) == EMP then
	 *					GameObject::Wdt(Attack::Step(a)) == GameObject::Wdt(a) - 1
	 *		   
	 *			else if Attack::Direction(a) == RIGHT then
	 *				if 	GameObject::Wdt(a) + 1 >= Screen::Width(CurrentEnvironnement(E))
	 *					|| Screen::CellNature(CurrentEnvironnement(E), GameObject::Wdt(a) + 1, GameObject::Hgt(a)) ∈ {PLT, MTL} then
	 *					List::Contains(Attacks(Step(E), a)) == false
	 *				else if Screen::CellNature(CurrentEnvironnement(E), GameObject::Wdt(a) + 1, GameObject::Hgt(a)) == EMP then
	 *					GameObject::Wdt(Attack::Step(a)) == GameObject::Wdt(a) + 1
	 *
	 * post: forall a in Attacks(E)
	 * 			if Attack::Direction(a) == LEFT then
	 * 				forall g in Guards(E)
	 * 					if Attack::Wdt(a)-1 == Character::Wdt(g) && Attack::Hgt(a) == Character::Hgt(g) then
	 * 						Character::Wdt(g) = Character::InitialWdt(g)
	 *						&& Character::Hgt(g) = Character::initialHgt(g)
	 *						&& List::Contains(Attacks(Step(E), a)) == false
	 *
	 *			else if Attack::Direction(a) == RIGHT then
	 *				if Attack::Wdt(a)+1 == Character::Wdt(g) && Attack::Hgt(a) == Character::Hgt(g) then
	 * 						Character::Wdt(g) = Character::InitialWdt(g)
	 *						&& Character::Hgt(g) = Character::initialHgt(g)
	 *						&& List::Contains(Attacks(Step(E), a)) == false
	 *
	 *post: forall a in Attacks(Step(E))
	 * 			forall g in Guards(Step(E))
	 * 				if Attack::Wdt(a) == Character::Wdt(g) && Attack::Hgt(a) == Character::Hgt(g) then
	 * 					Character::Wdt(g) = Character::InitialWdt(g)
	 *					&& Character::Hgt(g) = Character::initialHgt(g)
	 *					&& List::Contains(Attacks(Step(E), a)) == false
	 *
	 */
	public void step();
	
	/**
	 * pre: init(wdtScreen, hgtScreen, playerX, playerY) require 0 < playerX < wdtScreen && 0 < playerY < hgtScreen
	 *
	 * post: if Math::Min( Map::Keys( Levels( init(wdtScreen, hgtScreen, playerX, playerY) ) ) where Map::Get(Levels) == true ) == ∞ then
	 * 			Status(init(wdtScreen, hgtScreen, playerX, playerY)) = WIN
	 * 			&& CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) = ∞
	 * 		 else
	 * 			CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) == Math::Min( Map::Keys( Levels( init(wdtScreen, hgtScreen, playerX, playerY) ) ) where Map::Get(Levels) == true )
	 *  
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Status(init(wdtScreen, hgtScreen, playerX, playerY)) == PLAYING
	 * 
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			CurrentEnvironnement(init(wdtScreen, hgtScreen, playerX, playerY)) == Map::Get(Levels(init(wdtScreen, hgtScreen, playerX, playerY)), CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)))
	 * 
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Board(init(wdtScreen, hgtScreen, playerX, playerY)) != null
	 * 
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Player(init(wdtScreen, hgtScreen, playerX, playerY)) != null
	 * 
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			CurrentPathResolver(init(wdtScreen, hgtScreen, playerX, playerY)) == Map::Get(Levels(init(wdtScreen, hgtScreen, playerX, playerY)), CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)))
	 * 
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			forall g in Guards(init(wdtScreen, hgtScreen, playerX, playerY))
	 * 				Screen::CellNature(CurrentEnvironnement(init(wdtScreen, hgtScreen, playerX, playerY)), Character::Wdt(g), Character::Hgt(g)) == EMP
	 * 
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			forall t in Treasures(init(wdtScreen, hgtScreen, playerX, playerY))
 	 * 				Screen::CellNature(CurrentEnvironnement(init(wdtScreen, hgtScreen, playerX, playerY)), GameObject::Wdt(t), GameObject::Hgt(t)) == EMP
 	 * 				&& Screen::CellNature(GameObject::Wdt(t), GameObject::Hgt(t) - 1) ∈ {PLT, MTL}
 	 * 
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			forall g1 in Guards(init(wdtScreen, hgtScreen, playerX, playerY))
	 * 				forall g2 in Guards(init(wdtScreen, hgtScreen, playerX, playerY))
	 * 					Character::Wdt(g1) != Character::Wdt(g2) || Character::Hgt(g1) != Character::Hgt(g2)				
	 * 
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			forall t1 in Treasures(init(wdtScreen, hgtScreen, playerX, playerY))
	 * 				forall t2 in Treasures(init(wdtScreen, hgtScreen, playerX, playerY))
	 * 					GameObject::Wdt(t1) != GameObject::Wdt(t2) || GameObject::Hgt(t1) != GameObject::Hgt(t2)
	 * 
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			forall t in Treasures(init(wdtScreen, hgtScreen, playerX, playerY))
	 * 				if(GameObject::Wdt(t) == playerX && GameObject::Hgt(t) == playerY) then
	 * 					List::Remove(Treasures(init(wdtScreen, hgtScreen, playerX, playerY)), t)
	 * 
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			forall g in Guards(init(wdtScreen, hgtScreen, playerX, playerY))
	 * 				if Character::Wdt(g) == playerX && Character::Hgt(g) == playerY
	 * 				then
	 * 					Vie(Player(init(wdtScreen, hgtScreen, playerX, playerY))) == 3 - 1
	 * 
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			NbTreasuresTotal(init(wdtScreen, hgtScreen, playerX, playerY)) == Math::Sum(All environnement in Levels(init(wdtScreen, hgtScreen, playerX, playerY) where environnement is playable))
	 * 
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			CurrentNbTreasures(init(wdtScreen, hgtScreen, playerX, playerY)) == List::Size(Treasures(init(wdtScreen, hgtScreen, playerX, playerY)))
	 * 
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Score(init(wdtScreen, hgtScreen, playerX, playerY)) = 0
	 * 
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			NbStep(init(wdtScreen, hgtScreen, playerX, playerY)) = 0
	 * 
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			ScreenWdt(init(wdtScreen, hgtScreen, playerX, playerY)) = wdtScreen
	 * 
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			ScreenHgt(init(wdtScreen, hgtScreen, playerX, playerY)) = hgtScreen
	 * 
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Empty(Attack(init(wdtScreen, hgtScreen, playerX, playerY))) == true
	 * 
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Environnement::Wdt(Envi(init(wdtScreen, hgtScreen, playerX, playerY))) = wdtScreen
	 * 
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Environnement::Hgt(Envi(init(wdtScreen, hgtScreen, playerX, playerY))) = hgtScreen
	 * 
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Character::Wdt(Player(init(wdtScreen, hgtScreen, playerX, playerY))) = playerX
	 * 
	 * post: if CurrentLevel(init(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Character::Hgt(Player(init(wdtScreen, hgtScreen, playerX, playerY))) = playerY
	 * 
	 */
	public void init(int wdtScreen, int hgtScreen, int playerX, int playerY);
	
	/**
	 * pre: initWithContract(wdtScreen, hgtScreen, playerX, playerY) require 0 < playerX < wdtScreen && 0 < playerY < hgtScreen
	 *
	 * post: if Math::Min( Map::Keys( Levels( initWithContract(wdtScreen, hgtScreen, playerX, playerY) ) ) where Map::Get(Levels) == true ) == ∞ then
	 * 			Status(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) = WIN
	 * 			&& CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) = ∞
	 * 		 else
	 * 			CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) == Math::Min( Map::Keys( Levels( initWithContract(wdtScreen, hgtScreen, playerX, playerY) ) ) where Map::Get(Levels) == true )
	 *  
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Status(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) == PLAYING
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			CurrentEnvironnement(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) == Map::Get(Levels(initWithContract(wdtScreen, hgtScreen, playerX, playerY)), CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)))
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Board(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != null
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Player(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != null
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			CurrentPathResolver(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) == Map::Get(Levels(initWithContract(wdtScreen, hgtScreen, playerX, playerY)), CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)))
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			forall g in Guards(initWithContract(wdtScreen, hgtScreen, playerX, playerY))
	 * 				Screen::CellNature(CurrentEnvironnement(initWithContract(wdtScreen, hgtScreen, playerX, playerY)), Character::Wdt(g), Character::Hgt(g)) == EMP
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			forall t in Treasures(initWithContract(wdtScreen, hgtScreen, playerX, playerY))
 	 * 				Screen::CellNature(CurrentEnvironnement(initWithContract(wdtScreen, hgtScreen, playerX, playerY)), GameObject::Wdt(t), GameObject::Hgt(t)) == EMP
 	 * 				&& Screen::CellNature(GameObject::Wdt(t), GameObject::Hgt(t) - 1) ∈ {PLT, MTL}
 	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			forall g1 in Guards(initWithContract(wdtScreen, hgtScreen, playerX, playerY))
	 * 				forall g2 in Guards(initWithContract(wdtScreen, hgtScreen, playerX, playerY))
	 * 					Character::Wdt(g1) != Character::Wdt(g2) || Character::Hgt(g1) != Character::Hgt(g2)				
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			forall t1 in Treasures(initWithContract(wdtScreen, hgtScreen, playerX, playerY))
	 * 				forall t2 in Treasures(initWithContract(wdtScreen, hgtScreen, playerX, playerY))
	 * 					GameObject::Wdt(t1) != GameObject::Wdt(t2) || GameObject::Hgt(t1) != GameObject::Hgt(t2)
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			forall t in Treasures(initWithContract(wdtScreen, hgtScreen, playerX, playerY))
	 * 				if(GameObject::Wdt(t) == playerX && GameObject::Hgt(t) == playerY) then
	 * 					List::Remove(Treasures(initWithContract(wdtScreen, hgtScreen, playerX, playerY)), t)
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			forall g in Guards(initWithContract(wdtScreen, hgtScreen, playerX, playerY))
	 * 				if Character::Wdt(g) == playerX && Character::Hgt(g) == playerY
	 * 				then
	 * 					Vie(Player(initWithContract(wdtScreen, hgtScreen, playerX, playerY))) == 3 - 1
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			post: NbTreasuresTotal(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) == Math::Sum(All environnement in Levels(initWithContract(wdtScreen, hgtScreen, playerX, playerY) where environnement is playable))
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			CurrentNbTreasures(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) == List::Size(Treasures(initWithContract(wdtScreen, hgtScreen, playerX, playerY)))
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Score(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) = 0
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			NbStep(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) = 0
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			ScreenWdt(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) = wdtScreen
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			ScreenHgt(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) = hgtScreen
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Empty(Attack(initWithContract(wdtScreen, hgtScreen, playerX, playerY))) == true
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Environnement::Wdt(Envi(initWithContract(wdtScreen, hgtScreen, playerX, playerY))) = wdtScreen
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Environnement::Hgt(Envi(initWithContract(wdtScreen, hgtScreen, playerX, playerY))) = hgtScreen
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Character::Wdt(Player(initWithContract(wdtScreen, hgtScreen, playerX, playerY))) = playerX
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Character::Hgt(Player(initWithContract(wdtScreen, hgtScreen, playerX, playerY))) = playerY
	 * 
	 * On ajoute les post conditions sur les types des observateurs de engine
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Class(CurrentEnvironnement(initWithContract(wdtScreen, hgtScreen, playerX, playerY))) == Class(EnvironnementContract)
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Class(CurrentPathResolver(initWithContract(wdtScreen, hgtScreen, playerX, playerY))) == Class(PathResolverContract)
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Class(Player(initWithContract(wdtScreen, hgtScreen, playerX, playerY))) == Class(PlayerContract)
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			forall g in Guards(initWithContract(wdtScreen, hgtScreen, playerX, playerY))
	 * 				Class(g) == Class(GuardContract)
	 * 
	 * post: if CurrentLevel(initWithContract(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Class(Board(initWithContract(wdtScreen, hgtScreen, playerX, playerY))) == Class(BoardContract)
	 * 
	 * TODO Maintenir la liste à jour si on ajoute de nouveau types de contrat (Par exemple pour Board)
	 */
	public void initWithContract(int wdtScreen, int hgtScreen, int playerX, int playerY);
	
	/**
	 * pre: initForTests(wdtScreen, hgtScreen, playerX, playerY) require 0 < playerX < wdtScreen && 0 < playerY < hgtScreen
	 *
	 * post: if Math::Min( Map::Keys( Levels( initForTests(wdtScreen, hgtScreen, playerX, playerY) ) ) where Map::Get(Levels) == true ) == ∞ then
	 * 			Status(initForTests(wdtScreen, hgtScreen, playerX, playerY)) = WIN
	 * 			&& CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) = ∞
	 * 		 else
	 * 			CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) == Math::Min( Map::Keys( Levels( initForTests(wdtScreen, hgtScreen, playerX, playerY) ) ) where Map::Get(Levels) == true )
	 *  
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Status(initForTests(wdtScreen, hgtScreen, playerX, playerY)) == PLAYING
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			CurrentEnvironnement(initForTests(wdtScreen, hgtScreen, playerX, playerY)) == Map::Get(Levels(initForTests(wdtScreen, hgtScreen, playerX, playerY)), CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)))
	 * 
	 * DANS LES AUTRES INITS, BOARD DOIT ETRE INITIALISÉ, PAS ICI
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Board(initForTests(wdtScreen, hgtScreen, playerX, playerY)) == null
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Player(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != null
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			CurrentPathResolver(initForTests(wdtScreen, hgtScreen, playerX, playerY)) == Map::Get(Levels(initForTests(wdtScreen, hgtScreen, playerX, playerY)), CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)))
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			forall g in Guards(initForTests(wdtScreen, hgtScreen, playerX, playerY))
	 * 				Screen::CellNature(CurrentEnvironnement(initForTests(wdtScreen, hgtScreen, playerX, playerY)), Character::Wdt(g), Character::Hgt(g)) == EMP
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			forall t in Treasures(initForTests(wdtScreen, hgtScreen, playerX, playerY))
 	 * 				Screen::CellNature(CurrentEnvironnement(initForTests(wdtScreen, hgtScreen, playerX, playerY)), GameObject::Wdt(t), GameObject::Hgt(t)) == EMP
 	 * 				&& Screen::CellNature(GameObject::Wdt(t), GameObject::Hgt(t) - 1) ∈ {PLT, MTL}
 	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			forall g1 in Guards(initForTests(wdtScreen, hgtScreen, playerX, playerY))
	 * 				forall g2 in Guards(initForTests(wdtScreen, hgtScreen, playerX, playerY))
	 * 					Character::Wdt(g1) != Character::Wdt(g2) || Character::Hgt(g1) != Character::Hgt(g2)				
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			forall t1 in Treasures(initForTests(wdtScreen, hgtScreen, playerX, playerY))
	 * 				forall t2 in Treasures(initForTests(wdtScreen, hgtScreen, playerX, playerY))
	 * 					GameObject::Wdt(t1) != GameObject::Wdt(t2) || GameObject::Hgt(t1) != GameObject::Hgt(t2)
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			forall t in Treasures(initForTests(wdtScreen, hgtScreen, playerX, playerY))
	 * 				if(GameObject::Wdt(t) == playerX && GameObject::Hgt(t) == playerY) then
	 * 					List::Remove(Treasures(initForTests(wdtScreen, hgtScreen, playerX, playerY)), t)
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			forall g in Guards(initForTests(wdtScreen, hgtScreen, playerX, playerY))
	 * 				if Character::Wdt(g) == playerX && Character::Hgt(g) == playerY
	 * 				then
	 * 					Vie(Player(initForTests(wdtScreen, hgtScreen, playerX, playerY))) == 3 - 1
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			post: NbTreasuresTotal(initForTests(wdtScreen, hgtScreen, playerX, playerY)) == Math::Sum(All environnement in Levels(initForTests(wdtScreen, hgtScreen, playerX, playerY) where environnement is playable))
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			CurrentNbTreasures(initForTests(wdtScreen, hgtScreen, playerX, playerY)) == List::Size(Treasures(initForTests(wdtScreen, hgtScreen, playerX, playerY)))
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Score(initForTests(wdtScreen, hgtScreen, playerX, playerY)) = 0
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			NbStep(initForTests(wdtScreen, hgtScreen, playerX, playerY)) = 0
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			ScreenWdt(initForTests(wdtScreen, hgtScreen, playerX, playerY)) = wdtScreen
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			ScreenHgt(initForTests(wdtScreen, hgtScreen, playerX, playerY)) = hgtScreen
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Empty(Attack(initForTests(wdtScreen, hgtScreen, playerX, playerY))) == true
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Environnement::Wdt(CurrentEnvironnement(initForTests(wdtScreen, hgtScreen, playerX, playerY))) = wdtScreen
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Environnement::Hgt(CurrentEnvironnement(initForTests(wdtScreen, hgtScreen, playerX, playerY))) = hgtScreen
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Character::Wdt(Player(initForTests(wdtScreen, hgtScreen, playerX, playerY))) = playerX
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Character::Hgt(Player(initForTests(wdtScreen, hgtScreen, playerX, playerY))) = playerY
	 * 
	 * On ajoute les post conditions sur les types des observateurs de engine
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Class(CurrentEnvironnement(initForTests(wdtScreen, hgtScreen, playerX, playerY))) == Class(EnvironnementContract)
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Class(CurrentPathResolver(initForTests(wdtScreen, hgtScreen, playerX, playerY))) == Class(PathResolverContract)
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Class(Player(initForTests(wdtScreen, hgtScreen, playerX, playerY))) == Class(PlayerContract)
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			forall g in Guards(initForTests(wdtScreen, hgtScreen, playerX, playerY))
	 * 			Class(g) == Class(GuardContract)
	 * 
	 * post: if CurrentLevel(initForTests(wdtScreen, hgtScreen, playerX, playerY)) != ∞ then
	 * 			Class(Board(initForTests(wdtScreen, hgtScreen, playerX, playerY))) == Class(BoardContract)
	 * 
	 * TODO Maintenir la liste à jour si on ajoute de nouveau types de contrat (Par exemple pour Board)
	 */
	public void initForTests(int wdtScreen, int hgtScreen, int playerX, int playerY);
	
	/**
	 * pre: SetNextCommand(E, command) require command ∈ {LEFT, RIGHT, UP, DOWN, DIGL, DIGR, NEUTRAL}
	 * 
	 * post: NextCommand(SetNextCommand(E, command)) == command
	 * 
	 * post: Score(SetNextCommand(E, command)) == Score(E)
	 * 
	 * post: NbStep(SetNextCommand(E, command)) == NbStep(E)
	 * 
	 * post: Guards(SetNextCommand(E, command)) == Guards(E)
	 * 
	 * post: Treasures(SetNextCommand(E, command)) == Treasures(E)
	 * 
	 * post: CurrentEnvironnement(SetNextCommand(E, command)) == CurrentEnvironnement(E)
	 * 
	 * post: CurrentPathResolver(SetNextCommand(E, command)) == CurrentPathResolver(E)
	 * 
	 * post: Status(SetNextCommand(E, command)) == Status(E) 
	 * 
	 * post: Holes(SetNextCommand(E, command)) == Holes(E)
	 * 
	 * post: CurrentNbTreasures(SetNextCommand(E, command)) == CurrentNbTreasures(E)
	 * 
	 * post: CurrentLevel(SetNextCommand(E, command)) == CurrentLevel(E)
	 * 
	 * post: Attack(SetNextCommand(E, command)) == Attack(E)
	 */
	public void setNextCommand(Command command);
	
	/**
	 * pre: createEnvironnement(E, wdt, hgt) require wdt == ScreenWdt(E)  && hgt == ScreenHgt(E)
	 * 
	 * post: for nbNiveau in [0..3]
	 * 			if PathResolver::PlayerCanReachAllTreasuresAndPortal(Map::Get(PathResolvers(CreateEnvironnements(E, wdt, hgt)), nbNiveau),
	 * 													Treasures(Map::Get(Levels(CreateEnvironnements(E, wdt, hgt), nbNiveau))), 
	 * 													WdtPortal(Map::Get(Levels(CreateEnvironnements(E, wdt, hgt), nbNiveau))),
	 * 													HgtPortal(Map::Get(Levels(CreateEnvironnements(E, wdt, hgt), nbNiveau)))
	 * 													) 
	 * 			&& EditableScreen::Playable(Map::Get(Editors(CreateEnvironnements(E, wdt, hgt)), nbNiveau)) then
	 * 				Map::Get(PlayableLevels(CreateEnvironnements(E, wdt, hgt)), nbNiveau) = true
	 * 			else
	 * 				Map::Get(PlayableLevels(CreateEnvironnements(E, wdt, hgt)), nbNiveau) = false
	 * 			
	 * 			for line [List::Length(File::Lines(F))..1]
	 * 				for cell [0..List::Length(List::Get(File::Lines(F), line))]
	 * 					if Content(File, cell, line) == "EMP" then
	 * 						Environnement::CellNature(Map::Get(Levels(CreateEnvironnements(E, wdt, hgt)), nbNiveau), cell, line)  == "EMP"
	 * 						&& EditableScreen::CellNature(Map::Get(Editors(CreateEnvironnements(E, wdt, hgt)), nbNiveau), cell, line)  == "EMP"
	 * 					if Content(File, cell, line) == "HOL" then
	 * 						Environnement::CellNature(Map::Get(Levels(CreateEnvironnements(E, wdt, hgt)), nbNiveau), cell, line)  == "HOL"
	 * 						&& EditableScreen::CellNature(Map::Get(Editors(CreateEnvironnements(E, wdt, hgt)), nbNiveau), cell, line)  == "HOL"
	 * 					if  Content(File, cell, line) == "PLT" then
	 * 						Environnement::CellNature(Map::Get(Levels(CreateEnvironnements(E, wdt, hgt)), nbNiveau), cell, line) == "PLT"
	 * 						&& EditableScreen::CellNature(Map::Get(Editors(CreateEnvironnements(E, wdt, hgt)), nbNiveau), cell, line)  == "PLT"
	 * 					if  Content(File, cell, line) == "MTL" then
	 * 						Environnement::CellNature(Map::Get(Levels(CreateEnvironnements(E, wdt, hgt)), nbNiveau), cell, line) == "MTL"
	 * 						&& EditableScreen::CellNature(Map::Get(Editors(CreateEnvironnements(E, wdt, hgt)), nbNiveau), cell, line)  == "MTL"
	 * 					if  Content(File, cell, line) == "LAD" then
	 * 						Environnement::CellNature(Map::Get(Levels(CreateEnvironnements(E, wdt, hgt)), nbNiveau), cell, line) == "LAD"
	 * 						&& EditableScreen::CellNature(Map::Get(Editors(CreateEnvironnements(E, wdt, hgt)), nbNiveau), cell, line)  == "LAD"
	 * 					if  Content(File, cell, line) == "HDR" then
	 * 						Environnement::CellNature(Map::Get(Levels(CreateEnvironnements(E, wdt, hgt)), nbNiveau), cell, line) == "HDR"
	 * 						&& EditableScreen::CellNature(Map::Get(Editors(CreateEnvironnements(E, wdt, hgt)), nbNiveau), cell, line)  == "HDR"
	 * 					if  Content(File, cell, line) == "GRD" then
	 * 						Environnement::CellNature(Map::Get(Levels(CreateEnvironnements(E, wdt, hgt)), nbNiveau), cell, line) == "EMP"
	 * 						&& EditableScreen::CellNature(Map::Get(Editors(CreateEnvironnements(E, wdt, hgt)), nbNiveau), cell, line)  == "EMP"
	 * 						&& Environnement::CellContent(Map::Get(Levels(CreateEnvironnements(E, wdt, hgt)), nbNiveau), cell, line) == Character
	 * 						&& ∃ Character g in Guards(Map::Get(Levels(CreateEnvironnements(E, wdt, hgt)), nbNiveau))
	 * 					if  Content(File, cell, line) == "TRS" then
	 * 						Environnement::CellNature(Map::Get(Levels(CreateEnvironnements(E, wdt, hgt)), nbNiveau), cell, line) == "EMP"
	 * 						&& EditableScreen::CellNature(Map::Get(Editors(CreateEnvironnements(E, wdt, hgt)), nbNiveau), cell, line)  == "EMP"
	 * 						&& Environnement::CellContent(Map::Get(Levels(CreateEnvironnements(E, wdt, hgt)), nbNiveau), cell, line) == Item
	 * 						∃ Treasure t in Treasures(Map::Get(Levels(CreateEnvironnements(E, wdt, hgt)), nbNiveau))
	 * 					if  Content(File, cell, line) == "PRT" then
	 * 						Environnement::CellNature(Map::Get(Levels(CreateEnvironnements(E, wdt, hgt)), nbNiveau), cell, line) == "PRT"
	 * 						&& EditableScreen::CellNature(Map::Get(Editors(CreateEnvironnements(E, wdt, hgt)), nbNiveau), cell, line)  == "PRT"
	 * 						&& Environnement::WdtPortail(Map::Get(Levels(CreateEnvironnements(E, wdt, hgt)), nbNiveau)) == cell
	 * 						&& Environnement::HgtPortail(Map::Get(Levels(CreateEnvironnements(E, wdt, hgt)), nbNiveau)) == line
	 * 
	 * post: Environnement::Wdt(CurrentEnvironnement(CreateEnvironnements(E, wdt, hgt))) == wdt
	 * 
	 * post: Environnement::Hgt(CurrentEnvironnement(CreateEnvironnements(E, wdt, hgt))) == hgt
	 */
	public void createEnvironnements(int wdt, int hgt);
	
	/**
	 * pre: createEnvironnementWithContract(E, wdt, hgt) require wdt == ScreenWdt(E)  && hgt == ScreenHgt(E)
	 * 
	 * post: for nbNiveau in [0..3]
	 * 
	 * 			if PathResolver::PlayerCanReachAllTreasuresAndPortal(Map::Get(PathResolvers(createEnvironnementsWithContract(E, wdt, hgt)), nbNiveau),
	 * 													Treasures(Map::Get(Levels(createEnvironnementsWithContract(E, wdt, hgt), nbNiveau))), 
	 * 													WdtPortal(Map::Get(Levels(createEnvironnementsWithContract(E, wdt, hgt), nbNiveau))),
	 * 													HgtPortal(Map::Get(Levels(createEnvironnementsWithContract(E, wdt, hgt), nbNiveau)))
	 * 													)
	 * 			&& EditableScreen::Playable(Map::Get(Editors(CreateEnvironnements(E, wdt, hgt)), nbNiveau)) then
	 * 				Map::Get(PlayableLevels(createEnvironnementsWithContract(E, wdt, hgt)), nbNiveau) = true
	 * 			else
	 * 				Map::Get(PlayableLevels(createEnvironnementsWithContract(E, wdt, hgt)), nbNiveau) = false
	 * 
	 * 			for line [List::Length(File::Lines(F))..1]
	 * 				for cell [0..List::Lenght(List::Get(File::Lines(F), line))]
	 * 					Class(Map::Get(Levels(Environnement(createEnvironnementsWithContract(E, wdt, hgt)), nbNiveau))) == Class(EnvironnementContract)
	 * 					Class(Map::Get(Editors(Environnement(createEnvironnementsWithContract(E, wdt, hgt)), nbNiveau))) == Class(EditableScreenContract)
	 * 					Class(Map::Get(PathResolvers(Environnement(createEnvironnementsWithContract(E, wdt, hgt)), nbNiveau))) == Class(PathResolverContract)
	 * 					if Content(File, cell, line) == "EMP" then
	 * 						Environnement::CellNature(Map::Get(Levels(createEnvironnementsWithContract(E, wdt, hgt)), nbNiveau), cell, line)  == "EMP"
	 * 						&& EditableScreen::CellNature(Map::Get(Editors(createEnvironnementsWithContract(E, wdt, hgt)), nbNiveau), cell, line)  == "EMP"
	 * 					if Content(File, cell, line) == "HOL" then
	 * 						Environnement::CellNature(Map::Get(Levels(createEnvironnementsWithContract(E, wdt, hgt)), nbNiveau), cell, line)  == "HOL"
	 * 						&& EditableScreen::CellNature(Map::Get(Editors(createEnvironnementsWithContract(E, wdt, hgt)), nbNiveau), cell, line)  == "HOL"
	 * 					if  Content(File, cell, line) == "PLT" then
	 * 						Environnement::CellNature(Map::Get(Levels(createEnvironnementsWithContract(E, wdt, hgt)), nbNiveau), cell, line) == "PLT"
	 * 						&& EditableScreen::CellNature(Map::Get(Editors(createEnvironnementsWithContract(E, wdt, hgt)), nbNiveau), cell, line)  == "PLT"
	 * 					if  Content(File, cell, line) == "MTL" then
	 * 						Environnement::CellNature(Map::Get(Levels(createEnvironnementsWithContract(E, wdt, hgt)) , nbNiveau), cell, line) == "MTL"
	 * 						&& EditableScreen::CellNature(Map::Get(Editors(createEnvironnementsWithContract(E, wdt, hgt)), nbNiveau), cell, line)  == "MTL"
	 * 					if  Content(File, cell, line) == "LAD" then
	 * 						Environnement::CellNature(Map::Get(Levels(createEnvironnementsWithContract(E, wdt, hgt)) , nbNiveau), cell, line) == "LAD"
	 * 						&& EditableScreen::CellNature(Map::Get(Editors(createEnvironnementsWithContract(E, wdt, hgt)), nbNiveau), cell, line)  == "LAD"
	 * 					if  Content(File, cell, line) == "HDR" then
	 * 						Environnement::CellNature(Map::Get(Levels(createEnvironnementsWithContract(E, wdt, hgt)) , nbNiveau), cell, line) == "HDR"
	 * 						&& EditableScreen::CellNature(Map::Get(Editors(createEnvironnementsWithContract(E, wdt, hgt)), nbNiveau), cell, line)  == "HDR"
	 * 					if  Content(File, cell, line) == "GRD" then
	 * 						Environnement::CellNature(Map::Get(Levels(createEnvironnementsWithContract(E, wdt, hgt)) , nbNiveau), cell, line) == "EMP"
	 * 						&& EditableScreen::CellNature(Map::Get(Editors(createEnvironnementsWithContract(E, wdt, hgt)), nbNiveau), cell, line)  == "EMP"
	 * 						&& Environnement::CellContent(Map::Get(Levels(createEnvironnementsWithContract(E, wdt, hgt)) , nbNiveau), cell, line) == Character
	 * 						&& ∃ Character g in Guards(Map::Get(Levels(createEnvironnementsWithContract(E, wdt, hgt)) , nbNiveau))
	 * 						&& Class(g) == Class(GuardContract)
	 * 					if  Content(File, cell, line) == "TRS" then
	 * 						Environnement::CellNature(Map::Get(Levels(createEnvironnementsWithContract(E, wdt, hgt)) , nbNiveau), cell, line) == "EMP"
	 * 						&& EditableScreen::CellNature(Map::Get(Editors(createEnvironnementsWithContract(E, wdt, hgt)), nbNiveau), cell, line)  == "EMP"
	 * 						&& Environnement::CellContent(Map::Get(Levels(createEnvironnementsWithContract(E, wdt, hgt)) , nbNiveau), cell, line) == Item
	 * 						&& ∃ Treasure t in Treasures(Map::Get(Levels(createEnvironnementsWithContract(E, wdt, hgt)) , nbNiveau))
	 * 						&& Class(t) == Class(ItemContract)
	 * 					if  Content(File, cell, line) == "PRT" then
	 * 						Environnement::CellNature(Map::Get(Levels(createEnvironnementsWithContract(E, wdt, hgt)) , nbNiveau), cell, line) == "PRT"
	 * 						&& EditableScreen::CellNature(Map::Get(Editors(createEnvironnementsWithContract(E, wdt, hgt)), nbNiveau), cell, line)  == "PRT"
	 * 						&& Environnement::WdtPortail(Map::Get(Levels(createEnvironnementsWithContract(E, wdt, hgt)) , nbNiveau)) == cell
	 * 						&& Environnement::HgtPortail(Map::Get(Levels(createEnvironnementsWithContract(E, wdt, hgt)) , nbNiveau)) == line
	 * 			
	 * post: Environnement::Wdt(createEnvironnementsWithContract(E, wdt, hgt)) == wdt
	 * 
	 * post: Environnement::Hgt(createEnvironnementsWithContract(E, wdt, hgt)) == hgt
	 */
	public void createEnvironnementsWithContract(int wdt, int hgt);

	/**
	 * post: CurrentLevel(NextLevel(E)) == Math::Min( Map::Keys(Levels(NextLevel(E)))
	 * 																	where (Map::Get(PlayableLevels(NextLevel(E)), key) == true)
	 * 																	and (key > CurrentLevel(E))
	 * 												)
	 * post: if CurrentLevel(NextLevel(E)) == ∞ then
	 * 		Status(NextLevel(E)) = WIN
	 * 
	 * post: if CurrentLevel(NextLevel(E)) != ∞ then
	 * 			CurrentEnvironnement(nextLevel(E)) == Map::Get(Levels(E), CurrentLevel(NextLevel(E)))
	 * 
	 * post: if CurrentLevel(NextLevel(E)) != ∞ then
	 * 			CurrentPathResolver(nextLevel(E)) == Map::Get(PathResolvers(E), CurrentLevel(NextLevel(E)))
	 * 
	 * post: if CurrentLevel(NextLevel(E)) != ∞ then 
	 * 			Holes(nextLevel(E)) == Empty
	 * 
	 * post: if CurrentLevel(NextLevel(E)) != ∞ then
	 * 			Environnement(Board(nextLevel(E))) == Environnement(nextLevel(E))
	 * 
	 * post: if CurrentLevel(NextLevel(E)) != ∞ then
	 * 			Treasures(nextLevel(E)) == Treasures(Environnement(nextLevel(E)))
	 * 
	 * post: if CurrentLevel(NextLevel(E)) != ∞ then
	 * 			Guards(nextLevel(E)) == Guards(Environnement(nextLevel(E)))
	 * 
	 * post: if CurrentLevel(NextLevel(E)) != ∞ then
	 * 			CurrentNbTreasures(nextLevel(E)) == List::Size(Treasures(nextLevel(E))) + CurrentNbTreasures(E)
	 * 
	 * post: if CurrentLevel(NextLevel(E)) != ∞ then
	 * 			Character::Wdt(Player(NextLevel(E))) == Environnement::WdtPortal(CurrentEnvironnement(nextLevel(E)))
	 * 			&& Character::Hgt(Player(NextLevel(E))) == Environnement::HgtPortal(CurrentEnvironnement(nextLevel(E)))  
	 * 
	 * post: Score(nextLevel(E)) == Score(E)
	
	 * post: NbStep(nextLevel(E)) == NbStep(E)
	 * 
	 * post: if CurrentLevel(NextLevel(E)) != ∞ then 
	 * 			Attack(nextLevel(E)) == Empty
	 * 
	 * post: if CurrentLevel(NextLevel(E)) != ∞ then
	 * 			Player::Vie(Player(NextLevel(E))) = 3
	 * 
	 * post: if CurrentLevel(NextLevel(E)) != ∞ then
	 * 			Player::NbAttacks(Player(NextLevel(E))) = 3
	 */
	public void nextLevel();
	
	/**
	 * pre : AddAttack(E, x, y, direction) require
	 * 				0 <= x < Environnnement::Wdt(CurrentEnvironnement(E)) 
	 * 				&& 0 <= y < Environnnement::Hgt(CurrentEnvironnement(E))
	 * 				&& ∄ attack in Attack(E) with 
	 * 					GameObject::Wdt(attack) == x
	 * 					&& GameObject::Hgt(attack) == y
	 * 					&& Direction(attack) == direction
	 * 
	 * post: NextCommand(AddAttack(E, x, y, direction)) == command
	 * 
	 * post: Score(AddAttack(E, x, y, direction)) == Score(E)
	 * 
	 * post: NbStep(AddAttack(E, x, y, direction)) == NbStep(E)
	 * 
	 * post: Treasures(AddAttack(E, x, y, direction)) == Treasures(E)
	 * 
	 * post: Guards(AddAttack(E, x, y, direction)) == Guards(E)
	 * 
	 * post: CurrentEnvironnement(AddAttack(E, x, y, direction)) == CurrentEnvironnement(E)
	 * 
	 * post: CurrentPathResolver(AddAttack(E, x, y, direction)) == CurrentPathResolver(E)
	 * 
	 * post: Status(AddAttack(E, x, y, direction)) == Status(E) 
	 * 
	 * post: Holes(AddAttack(E, x, y, direction)) == Holes(E)
	 * 
	 * post: CurrentNbTreasures(AddAttack(E, x, y, direction)) == CurrentNbTreasures(E)
	 * 
	 * post: CurrentLevel(AddAttack(E, x, y, direction)) == CurrentLevel(E)
	 * 
	 * post: if NbAttacks(Player(E)) > 0 then 
	 * 			∃ attack in Attack(AddAttack(E, x, y, direction)) with
	 * 				 Wdt(attack) == x && Hgt(attack) == y && Screen(attack) == Environnement(attack) && Direction(attack) == direction
	 * 			NbAttacks(Player(AddAttack(E, x, y, direction))) == NbAttacks(Player(E)) - 1
	 */
	public void addAttack(int x, int y, Direction direction);
}
