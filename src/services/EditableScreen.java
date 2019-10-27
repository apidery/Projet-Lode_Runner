package services;

import data.Cell;

public interface EditableScreen extends /* include */ Screen{	
	///////////////////////////////
	///////// OBSERVATORS /////////
	///////////////////////////////
	/**
	 * inv de minimisation: 
	 * 		for x [0..Width(S)]
	 * 			for y [0..Height(S)]
	 * 				CellNature(S, x, y) != HOL
	 * 		&& 
	 * 		for x [0..Width(S)]
	 * 			CellNature(S, x, 0) == MTL				 
	 */
	public boolean playable();

	
	///////////////////////////////
	////////// OPERATORS //////////
	///////////////////////////////
	/**
	 * pre: SetNature(S,x,y,C) requires (0 <= y < Height(S)) && (0 <= x < Width(S))
	 * 
	 * post: CellNature(SetNature(S,x,y,C), x, y) == C
	 * 
	 * post: for u [0..Width(S)]
	 * 			for v [0..Height(S)]
	 * 				if(x != u || y != v) then
	 * 					CellNature(SetNature(S,u,v,C)), x, y) == CellNature(x,y) 
	 */
	public void setNature(int x, int y, Cell c);
	
	/**
	 * post: WillReturnPlayableCells(E) defined by:   
	 * 		 for x [0..Width(S)]
	 * 				for y [0..Height(S)]
	 * 					CellNature(S, x, y) != HOL
	 * 		 	&& 
	 * 	    		for x [0..Width(S)]
	 * 		    		CellNature(S, x, 0) == MTL
	 */
	public Cell[][] getPlayableEnvi();
}
