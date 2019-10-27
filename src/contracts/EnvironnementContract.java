package contracts;


import java.util.List;

import contracts.errors.InvariantError;
import contracts.errors.PostconditionError;
import contracts.errors.PreconditionError;
import data.Cell;
import data.Couple;
import data.Item;
import decorators.EnvironnementDecorator;
import services.Environnement;
import services.Guard;
import services.Player;

public class EnvironnementContract extends EnvironnementDecorator {

	public EnvironnementContract(Environnement delegate) {
		super(delegate);
	}
	
	public void checkInvariants() {
		// Invariant 1
		for(int x=0; x<super.getWidth(); x++) {
			for(int y=0; y<super.getHeight(); y++) {
				Couple c1 = super.cellContent(x, y);
				Couple c2 = super.cellContent(x, y);
				if (c1.getCharacter() != c2.getCharacter() || c1.getItem() != c2.getItem())
					throw new InvariantError("ENVIRONNEMENT invariant n°1 : for all Character c1, c2 in CellContent(E,x,y), c1 = c2");
			}
		}
		
		// Invariant 2
		for(int x=0; x<super.getWidth(); x++) {
			for(int y=0; y<super.getHeight(); y++) {
				if (super.cellNature(x, y) == Cell.MTL || super.cellNature(x, y) == Cell.PLT) {
					Couple c = super.cellContent(x, y);
					if (c.getCharacter() != null || c.getItem() != null) 
						throw new InvariantError("ENVIRONNEMENT invariant n°2 : CellContent(E,x,y) == null");
				}
			}
		}
		// Invariant 3
		for(int x=0; x<super.getWidth(); x++) {
			for(int y=1; y<super.getHeight(); y++) {
				if (super.cellContent(x, y).getItem() != null)
					if (!(super.cellNature(x, y) == Cell.EMP && (cellNature(x, y-1) == Cell.PLT || cellNature(x, y-1) == Cell.MTL) )) {
						System.out.println("x="+x+" y="+y);
						System.out.println(super.cellNature(x, y));
						System.out.println(super.cellNature(x, y-1));
						throw new InvariantError("ENVIRONNEMENT invariant n°3 : CellNature(E,x,y) == EMP && CellNature(E,x,y-1) ∈ {PLT,MTL}");
					}
			}
		}
	}
	
	@Override
	public void init(int wdt, int hgt, Player player, List<Guard> gs, List<Item> trs, int wdtPortail, int hgtPortail) {
		super.init(wdt, hgt, player, gs, trs, wdtPortail, hgtPortail);
		
		if (getPlayer() != player)
			throw new PostconditionError("ENVIRONNEMENT init 1 : player pas affecté à l'environnement");
		
		if(getHgtPortail() != hgtPortail)
			throw new PostconditionError("ENVIRONNEMENT init 2 : largeur du portail pas été affecté");
		
		if(getHgtPortail() != hgtPortail)
			throw new PostconditionError("ENVIRONNEMENT init 3 : hauteur du portail pas été affecté");
		
		for(int i=0; i<gs.size(); i++) {
			if (gs.get(i) != getGuards().get(i))
				throw new PostconditionError("ENVIRONNEMENT init 4 : Le guard " + i + "n'est pas affecté à l'environnement");
		}
		
		for(int i=0; i<trs.size(); i++) {
			if (trs.get(i) != getTreasures().get(i))
				throw new PostconditionError("ENVIRONNEMENT init 5 : Le trésor " + i + "n'est pas affecté à l'environnement");
		}
		checkInvariants();
	}
	
	@Override
	public void init(int wdt, int hgt, Player player, List<Guard> gs, List<Item> trs, Cell[][] cells, int wdtPortail, int hgtPortail) {
		if (wdt != cells.length || hgt != cells[0].length)
			throw new PreconditionError("ENVIRONNEMENT init 1 : La taille du plateau et celle des arguments sont différentes");
		
		super.init(wdt, hgt, player, gs, trs, cells, wdtPortail, hgtPortail);
		
		if (getPlayer() != player)
			throw new PostconditionError("ENVIRONNEMENT init 1 : player pas affecté à l'environnement");
		
		if(getHgtPortail() != hgtPortail)
			throw new PostconditionError("ENVIRONNEMENT init 2 : largeur du portail pas été affecté");
		
		if(getHgtPortail() != hgtPortail)
			throw new PostconditionError("ENVIRONNEMENT init 3 : hauteur du portail pas été affecté");
		
		
		for(int i=0; i<gs.size(); i++) {
			if (gs.get(i) != getGuards().get(i))
				throw new PostconditionError("ENVIRONNEMENT init 4 : Le guard " + i + "n'est pas affecté à l'environnement");
		}
		
		for(int i=0; i<trs.size(); i++) {
			if (trs.get(i) != getTreasures().get(i))
				throw new PostconditionError("ENVIRONNEMENT init 5 : Le trésor " + i + "n'est pas affecté à l'environnement");
		}
		for(int i=0; i<wdt; i++) {
			for(int j=0; j<hgt; j++) {
				if (cellNature(i, j) != cells[i][j])
					throw new PostconditionError("ENVIRONNEMENT init 6 : Le plateau de l'environnement ne correspond pas à celui passé en argument");
			}
		}
		
		checkInvariants();
	}

	@Override
	public Couple cellContent(int x, int y) {
		checkInvariants();
		if (!(0 <= y && y < getHeight() && 0 <= x && x < getWidth()))
			throw new PreconditionError("ENVIRONNEMENT cellContent : 0 <= y < height(E) and 0 <= x < width(E)");
		
		Couple res = super.cellContent(x, y);
		checkInvariants();
		
		return res;
	}
}
