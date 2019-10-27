package impl;

import data.Cell;
import services.Screen;

public class ScreenImpl implements Screen{

	private int height, width;
	protected Cell plateau[][];
	
	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public Cell cellNature(int x, int y) {
		return plateau[x][y];
	}

	@Override
	public void init(int width, int height) {
		this.width = width;
		this.height = height;
		plateau = new Cell[width][height];
		for(int i=0; i<width; i++) {
			for(int j=0; j<height; j++) {
				plateau[i][j] = Cell.EMP;
			}
		}
	}

	@Override
	public void dig(int x, int y) {
		if (cellNature(x, y) == Cell.PLT)
			plateau[x][y] = Cell.HOL;
	}

	@Override
	public void fill(int x, int y) {
		if (cellNature(x, y) == Cell.HOL)
			plateau[x][y] = Cell.PLT;
	}

}
