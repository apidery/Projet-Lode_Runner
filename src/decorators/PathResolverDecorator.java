package decorators;

import java.util.List;

import data.Cell;
import data.Item;
import data.Move;
import services.Environnement;
import services.Guard;
import services.PathResolver;
import services.Player;

public class PathResolverDecorator implements PathResolver {
	private PathResolver delegate;

	public PathResolverDecorator(PathResolver delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public int getWdtOfNode(int nodeId) {
		return delegate.getWdtOfNode(nodeId);
	}

	@Override
	public int getHgtOfNode(int nodeId) {
		return delegate.getHgtOfNode(nodeId);
	}

	@Override
	public Cell getNatureOfNode(int nodeId) {
		return delegate.getNatureOfNode(nodeId);
	}

	@Override
	public Player getPlayer() {
		return delegate.getPlayer();
	}

	@Override
	public Environnement getEnvi() {
		return delegate.getEnvi();
	}

	@Override
	public List<Integer>[] getGraphe() {
		return delegate.getGraphe();
	}

	@Override
	public Move getNextMoveToReachPlayer(Guard g) {
		return delegate.getNextMoveToReachPlayer(g);
	}
	
	@Override
	public void init(Environnement envi, Player player) {
		delegate.init(envi, player);
	}

	@Override
	public int[] getShortestPathToPlayer(Guard g) {
		return delegate.getShortestPathToPlayer(g);
	}

	@Override
	public int getNodeIdOfPosition(int x, int y) {
		return delegate.getNodeIdOfPosition(x, y);
	}

	@Override
	public void recomputeGraph() {
		delegate.recomputeGraph();
	}

	@Override
	public boolean playerCanReachPos(int posX, int posY) {
		return delegate.playerCanReachPos(posX, posY);
	}

	@Override
	public boolean playerCanReachAllTreasuresAndPortal(List<Item> treasures, int portalX, int portalY) {
		return delegate.playerCanReachAllTreasuresAndPortal(treasures, portalX, portalY);
	}
}
