package services;

import data.Command;

/**
 * inv: Screen::CellNature(getEnvi(), getWdt(), getHgt()) ∈ {EMP, HOL, LAD, HDR}
 */
public interface Character {
	///////////////////////////////
	///////// OBSERVATORS /////////
	///////////////////////////////
	// CONST
	public int getInitialWdt();
	
	// CONST
	public int getInitialHgt();
	
	// CONST
	public Environnement getEnvi();
	
	public int getHgt();
	
	public int getWdt();
	
	public Command getLastAction();
	
	
	///////////////////////////////
	//////// CONSTRUCTORS /////////
	///////////////////////////////
	/**
	 * pre: init(S,x,y) require Screen::cellNature(S,x,y) = EMP 
	 * 
	 * post: Hgt(init(e,x,y)) = y
	 * 
	 * post: Wdt(init(e,x,y)) = x
	 * 
	 * post: Envi(init(e,x,y)) = e
	 * 
	 * post: InitialWdt(init(e, x, y)) = x
	 * 
	 * post: InitialHgt(init(e, x, y)) = y
	 */
	public void init(Screen e, int x, int y);
	
	
	///////////////////////////////
	////////// OPERATORS //////////
	///////////////////////////////
	/**
	 * post : LastAction(GoLeft(C)) == LEFT
	 *  
	 * post: if Wdt(C) == 0
	 * 		then
	 * 			Wdt(GoLeft(C)) == Wdt(C)
	 * 			&& Hgt(GoLeft(C)) == Hgt(C)
	 * 
	 * post: if Screen::CellNature(Envi(C), Wdt(C) - 1, Hgt(C)) ∈ {MTL, PLT}
	 * 			&& Screen::CellNature(Envi(C), Wdt(C), Hgt(C)) != HDR
	 * 		then
	 * 			Wdt(GoLeft(C)) == Wdt(C)
	 * 			&& Hgt(GoLeft(C)) == Hgt(C)
	 * 
	 * post: if Screen::CellNature(Envi(C), Wdt(C), Hgt(C)) ∉ {LAD, HDR} 
	 * 			&& Screen::CellNature(Envi(C), Wdt(C), Hgt(C)-1) ∉ {PLT, MLT} 
	 * 			&& Environnement::CellContent(Envi(C), Wdt(C), Hgt(C)-1) != Character otherCaract
	 * 		then
	 * 			Wdt(GoLeft(C)) == Wdt(C)
	 * 			&& Hgt(GoLeft(C)) == Hgt(C)
	 * 
	 * post: if (Environnement::CellContent(Envi(C), Wdt(C)-1, Hgt(C) == Character otherCaract))
	 * 		then
	 * 			Wdt(GoLeft(C)) == Wdt(C)
	 * 			&& Hgt(GoLeft(C)) == Hgt(C)
	 * 
	 * post: if (Wdt(C) != 0) && (Screen::CellNature(Envi(C), Wdt(C)-1, Hgt(C)) ∉ {MTL,PLT}
	 * 			&& (Screen::CellNature(Envi(C), Wdt(C), Hgt(C)) ∈ {LAD,HDR,HOL}
	 * 				|| Screen::CellNature(Envi(C), Wdt(C), Hgt(C)-1) ∈ {PLT,MTL,LAD}
	 * 				|| Environnement::CellContent(Envi(C), Wdt(C), Hgt(C)-1) == Character otherCaract)
	 * 			&& !((Environnement::CellContent(Envi(C), Wdt(C)-1, Hgt(C)) == Character otherCaract))
	 * 		then 
	 * 			Wdt(GoLeft(C)) == Wdt(C) - 1
	 * 
	 * post : if(Screen::CellNature(Envi(C), Wdt(C), Hgt(C)) ∉ {HDR,LAD,HOL}
	 * 			&& Screen::CellNature(Envi(C), Wdt(C) - 1, Hgt(C) - 1) == HDR)
	 * 		then
	 * 			Hgt(GoLeft(C)) = Hgt - 1
	 * 
	 * post: if(Screen::CellNature(Envi(C), Wdt(C), Hgt(C)) ∈ {HDR,HOL}
	 * 			&& Screen::CellNature(Envi(C), Wdt(C) - 1, Hgt(C)) ∈ {PLT,MTL}
	 * 			&& Screen::CellNature(Envi(C), Wdt(C) - 1, Hgt(C) + 1) ∈ {EMP, LAD, HDR, HOL})
	 * 			&& Environnement::CellContent(Envi(C), Wdt(C) - 1, Hgt(C) + 1) != Character otherCaract) 
	 * 		 then
	 * 			Hgt(GoLeft(C)) = Hgt + 1
	 * 			&& Wdt(GoLeft(C)) == Wdt(C) - 1
	 */
	public void goLeft();
	
