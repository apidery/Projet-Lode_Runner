package contracts;


import java.util.List;

import contracts.errors.InvariantError;
import contracts.errors.PostconditionError;
import contracts.errors.PreconditionError;
import data.Cell;
import data.Item;
import data.Move;
import decorators.PathResolverDecorator;
import services.Environnement;
import services.Guard;
import services.PathResolver;
import services.Player;

public class PathResolverContract extends PathResolverDecorator{

	public PathResolverContract(PathResolver delegate) {
		super(delegate);
	}
	
	public void checkInvariant() {
		List<Integer>[] adjacencyList = getGraphe();
		for(int node_id = 0; node_id<adjacencyList.length; node_id++) {
			Cell node_idNature = getNatureOfNode(node_id);
			for(Integer neighbour_idInteger : adjacencyList[node_id]) {
				int neighbour_id = neighbour_idInteger.intValue();
				Cell neighbour_idNature = getNatureOfNode(neighbour_id);
				
				boolean goLeft = false, goRight = false;
				boolean goLeftToNewHDR = false, goRightToNewHDR = false;
				boolean goLeftFromHDR = false, goRightFromHDR = false;
				boolean goUp = false, goDown = false;
				
				if (getHgtOfNode(node_id)-1 >= 0) {
					Cell node_idBellowNature = getEnvi().cellNature(getWdtOfNode(node_id), getHgtOfNode(node_id)-1);
					boolean hasCharacterBellow = (getEnvi().cellContent(getWdtOfNode(node_id), getHgtOfNode(node_id)-1).getCharacter() != null);
					// goLeft
					if (getWdtOfNode(node_id) > 0
							&& getWdtOfNode(neighbour_id) == getWdtOfNode(node_id) - 1
							&& getHgtOfNode(neighbour_id) == getHgtOfNode(node_id)
							&& (node_idNature != Cell.PLT && node_idNature != Cell.MTL)
							&& (neighbour_idNature != Cell.PLT && neighbour_idNature != Cell.MTL)
							&& ( (node_idNature == Cell.LAD || node_idNature == Cell.HDR || node_idNature == Cell.HOL)
									|| (node_idBellowNature == Cell.PLT || node_idBellowNature == Cell.MTL || node_idBellowNature == Cell.LAD)
									|| hasCharacterBellow)
							)
						goLeft = true;
					
					// goRight
					if (getWdtOfNode(node_id) < (getEnvi().getWidth() - 1)
							&& getWdtOfNode(neighbour_id) == getWdtOfNode(node_id) + 1
							&& getHgtOfNode(neighbour_id) == getHgtOfNode(node_id)
							&& (node_idNature != Cell.PLT && node_idNature != Cell.MTL)
							&& (neighbour_idNature != Cell.PLT && neighbour_idNature != Cell.MTL)
							&& ( (node_idNature == Cell.LAD || node_idNature == Cell.HDR || node_idNature == Cell.HOL)
									|| (node_idBellowNature == Cell.PLT || node_idBellowNature == Cell.MTL || node_idBellowNature == Cell.LAD)
									|| hasCharacterBellow)
						)
						goRight = true;
				}
				// goUp
				if (getHgtOfNode(node_id) < (getEnvi().getHeight() - 1)
						&& getWdtOfNode(neighbour_id) == getWdtOfNode(node_id)
						&& getHgtOfNode(neighbour_id) == getHgtOfNode(node_id) + 1 
						&& node_idNature == Cell.LAD
						&& (neighbour_idNature == Cell.LAD || neighbour_idNature == Cell.EMP)
					)
					goUp = true;
				
				// goDown
				if (getHgtOfNode(node_id) > 0
						&& getWdtOfNode(neighbour_id) == getWdtOfNode(node_id)
						&& getHgtOfNode(neighbour_id) == getHgtOfNode(node_id) - 1 
						&& (node_idNature == Cell.LAD || node_idNature == Cell.EMP || node_idNature == Cell.HDR)
						&& (neighbour_idNature == Cell.LAD || neighbour_idNature == Cell.HDR || neighbour_idNature == Cell.EMP || neighbour_idNature == Cell.HOL)
					)
					goDown = true;
			
				// go to new HDR with goLeft
				if (getWdtOfNode(node_id) > 0
						&& getWdtOfNode(neighbour_id) == getWdtOfNode(node_id) - 1
						&& getHgtOfNode(neighbour_id) == getHgtOfNode(node_id) - 1
						&& (node_idNature != Cell.PLT && node_idNature != Cell.MTL && node_idNature != Cell.HDR && node_idNature != Cell.LAD && node_idNature != Cell.HOL)
						&& neighbour_idNature == Cell.HDR
						)
					goLeftToNewHDR = true;
				
				// go to new HDR with goRight
				if (getWdtOfNode(node_id) < (getEnvi().getWidth() - 1)
						&& getWdtOfNode(neighbour_id) == getWdtOfNode(node_id) + 1
						&& getHgtOfNode(neighbour_id) == getHgtOfNode(node_id) - 1
						&& (node_idNature != Cell.PLT && node_idNature != Cell.MTL && node_idNature != Cell.HDR && node_idNature != Cell.LAD && node_idNature != Cell.HOL)
						&& neighbour_idNature == Cell.HDR
						)
					goRightToNewHDR = true;
				
				if (getWdtOfNode(node_id) > 0) {
					Cell node_idLeftNature = getEnvi().cellNature(getWdtOfNode(node_id) - 1, getHgtOfNode(node_id));
					// go to platform from HDR/HOL with goLeft
					if (getWdtOfNode(neighbour_id) == getWdtOfNode(node_id) - 1
							&& getHgtOfNode(neighbour_id) == getHgtOfNode(node_id) + 1
							&& (node_idNature == Cell.HDR || node_idNature == Cell.HOL)
							&& (node_idLeftNature == Cell.PLT || node_idLeftNature == Cell.MTL)
							&& (neighbour_idNature == Cell.EMP || neighbour_idNature == Cell.LAD || neighbour_idNature == Cell.HDR || neighbour_idNature == Cell.HOL)
							)
						goLeftFromHDR = true;
				}
				if (getWdtOfNode(node_id) + 1 < getEnvi().getWidth()) {
					Cell node_idRightNature = getEnvi().cellNature(getWdtOfNode(node_id) + 1, getHgtOfNode(node_id));
					// go to platform from HDR/HOL with goRight
					if (getWdtOfNode(neighbour_id) == getWdtOfNode(node_id) + 1
							&& getHgtOfNode(neighbour_id) == getHgtOfNode(node_id) + 1
							&& (node_idNature == Cell.HDR || node_idNature == Cell.HOL)
							&& (node_idRightNature == Cell.PLT || node_idRightNature == Cell.MTL)
							&& (neighbour_idNature == Cell.EMP || neighbour_idNature == Cell.LAD || neighbour_idNature == Cell.HDR || neighbour_idNature == Cell.HOL)
							)
						goRightFromHDR = true;				
				}
				if (!goLeft && !goRight && !goUp && !goDown
						&& !goLeftToNewHDR && !goRightToNewHDR && !goLeftFromHDR && !goRightFromHDR) 
					throw new InvariantError("PATH_RESOLVER : Un noeud est voisin d'un noeud alors qu'il en devrait pas l'être.\n"
							+ "Node " + getWdtOfNode(node_id) + ":" + getHgtOfNode(node_id) + " => " + getWdtOfNode(neighbour_id) + ":" + getHgtOfNode(neighbour_id));
				
			}// for neighbour_id
		}// node_id
	}
	
