package contracts;

import contracts.errors.PostconditionError;
import contracts.errors.PreconditionError;
import data.Cell;
import decorators.EditableScreenDecorator;
import services.EditableScreen;

public class EditableScreenContract extends EditableScreenDecorator {

	public EditableScreenContract(EditableScreen delegate) {
		super(delegate);
	}
	
	public void checkInvariants() {
		// Aucun invariants
	}
	
	@Override
	public boolean playable() {
		return super.playable();
	}

	@Override
	public void setNature(int x, int y, Cell c) {
		checkInvariants();
		
		if (!(0 <= y && y < getHeight() && 0 <= x && x < getWidth()))
			throw new PreconditionError("EDITABLESCREEN setNature : 0 <= y < height(S) and 0 <= x < width(S)");
		
		Cell[][] cells_atPre = new Cell[getWidth()][getHeight()];
		for (int u = 0; u < getWidth(); u++) {
			for (int v = 0; v < getHeight(); v++) {
				cells_atPre[u][v] = cellNature(u, v);
			}
		}
		
		super.setNature(x, y, c);
		
		if (cellNature(x, y) != c)
			throw new PostconditionError("EDITABLESCREEN setNature : La nature de la cellule n'a pas changée");
		
		for (int u = 0; u < getWidth(); u++) {
			for (int v = 0; v < getHeight(); v++) {
				if (x != u || v != y)
					if (cells_atPre[u][v] != cellNature(u, v))
						throw new PostconditionError("EDITABLESCREEN setNature : La nature n'une autre celle à été changée");
			}
		}
		checkInvariants();
	}
}
