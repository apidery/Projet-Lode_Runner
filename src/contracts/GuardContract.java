package contracts;

import contracts.errors.PostconditionError;
import contracts.errors.PreconditionError;
import data.Cell;
import data.Command;
import data.Couple;
import data.Move;
import decorators.GuardDecorator;
import services.Guard;
import services.PathResolver;
import services.Screen;

public class GuardContract extends GuardDecorator {

	public GuardContract(Guard delegate) {
		super(delegate);
	}
	
	public void checkInvariants() {
		
	}

	@Override
	public void init(Screen e, int x, int y, int id, PathResolver resolver) {
		super.init(e, x, y, id, resolver);

		if (getId() != id)
			throw new PostconditionError("GUARD Init 1 : ID non affecté");

		if (getResolver() != resolver)
			throw new PostconditionError("GUARD Init 2 : R non affecté");

		if (getTimeInHole() != 0)
			throw new PostconditionError("GUARD Init 3 : timeInHole != 0");

		checkInvariants();
	}

	@Override
	public int getId() {
		return super.getId();
	}

	@Override
	public Move getBehaviour() {
		return super.getBehaviour();
	}

	@Override
	public PathResolver getResolver() {
		return super.getResolver();
	}

	@Override
	public int getTimeInHole() {
		return super.getTimeInHole();
	}

	@Override
	public void climbLeft() {
		checkInvariants();

		int wdtAt_pre = getWdt();
		int HgtAt_pre = getHgt();
		
		boolean hadCharacter = false;
		
		if (wdtAt_pre - 1 >= 0 && HgtAt_pre + 1 < getEnvi().getHeight())
			hadCharacter = (getEnvi().cellContent(wdtAt_pre - 1, HgtAt_pre + 1).getCharacter() != null);

		if (getEnvi().cellNature(getWdt(), getHgt()) != Cell.HOL)
			throw new PreconditionError("GUARD ClimbLeft : Le garde n'est pas dans un trou");

		super.climbLeft();

		if (wdtAt_pre == 0) {
			if (wdtAt_pre != getWdt() || HgtAt_pre != getHgt())
				throw new PostconditionError("GUARD ClimbLeft 1 : Le garde n'est pas censé bouger");
		}
		
		if (wdtAt_pre - 1 >= 0 && HgtAt_pre + 1 < getEnvi().getHeight()) {
			Cell c = getEnvi().cellNature(wdtAt_pre - 1, HgtAt_pre + 1);
			if (c == Cell.MTL || c == Cell.PLT)
				if (wdtAt_pre != getWdt() || HgtAt_pre != getHgt())
					throw new PostconditionError("GUARD ClimbLeft 2 : Le garde n'est pas censé bouger");
			
			if (hadCharacter)
				if (wdtAt_pre != getWdt() || HgtAt_pre != getHgt())
					throw new PostconditionError("GUARD ClimbLeft 3 : Le garde n'est pas censé bouger");

			c = getEnvi().cellNature(wdtAt_pre - 1, HgtAt_pre + 1);
			if (wdtAt_pre != 0 && (c != Cell.PLT && c != Cell.MTL) && !hadCharacter)
				if (getWdt() != wdtAt_pre - 1 && getHgt() == HgtAt_pre + 1)
					throw new PostconditionError("GUARD ClimbLeft 4 : Le garde est censé bouger");
		}
		checkInvariants();
	}

	@Override
	public void climbRight() {
		checkInvariants();

		int wdtAt_pre = getWdt();
		int HgtAt_pre = getHgt();
		
		boolean hadCharacter = false;
		
		if (wdtAt_pre + 1 < getEnvi().getWidth() && HgtAt_pre + 1 < getEnvi().getHeight())
			hadCharacter = (getEnvi().cellContent(wdtAt_pre + 1, HgtAt_pre + 1).getCharacter() != null);

		if (getEnvi().cellNature(getWdt(), getHgt()) != Cell.HOL)
			throw new PreconditionError("GUARD ClimbRight : Le garde n'est pas dans un trou");

		super.climbRight();

		if (wdtAt_pre == getEnvi().getWidth()-1) {
			if (wdtAt_pre != getWdt() || HgtAt_pre != getHgt())
				throw new PostconditionError("GUARD ClimbRight 1 : Le garde n'est pas censé bouger");
		}

		if (wdtAt_pre + 1 < getEnvi().getWidth() && HgtAt_pre + 1 < getEnvi().getHeight()) {
			Cell c = getEnvi().cellNature(wdtAt_pre + 1, HgtAt_pre + 1);
			if (c == Cell.MTL || c == Cell.PLT)
				if (wdtAt_pre != getWdt() || HgtAt_pre != getHgt())
					throw new PostconditionError("GUARD ClimbRight 2 : Le garde n'est pas censé bouger");

			if (hadCharacter)
				if (wdtAt_pre != getWdt() || HgtAt_pre != getHgt())
					throw new PostconditionError("GUARD ClimbRight 3 : Le garde n'est pas censé bouger");

			c = getEnvi().cellNature(wdtAt_pre + 1, HgtAt_pre + 1);
			if (wdtAt_pre != getEnvi().getWidth()-1 && (c != Cell.PLT && c != Cell.MTL) && !hadCharacter)
				if (getWdt() != wdtAt_pre + 1 && getHgt() == HgtAt_pre + 1)
					throw new PostconditionError("GUARD ClimbLeft 4 : Le garde est censé bouger");
		}
		checkInvariants();
	}

