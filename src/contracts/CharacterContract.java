package contracts;

import contracts.errors.InvariantError;
import contracts.errors.PostconditionError;
import contracts.errors.PreconditionError;
import data.Cell;
import data.Command;
import data.Couple;
import decorators.CharacterDecorator;
import services.Character;
import services.Environnement;
import services.Screen;

public class CharacterContract extends CharacterDecorator {

	public CharacterContract(Character delegate) {
		super(delegate);
	}
	
	public void checkInvariants() {
		// Invariant 1
		if (! (getEnvi().cellNature(getWdt(), getHgt()) == Cell.EMP || getEnvi().cellNature(getWdt(), getHgt()) == Cell.HOL || getEnvi().cellNature(getWdt(), getHgt()) == Cell.LAD || getEnvi().cellNature(getWdt(), getHgt()) == Cell.HDR))
			throw new InvariantError("CHARACTER invariant n°1 : Environnement::CellNature(getEnvi(), getWdt(), getHgt()) ∈ {EMP, HOL, LAD, HDR}");
	}

	@Override
	public Environnement getEnvi() {
		return super.getEnvi();
	}

	@Override
	public int getHgt() {
		return super.getHgt();		
	}

	@Override
	public int getWdt() {
		return super.getWdt();
	}

	@Override
	public void init(Screen e, int x, int y) {
		if (!(0 <= y && y < e.getHeight() && 0 <= x && x < e.getWidth()))
			throw new PreconditionError("CHARACTER init : 0 <= y < height(S) and 0 <= x < width(S)");
		
		if (!(e.cellNature(x, y) == Cell.EMP))
			throw new PreconditionError("CHARACTER init : Environnement::cellNature(S,x,y) = EMP");
		
		super.init(e, x, y);
		
		if (getHgt() != y)
			throw new PostconditionError("CHARACTER Init 1 : Hgt(C) = y");
		
		if (getWdt() != x)
			throw new PostconditionError("CHARACTER Init 2 : Wdt(C) = x");
		
		if (getEnvi() != e)
			throw new PostconditionError("CHARACTER Init 3 : Envi(C) = e");
		
		if (getInitialWdt() != x)
			throw new PostconditionError("CHARACTER Init 4 : InitialWdt non affecté");

		if (getInitialHgt() != y)
			throw new PostconditionError("CHARACTER Init 5 : InitialHgt non affecté");
		
		checkInvariants();
	}