	@Override
	public int getWdtOfNode(int nodeId) {
		return super.getWdtOfNode(nodeId);
	}
	
	@Override
	public int getHgtOfNode(int nodeId) {
		return super.getHgtOfNode(nodeId);
	}

	@Override
	public Cell getNatureOfNode(int nodeId) {
		return super.getNatureOfNode(nodeId);
	}

	@Override
	public Player getPlayer() {
		return super.getPlayer();
	}

	@Override
	public Environnement getEnvi() {
		return super.getEnvi();
	}

	@Override
	public Move getNextMoveToReachPlayer(Guard g) {
		int pred[] = getShortestPathToPlayer(g);
		int guardId = getNodeIdOfPosition(g.getWdt(), g.getHgt());
		int guardHgt = g.getHgt();
		
		guardId = getNodeIdOfPosition(g.getWdt(), guardHgt);
		Move res = super.getNextMoveToReachPlayer(g);
		Cell c = getNatureOfNode(guardId);
		int nextId;
		if (res == Move.LEFT) {
			if (c != Cell.PLT && c != Cell.MTL && c != Cell.HDR && c != Cell.LAD && c != Cell.HOL
					&& getNatureOfNode(getNodeIdOfPosition(g.getWdt()-1, guardHgt-1)) == Cell.HDR)
				nextId = getNodeIdOfPosition(g.getWdt()-1, guardHgt-1);
			
			else if ((c == Cell.HDR || c == Cell.HOL)
					&& (getNatureOfNode(getNodeIdOfPosition(g.getWdt()-1, guardHgt)) == Cell.PLT || getNatureOfNode(getNodeIdOfPosition(g.getWdt()-1, guardHgt)) == Cell.MTL)
					&& (getNatureOfNode(getNodeIdOfPosition(g.getWdt()-1, guardHgt+1)) == Cell.EMP || getNatureOfNode(getNodeIdOfPosition(g.getWdt()-1, guardHgt+1)) == Cell.LAD || getNatureOfNode(getNodeIdOfPosition(g.getWdt()-1, guardHgt+1)) == Cell.HDR || getNatureOfNode(getNodeIdOfPosition(g.getWdt()-1, guardHgt+1)) == Cell.HOL))
				nextId = getNodeIdOfPosition(g.getWdt()-1, guardHgt+1);
			else
				nextId = getNodeIdOfPosition(g.getWdt()-1, guardHgt);
		}
		else if (res == Move.RIGHT) {
			if (c != Cell.PLT && c != Cell.MTL && c != Cell.HDR && c != Cell.LAD && c != Cell.HOL
					&& getNatureOfNode(getNodeIdOfPosition(g.getWdt()+1, guardHgt-1)) == Cell.HDR)
				nextId = getNodeIdOfPosition(g.getWdt()+1, guardHgt-1);
			
			else if ((c == Cell.HDR || c == Cell.HOL)
					&& (getNatureOfNode(getNodeIdOfPosition(g.getWdt()+1, guardHgt)) == Cell.PLT || getNatureOfNode(getNodeIdOfPosition(g.getWdt()+1, guardHgt)) == Cell.MTL)
					&& (getNatureOfNode(getNodeIdOfPosition(g.getWdt()+1, guardHgt+1)) == Cell.EMP || getNatureOfNode(getNodeIdOfPosition(g.getWdt()+1, guardHgt+1)) == Cell.LAD || getNatureOfNode(getNodeIdOfPosition(g.getWdt()+1, guardHgt+1)) == Cell.HDR || getNatureOfNode(getNodeIdOfPosition(g.getWdt()+1, guardHgt+1)) == Cell.HOL))
				nextId = getNodeIdOfPosition(g.getWdt()+1, guardHgt+1);
			else
				nextId = getNodeIdOfPosition(g.getWdt()+1, guardHgt);
		}
		else if (res == Move.DOWN)
			nextId = getNodeIdOfPosition(g.getWdt(), guardHgt-1);
		else if (res == Move.UP)
			nextId = getNodeIdOfPosition(g.getWdt(), guardHgt+1);
		else
			nextId = guardId;
		
		if (res != Move.NEUTRAL && pred[nextId] != guardId)
			throw new PostconditionError("La convertion du plus court chemin vers le MOVE n'a pas donné le résultat attendu");
		
		return res;
	}
	
