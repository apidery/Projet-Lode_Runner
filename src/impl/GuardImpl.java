package impl;

import data.Cell;
import data.Command;
import data.Couple;
import data.Move;
import services.Guard;
import services.PathResolver;
import services.Screen;

public class GuardImpl extends CharacterImpl implements Guard {

	private Move behaviour;
	private int id;
	private PathResolver resolver;
	private int timeInHole;

	@Override
	public void init(Screen e, int x, int y, int id, PathResolver resolver) {
		super.init(e, x, y);
		this.id = id;
		timeInHole = 0;
		this.resolver = resolver;
		behaviour = Move.NEUTRAL;
	}

	@Override
	public void climbLeft() {
		// Utilisé pour suivre le comportement du garde dans les contrats
		lastAction = Command.LEFT;

		// Le climbLeft est impossible
		if (wdt - 1 < 0 || hgt + 1 >= envi.getHeight())
			return;

		Couple cellDestination = envi.cellContent(wdt - 1, hgt + 1);
		Cell currentCellNature = envi.cellNature(wdt, hgt);
		Cell destinationCellNature = envi.cellNature(wdt - 1, hgt + 1);

		// Si on est pas dans un trou, climb left n'a pa d'effet.
		if (currentCellNature != Cell.HOL)
			return;

		// S'il y a deja un personnage a la case ou on veut aller, on ne bouge pas
		if (cellDestination.getCharacter() != null)
			return;

		if (wdt != 0 && destinationCellNature != Cell.MTL && destinationCellNature != Cell.PLT) {
			wdt -= 1;
			hgt += 1;
		}
	}

	@Override
	public void climbRight() {
		// Utilisé pour suivre le comportement du garde dans les contrats
		lastAction = Command.RIGHT;

		if (wdt + 1 >= envi.getWidth() || hgt + 1 >= envi.getHeight())
			return;

		Couple cellDestination = envi.cellContent(wdt + 1, hgt + 1);
		Cell currentCellNature = envi.cellNature(wdt, hgt);
		Cell destinationCellNature = envi.cellNature(wdt + 1, hgt + 1);

		// Si on est pas dans un trou, climb right n'a pa d'effet.
		if (currentCellNature != Cell.HOL)
			return;

		// S'il y a deja un personnage a la case ou on veut aller, on ne bouge pas
		if (cellDestination.getCharacter() != null)
			return;

		if (wdt != 0 && destinationCellNature != Cell.MTL && destinationCellNature != Cell.PLT) {
			wdt += 1;
			hgt += 1;
		}
	}

	@Override
	public void step() {
		behaviour = resolver.getNextMoveToReachPlayer(this);

		Cell currentCellNature = envi.cellNature(wdt, hgt);
		Cell destinationCellNature = envi.cellNature(wdt, hgt - 1);
		Couple destinationCellContent = envi.cellContent(wdt, hgt - 1);

		if ((currentCellNature != Cell.HDR && currentCellNature != Cell.LAD && currentCellNature != Cell.HOL)
				&& (destinationCellNature == Cell.EMP || destinationCellNature == Cell.HOL || destinationCellNature == Cell.HDR)
				&& destinationCellContent.getCharacter() == null) {
			goDown();
		} else if (currentCellNature == Cell.HOL && getTimeInHole() < 2) {
			timeInHole++;
			lastAction = Command.NEUTRAL;
		} else if (currentCellNature == Cell.HOL && getTimeInHole() == 2 && behaviour == Move.LEFT) {
			climbLeft();
			timeInHole = 0;
		} else if (currentCellNature == Cell.HOL && getTimeInHole() == 2 && behaviour == Move.RIGHT) {
			climbRight();
			timeInHole = 0;
		} else if (currentCellNature == Cell.HOL && getTimeInHole() == 2 && behaviour == Move.NEUTRAL) {
			timeInHole = 0; // Pas d'effet visible si NEUTRAL
			lastAction = Command.NEUTRAL;
		} else if (behaviour == Move.LEFT) {
			goLeft();
		} else if (behaviour == Move.RIGHT) {
			goRight();
		} else if (behaviour == Move.UP) {
			goUp();
		} else if (behaviour == Move.DOWN) {
			goDown();
		} else { // NEUTRAL
			lastAction = Command.NEUTRAL;
		}
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public Move getBehaviour() {
		return behaviour;
	}

	@Override
	public PathResolver getResolver() {
		return resolver;
	}

	@Override
	public int getTimeInHole() {
		return timeInHole;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GuardImpl other = (GuardImpl) obj;
		if (id != other.id)
			return false;
		return true;
	}

	
}