	@Override
	public void goLeft() {
		checkInvariants();
		
		int hgt_atPre = getHgt();
		int wdt_atPre = getWdt();
		
		Couple couple1 = null;
		Couple couple2 = null;
		Couple couple3 = null;
		if (wdt_atPre -1 >= 0)
			couple1 = getEnvi().cellContent(wdt_atPre - 1, hgt_atPre);
			
		if (hgt_atPre -1 >= 0)
			couple2 = getEnvi().cellContent(wdt_atPre, hgt_atPre - 1);
		if (wdt_atPre -1 >= 0 && hgt_atPre+1 < getEnvi().getHeight())
			couple3 = getEnvi().cellContent(wdt_atPre-1, hgt_atPre+1);
		
		
		super.goLeft();
		
		if (getLastAction() != Command.LEFT)
			throw new PostconditionError("CHARACTER goLeft 1 : L'observateur LastAction n'est pas fixé à LEFT après goLeft");
		
		if (wdt_atPre == 0) {
			if (getWdt() != wdt_atPre)
				throw new PostconditionError("CHARACTER goLeft 2 : Le personnage n'est pas censé pouvoir aller à gauche");
			if (hgt_atPre != getHgt())
				throw new PostconditionError("CHARACTER goLeft 2 : La hauteur du personnage n'est pas censée changer");
		}
		
		if (wdt_atPre -1 >= 0) {
			Cell c = getEnvi().cellNature(wdt_atPre, hgt_atPre);
			Cell c1 = getEnvi().cellNature(wdt_atPre - 1, hgt_atPre);
			if (c != Cell.HDR && c != Cell.HOL && (c1 == Cell.MTL || c1 == Cell.PLT)) {
				if (getWdt() != wdt_atPre)
					throw new PostconditionError("CHARACTER goLeft 3 : Le personnage n'est pas censé pouvoir aller à gauche");
				if (hgt_atPre != getHgt())
					throw new PostconditionError("CHARACTER goLeft 3 : La hauteur du personnage n'est pas censée changer");
			}
		}
		
		if (hgt_atPre - 1 >= 0) {
			Cell c1 = getEnvi().cellNature(wdt_atPre, hgt_atPre);
			Cell c2 = getEnvi().cellNature(wdt_atPre, hgt_atPre - 1);
			if ((c1 != Cell.LAD && c1 != Cell.HDR)
					&& (c2 != Cell.PLT && c2 != Cell.MTL)
					&& (couple2.getCharacter() != null)) {
				if (getWdt() != wdt_atPre)
					throw new PostconditionError("CHARACTER goLeft 4 : Le personnage n'est pas censé pouvoir aller à gauche");
				if (hgt_atPre != getHgt())
					throw new PostconditionError("CHARACTER goLeft 4 : La hauteur du personnage n'est pas censée changer");
			}
		}
		
		if (wdt_atPre -1 >= 0) {
			if (couple1.getCharacter() != null) {
				if (getWdt() != wdt_atPre)
					throw new PostconditionError("CHARACTER goLeft 5 : Le personnage n'est pas censé pouvoir aller à gauche");
				if (hgt_atPre != getHgt())
					throw new PostconditionError("CHARACTER goLeft 5 : La hauteur du personnage n'est pas censée changer");
			}
		}
		
		if (wdt_atPre -1 >= 0 && hgt_atPre - 1 >= 0) {
			Cell c = getEnvi().cellNature(wdt_atPre, hgt_atPre);
			Cell c1 = getEnvi().cellNature(wdt_atPre - 1, hgt_atPre);
			Cell c2 = getEnvi().cellNature(wdt_atPre, hgt_atPre - 1);
			if ( ( (wdt_atPre != 0) && c1 != Cell.MTL && c1 != Cell.PLT
					&& ( (c == Cell.LAD || c == Cell.HDR || c == Cell.HOL)
						|| (c2 == Cell.PLT || c2 == Cell.MTL || c2 == Cell.LAD)
						|| (couple2.getCharacter() != null) ))
					&& !(couple1.getCharacter() != null) ) {
				if (getWdt() != wdt_atPre - 1)
					throw new PostconditionError("CHARACTER goLeft 6 : Le personnage est censé pouvoir aller à gauche");
			}
		}
		
		if (wdt_atPre -1 >= 0 && hgt_atPre-1 >= 0) {
			Cell c = getEnvi().cellNature(wdt_atPre, hgt_atPre);
			Cell c3 = getEnvi().cellNature(wdt_atPre-1, hgt_atPre-1);
			if ((c != Cell.HDR && c != Cell.LAD && c != Cell.HOL) && c3 == Cell.HDR)
				if (getHgt() != hgt_atPre-1)
					throw new PostconditionError("CHARACTER goLeft 7 : Le personnage aurait dû aller sur le HDR en bas à gauche");
		}
		
		if (wdt_atPre -1 >= 0 && hgt_atPre+1 < getEnvi().getHeight()) {
			Cell c = getEnvi().cellNature(wdt_atPre, hgt_atPre);
			Cell c1 = getEnvi().cellNature(wdt_atPre-1, hgt_atPre);
			Cell c3 = getEnvi().cellNature(wdt_atPre-1, hgt_atPre+1);
			if ((c == Cell.HDR || c == Cell.HOL)
					&& (c1 == Cell.PLT || c1 == Cell.MTL)
					&& (c3 == Cell.EMP || c3 == Cell.LAD || c3 == Cell.HDR || c3 == Cell.HOL)
					&& couple3.getCharacter() == null) {
				if (getWdt() != wdt_atPre - 1)
					throw new PostconditionError("CHARACTER goLeft 8 : Le personnage aurait dû passer de l'HDR à la plateforme à gauche");
				if (getHgt() != hgt_atPre + 1)
					throw new PostconditionError("CHARACTER goLeft 8 : Le personnage aurait dû passer de l'HDR à la plateforme à gauche");
			}
		}
		checkInvariants();
	}

