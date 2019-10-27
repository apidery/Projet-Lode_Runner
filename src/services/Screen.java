package services;

import data.Cell;

public interface Screen {
	///////////////////////////////
	///////// OBSERVATORS /////////
	///////////////////////////////
	// CONST
	public int getHeight();
	
	// CONST
	public int getWidth();

	/**
	 * pre: cellNature(S,x,y) require 0 <= y < height(S) and 0 <= x < width(S)
	 */
	public Cell cellNature(int x, int y);

	
	///////////////////////////////
	//////// CONSTRUCTORS /////////
	///////////////////////////////
	/**
	 * pre: init(height, width) require height > 0 and width > 0 
	 * 
	 * post: getHeight() = height
	 * 
	 * post: getWidth() = width
	
	 * post: for x [0.. getWidth()]
	 * 			for y [0.. getHeight()]
	 * 				cellNature(init(height, width), x, y) = EMP
	 */
	public void init(int width, int height);

	
	///////////////////////////////
	////////// OPERATORS //////////
	///////////////////////////////
	/**
	 * pre: dig(S,x,y) require cellNature(S,x,y) = PLT
	 * 
	 * post: cellNature(dig(x,y), x,y) = HOL 
	 * 
	 * post: for u [0.. getWidth()]
	 * 			for v [0.. getHeight()]
	 * 				if(x != u or y != v) 
	 * 					cellNature(dig(u,v) x,y) = cellNature(x,y)
	 */
	public void dig(int x, int y);
	
	/**
	 * pre: fill(S, x, y) require cellNature(S,x,y) = HOL

	 * post: cellNature(dig(x,y), x,y) = PLT 
	 * 
	 * post: for u [0.. getWidth()]
	 * 			for v [0.. getHeight()]
	 * 				if(x != u or y != v) 
	 * 					cellNature(dig(x,y) u,v) = cellNature(u,v)
	 */
	public void fill(int x, int y);	
}
