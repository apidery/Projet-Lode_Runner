package decorators;

import data.Item;
import data.ItemType;
import services.Screen;

public class ItemDecorator extends GameObjectDecorator implements Item{

	private Item delegate;
	
	public ItemDecorator(Item delegate) {
		super(delegate);
		this.delegate = delegate;
	}
	
	@Override
	public int getId() {
		return delegate.getId();
	}

	@Override
	public ItemType getNature() {
		return delegate.getNature();
	}

	@Override
	public void init(Screen e, int x, int y, int id, ItemType nature) {
		delegate.init(e, x, y, id, nature);
	}
}