	@Override
	public void goRight() {
		checkInvariants();
		
		int hgt_atPre = getHgt();
		int wdt_atPre = getWdt();
		
		Couple couple1 = null;
		Couple couple2 = null;
		Couple couple3 = null;
		if (wdt_atPre + 1 < getEnvi().getWidth())
			couple1 = getEnvi().cellContent(wdt_atPre + 1, hgt_atPre);
		if (hgt_atPre -1 >= 0)
			couple2 = getEnvi().cellContent(wdt_atPre, hgt_atPre - 1);
		if (hgt_atPre + 1 < getEnvi().getHeight() && wdt_atPre+1 < getEnvi().getWidth())
			couple3 = getEnvi().cellContent(wdt_atPre+1, hgt_atPre+1);
		
		super.goRight();
		
		if (getLastAction() != Command.RIGHT)
			throw new PostconditionError("CHARACTER goRight 1 : L'observateur LastAction n'est pas fixé à RIGHT après goRight");
		
		if (wdt_atPre == getEnvi().getWidth() - 1) {
			if (getWdt() != wdt_atPre)
				throw new PostconditionError("CHARACTER goRight 2 : Le personnage n'est pas censé pouvoir aller à droite");
			if (hgt_atPre != getHgt())
				throw new PostconditionError("CHARACTER goRight 2 : La hauteur du personnage n'est pas censée changer");
		}
		
		if (wdt_atPre + 1 < getEnvi().getWidth()) {
			Cell c = getEnvi().cellNature(wdt_atPre, hgt_atPre);
			Cell c1 = getEnvi().cellNature(wdt_atPre + 1, hgt_atPre);
			if (c != Cell.HDR && c != Cell.HOL && (c1 == Cell.MTL || c1 == Cell.PLT)) {
				if (getWdt() != wdt_atPre)
					throw new PostconditionError("CHARACTER goRight 3 : Le personnage n'est pas censé pouvoir aller à droite");
				if (hgt_atPre != getHgt())
					throw new PostconditionError("CHARACTER goRight 3 : La hauteur du personnage n'est pas censée changer");
			}
		}
		
		if (hgt_atPre - 1 >= 0) {
			Cell c1 = getEnvi().cellNature(wdt_atPre, hgt_atPre);
			Cell c2 = getEnvi().cellNature(wdt_atPre, hgt_atPre - 1);
			if ((c1 != Cell.LAD && c1 != Cell.HDR)
					&& (c2 != Cell.PLT && c2 != Cell.MTL)
					&& (couple2.getCharacter() != null)) {
				if (getWdt() != wdt_atPre)
					throw new PostconditionError("CHARACTER goRight 4 : Le personnage n'est pas censé pouvoir aller à droite");
				if (hgt_atPre != getHgt())
					throw new PostconditionError("CHARACTER goRight 4 : La hauteur du personnage n'est pas censée changer");
			}
		}
		
		if (wdt_atPre + 1 < getEnvi().getWidth()) {
			if (couple1.getCharacter() != null) {
				if (getWdt() != wdt_atPre)
					throw new PostconditionError("CHARACTER goRight 5 : Le personnage n'est pas censé pouvoir aller à droite");
				if (hgt_atPre != getHgt())
					throw new PostconditionError("CHARACTER goRight 5 : La hauteur du personnage n'est pas censée changer");
			}
		}
		
		if (wdt_atPre + 1 < getEnvi().getWidth() && hgt_atPre -1 >= 0) {
			Cell c = getEnvi().cellNature(wdt_atPre, hgt_atPre);
			Cell c1 = getEnvi().cellNature(wdt_atPre + 1, hgt_atPre);
			Cell c2 = getEnvi().cellNature(wdt_atPre, hgt_atPre - 1);
			if ( ( (wdt_atPre != getEnvi().getWidth()-1) && (c1 != Cell.MTL && c1 != Cell.PLT)
					&& ( (c == Cell.LAD || c == Cell.HDR || c == Cell.HOL)
						|| (c2 == Cell.PLT || c2 == Cell.MTL || c2 == Cell.LAD)
						|| (couple2.getCharacter() != null) ))
					&& !(couple1.getCharacter() != null) )
				if (getWdt() != wdt_atPre + 1)
					throw new PostconditionError("CHARACTER goRight 6 : Le personnage est censé pouvoir aller à droite");
		}
		
		if (wdt_atPre +1 < getEnvi().getWidth() && hgt_atPre-1 >= 0) {
			Cell c = getEnvi().cellNature(wdt_atPre, hgt_atPre);
			Cell c3 = getEnvi().cellNature(wdt_atPre+1, hgt_atPre-1);
			if ((c != Cell.HDR && c != Cell.LAD && c != Cell.HOL) && c3 == Cell.HDR) {
				if (getHgt() != hgt_atPre-1)
					throw new PostconditionError("CHARACTER goRight 7 : Le personnage aurait dû aller sur le HDR en bas à gauche");
			}
		}
		
		if (hgt_atPre+1 < getEnvi().getHeight() && wdt_atPre+1 < getEnvi().getWidth()) {
			Cell c = getEnvi().cellNature(wdt_atPre, hgt_atPre);
			Cell c1 = getEnvi().cellNature(wdt_atPre+1, hgt_atPre);
			Cell c3 = getEnvi().cellNature(wdt_atPre+1, hgt_atPre+1);
			if ( (c == Cell.HDR || c == Cell.HOL)
					&& (c1 == Cell.PLT || c1 == Cell.MTL)
					&& (c3 == Cell.EMP || c3 == Cell.LAD || c3 == Cell.HDR || c3 == Cell.HOL)
					&& couple3.getCharacter() == null) {
				if (getWdt() != wdt_atPre + 1)
					throw new PostconditionError("CHARACTER goRight 8 : Le personnage aurait dû passer de l'HDR à la plateforme à droite");
				if (getHgt() != hgt_atPre + 1)
					throw new PostconditionError("CHARACTER goRight 8 : Le personnage aurait dû passer de l'HDR à la plateforme à droite");
			}
		}
		checkInvariants();
	}

