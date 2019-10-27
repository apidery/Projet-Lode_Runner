package impl_bug;

import data.Direction;
import services.Attack;
import services.Screen;

public class AttackImpl implements Attack {

	private int hgt;
	private int wdt;
	private Screen screen;
	private Direction direction;

	@Override
	public void init(Screen e, int x, int y) {
		this.screen = e;
		this.wdt = x;
		this.hgt = y;
	}

	@Override
	public void init(Screen e, int x, int y, Direction direction) {
		init(e, x, y);
		this.direction = direction;
	}

	@Override
	public void step() {
		if(direction == Direction.LEFT)
			wdt += 1;
		else
			wdt += 1;
	}
	
	@Override
	public int getHgt() {
		return this.hgt;
	}

	@Override
	public int getWdt() {
		return this.wdt;
	}

	@Override
	public Screen getScreen() {
		return this.screen;
	}

	@Override
	public Direction getDirection() {
		return this.direction;
	}
}
