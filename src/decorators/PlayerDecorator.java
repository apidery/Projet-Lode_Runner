package decorators;

import services.Engine;
import services.Player;
import services.Screen;

public class PlayerDecorator extends CharacterDecorator implements Player {
	
	private Player delegate;
	
	public PlayerDecorator(Player delegate) {
		super(delegate);
		this.delegate = delegate;
	}

	@Override
	public void init(Screen e, int x, int y, Engine engine) {
		super.init(e, x, y);
		delegate.init(e, x, y, engine);
	}
	
	public Engine getEngine() {
		return delegate.getEngine();
	}

	public void step() {
		delegate.step();
	}

	@Override
	public int getVie() {
		return delegate.getVie();
	}

	@Override
	public void decreaseVie() {
		delegate.decreaseVie();
	}

	@Override
	public int getNbAttacks() {
		return delegate.getNbAttacks();
	}

	@Override
	public void decreaseNbAttacks() {
		delegate.decreaseNbAttacks();
	}
}