	@Override
	public void goUp() {
		checkInvariants();
		
		int hgt_atPre = getHgt();
		int wdt_atPre = getWdt();
		
		Couple couple2 = null;
		if (hgt_atPre + 1 < getEnvi().getHeight())
			couple2 = getEnvi().cellContent(wdt_atPre, hgt_atPre + 1);
		
		super.goUp();
		
		if (getLastAction() != Command.UP)
			throw new PostconditionError("CHARACTER goUp 1 : L'observateur LastAction n'est pas fixé à UP après goUp");
		
		if (wdt_atPre != getWdt())
			throw new PostconditionError("CHARACTER goUp 2 : La position Width du personnage n'est pas censée changer");
		
		if (hgt_atPre == getEnvi().getHeight() - 1)
			if (getHgt() != hgt_atPre)
				throw new PostconditionError("CHARACTER goUp 3 : Le personnage n'est pas censé pouvoir aller en haut");
		
		if (hgt_atPre + 1 < getEnvi().getHeight()) {
			if (couple2.getCharacter() != null)
				if (getHgt() != hgt_atPre)
					throw new PostconditionError("CHARACTER goUp 4 : Le personnage n'est pas censé pouvoir aller en haut");
		}
		
		if (hgt_atPre + 1 < getEnvi().getHeight()) {
			Cell c = getEnvi().cellNature(wdt_atPre, hgt_atPre);
			Cell c2 = getEnvi().cellNature(wdt_atPre, hgt_atPre + 1);
			if (c == Cell.LAD && c2 == Cell.PLT) {
				if (getHgt() != hgt_atPre)
					throw new PostconditionError("CHARACTER goUp 5 : Le personnage n'est pas censé pouvoir aller en haut");
			}
		}
		
		if (hgt_atPre + 1 < getEnvi().getHeight()) {
			Cell c = getEnvi().cellNature(wdt_atPre, hgt_atPre);
			Cell c2 = getEnvi().cellNature(wdt_atPre, hgt_atPre + 1);
			if (c == Cell.LAD && (c2 == Cell.EMP || c2 == Cell.LAD) && couple2.getCharacter() == null) {
				if (getHgt() != hgt_atPre + 1)
					throw new PostconditionError("CHARACTER goUp 6 : Le personnage est censé pouvoir aller en haut");
			}
		}
		checkInvariants();
	}

