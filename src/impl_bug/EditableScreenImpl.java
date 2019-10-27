package impl_bug;

import data.Cell;
import services.EditableScreen;

public class EditableScreenImpl extends ScreenImpl implements EditableScreen {

	@Override
	public void init(int width, int height) {
		super.init(width, height);
		
		for(int i=0; i < width; i++) {
			setNature(i, 0, Cell.MTL);
		}
	}
	
	@Override
	public boolean playable() {
		for(int i=0; i<getWidth(); i++) {
			for(int j=0; j<getHeight(); j++) {
				if (cellNature(i, j) == Cell.HOL)
					return false;
			}
		}
		
		for(int i=0; i<getWidth(); i++) {
			if (cellNature(i, 0) != Cell.MTL)
				return false;
		}
		return true;
	}

	@Override
	public void setNature(int x, int y, Cell c) {
		this.plateau[x][y] = c;
	}

	@Override
	public Cell[][] getPlayableEnvi() {
		return this.plateau;
	}

}
