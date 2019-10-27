package services;

import data.Move;

public interface Guard extends /* include */ Character{
	///////////////////////////////
	///////// OBSERVATORS /////////
	///////////////////////////////
	
	// CONST
	public int getId();
	
	// CONST
	public PathResolver getResolver();
	
	public Move getBehaviour();
	
	public int getTimeInHole();
	
	
	///////////////////////////////
	//////// CONSTRUCTORS /////////
	///////////////////////////////
	/** 
	 * post: Id(init(e, x, y, id, resolver)) = id
	 * 
	 * post: Resolver(init(e, x, y, id, resolver)) = resolver
	 * 
	 * post: TimeInHole(init(e, x, y, id, resolver)) = 0
	 */
	public void init(Screen e, int x, int y, int id, PathResolver resolver);
	
	
	///////////////////////////////
	////////// OPERATORS //////////
	///////////////////////////////
	/**
	 * pre: ClimbLeft(G) requires Screen::CellNature(Character::Envi(G), Character::Wdt(G), Character::Hgt(G)) == HOL
	 * 
	 * post: if Character::Wdt(G) == 0 then 
	 * 			Character::Wdt(ClimbLeft(G)) == Character::Wdt(G) && Character::Hgt(ClimbLeft(G)) == Character::Hgt(G)
	 * 
	 * post: if (Screen::CellNature(Character::Envi(G), Character::Wgt(G)-1, Character::Hgt(G)+1) ∈ {MTL, PLT}) then
	 * 			Character::Wdt(ClimbLeft(G)) == Character::Wgt(G) && Character::Hgt(ClimbLeft(G)) == Character::Hgt(G)
	 * 
	 * post: if Environnement::CellContent(Character::Envi(G), Character::Wgt(G)-1, Character::Hgt(G)+1) == Character c then
	 * 			Character::Wdt(ClimbLeft(G)) == Character::Wgt(G) && Character::Hgt(ClimbLeft(G)) == Character::Hgt(G)
	 * 
	 * post: if Character::Wdt(G) != 0 
	 * 			&& Screen::CellNature(Character::Envi(G), Character::Wdt(G)-1, Character::Hgt(G)+1) ∉ {PLT, MTL}
	 * 			&& Environnement::CellContent(Character::Envi(G), Character::Wdt(G)-1, Character::Hgt(G)+1) != Character c
	 * 		 then
	 * 			Character::Wdt(ClimbLeft(G)) == Character::Wdt(G)-1 && Character::Hgt(ClimbLeft(G)) == Character::Hgt(G)+1
	 */
	public void climbLeft();
	
	/**
	 * pre: ClimbRight(G) requires Screen::CellNature(Character::Envi(G), Character::Wdt(G), Character::Hgt(G)) == HOL
	 * 
	 * post: if Character::Wdt(G) == (Screen::Wdt(Character::Envi(G)) - 1) then 
	 * 			Character::Wdt(ClimbRight(G)) == Character::Wdt(G) && Character::Hgt(ClimbRight(G)) == Character::Hgt(G)
	 * 
	 * post: if (Screen::CellNature(Character::Envi(G), Character::Wgt(G)+1, Character::Hgt(G)+1) ∈ {MTL, PLT}) then
	 * 			Character::Wdt(ClimbRight(G)) == Character::Wgt(G) && Character::Hgt(ClimbRight(G)) == Character::Hgt(G)
	 * 
	 * post: if Environnement::CellContent(Character::Envi(G), Character::Wgt(G)+1, Character::Hgt(G)+1) == Character c then
	 * 			Character::Wdt(ClimbRight(G)) == Character::Wgt(G) && Character::Hgt(ClimbRight(G)) == Character::Hgt(G)
	 * 
	 * post: if Character::Wdt(G) != (Screen::Wdt(Character::Envi(G)) - 1) 
	 * 			&& Screen::CellNature(Character::Envi(G), Character::Wdt(G)+1, Character::Hgt(G)+1) ∉ {PLT, MTL}
	 * 			&& Environnement::CellContent(Character::Envi(G), Character::Wdt(G)+1, Character::Hgt(G)+1) != Character c
	 * 		 then
	 * 			Character::Wdt(ClimbRight(G)) == Character::Wdt(G)+1 && Character::Hgt(ClimbRight(G)) == Character::Hgt(G)+1
	 */
	public void climbRight();
	
	/**
	 * post: WillFall(G) defined by 
	 * 			if Screen::CellNature(Character::Envi(G), Character::Wdt(G), Character::Hgt(G)) ∉ {HDR, LAD, HOL}
	 * 				&& Screen::CellNature(Character::Envi(G), Character::Wdt(G), Character::Hgt(G)-1) ∈ {EMP, HOL, HDR}  
	 * 				&& Environnement::CellContent(Character::Envi(G), Character::Wdt(G), Character::Hgt(G)-1) != Character c
	 * 			then 
	 * 				Step(G) = GoDown(G)
	 * 				&& LastAction(Step(P)) == DOWN
	 *
	 * post: if Screen::CellNature(Character::Envi(G), Character::Wdt(G), Character::Hgt(G)) == HOL
	 * 			&& TimeInHole(G) < 2
	 * 		 then
	 * 			TimeInHole(Step(G)) = TimeInHole(G) + 1
	 *
	 * post: WillClimbLeft(G) defined by
	 * 			if Screen::CellNature(Character::Envi(G), Character::Wdt(G), Character::Hgt(G)) == HOL
	 * 				&& TimeInHole(G) == 2
	 * 				&& Behaviour(G) == LEFT
	 * 		 	then
	 * 				Step(G) = GoLeft(G)
	 * 				&& LastAction(Step(P)) == LEFT
	 * 				&& TimeInHole(Step(G)) == 0
	 *
	 * post: WillClimbRight(G) defined by
	 * 			if Screen::CellNature(Character::Envi(G), Character::Wdt(G), Character::Hgt(G)) == HOL
	 * 				&& TimeInHole(G) == 2
	 * 				&& Behaviour(G) == RIGHT
	 * 		 	then
	 * 				Step(G) = GoRight(G)
	 *				&& LastAction(Step(P)) == RIGHT
	 *				&& TimeInHole(Step(G)) == 0
	 *
	 * post: WillNotMove(G) defined by
	 * 			if Screen::CellNature(Character::Envi(G), Character::Wdt(G), Character::Hgt(G)) == HOL
	 * 				&& TimeInHole(G) == 2
	 * 				&& Behaviour(G) == NEUTRAL
	 * 		 	then
	 * 				Behaviour(Step(G)) == Behaviour(G)
	 * 				&& TimeInHole(Step(G)) == 0
	 * 
	 * post: WillGoLeft(G) defined by
	 * 			if	Screen::CellNature(Character::Envi(G), Character::Wdt(G), Character::Hgt(G)) != HOL
	 * 				&& Behaviour(G) == LEFT
	 * 			then
	 * 				Step(G) = GoLeft(G)
	 * 				&& LastAction(Step(P)) == LEFT
	 *
	 * post: WillGoRight(G) defined by
	 * 			if  Screen::CellNature(Character::Envi(G), Character::Wdt(G), Character::Hgt(G)) != HOL  
	 * 				&& Behaviour(G) == RIGHT
	 * 			then
	 * 				Step(G) = GoRight(G)
	 * 				&& LastAction(Step(P)) == RIGHT
	 * 
	 */
	public void step();
}
