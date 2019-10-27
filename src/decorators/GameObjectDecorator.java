package decorators;

import services.GameObject;
import services.Screen;

public class GameObjectDecorator implements GameObject{
	private GameObject delegate;

	public GameObjectDecorator(GameObject delegate) {
		this.delegate = delegate;
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
	public Screen getScreen() {
		return delegate.getScreen();
	}

	@Override
	public void init(Screen e, int x, int y) {
		delegate.init(e, x, y);
	}
}