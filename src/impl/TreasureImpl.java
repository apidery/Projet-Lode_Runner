package impl;

import data.Item;
import data.ItemType;
import services.Screen;

public class TreasureImpl implements Item{
	private ItemType nature;
	private int id;
	private int hgt;
	private int wdt;
	private Screen screen;
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public ItemType getNature() {
		return nature;
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
	public Screen getScreen() {
		return screen;
	}

	@Override
	public void init(Screen e, int x, int y) {
		screen = e;
		wdt = x;
		hgt = y;
	}

	@Override
	public void init(Screen e, int x, int y, int id, ItemType nature) {
		init(e, x, y);
		this.id = id;
		this.nature = nature;
	}
}
