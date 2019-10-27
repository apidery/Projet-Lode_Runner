package contracts;

import contracts.errors.PostconditionError;
import contracts.errors.PreconditionError;
import data.Item;
import data.ItemType;
import decorators.ItemDecorator;
import services.Screen;

public class ItemContract extends ItemDecorator implements Item{

	public ItemContract(Item delegate) {
		super(delegate);
	}

	@Override
	public void init(Screen e, int x, int y, int id, ItemType nature) {
		
		if(nature != ItemType.TREASURE)
			throw new PreconditionError("Item : la nature est incorrect");
		
		super.init(e, x, y, id, nature);

		if(getId() != id)
			throw new PostconditionError("Item 1 : la l'id n'a pas été affectée");
		
		if(getNature() != nature)
			throw new PostconditionError("Item 2 : la nature n'a pas été affectée");
	}
}
