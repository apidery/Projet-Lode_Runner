package decorators;


import impl.LodeRunnerFrame;
import services.Board;
import services.Engine;
import services.Environnement;

public class BoardDecorator implements Board{

	private Board delegate;
	
	public BoardDecorator(Board delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public Engine getEngine() {
		return delegate.getEngine();
	}

	@Override
	public LodeRunnerFrame getFrame() {
		return delegate.getFrame();
	}

	@Override
	public Environnement getEnvi() {
		return delegate.getEnvi();
	}

	@Override
	public boolean getDisplayPortal() {
		return delegate.getDisplayPortal();
	}

	@Override
	public void init(Engine engine, Environnement envi, LodeRunnerFrame frame) {
		delegate.init(engine, envi, frame);
	}

	@Override
	public void nextLevel(Environnement envi) {
		delegate.nextLevel(envi);
	}

	@Override
	public void displayPortal(boolean display) {
		delegate.displayPortal(display);
	}

	@Override
	public int getBWidth() {
		return delegate.getBWidth();
	}

	@Override
	public int getBHeight() {
		return delegate.getBHeight();
	}

	@Override
	public int getBLOCSize() {
		return delegate.getBLOCSize();
	}

	@Override
	public int getPLAYERSize() {
		return delegate.getPLAYERSize();
	}

	@Override
	public int getGUARDSize() {
		return delegate.getGUARDSize();
	}

	@Override
	public int getTREASURESize() {
		return delegate.getTREASURESize();
	}

	@Override
	public int getPLTSize() {
		return delegate.getPLTSize();
	}

	@Override
	public int getMTLSize() {
		return delegate.getMTLSize();
	}

	@Override
	public int getHDRSize() {
		return delegate.getHDRSize();
	}

	@Override
	public int getLADSize() {
		return delegate.getLADSize();
	}

	@Override
	public int getPRTSize() {
		return delegate.getPRTSize();
	}

	@Override
	public int getATKSize() {
		return delegate.getATKSize();
	}

	@Override
	public int getDELAY() {
		return delegate.getDELAY();
	}
}
