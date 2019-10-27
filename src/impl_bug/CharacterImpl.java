package impl_bug;

import data.Cell;
import data.Command;
import data.Couple;
import services.Character;
import services.Environnement;
import services.Screen;

public class CharacterImpl implements Character {

	protected Environnement envi;
	protected int wdt, hgt, initialWdt, initialHgt;
	protected Command lastAction;

	@Override
	public Environnement getEnvi() {
		return envi;
	}

	@Override
	public int getHgt() {
		return hgt;
	}

	@Override
	public int getWdt() {
		return wdt;
	}
	
	@Override
	public int getInitialWdt() {
		return initialWdt;
	}

	@Override
	public int getInitialHgt() {
		return initialHgt;
	}
	
	@Override
	public Command getLastAction() {
		return lastAction;
	}
	
	@Override
	public void init(Screen e, int x, int y) {
		this.envi = (Environnement) e;
		this.wdt = x;
		this.hgt = y;
		this.initialWdt = x;
		this.initialHgt = y;
	}

	@Override
	public void goLeft() {
		lastAction = Command.LEFT;
		// Utiliser pour s'aasurer que l'on n'exedera pas les limites du plateau
		int oldWdt = wdt;
		int oldHgt = hgt;
		
		if (wdt - 1 < 0)
			return;
		
		Couple nextCellContent = envi.cellContent(wdt - 1, hgt);
		Couple cellBellowContent = envi.cellContent(wdt, hgt - 1);
		
		Cell natureCurrentCell = envi.cellNature(wdt, hgt);
		Cell natureNextCell = envi.cellNature(wdt - 1, hgt);
		Cell natureCellBelow = envi.cellNature(wdt, hgt - 1);
		Cell natureCellBellowLeft = envi.cellNature(wdt - 1, hgt - 1);
		
		boolean otherCharacterBellow = false;
		
		// S'il y a deja un personnage a la case ou on veut aller, on ne bouge pas
		if(nextCellContent.getCharacter() != null)
			return;

		// S'il y a un personnage a la case en dessous de nous.
		if(cellBellowContent.getCharacter() != null)
			otherCharacterBellow = true;
		
		 //System.out.println("GOLEFT: nextCell="+natureNextCell+" currentCell="+natureCurrentCell+" cellBellow="+natureCellBelow+" charactBellow="+otherCharacterBellow);
		
		if (wdt != 0 && natureNextCell != Cell.MTL && natureNextCell != Cell.PLT
				&& ((natureCurrentCell == Cell.LAD || natureCurrentCell == Cell.HDR || natureCurrentCell == Cell.HOL)
						|| (natureCellBelow == Cell.PLT || natureCellBelow == Cell.MTL || natureCellBelow == Cell.LAD)
						|| (otherCharacterBellow))) {
			wdt -= 1;
			
			// Si on va sur un HDR, on s'accroche à son niveau.
			if(natureCellBellowLeft == Cell.HDR &&
					(natureCurrentCell != Cell.HDR && natureCurrentCell != Cell.LAD && natureCurrentCell != Cell.HOL))
				hgt -= 1;
		}
		
		if (oldHgt + 1 < getEnvi().getHeight()) {
			Cell natureCellAboveLeft = envi.cellNature(oldWdt - 1, oldHgt + 1);
			Couple cellAboveLeftContent = envi.cellContent(oldWdt - 1, oldHgt + 1);
			boolean otherCharacterAboveLeft = false;
			if(cellAboveLeftContent.getCharacter() != null)
				otherCharacterAboveLeft = true;
			
			// On verifie si on peu monter a gauche (si c'est libre)
			if((natureCurrentCell == Cell.HDR || natureCurrentCell == Cell.HOL)
					&& (natureNextCell == Cell.PLT || natureNextCell == Cell.MTL) 
					&& otherCharacterAboveLeft == false 
					&& (natureCellAboveLeft == Cell.EMP || natureCellAboveLeft == Cell.LAD || natureCellAboveLeft == Cell.HDR || natureCellAboveLeft == Cell.HOL )) {
				hgt += 1;
				wdt -= 1;
			}
		}
	}

