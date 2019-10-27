package contracts;

import contracts.errors.PostconditionError;
import contracts.errors.PreconditionError;
import data.Cell;
import data.Command;
import data.Couple;
import decorators.PlayerDecorator;
import services.Engine;
import services.Player;
import services.Screen;

public class PlayerContract extends PlayerDecorator {

	public PlayerContract(Player delegate) {
		super(delegate);
	}
	
	public void checkInvariants() {
		// Aucun invariants
	}
	
	@Override
	public void init(Screen e, int x, int y, Engine engine) {
		super.init(e, x, y, engine);
		
		if (getEngine() != engine)
			throw new PostconditionError("ENGINE init 1: Engine(init(E,x,y,engine)) == engine");
		
		if (getVie() != 3)
			throw new PostconditionError("ENGINE init 2: la vie du joueur n'est pas initialisée à 3.");
		
		if (getNbAttacks() != 3)
			throw new PostconditionError("ENGINE init 3: le nombre d'attaques n'est pas égale à 3.");
		
		checkInvariants();
	}

	@Override
	public Engine getEngine() {
		return super.getEngine();
	}
	
	@Override
	public Command getLastAction() {
		return super.getLastAction();
	}

	@Override
	public void step() {
		checkInvariants();
		
		int wdtAt_pre = getWdt();
		int hgtAt_pre = getHgt();
		Command cmdAt_pre = getEngine().getNextCommand();
		
		Cell c = null;
		Cell c1 = null;
		Cell c2 = null;
		Cell c3 = null;
		Cell c4 = null;
		Cell c5 = null;
		Couple couple1 = null;
		Couple couple2 = null;
		Couple couple3 = null;
		
		c = getEnvi().cellNature(wdtAt_pre, hgtAt_pre);
		
		if (wdtAt_pre -1 >= 0) {
			c1 = getEnvi().cellNature(wdtAt_pre-1, hgtAt_pre);
			couple1 = getEnvi().cellContent(wdtAt_pre-1, hgtAt_pre);
		}
		
		if (hgtAt_pre -1 >= 0) {
			c2 = getEnvi().cellNature(wdtAt_pre, hgtAt_pre-1);
			couple2 = getEnvi().cellContent(wdtAt_pre, hgtAt_pre-1);
		}
		
		if (wdtAt_pre -1 >= 0 && hgtAt_pre -1 >= 0) {
			c3 = getEnvi().cellNature(wdtAt_pre-1, hgtAt_pre-1);
		}
		
		if (wdtAt_pre+1 < getEnvi().getWidth()) {
			c5 = getEnvi().cellNature(wdtAt_pre+1, hgtAt_pre);
			couple3 = getEnvi().cellContent(wdtAt_pre+1, hgtAt_pre);
		}
		if (hgtAt_pre -1  >= 0 && wdtAt_pre+1 < getEnvi().getWidth()) {
			c4 = getEnvi().cellNature(wdtAt_pre+1, hgtAt_pre-1);
		}
		
		super.step();
		
		if (hgtAt_pre -1 >= 0) {
			if ( (c != Cell.HDR && c != Cell.LAD && c != Cell.HOL)
					&& (c2 == Cell.EMP || c2 == Cell.HOL || c2 == Cell.HDR)
					&& couple2.getCharacter() == null ) {
				if (getWdt() != wdtAt_pre || getHgt() != hgtAt_pre-1)
					throw new PostconditionError("PLAYER step : Le joueur aura dû faire goDown()");
			}
			else if (cmdAt_pre == Command.LEFT) {
				if (getLastAction() != Command.LEFT)
					throw new PostconditionError("PLAYER step : Le joueur aura dû faire goLeft()");
			}
			else if (cmdAt_pre == Command.RIGHT) {
				if (getLastAction() != Command.RIGHT)
					throw new PostconditionError("PLAYER step : Le joueur aura dû faire goRight()");
			}
			else if (cmdAt_pre == Command.UP) {
				if (getLastAction() != Command.UP)
					throw new PostconditionError("PLAYER step : Le joueur aura dû faire goUp()");
			}
			else if (cmdAt_pre == Command.DOWN) {
				if (getLastAction() != Command.DOWN)
					throw new PostconditionError("PLAYER step : Le joueur aura dû faire goDown()");
			}
		}
		
		// WillDigLeft
		if (hgtAt_pre -1  >= 0 && wdtAt_pre-1 >= 0) {
			if ((getEngine().getNextCommand() == Command.DIGL
					&& ( (c2 == Cell.PLT || c2 == Cell.MTL || c2 == Cell.LAD ) 
							|| couple2.getCharacter() != null))
					&& (c1 == Cell.EMP
						&& couple1.getCharacter() == null
						&& couple1.getItem() == null)
					&& c3 == Cell.PLT) {
				if (getLastAction() != Command.DIGL)
					throw new PostconditionError("PLAYER step : Le joueur aura dû creuser à gauche");
			}
		}
		
		// WillDigRight
		if (hgtAt_pre -1  >= 0 && wdtAt_pre+1 < getEnvi().getWidth()) {
			if ((getEngine().getNextCommand() == Command.DIGR
					&& ( (c2 == Cell.PLT || c2 == Cell.MTL || c2 == Cell.LAD ) 
							|| couple2.getCharacter() != null))
					&& (c5 == Cell.EMP
						&& couple3.getCharacter() == null
						&& couple3.getItem() == null)
					&& c4 == Cell.PLT) {
				if (getLastAction() != Command.DIGR)
					throw new PostconditionError("PLAYER step : Le joueur aura dû creuser à droite");
			}
		}
		checkInvariants();
	}