	@Override
	public List<Integer>[] getGraphe() {
		return super.getGraphe();		
	}
	
	@Override
	public void init(Environnement envi, Player player) {
		super.init(envi, player);
		
		if (getEnvi() != envi)
			throw new PostconditionError("PATH_RESOLVER init 1 : Le screen n'est pas affectée au PathResolver");
		
		if (getPlayer() != player)
			throw new PostconditionError("PATH_RESOLVER init 2 : Le joueur n'est pas affecté au PathResolver");
		
		checkInvariant();
	}

	public int[] getShortestPathToPlayer(Guard g) {
		return super.getShortestPathToPlayer(g);		
	}

	@Override
	public int getNodeIdOfPosition(int x, int y) {
		if (!(0 <= y && y < getEnvi().getHeight() && 0 <= x && x < getEnvi().getWidth()))
			throw new PreconditionError("SCREEN NodeIdOfPosition : 0 <= y < Environnement::height(Envi(P)) and 0 <= x < Environnement::width(Envi(P))");
		
		return super.getNodeIdOfPosition(x, y);		
	}
	
	@Override
	public void recomputeGraph() {
		// Le terrain à changer, on recalcule le graphe correspondant au terrain
		super.recomputeGraph();
		checkInvariant();
	}

	@Override
	public boolean playerCanReachPos(int posX, int posY) {
		return super.playerCanReachPos(posX, posY);
	}

	@Override
	public boolean playerCanReachAllTreasuresAndPortal(List<Item> treasures, int portalX, int portalY) {
		return super.playerCanReachAllTreasuresAndPortal(treasures, portalX, portalY);
	}
}
