package services;


import java.util.List;

import data.Cell;
import data.Couple;
import data.Item;

/**
 * inv: for x [0..Width(E)]
 * 			for y [0..Height(E)]
 * 				for all Character c1, c2 in CellContent(E,x,y), c1 = c2  
 * 
 * inv: for x [0..Width(E)]
 * 			for y [0..Height(E)]
 * 				if CellNature(E,x,y) ∈ {MTL,PLT} then
 * 					CellContent(E,x,y) == null
 * 
 * inv: for x [0..Width(E)]
 * 			for y [0..Height(E)]
 * 				if CellContent(E,x,y) == Treasure t then
 * 					CellNature(E,x,y) == EMP && CellNature(E,x,y-1) ∈ {PLT,MTL}
 */
public interface Environnement extends /* include */ Screen{
	///////////////////////////////
	///////// OBSERVATORS /////////
	///////////////////////////////
	// CONST
	public Player getPlayer();
	
	// CONST
	public List<Guard> getGuards();
	
	// CONST
	public List<Item> getTreasures();
	
	//CONST
	public int getHgtPortail();
	
	//CONST
	public int getWdtPortail();
	
	/**
	 * pre: CellContent(E,x,y) requires (0 <= y < Height(E)) && (0 <= x < Width(E))
	 */
	public Couple cellContent(int x, int y);

	
	///////////////////////////////
	//////// CONSTRUCTORS /////////
	///////////////////////////////
	/**
	 * post: Player(init(wdt, hgt, player, guards, treasures, wdtPortail, hgtPortail)) == player
	 * 
	 * post: WdtPortail(init(wdt, hgt, player, guards, treasures, wdtPortail, hgtPortail)) == wdtPortail
	 * 
	 * post: HgtPortail(init(wdt, hgt, player, guards, treasures, wdtPortail, hgtPortail)) == hgtPortail
	 * 
	 * post: for i in [0...List::Size(guards)] then
	 * 			List::Get(Guards(init(wdt, hgt, player, guards, treasures, wdtPortail, hgtPortail)), i) == List::Get(guards, i)
	 * 
	 * post: for i in [0...List::Size(treasures)] then
	 * 			List::Get(Treasures(init(wdt, hgt, player, guards, treasures, wdtPortail, hgtPortail)), i) == List::Get(treasures, i)
	 */
	public void init(int wdt, int hgt, Player player, List<Guard> guards, List<Item> treasures, int wdtPortail, int hgtPortail);
	
	/**
	 * pre Array::Length(cells) = wdt && Array::Length(cells[0]) = hgt 
	 * 
	 * post: Player(init(wdt, hgt, player, guards, treasures, wdtPortail, hgtPortail)) == player
	 * 
	 * post: WdtPortail(init(wdt, hgt, player, guards, treasures, wdtPortail, hgtPortail)) == wdtPortail
	 * 
	 * post: HgtPortail(init(wdt, hgt, player, guards, treasures, wdtPortail, hgtPortail)) == hgtPortail
	 * 
	 * post: for i in [0...List::Size(guards)] then
	 * 			List::Get(Guards(init(wdt, hgt, player, guards, treasures)), i) == List::Get(guards, i)
	 * 
	 * post: for i in [0...List::Size(treasures)] then
	 * 			List::Get(Treasures(init(wdt, hgt, player, guards, treasures)), i) == List::Get(treasures, i)
	 * 
	 * post: for i in [0...wdt] 
	 * 			for j in [0...hgt] then
	 * 				cells[i][j] = CellNature(E,i,j)
	 */
	public void init(int wdt, int hgt, Player player, List<Guard> guards, List<Item> treasures, Cell[][] cells, int wdtPortail, int hgtPortail);
}