	/**
	 * post : LastAction(GoRight(C)) == RIGHT
	 *  
	 * post: if Wdt(C) == Screen::Width(Envi(C)) - 1 
	 * 		then
	 * 			Wdt(GoRight(C)) == Wdt(C)
	 * 			&& Hgt(GoRight(C)) == Hgt(C)
	 * 
	 * post: if Screen::CellNature(Envi(C), Wdt(C) + 1, Hgt(C)) ∈ {MTL, PLT}
	 * 			&& Screen::CellNature(Envi(C), Wdt(C), Hgt(C)) != HDR
	 * 		then
	 * 			Wdt(GoRight(C)) == Wdt(C)
	 * 			&& Hgt(GoRight(C)) == Hgt(C)
	 * 
	 * post: if Screen::CellNature(Envi(C), Wdt(C), Hgt(C)) ∉ {LAD, HDR} 
	 * 			&& Screen::CellNature(Envi(C), Wdt(C), Hgt(C)-1) ∉ {PLT, MLT} 
	 * 			&& Environnement::CellContent(Envi(C), Wdt(C), Hgt(C)-1) != Character otherCaract
	 * 		then
	 * 			Wdt(GoRight(C)) == Wdt(C)
	 * 			&& Hgt(GoRight(C)) == Hgt(C)
	 * 
	 * post: if (Environnement::CellContent(Envi(C), Wdt(C)+1, Hgt(C) == Character otherCaract))
	 * 		then
	 * 			Wdt(GoRight(C)) == Wdt(C)
	 * 			&& Hgt(GoRight(C)) == Hgt(C) 
	 * 
	 * post: if((Wdt(C) != Screen::Width(Envi(C) - 1)) && (Screen::CellNature(Envi(C), Wdt(C)+1, Hgt(C)) ∉ {MTL,PLT} )
	 * 			&& ((Screen::CellNature(Envi(C), Wdt(C), Hgt(C)) ∈ {LAD,HDR,HOL})
	 * 			|| ((Screen::CellNature(Envi(C), Wdt(C), Hgt(C)-1) ∈ {PLT,MLT,LAD}))
	 * 			|| ((Environnement::CellContent(Envi(C), Wdt(C), Hgt(C)-1) == Character otherCaract)))
	 * 			&& !((Environnement::CellContent(Envi(C), Wdt(C) + 1, Hgt(C)) == Character otherCaract))
	 * 		then 
	 * 			Wdt(GoRight(C)) == Wdt(C) + 1
	 * 
	 * post : if(Screen::CellNature(Envi(C), Wdt(C), Hgt(C)) ∉ {HDR,LAD,HOL}
	 * 			&& Screen::CellNature(Envi(C), Wdt(C) + 1, Hgt(C) - 1) == HDR)
	 * 			then
	 * 				Hgt(GoRight(C)) = Hgt - 1
	 * 			 
	 * post: if(Screen::CellNature(Envi(C), Wdt(C), Hgt(C)) ∈ {HDR,HOL}
	 * 			&& Screen::CellNature(Envi(C), Wdt(C) + 1, Hgt(C)) ∈ {PLT,MTL}
	 * 			&& Screen::CellNature(Envi(C), Wdt(C) + 1, Hgt(C) + 1) ∈ {EMP, LAD, HDR, HOL})
	 * 			&& Environnement::CellContent(Envi(C), Wdt(C) + 1, Hgt(C) + 1) != Character otherCaract) 
	 * 		 then
	 * 			Hgt(GoRight(C)) = Hgt + 1
	 * 			&& Wdt(GoRight(C)) == Wdt(C) + 1
	 */
	public void goRight();
	
	/**
	 * post : LastAction(GoUp(C)) == UP
	 * 
	 * post: Wdt(GoUp(C)) == Wdt(C)
	 * 
	 * post: if Hgt(C) == Screen::Height(Envi(C)) - 1 then Hgt(GoUp(C)) == Hgt(C)
	 * 
	 * post: if Environnement::CellContent(Envi(C), Wdt(C), Hgt(C) + 1) == Character c then Hgt(GoUp(C)) == Hgt(C)
	 * 
	 * post: if (Screen::CellNature(Envi(C), Wdt(C), Hgt(C)) == LAD)
	 * 			&& (Screen::CellNature(Envi(C), Wdt(C), Hgt(C) + 1) == PLT)
	 * 		then 
	 * 			Hgt(GoUp(C)) == Hgt(C)
	 * 
	 * post: if (Screen::CellNature(Envi(C), Wdt(C), Hgt(C)) == LAD) 
	 * 			&& (Screen::CellNature(Envi(C), Wdt(C), Hgt(C) + 1) ∈ {LAD, EMP})
	 * 			&& (Environnement::CellContent(Envi(C), Wdt(C), Hgt(C) + 1) != Character c)
	 * 		then 
	 * 			Hgt(GoUp(C)) == Hgt(C) + 1 
	 */
	public void goUp();
	
	/**
	 * post : LastAction(GoDown(C)) == DOWN
	 * 
	 * post: Wdt(GoDown(C)) == Wdt(C)
	 * 
	 * post: if Hgt(C) == 1 then Hgt(GoDown(C)) == Hgt(C)
	 * 
	 * post: if Environnement::CellContent(Envi(C), Wdt(C), Hgt(C) - 1) == Character c then Hgt(GoDown(C)) == Hgt(C)
	 * 
	 * post: if (Screen::CellNature(Envi(C), Wdt(C), Hgt(C)) == LAD)
	 * 			&& (Screen::CellNature(Envi(C), Wdt(C), Hgt(C) - 1) == PLT)
	 * 		then 
	 * 			Hgt(GoDown(C)) == Hgt(C)
	 * 
	 * post: if (Screen::CellNature(Envi(C), Wdt(C), Hgt(C)) ∈ {LAD, EMP, HDR}) 
	 * 			&& (Screen::CellNature(Envi(C), Wdt(C), Hgt(C) - 1) ∈ {LAD, EMP, HDR, HOL})
	 * 			&& (Environnement::CellContent(Envi(C), Wdt(C), Hgt(C) - 1) != Character c)
	 * 		then 
	 * 			Hgt(GoDown(C)) == Hgt(C) - 1 
	 */
	public void goDown();
	
	/** 
	 * post: Wdt(BackInitialPosition(C)) == initialWdt(C) && Hgt(BackInitialPosition(C)) == initialHgt(C)
	 */
	public void backInitialPosition();
}
