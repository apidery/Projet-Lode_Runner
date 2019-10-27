package contracts;

import contracts.errors.PostconditionError;
import contracts.errors.PreconditionError;
import data.Cell;
import decorators.ScreenDecorator;
import services.Screen;

public class ScreenContract extends ScreenDecorator {

	public ScreenContract(Screen delegate) {
		super(delegate);
	}

	public void checkInvariants() {
		// Aucun invariants
	}

	@Override
	public int getHeight() {
		return super.getHeight();
	}

	@Override
	public int getWidth() {
		return super.getWidth();
	}

	@Override
	public Cell cellNature(int x, int y) {
		checkInvariants();
		if (!(0 <= y && y < getHeight() && 0 <= x && x < getWidth()))
			throw new PreconditionError("SCREEN CellNature : 0 <= y < height(S) and 0 <= x < width(S)");
		
		Cell res = super.cellNature(x, y);
		checkInvariants();
		
		return res;
	}

	@Override
	public void init(int width, int height) {
		if (!(height > 0 && width > 0))
			throw new PreconditionError("SCREEN Init : height > 0 and width > 0");

		super.init(width, height);

		if (getWidth() != width)
			throw new PostconditionError("SCREEN Init : getWidth() = width");
		if (getHeight() != height)
			throw new PostconditionError("SCREEN Init : getHeight() = height");

		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				if (cellNature(x, y) != Cell.EMP)
					throw new PostconditionError("SCREEN Init : cellNature(init(height, width), x, y) = EMP");
			}
		}
		checkInvariants();
	}

	@Override
	public void dig(int x, int y) {
		checkInvariants();
		
		if (!(cellNature(x, y) == Cell.PLT))
			throw new PreconditionError("SCREEN dig : cellNature(S,x,y) = PLT");

		// On sauvegarde l'ancien plateau
		Cell plateau_atPre[][] = new Cell[getHeight()][getWidth()];
		for (int x1 = 0; x1 < getWidth(); x1++) {
			for (int y1 = 0; y1 < getHeight(); y1++) {
				plateau_atPre[x1][y1] = cellNature(x1, y1);
			}
		}

		super.dig(x, y);

		if (!(cellNature(x, y) == Cell.HOL))
			throw new PostconditionError("SCREEN dig : cellNature(S,x,y) = HOL");

		for (int x1 = 0; x1 < getWidth(); x1++) {
			for (int y1 = 0; y1 < getHeight(); y1++) {
				if (!(cellNature(x1, y1) == plateau_atPre[x1][y1]))
					throw new PostconditionError("SCREEN dig : cellNature(dig(u,v) x,y) = cellNature(x,y)");
			}
		}
		checkInvariants();
	}

	@Override
	public void fill(int x, int y) {
		checkInvariants();
		
		if (!(cellNature(x, y) == Cell.HOL))
			throw new PreconditionError("SCREEN fill : cellNature(S,x,y) = HOL");

		// On sauvegarde l'ancien plateau
		Cell plateau_atPre[][] = new Cell[getHeight()][getWidth()];
		for (int x1 = 0; x1 < getWidth(); x1++) {
			for (int y1 = 0; y1 < getHeight(); y1++) {
				plateau_atPre[x1][y1] = cellNature(x1, y1);
			}
		}

		super.fill(x, y);

		if (!(cellNature(x, y) == Cell.PLT))
			throw new PostconditionError("SCREEN fill : cellNature(S,x,y) = PLT");

		for (int x1 = 0; x1 < getWidth(); x1++) {
			for (int y1 = 0; y1 < getHeight(); y1++) {
				if (!(cellNature(x1, y1) == plateau_atPre[x1][y1]))
					throw new PostconditionError("SCREEN fill : cellNature(dig(u,v) x,y) = cellNature(x,y)");
			}
		}
		checkInvariants();
	}
}