	@Override
	public void goRight() {
		lastAction = Command.RIGHT;
		// Utiliser pour s'aasurer que l'on n'exedera pas les limites du plateau
		int oldWdt = wdt;
		int oldHgt = hgt;
		
		if (wdt + 1 >= getEnvi().getWidth())
			return;
		
		Couple nextCellContent = envi.cellContent(wdt + 1, hgt);
		Couple cellBellowContent = envi.cellContent(wdt, hgt - 1);
		
		Cell natureCurrentCell = envi.cellNature(wdt, hgt);
		Cell natureNextCell = envi.cellNature(wdt + 1, hgt);
		Cell natureCellBelow = envi.cellNature(wdt, hgt - 1);
		Cell natureCellBellowRight = envi.cellNature(wdt + 1, hgt - 1);
		
		
		boolean otherCharacterBellow = false;
		// S'il y a deja un personnage sur la case ou on veut aller, on ne bouge pas
		if(nextCellContent.getCharacter() != null)
			return;

		// S'il y a un personnage a la case en dessous de nous.
		if(cellBellowContent.getCharacter() != null)
			otherCharacterBellow = true;
		
		//System.out.println("GORIGHT: wdt="+wdt+" nextCell="+natureNextCell+" currentCell="+natureCurrentCell+" cellBellow="+natureCellBelow+" charactBellow="+otherCharacterBellow);
		
		if (wdt != envi.getWidth() && natureNextCell != Cell.MTL && natureNextCell != Cell.PLT
				&& ((natureCurrentCell == Cell.LAD || natureCurrentCell == Cell.HDR || natureCurrentCell == Cell.HOL)
						|| (natureCellBelow == Cell.PLT || natureCellBelow == Cell.MTL || natureCellBelow == Cell.LAD)
						|| (otherCharacterBellow))) {
			wdt += 1;
			
			// Si on va sur un HDR, on s'accroche à son niveau.
			if(natureCellBellowRight == Cell.HDR
					&& (natureCurrentCell != Cell.HDR && natureCurrentCell != Cell.LAD && natureCurrentCell != Cell.HOL))
				hgt -= 1;
		}
		
		if (oldHgt + 1 < getEnvi().getHeight()) {
			Cell natureCellAboveRight = envi.cellNature(oldWdt + 1, oldHgt + 1);
			Couple cellAboveRightContent = envi.cellContent(oldWdt+1, oldHgt + 1);
			boolean otherCharacterAboveRight = false;
			// S'il y a un personnage a la case en haut à droite de nous
			if(cellAboveRightContent.getCharacter() != null)
				otherCharacterAboveRight = true;
			
			// On verifie si on peu monter a droite (si c'est libre)
			if((natureCurrentCell == Cell.HDR || natureCurrentCell == Cell.HOL)
					&& (natureNextCell == Cell.PLT || natureNextCell == Cell.MTL) 
					&& otherCharacterAboveRight == false 
					&& (natureCellAboveRight == Cell.EMP || natureCellAboveRight == Cell.LAD || natureCellAboveRight == Cell.HDR || natureCellAboveRight == Cell.HOL )) {
				hgt += 1;
				wdt += 1;
			}
		}
	}

	@Override
	public void goUp() {
		lastAction = Command.UP;
		// Le character ne peut pas monter plus
		if (hgt + 1 == getEnvi().getHeight())
			return;
		
		Cell natureCurrentCell = envi.cellNature(wdt, hgt);
		Cell natureNextCell = envi.cellNature(wdt, hgt + 1);

		Couple cellAboveContent = envi.cellContent(wdt, hgt + 1);
		
		boolean otherCharacterAbove = false;
		
		// S'il y a un personnage a la case en dessous de nous.
		if(cellAboveContent.getCharacter() != null)
			otherCharacterAbove = true;
		
		//System.out.println("GOUP: hgt < Height="+(hgt < envi.getHeight())+" currentCell="+natureCurrentCell+" nextCell="+natureNextCell+" charactAbove="+otherCharacterAbove);
		
		if (hgt < envi.getHeight()-1 && natureCurrentCell == Cell.LAD && (natureNextCell == Cell.LAD || natureNextCell == Cell.EMP || natureNextCell == Cell.HDR )  && !otherCharacterAbove) {
			hgt += 1;
		}
	}

	@Override
	public void goDown() {
		lastAction = Command.DOWN;
		
		// Le character ne peut pas descendre plus
		if (hgt - 1 < 0)
			return;
		
		Cell natureCurrentCell = envi.cellNature(wdt, hgt);
		Cell natureNextCell = envi.cellNature(wdt, hgt - 1);
		Couple cellBellowContent = envi.cellContent(wdt, hgt - 1);
		
		boolean otherCharacterBellow = false;
		
		// S'il y a un personnage a la case en dessous de nous.
		if(cellBellowContent.getCharacter() != null)
			otherCharacterBellow = true;
		
		//System.out.println("GODOWN: hgt > 1="+(hgt > 1)+" currentCell="+natureCurrentCell+" nextCell="+natureNextCell+" charactBellow="+otherCharacterBellow);
		
		if (hgt > 1 && (natureCurrentCell == Cell.EMP || natureCurrentCell == Cell.LAD || natureCurrentCell == Cell.HDR) && (natureNextCell == Cell.LAD || natureNextCell == Cell.EMP || natureNextCell == Cell.HDR  || natureNextCell == Cell.HOL) && !otherCharacterBellow) {
			hgt -= 1;
		}
	}
	
	@Override
	public void backInitialPosition() {
		this.wdt = initialHgt;
		this.hgt = initialHgt;
	}
}
