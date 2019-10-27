package decorators;

import data.Move;
import services.Guard;
import services.PathResolver;
import services.Screen;

public class GuardDecorator extends CharacterDecorator implements Guard{
	
	private Guard delegate;

	public GuardDecorator(Guard delegate) {
		super(delegate);
		this.delegate = delegate;
	}
		
	@Override
	public void init(Screen e, int x, int y, int id, PathResolver resolver) {
		super.init(e, x, y);
		delegate.init(e, x, y, id, resolver);
	}

	public int getId() {
		return delegate.getId();
	}

	public Move getBehaviour() {
		return delegate.getBehaviour();
	}

	@Override
	public PathResolver getResolver() {
		return delegate.getResolver();
	}

	public int getTimeInHole() {
		return delegate.getTimeInHole();
	}

	public void climbLeft() {
		delegate.climbLeft();
	}

	public void climbRight() {
		delegate.climbRight();
	}

	public void step() {
		delegate.step();
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((delegate == null) ? 0 : delegate.hashCode());
		return result;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GuardDecorator other = (GuardDecorator) obj;
		if (delegate == null) {
			if (other.delegate != null)
				return false;
		} else if (!delegate.equals(other.delegate))
			return false;
		return true;
	}

	
}
