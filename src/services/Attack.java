package services;

import data.Direction;

public interface Attack extends /* include */ GameObject{

	///////////////////////////////
	///////// OBSERVATORS /////////
	///////////////////////////////
	// CONST
	public Direction getDirection();
	
	///////////////////////////////
	//////// CONSTRUCTORS /////////
	///////////////////////////////
	/**
	 * pre: init(e, x, y, direction) require direction ∈ {LEFT, RIGHT}
	 * 
	 * post: Direction(init(e, x, y, direction)) = direction
	 */
	public void init(Screen e, int x, int y, Direction direction); 
	
	///////////////////////////////
	//////// OPERATORS ////////////
	///////////////////////////////
	/**
	 * pre: Step(A) require 0 <= GameObject::wdt(A) < Screen::Wdt(Screen(A))
	 *  
	 * post: Hgt(Step(A)) == Hgt(A)
	 * 
	 * post: if Direction(A) == LEFT then
	 * 			Wdt(Step(A)) == Wdt(A) - 1
	 * 		else if (Direction(A)) == RIGHT then 
	 * 			Wdt(Step(A)) == Wdt(A) + 1
	 */
	public void step();
	
}
