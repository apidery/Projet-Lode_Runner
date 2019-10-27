package services;

import impl.LodeRunnerFrame;

public interface Board{

	///////////////////////////////
	///////// OBSERVATORS /////////
	///////////////////////////////
	// CONST
	public Engine getEngine();
	
	// CONST
	public LodeRunnerFrame getFrame();
	
	// CONST
	public int getBWidth();
	
	// CONST
	public int getBHeight();
	
	// CONST
	public int getBLOCSize();
	
	// CONST
	public int getPLAYERSize();
	
	// CONST
	public int getGUARDSize();
	
	// CONST
	public int getTREASURESize();
	
	// CONST
	public int getPLTSize();
	
	// CONST
	public int getMTLSize();
	
	// CONST
	public int getHDRSize();
	
	// CONST
	public int getLADSize();
	
	// CONST
	public int getPRTSize();
	
	// CONST
	public int getATKSize();
	
	// CONST
	public int getDELAY();
	
	public Environnement getEnvi();
	
	public boolean getDisplayPortal();
	
	///////////////////////////////
	//////// CONSTRUCTORS /////////
	///////////////////////////////
	/**
	 * post: Engine(init(eng, envi, frame)) == eng
	 * 
	 * post: Environnement(init(eng, envi, frame)) == envi
	 * 
	 * post: Frame(init(eng, envi, frame)) = frame
	 * 
	 * post: WIDTH(init(eng, envi, frame)) == BLOC_SIZE * Environnement::Wdt(envi) 
	 * 
 	 * post: HEIGHT(init(eng, envi, frame)) == BLOC_SIZE * Environnement::Hgt(eng) 
 	 * 
 	 * post: BLOC_SIZE(init(eng, envi, frame)) == 30
	 * 
	 * post: PLAYER_SIZE(init(eng, envi, frame)) == 30
	 * 
	 * post: GUARD_SIZE(init(eng, envi, frame)) == 30
	 * 
	 * post: TREASURE_SIZE(init(eng, envi, frame)) == 30
	 * 
	 * post: PLT_SIZE(init(eng, envi, frame)) == 30
	 * 
	 * post: MTL_SIZE(init(eng, envi, frame)) == 30
	 * 
	 * post: LAD_SIZE(init(eng, envi, frame)) == 30
	 * 
	 * post: TREASURE_SIZE(init(eng, envi, frame)) == 30
	 *
	 * post: HDR_SIZE(init(eng, envi, frame)) == 30
	 * 
	 * post: PRT_SIZE(init(eng, envi, frame)) == 30
	 * 
	 * post: ATK_SIZE(init(eng, envi, frame)) == 30
	 *
	 * post: DELAY(init(eng, envi, frame)) == 120
	 * 
 	 * post: DisplayPortal(init(eng, envi, frame)) == false
	 */
	void init(Engine engine, Environnement envi, LodeRunnerFrame frame);

	///////////////////////////////
	////////// OPERATORS //////////
	///////////////////////////////
	/**
	 * post: Envi(nextLevel(B, env)) == env
	 * 
	 * post: DisplayPortal(nextLevel(B, env)) == false
	 */
	public void nextLevel(Environnement envi);
		
	/**
	 * post: DisplayPortal(displayPortal(B, dis)) == dis
	 * 
	 * post: Envi(displayPortal(B, dis)) == Envi(B)
	 */
	public void displayPortal(boolean display);

}
