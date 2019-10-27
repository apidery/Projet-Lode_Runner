package decorators;

import data.Cell;
import services.EditableScreen;

public class EditableScreenDecorator extends ScreenDecorator implements EditableScreen{
	
	private EditableScreen delegate;
	
	public EditableScreenDecorator(EditableScreen delegate) {
		super(delegate);
		this.delegate = delegate;
	}

	@Override
	public boolean playable() {
		return delegate.playable();
	}

	@Override
	public void setNature(int x, int y, Cell c) {
		delegate.setNature(x, y, c);
	}

	@Override
	public Cell[][] getPlayableEnvi() {
		return delegate.getPlayableEnvi();
	}
}
