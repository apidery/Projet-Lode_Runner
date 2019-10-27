package contracts;

import contracts.errors.PostconditionError;
import contracts.errors.PreconditionError;
import decorators.GameObjectDecorator;
import services.GameObject;
import services.Screen;

public class GameObjectContract extends GameObjectDecorator {

	public GameObjectContract(GameObject delegate) {
		super(delegate);
	}

	@Override
	public int getHgt() {
		return super.getHgt();
	}

	@Override
	public int getWdt() {
		return super.getWdt();
	}

	@Override
	public void init(Screen e, int x, int y) {
		if (!( 0 < x &&  x < e.getWidth() && 0 < y && y <  e.getHeight()))
			throw new PreconditionError("GameObject init : les coordonnées sont incohérente");
		
		super.init(e, x, y);
	
		if(getWdt() != x)
			throw new PostconditionError("GameObject init 1 : le x ne correspond pas.");
		
		if(getHgt() != y)
			throw new PostconditionError("GameObject init 2 : le y ne correspond pas.");
	
		if(getScreen() != e)
			throw new PostconditionError("GameObject init 3 : le screen ne correspond pas.");
	}
}
