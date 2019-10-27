package decorators;

import services.Character;
import data.Command;
import services.*;

public class CharacterDecorator implements Character {

	private Character delegate;
	
	public CharacterDecorator(Character delegate) {
		this.delegate = delegate;
	}

	@Override
	public Environnement getEnvi() {
		return delegate.getEnvi();
	}

	@Override
	public int getHgt() {
		return delegate.getHgt();
	}

	@Override
	public int getWdt() {
		return delegate.getWdt();
	}
	
	@Override
	public int getInitialWdt() {
		return delegate.getInitialWdt();
	}

	@Override
	public int getInitialHgt() {
		return delegate.getInitialHgt();
	}
	
	@Override
	public Command getLastAction() {
		return delegate.getLastAction();
	}

	@Override
	public void init(Screen e, int x, int y) {
		delegate.init(e, x, y);
	}

	@Override
	public void goLeft() {
		delegate.goLeft();
	}

	@Override
	public void goRight() {
		delegate.goRight();
	}

	@Override
	public void goUp() {
		delegate.goUp();
	}

	@Override
	public void goDown() {
		delegate.goDown();
	}

	@Override
	public void backInitialPosition() {
		delegate.backInitialPosition();
	}
}
