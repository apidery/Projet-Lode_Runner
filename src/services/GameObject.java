package services;

public interface GameObject {
	///////////////////////////////
	///////// OBSERVATORS /////////
	///////////////////////////////
	public int getHgt();
	
	public int getWdt();
	
	//CONST
	public Screen getScreen();
	
	///////////////////////////////
	//////// CONSTRUCTORS /////////
	///////////////////////////////
		
	/**
	 * 
	 * pre: init(e, x, y) require 0 < x < Screen::Wdt(e) && 0 < y < Screen::Hgt(E)
	 * 
	 * post: Wdt(init(e, x, y)) == x
	 * 
	 * post: Hgt(init(e, x, y)) == y
	 * 
	 * post: Screen(init(e, x, y)) == e
	 */
	public void init(Screen e, int x, int y);
}
