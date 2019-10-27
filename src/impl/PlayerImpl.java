package impl;

import data.Cell;
import data.Command;
import data.Couple;
import services.Engine;
import services.Player;
import services.Screen;

public class PlayerImpl extends CharacterImpl implements Player {

	private Engine engine;
	private int vie;
	private int nbAttacks;
	
	@Override
	public void init(Screen e, int x, int y, Engine engine) {
		super.init(e, x, y);
		this.engine = engine;
		this.vie = 3;
		this.nbAttacks = 3;
	}
	
	@Override
	public Engine getEngine() {
		return engine;
	}

	@Override
	public void step() {
		Cell currentCellNature = envi.cellNature(wdt, hgt);
		Cell cellBellowNature = envi.cellNature(wdt, hgt - 1);
		Couple cellBellowContent = envi.cellContent(wdt, hgt - 1);
		
		if ((currentCellNature != Cell.HDR && currentCellNature != Cell.LAD && currentCellNature != Cell.HOL)
				&& (cellBellowNature == Cell.EMP || cellBellowNature == Cell.HOL || cellBellowNature == Cell.HDR)
				&& cellBellowContent.getCharacter() == null ) {
			goDown();
		} 
		else if (engine.getNextCommand() == Command.LEFT) {
			goLeft();
		} else if (engine.getNextCommand() == Command.RIGHT) {
			goRight();
		} else if (engine.getNextCommand() == Command.UP) {
			goUp();
		} else if (engine.getNextCommand() == Command.DOWN) {
			goDown();
		} else {
			if (engine.getNextCommand() == Command.DIGL && wdt-1 >= 0 && hgt-1 >= 0) {
				Cell targetCellNature = envi.cellNature(wdt - 1, hgt - 1);
				Couple leftCellContent = envi.cellContent(wdt - 1, hgt);
				
				if (targetCellNature == Cell.PLT && leftCellContent.getCharacter() == null && leftCellContent.getItem() == null) {
					engine.addHole(wdt - 1, hgt - 1);
					lastAction = Command.DIGL;
				}

			} else if (engine.getNextCommand() == Command.DIGR && wdt+1 < getEnvi().getWidth() && hgt-1 >= 0) {
				Cell targetCellNature = envi.cellNature(wdt + 1, hgt - 1);
				Couple rightCellContent = envi.cellContent(wdt + 1, hgt);
								
				if (targetCellNature == Cell.PLT && rightCellContent.getCharacter() == null && rightCellContent.getItem() == null) {
					engine.addHole(wdt + 1, hgt - 1);
					lastAction = Command.DIGR;
				}
			}
		}
	}

	@Override
	public int getVie() {
		return this.vie;
	}

	@Override
	public void decreaseVie() {
		this.vie--;
	}
	
	@Override
	public void decreaseNbAttacks() {
		this.nbAttacks--;
	}
	
	@Override
	public int getNbAttacks() {
		return nbAttacks;
	}
}
