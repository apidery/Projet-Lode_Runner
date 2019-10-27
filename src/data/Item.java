package data;

import services.GameObject;
import services.Screen;

public interface Item extends /* include */ GameObject{
	///////////////////////////////
	///////// OBSERVATORS /////////
	///////////////////////////////
	// CONST
	public int getId();

	// CONST
	public ItemType getNature();
	
	/**
	 * pre: init(e, x, y, id, nature) require nature ∈ {TREASURE}
	 * 
	 * post: Nature(init(e, x, y, id, nature)) == nature
	 * 
	 * post: Id(init(e, x, y, id, nature)) == id
	 */
	public void init(Screen e, int x, int y, int id, ItemType nature);
}
