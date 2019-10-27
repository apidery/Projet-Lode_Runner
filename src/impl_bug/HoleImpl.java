package impl_bug;

import services.GameObject;
import services.Screen;

public class HoleImpl implements GameObject{
	private int wdt;
	private int hgt;
	private Screen screen;
	
	@Override
	public void init(Screen e, int wdt, int hgt) {
		this.wdt = wdt;
		this.hgt = hgt;
		this.screen = e;
	}
	
	@Override
	public int getWdt() {
		return wdt;
	}
	
	@Override
	public int getHgt() {
		return hgt;
	}

	@Override
	public Screen getScreen() {
		return screen;
	}
}
