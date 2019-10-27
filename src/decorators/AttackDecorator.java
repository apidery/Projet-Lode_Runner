package decorators;

import data.Direction;
import services.Attack;
import services.Screen;

public class AttackDecorator extends GameObjectDecorator implements Attack{

	private Attack delegate;
	
	public AttackDecorator(Attack delegate) {
		super(delegate);
		this.delegate = delegate;
	}

	@Override
	public Direction getDirection() {
		return delegate.getDirection();
	}

	@Override
	public void init(Screen e, int x, int y, Direction direction) {
		delegate.init(e, x, y, direction);
	}

	@Override
	public void step() {
		delegate.step();
	}
}