	@Override
	public void goDown() {
		checkInvariants();
		
		int hgt_atPre = getHgt();
		int wdt_atPre = getWdt();
		
		Couple couple2 = null;
		if (hgt_atPre - 1 >= 0)
			couple2 = getEnvi().cellContent(wdt_atPre, hgt_atPre - 1);
		
		super.goDown();
		
		if (getLastAction() != Command.DOWN)
			throw new PostconditionError("CHARACTER goDown 1 : L'observateur LastAction n'est pas fixé à DOWN après goDown");
		
		if (wdt_atPre != getWdt())
			throw new PostconditionError("CHARACTER goDown 2 : La position Width du personnage n'est pas censée changer");
		
		if (hgt_atPre == 1)
			if (getHgt() != hgt_atPre)
				throw new PostconditionError("CHARACTER goDown 3 : Le personnage n'est pas censé pouvoir aller en bas");
		
		if (hgt_atPre - 1 >= 0) {
			if (couple2.getCharacter() != null)
				if (getHgt() != hgt_atPre)
					throw new PostconditionError("CHARACTER goDown 4 : Le personnage n'est pas censé pouvoir aller en bas");
		}
		
		if (hgt_atPre - 1 >= 0) {
			Cell c = getEnvi().cellNature(wdt_atPre, hgt_atPre);
			Cell c2 = getEnvi().cellNature(wdt_atPre, hgt_atPre - 1);
			if (c == Cell.LAD && c2 == Cell.PLT) {
				if (getHgt() != hgt_atPre)
					throw new PostconditionError("CHARACTER goDown 5 : Le personnage n'est pas censé pouvoir aller en bas");
			}
		}
		
		if (hgt_atPre - 1 >= 0) {
			Cell c = getEnvi().cellNature(wdt_atPre, hgt_atPre);
			Cell c2 = getEnvi().cellNature(wdt_atPre, hgt_atPre - 1);
			if ((c == Cell.LAD || c == Cell.EMP || c == Cell.HDR)
					&& (c2 == Cell.EMP || c2 == Cell.LAD || c2 == Cell.HDR || c2 == Cell.HOL)
					&& couple2.getCharacter() == null) {
				if (getHgt() != hgt_atPre - 1)
					throw new PostconditionError("CHARACTER goDown 6 : Le personnage est censé pouvoir aller en bas");
			}
		}
		checkInvariants();
	}

	@Override
	public void backInitialPosition() {
		//checkInvariants();
		super.backInitialPosition();
		
		/*if (getWdt() != getInitialWdt())
			throw new PostconditionError("CHARACTER backInitialPosition 1 : La Wdt du personnage n'est pas égale à la Wdt initiale");
		if (getHgt() != getInitialHgt())
			throw new PostconditionError("CHARACTER backInitialPosition 1 : La Hgt du personnage n'est pas égale à la Hgt initiale");
			
		
		checkInvariants();*/
	}
}
