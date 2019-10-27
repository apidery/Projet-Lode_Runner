package decorators;

import data.Cell;
import services.Screen;

public class ScreenDecorator implements Screen {
	
	private Screen delegate;
	
	public int getHeight() {
		return delegate.getHeight();
	}

	public int getWidth() {
		return delegate.getWidth();
	}

	public Cell cellNature(int x, int y) {
		return delegate.cellNature(x, y);
	}

	public void init(int width, int height) {
		delegate.init(width, height);
	}

	public void dig(int x, int y) {
		delegate.dig(x, y);
	}

	public void fill(int x, int y) {
		delegate.fill(x, y);
	}

	public ScreenDecorator(Screen delegate) {
		this.delegate = delegate;
	}
	
}