	@Override
	public void step() {
		checkInvariants();
		
		int wdtAt_pre = getWdt();
		int HgtAt_pre = getHgt();
		int timeInHoleAt_pre = getTimeInHole();
		Couple couple2 = getEnvi().cellContent(wdtAt_pre, HgtAt_pre-1);
		Cell c = getEnvi().cellNature(wdtAt_pre, HgtAt_pre);
		
		super.step();
		
		Move behaviour = getBehaviour();
		boolean hasMoved = false;
		
		if (HgtAt_pre -1 >= 0) {
			Cell c2 = getEnvi().cellNature(wdtAt_pre, HgtAt_pre-1);
			
			if ( (c != Cell.HDR && c != Cell.LAD && c != Cell.HOL)
					&& (c2 == Cell.EMP || c2 == Cell.HOL || c2 == Cell.HDR)
					&& couple2.getCharacter() == null) {
				hasMoved = true;
				if (getLastAction() != Command.DOWN) {
					throw new PostconditionError("GUARD step 1 : Le garde aurait du faire goDown");
				}
			}
		}
		
		if (c == Cell.HOL && timeInHoleAt_pre < 2) {
			if (getTimeInHole() != timeInHoleAt_pre + 1)
				throw new PostconditionError("GUARD step 2 : Le délais passé dans le trou aurait dû être incrémenté de 1");
		
		}
		else if (c == Cell.HOL && timeInHoleAt_pre == 2 && behaviour == Move.LEFT && !hasMoved) {
			if (getLastAction() != Command.LEFT)
				throw new PostconditionError("GUARD step 3 : Le garde aurait du faire climbLeft");
			if (getTimeInHole() != 0)
				throw new PostconditionError("GUARD step 3 : Le TimeInHole du garde aurait dû etre remis à zéro après le climbLeft");
		}
		else if (c == Cell.HOL && timeInHoleAt_pre == 2 && behaviour == Move.RIGHT && !hasMoved) {
			if (getLastAction() != Command.RIGHT)
				throw new PostconditionError("GUARD step 4 : Le garde aurait du faire climbRight");
			if (getTimeInHole() != 0)
				throw new PostconditionError("GUARD step 4 : Le TimeInHole du garde aurait dû etre remis à zéro après le climbRight");
		} 
		else if (c == Cell.HOL && timeInHoleAt_pre == 2 && behaviour == Move.NEUTRAL && !hasMoved) {
			if (getBehaviour() != behaviour)
				throw new PostconditionError("GUARD step 5 : Le garde aurait du ne rien faire");
			if (getTimeInHole() != 0)
				throw new PostconditionError("GUARD step 5 : Le TimeInHole du garde aurait dû etre remis à zéro après être resté immobile");
		}
		else if (c != Cell.HOL && behaviour == Move.LEFT && !hasMoved) {
			if (getLastAction() != Command.LEFT)
				throw new PostconditionError("GUARD step 6 : Le garde aurait du faire goLeft");
		}
		else if (c != Cell.HOL && behaviour == Move.RIGHT && !hasMoved) {
			if (getLastAction() != Command.RIGHT)
				throw new PostconditionError("GUARD step 7 : Le garde aurait du faire goRight");
		}
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
		checkInvariants();
		super.backInitialPosition();
		
		if (getWdt() != getInitialWdt())
			throw new PostconditionError("CHARACTER backInitialPosition 1 : La Wdt du personnage n'est pas égale à la Wdt initiale");
		if (getHgt() != getInitialHgt())
			throw new PostconditionError("CHARACTER backInitialPosition 1 : La Hgt du personnage n'est pas égale à la Hgt initiale");
			
		
		checkInvariants();
	}
}
