package impl;

import java.util.List;
import services.Character;
import data.Cell;
import data.Couple;
import data.Item;
import services.Environnement;
import services.Guard;
import services.Player;

public class EnvironnementImpl extends ScreenImpl implements Environnement {

	protected int wdtPortail;
	protected int hgtPortail;
	protected List<Guard> guards;
	protected List<Item> treasures;
	protected Player player;
	
	@Override
	public void init(int wdt, int hgt, Player player, List<Guard> guards, List<Item> trs, int wdtPortail, int hgtPortail) {
		super.init(wdt, hgt);
		this.wdtPortail = wdtPortail;
		this.hgtPortail = hgtPortail;
		this.player = player;
		this.guards = guards;
		this.treasures = trs;	
	}
	
	@Override
	public void init(int wdt, int hgt, Player player, List<Guard> guards, List<Item> trs, Cell[][] cells, int wdtPortail, int hgtPortail) {
		init(wdt,hgt, player, guards, trs, wdtPortail, hgtPortail);
		for(int i=0; i<wdt; i++) {
			for(int j=0; j<hgt; j++) 
				plateau[i][j] = cells[i][j];
		}
	}
	
	@Override
	public Player getPlayer() {
		return player;
	}
	
	@Override
	public List<Guard> getGuards() {
		return guards;
	}
	
	@Override
	public List<Item> getTreasures() {
		return treasures;
	}
	
	@Override
	public Couple cellContent(int x, int y) {
		Couple ret = new Couple();
		
		Character c = null;
		Item i = null;

		for (Character ch : guards) {
			if (ch.getWdt() == x && ch.getHgt() == y) {
				c = ch;
				break;
			}
		}

		for (Item it : treasures) {
			if (it.getWdt() == x && it.getHgt() == y) {
				i = it;
				break;
			}
		}

		ret.setCharacter(c);
		ret.setItem(i);

		return ret;
	}

	@Override
	public int getHgtPortail() {
		return this.hgtPortail;
	}

	@Override
	public int getWdtPortail() {
		return this.wdtPortail;
	}
}