	@Override
	public void decreaseVie() {
		//Capture
		int vieAt_pre = getVie();
		int nbAttackAt_pre = getNbAttacks();
		int wdtAt_pre = getWdt();
		int hgtAt_pre = getHgt();
		Screen screenAt_pre = getEnvi();
		Engine engineAt_pre = getEngine();
		
		if(getVie() == 0)
			throw new PreconditionError("PLAYER DecreaseVie 1 : La vie est déjà a 0.");
		
		super.decreaseVie();
		
		if (vieAt_pre - 1 != getVie()) 
			throw new PostconditionError("PLAYER DecreaseVie 1 : La vie n'a pas été décrémenté de 1");
		
		if (wdtAt_pre != getWdt())
			throw new PostconditionError("PLAYER DecreaseVie 2 : Le 'wdt' du joueur a changé.");
		
		if (hgtAt_pre != getHgt())
			throw new PostconditionError("PLAYER DecreaseVie 3 : Le 'hgt' du joueur a changé.");
		
		if (nbAttackAt_pre != getNbAttacks())
			throw new PostconditionError("PLAYER DecreaseVie 4 : Le nombre d'attaque du joueur a changé.");
		
		if (screenAt_pre != getEnvi())
			throw new PostconditionError("PLAYER DecreaseVie 5 : L'environnement a changé.");
		
		if (engineAt_pre != getEngine())
			throw new PostconditionError("PLAYER DecreaseVie 6 : L'engine a changé.");
	}
	
	@Override
	public void decreaseNbAttacks() {
		//Capture
		int vieAt_pre = getVie();
		int nbAttackAt_pre = getNbAttacks();
		int wdtAt_pre = getWdt();
		int hgtAt_pre = getHgt();
		Screen screenAt_pre = getEnvi();
		Engine engineAt_pre = getEngine();
		
		if(getNbAttacks() == 0)
			throw new PreconditionError("PLAYER DecreaseNbAttacks 1 : Le nombre d'attaques est déjà a 0.");
		
		super.decreaseNbAttacks();
		
		if (nbAttackAt_pre - 1 != getNbAttacks()) 
			throw new PostconditionError("PLAYER DecreaseNbAttacks 1 : Le nombre d'attaques restantes n'a pas été décrémenté de 1");
		
		if (wdtAt_pre != getWdt())
			throw new PostconditionError("PLAYER DecreaseNbAttacks 2 : Le 'wdt' du joueur a changé.");
		
		if (hgtAt_pre != getHgt())
			throw new PostconditionError("PLAYER DecreaseNbAttacks 3 : Le 'hgt' du joueur a changé.");
		
		if (vieAt_pre != getVie())
			throw new PostconditionError("PLAYER DecreaseNbAttacks 4 : La vie du joueur a changé.");
		
		if (screenAt_pre != getEnvi())
			throw new PostconditionError("PLAYER DecreaseNbAttacks 5 : L'environnement a changé.");
		
		if (engineAt_pre != getEngine())
			throw new PostconditionError("PLAYER DecreaseNbAttacks 6 : L'engine a changé.");
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
