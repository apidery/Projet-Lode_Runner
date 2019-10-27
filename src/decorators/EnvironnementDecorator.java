package decorators;


import java.util.List;

import data.Cell;
import data.Couple;
import data.Item;
import services.Environnement;
import services.Guard;
import services.Player;

public class EnvironnementDecorator extends ScreenDecorator implements Environnement{
	
	private Environnement delegate;
	
	public EnvironnementDecorator(Environnement delegate) {
		super(delegate);
		this.delegate = delegate;
	}
	
	@Override
	public Player getPlayer() {
		return delegate.getPlayer();
	}
	
	@Override
	public List<Guard> getGuards() {
		return delegate.getGuards();
	}

	@Override
	public List<Item> getTreasures() {
		return delegate.getTreasures();
	}

	@Override
	public Couple cellContent(int x, int y) {
		return delegate.cellContent(x, y);
	}
	
	@Override
	public void init(int wdt, int hgt, Player player, List<Guard> gs, List<Item> trs, int wdtPortail, int hgtPortail) {
		super.init(wdt, hgt);
		delegate.init(wdt, hgt, player, gs, trs, wdtPortail, hgtPortail);
	}

	@Override
	public void init(int wdt, int hgt, Player player, List<Guard> guards, List<Item> treasures, Cell[][] cells, int wdtPortail, int hgtPortail) {
		super.init(wdt, hgt);
		delegate.init(wdt, hgt, player, guards, treasures, cells, wdtPortail, hgtPortail);
	}

	@Override
	public int getHgtPortail() {
		return delegate.getHgtPortail();
	}

	@Override
	public int getWdtPortail() {
		return delegate.getWdtPortail();
	}
}
