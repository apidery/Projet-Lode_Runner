package contracts;

import contracts.errors.PostconditionError;
import contracts.errors.PreconditionError;
import data.Direction;
import decorators.AttackDecorator;
import services.Attack;
import services.Screen;

public class AttackContract  extends AttackDecorator implements Attack{

	public AttackContract(Attack delegate) {
		super(delegate);
	}

	@Override
	public void init(Screen e, int x, int y, Direction direction) {
		if( !( direction == Direction.LEFT || direction == Direction.RIGHT))
			throw new PreconditionError("ATTACK init : la direction n'est pas bonne.");
		
		super.init(e, x, y, direction);
		
		if(getDirection() != direction)
			throw new PostconditionError("ATTACK 1 init : la direction n'a pas été affectée.");
	}

	@Override
	public void step() {
		// Capture
		int hgtAt_pre = getHgt();
		int wdtAt_pre = getWdt();
		
		if(!(getWdt() >= 0 && getWdt() < getScreen().getWidth()))
			throw new PreconditionError("ATTACK step: le x n'est pas correct.");
		
		super.step();
		
		if(getHgt() != hgtAt_pre)
			throw new PostconditionError("ATTACK step 3 : le hgt a changé.");
		
		if(getDirection() == Direction.LEFT)
			if(getWdt() != wdtAt_pre - 1)
				throw new PostconditionError("ATTACK step 5 : le wdt n'a pas changé correctement pour la direction LEFT.");
		
			else if(getDirection() == Direction.RIGHT)
			if(getWdt() != wdtAt_pre + 1)
				throw new PostconditionError("ATTACK step 5 : le wdt n'a pas changé correctement pour la direction RIGHT.");
	}
}
