package services;

public interface Player extends /* include */ Character {
	///////////////////////////////
	///////// OBSERVATORS /////////
	///////////////////////////////
	// CONST
	public Engine getEngine();
	
	public int getNbAttacks();
	
	public int getVie();
	
	///////////////////////////////
	//////// CONSTRUCTORS /////////
	///////////////////////////////
	/**
	 * post: Engine(init(e,x,y,engine)) == engine
	 *  
	 * post: Vie(init(e,x,y,engine)) = 3
	 * 
	 * post: NbAttack(init(e,x,y,engine)) = 3
	 */
	public void init(Screen e, int x, int y, Engine engine);

	
	///////////////////////////////
	////////// OPERATORS //////////
	///////////////////////////////
	/**
	 * post: if Environnement::CellNature(Envi(P), Wdt(P), Hgt(P)) ∉ {HDR, LAD, HOL}
	 * 			&& Environnement::CellNature(Envi(P), Wdt(P), Hgt(P) - 1) ∈ {EMP, HOL, HDR}
	 * 			&& Environnement::CellContent(Envi(P), Wdt(P), Hgt(P) - 1) != Character c
	 * 		then
	 * 			Step(G) = GoDown(G)
	 * 			&& LastAction(Step(P)) == DOWN
	 * 		else
	 * 			if Engine::NextCommand(P) == LEFT
	 * 				Step(G) = GoLeft(G)
	 * 				&& LastAction(Step(P)) == LEFT
	 * 			else if Engine::NextCommand(P) == RIGHT
	 * 				Step(G) = GoRight(G)
	 * 				&& LastAction(Step(P)) == RIGHT
	 * 			else if Engine::NextCommand(P) == UP
	 * 				Step(G) = GoUp(G)
	 * 	 			&& LastAction(Step(P)) == UP
	 * 			else if Engine::NextCommand(P) == DOWN
	 * 				Step(G) = GoDown(G)
	 * 				&& LastAction(Step(P)) == DOWN
	 * 
	 * post: WillDigLeft(P) defined by
	 * 			if (Engine::NextCommand(P) == DigL
	 * 					&& (Environnement::CellNature(Envi(P), Wdt(P), Hgt(P) - 1) ∈ {PLT, MTL, LAD}
	 * 						|| Environnement::CellContent(Envi(P), Wdt(P), Hgt(P) - 1) = Character c))
	 * 				&& (Environnement::CellNature(Envi(P), Wdt(P) - 1, Hgt(P)) == EMP 
	 * 						&& Environnement::CellContent(Envi(P), Wdt(P) - 1, Hgt(P)) != Character c
	 * 						&& Environnement::CellContent(Envi(P), Wdt(P) - 1, Hgt(P)) != Treasure t)
	 * 				&& Environnement::CellNature(Envi(P), Wdt(P) - 1 , Hgt(P) - 1) == PLT
	 * 		 	then
	 * 				Environnement::dig(Envi(P), Wdt(P) - 1, Hgt(P) - 1)
	 * 					&& Engine::addHole(Wdt(P) - 1, Hgt(P) - 1)
	 * 					&& LastAction(Step(P)) == DigL
	 *
	 * post: WillDigRight(P) defined by
	 * 			if (Engine::NextCommand(P) == DigR
	 * 					&& (Environnement::CellNature(Envi(P), Wdt(P), Hgt(P) - 1) ∈ {PLT, MTL, LAD}
	 * 						|| Environnement::CellContent(Envi(P), Wdt(P), Hgt(P) - 1) = Character c))
	 * 				&& (Environnement::CellNature(Envi(P), Wdt(P) + 1, Hgt(P)) == EMP 
	 * 						&& Environnement::CellContent(Envi(P), Wdt(P) + 1, Hgt(P)) != Character c
	 * 						&& Environnement::CellContent(Envi(P), Wdt(P) + 1, Hgt(P)) != Treasure t)
	 * 				&& Environnement::CellNature(Envi(P), Wdt(P) + 1 , Hgt(P) - 1) == PLT
	 * 		 	then
	 * 				Environnement::dig(Envi(P), Wdt(P) + 1, Hgt(P) - 1)
	 * 					&& Engine::addHole(Wdt(P) + 1, Hgt(P) - 1)
	 * 	 				&& LastAction(Step(P)) == DigR
	 *
	 */
	public void step();
	
	/**
	 * pre: DecreaseVie(E) require Vie(E) > 0
	 * 
	 * post: Vie(DecreaseVie(E)) == Vie(E) - 1
	 * 
	 * post: Wdt(DecreaseVie(E)) == Wdt(E)
	 * 
	 * post: Hgt(DecreaseVie(E)) == Hgt(E)
	 * 
	 * post: NbAttack(DecreaseVie(E)) == NbAttack(E)
	 * 
	 * post: Screen(DecreaseVie(E)) == Screen(E)
	 * 
	 * post: Engine(DecreaseVie(E)) == Engine(E)
	 */
	public void decreaseVie();
	
	/**
	 * pre: DecreaseNbAttacks(E) require NbAttack(E) > 0
	 * 
	 * post: Vie(DecreaseNbAttacks(E)) == Vie(E)
	 * 
	 * post: NbAttack(DecreaseNbAttacks(E)) == NbAttack(E) - 1
	 * 
	 * post: Wdt(DecreaseNbAttacks(E)) == Wdt(E)
	 * 
	 * post: Hgt(DecreaseNbAttacks(E)) == Hgt(E)
	 * 
	 * post: Screen(DecreaseNbAttacks(E)) == Screen(E)
	 * 
	 * post: Engine(DecreaseNbAttacks(E)) == Engine(E) 
	 */
	public void decreaseNbAttacks();
}
