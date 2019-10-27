package contracts;

import contracts.errors.PostconditionError;
import decorators.BoardDecorator;
import impl.LodeRunnerFrame;
import services.Board;
import services.Engine;
import services.Environnement;

public class BoardContract extends BoardDecorator{

	public BoardContract(Board delegate) {
		super(delegate);
	}
	
	@Override
	public void init(Engine engine, Environnement envi, LodeRunnerFrame frame) {
		super.init(engine, envi, frame);
		
		if(getEngine() != engine)
			throw new PostconditionError("BOARD init: l'engine n'a pas été affecté.");
		
		if(getEnvi() != envi)
			throw new PostconditionError("BOARD init: l'environnement n'a pas été affecté.");
		
		if(getFrame() != frame)
			throw new PostconditionError("BOARD init: le frame n'a pas été affecté.");
		
		if(getBWidth() != getBLOCSize() * envi.getWidth())
			throw new PostconditionError("BOARD init: le 'WIDTH' n'a pas été initialisé correctement.");
		
		if(getBHeight() != getBLOCSize() * envi.getHeight())
			throw new PostconditionError("BOARD init: le 'HEIGHT' n'a pas été initialisé correctement.");
		
		if(getBLOCSize() != 30)
			throw new PostconditionError("BOARD init: Les 'BLOC' doivent être de taille 30.");
		
		if(getPLAYERSize() != 30)
			throw new PostconditionError("BOARD init: Le 'PLAYER' doit être de taille 30.");
		
		if(getGUARDSize() != 30)
			throw new PostconditionError("BOARD init: Les 'GUARD' doivent être de taille 30.");
		
		if(getTREASURESize() != 30)
			throw new PostconditionError("BOARD init: Les 'TREASURE' doivent être de taille 30.");
		
		if(getPLTSize() != 30)
			throw new PostconditionError("BOARD init: Les 'PLT' doivent être de taille 30.");
		
		if(getMTLSize() != 30)
			throw new PostconditionError("BOARD init: Les 'MTL' doivent être de taille 30.");
		
		if(getHDRSize() != 30)
			throw new PostconditionError("BOARD init: Les 'HDR' doivent être de taille 30.");
		
		if(getLADSize() != 30)
			throw new PostconditionError("BOARD init: Les 'LAD' doivent être de taille 30.");
		
		if(getPRTSize() != 30)
			throw new PostconditionError("BOARD init: Les 'PRT' doivent être de taille 30.");
		
		if(getATKSize() != 30)
			throw new PostconditionError("BOARD init: Les 'ATK' doivent être de taille 30.");
		
		if(getDELAY() != 120)
			throw new PostconditionError("BOARD init: Le 'DELAY' doit être de 120.");
	}
	
	@Override
	public void nextLevel(Environnement envi) {
		boolean displayAt_pre = getDisplayPortal();
		
		super.nextLevel(envi);
		
		if(getEnvi() != envi)
			throw new PostconditionError("BOARD nextLevel: le nouvel environnement n'a pas été affecté.");
		
		if(getDisplayPortal() != displayAt_pre)
			throw new PostconditionError("BOARD nextLevel: la valeur de displayPortal a changé.");
	}

	@Override
	public void displayPortal(boolean display) {
		
		Environnement enviAt_pre = getEnvi();
		
		super.displayPortal(display);
		
		if(getDisplayPortal() != display)
			throw new PostconditionError("BOARD displayPortal : displayPortal n'a pas été modifié correctement.");
		
		if(getEnvi() != enviAt_pre)
			throw new PostconditionError("BOARD displayPortal : l'environnement a changé");
	}
}
