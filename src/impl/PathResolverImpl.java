package impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import data.Cell;
import data.Item;
import data.Move;
import services.Environnement;
import services.Guard;
import services.PathResolver;
import services.Player;

public class PathResolverImpl implements PathResolver {

	private Environnement envi;;
	private Player player;
	private List<Integer>[] adjacencyList;
	private Map<Integer, Node> map;

	public PathResolverImpl() {}
	
	@Override
	public int getWdtOfNode(int nodeId) {
		return map.get(nodeId).x;
	}

	@Override
	public int getHgtOfNode(int nodeId) {
		return map.get(nodeId).y;
	}

	@Override
	public Cell getNatureOfNode(int nodeId) {
		return map.get(nodeId).c;
	}

	@Override
	public int getNodeIdOfPosition(int x, int y) {
		return getNodeFromPos(x, y).id;
	}
	
	@Override
	public Environnement getEnvi() {
		return envi;
	}
	
	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public Move getNextMoveToReachPlayer(Guard g) {
		// Le garde est deja sur le joueur, il n'y a rien à changer
		if (g.getWdt() == player.getWdt() && g.getHgt() == player.getHgt())
			return Move.NEUTRAL;
		
		int[] pred = getShortestPathToPlayer(g);
		// Injoignable
		if (pred == null)
			return Move.NEUTRAL;
		int idSrc = getNodeFromPos(g.getWdt(), g.getHgt()).id;
		int idDst = getNodeFromPos(player.getWdt(), player.getHgt()).id;
		
		List<Move> moves = convertPredToBehaviours(pred, idSrc, idDst);
		// Joueur injoignable
		if (moves == null)
			return Move.NEUTRAL;
		else
			return moves.get(moves.size()-1);
	}
	
	@Override
	public List<Integer>[] getGraphe() {
		return adjacencyList;
	}

	@Override
	public void init(Environnement envi, Player player) {
		this.envi = envi;
		this.player = player;
		adjacencyList = new List[envi.getWidth() * envi.getHeight()];
		map = new HashMap<>();

		recomputeGraph();
	}
	
	@Override
	public int[] getShortestPathToPlayer(Guard g) {
		int srcX = g.getWdt();
		int srcY = g.getHgt();
		
		int idSrc = getNodeFromPos(srcX, srcY).id;
		int idDst = getNodeFromPos(player.getWdt(), player.getHgt()).id;
				
		return computeShortestPath(idSrc, idDst);	
	}
	
	private Node getNodeFromPos(int x, int y) {
		for (Node n : map.values())
			if (n.x == x && n.y == y)
				return n;
		return null;
	}

	@Override
	public String toString() {
		String res = "";
		for (int i = 0; i < adjacencyList.length; i++) {
			if (map.get(i) != null) {
				res += map.get(i).toString() + " ==> [";
				for (int j = 0; j < adjacencyList[i].size(); j++) {
					res += map.get(adjacencyList[i].get(j)) + " ";
				}
				res += "]\n";
			}
		}
		return res;
	}

	@Override
	public void recomputeGraph() {
		int cpt = 0;
		for (int j = 0; j < envi.getHeight(); j++) {
			for (int i = 0; i < envi.getWidth(); i++) {
				Node n = new Node(cpt, i, j, envi.cellNature(i, j));
				adjacencyList[cpt] = new ArrayList<>();
				map.put(cpt, n);

				// On ne peut pas regarder la case au dessus
				if (j == envi.getHeight() - 1) {
					// On peut regarder la case à droite
					if (i != envi.getWidth() - 1) {
						if (hasEdgeToRight(i, j, i + 1, j))
							adjacencyList[cpt].add(cpt + 1);
						if (hasEdgeToBottomRight(i, j, i+1, j-1))
							adjacencyList[cpt].add(cpt - (envi.getWidth() - 1));
					}
					// On peut regarder la case à gauche
					if (i != 0) {
						if (hasEdgeToLeft(i, j, i - 1, j))
							adjacencyList[cpt].add(cpt - 1);
						if (hasEdgeToBottomLeft(i, j, i-1, j-1))
							adjacencyList[cpt].add(cpt - (envi.getWidth() + 1));
					}
					if (hasEdgeToDown(i, j, i, j - 1))
						adjacencyList[cpt].add(cpt - envi.getWidth());
				}
				// On ne peut pas regarder la case en dessous
				else if (j == 0) {
					// On peut regarder la case à droite
					if (i != envi.getWidth() - 1) {
						if (hasEdgeToRight(i, j, i + 1, j))
							adjacencyList[cpt].add(cpt + 1);
						if (hasEdgeToUpRight(i, j, i + 1, j + 1))
							adjacencyList[cpt].add(cpt + envi.getWidth() + 1);
					}
					// On peut regarder la case à gauche
					if (i != 0) {
						if (hasEdgeToLeft(i, j, i - 1, j))
							adjacencyList[cpt].add(cpt - 1);
						if (hasEdgeToUpLeft(i, j, i - 1, j + 1))
							adjacencyList[cpt].add(cpt + envi.getWidth() - 1);
					}
					if (hasEdgeToUp(i, j, i, j + 1))
						adjacencyList[cpt].add(cpt + envi.getWidth());
				}
				// On ne peut pas regarder la case à droite
				else if (i == envi.getWidth() - 1) {
					// On peut regarder la case au dessus
					if (j != envi.getHeight() - 1) {
						if (hasEdgeToUp(i, j, i, j + 1))
							adjacencyList[cpt].add(cpt + envi.getWidth());
						if (hasEdgeToUpLeft(i, j, i - 1, j + 1))
							adjacencyList[cpt].add(cpt + envi.getWidth() - 1);
					}
					// On peut regarder la case en dessous
					if (j != 0) {
						if (hasEdgeToDown(i, j, i, j - 1))
							adjacencyList[cpt].add(cpt - envi.getWidth());
						if (hasEdgeToBottomLeft(i, j, i-1, j-1))
							adjacencyList[cpt].add(cpt - (envi.getWidth() + 1));
					}
					if (hasEdgeToLeft(i, j, i - 1, j))
						adjacencyList[cpt].add(cpt - 1);
				}
				// On ne peut pas regarder la case à gauche
				else if (i == 0) {
					// On peut regarder la case au dessus
					if (j != envi.getHeight() - 1) {
						if (hasEdgeToUp(i, j, i, j + 1))
							adjacencyList[cpt].add(cpt + envi.getWidth());
						if (hasEdgeToUpRight(i, j, i + 1, j + 1))
							adjacencyList[cpt].add(cpt + envi.getWidth() + 1);
					}
					// On peut regarder la case en dessous
					if (j != 0) {
						if (hasEdgeToDown(i, j, i, j - 1))
							adjacencyList[cpt].add(cpt - envi.getWidth());
						if (hasEdgeToBottomRight(i, j, i+1, j-1))
							adjacencyList[cpt].add(cpt - (envi.getWidth() -	 1));
					}
					if (hasEdgeToRight(i, j, i + 1, j))
						adjacencyList[cpt].add(cpt + 1);

				} else {
					// Cas classique, on peut regarder partout
					if (hasEdgeToRight(i, j, i + 1, j))
						adjacencyList[cpt].add(cpt + 1);
					if (hasEdgeToLeft(i, j, i - 1, j))
						adjacencyList[cpt].add(cpt - 1);
					if (hasEdgeToUp(i, j, i, j + 1))
						adjacencyList[cpt].add(cpt + envi.getWidth());
					if (hasEdgeToDown(i, j, i, j - 1))
						adjacencyList[cpt].add(cpt - envi.getWidth());
					if (hasEdgeToUpLeft(i, j, i - 1, j + 1))
						adjacencyList[cpt].add(cpt + envi.getWidth() - 1);
					if (hasEdgeToUpRight(i, j, i + 1, j + 1))
						adjacencyList[cpt].add(cpt + envi.getWidth() + 1);
					if (hasEdgeToBottomLeft(i, j, i-1, j-1))
						adjacencyList[cpt].add(cpt - (envi.getWidth() + 1));
					if (hasEdgeToBottomRight(i, j, i+1, j-1))
						adjacencyList[cpt].add(cpt - (envi.getWidth() - 1));
				}
				cpt++;
			}
		}
	}

	private boolean hasEdgeToRight(int srcX, int srcY, int dstX, int dstY) {
		if (srcY - 1 < 0)
			return false;

		Cell srcC = envi.cellNature(srcX, srcY);
		Cell bellowSrcC = envi.cellNature(srcX, srcY - 1);
		Cell dstC = envi.cellNature(dstX, dstY);
		Cell bellowdstC = envi.cellNature(dstX, dstY - 1);
		boolean hasCharacterBellow = (envi.cellContent(srcX, srcY - 1).getCharacter() != null);

		if ((srcC != Cell.HDR && srcC != Cell.LAD && srcC != Cell.HOL) && bellowdstC == Cell.HDR)
			return false;
		else if ( (srcC != Cell.PLT && srcC != Cell.MTL)
				&& (dstC != Cell.MTL && dstC != Cell.PLT)
				&& ((srcC == Cell.LAD || srcC == Cell.HDR || srcC == Cell.HOL)
				|| (bellowSrcC == Cell.PLT || bellowSrcC == Cell.MTL || bellowSrcC == Cell.LAD)
				|| hasCharacterBellow))
			return true;
		else
			return false;
	}

	private boolean hasEdgeToLeft(int srcX, int srcY, int dstX, int dstY) {
		if (srcY - 1 < 0)
			return false;

		Cell srcC = envi.cellNature(srcX, srcY);
		Cell bellowSrcC = envi.cellNature(srcX, srcY - 1);
		Cell dstC = envi.cellNature(dstX, dstY);
		Cell bellowdstC = envi.cellNature(dstX, dstY - 1);
		boolean hasCharacterBellow = (envi.cellContent(srcX, srcY - 1).getCharacter() != null);

		if ((srcC != Cell.HDR && srcC != Cell.LAD && srcC != Cell.HOL) && bellowdstC == Cell.HDR)
			return false;
		else if ( (srcC != Cell.PLT && srcC != Cell.MTL)
				&& (dstC != Cell.MTL && dstC != Cell.PLT) && ((srcC == Cell.LAD || srcC == Cell.HDR || srcC == Cell.HOL)
				|| (bellowSrcC == Cell.PLT || bellowSrcC == Cell.MTL || bellowSrcC == Cell.LAD)
				|| hasCharacterBellow))
			return true;
		else
			return false;
	}

	private boolean hasEdgeToDown(int srcX, int srcY, int dstX, int dstY) {
		Cell srcC = envi.cellNature(srcX, srcY);
		Cell dstC = envi.cellNature(dstX, dstY);

		if ((srcC == Cell.LAD || srcC == Cell.EMP || srcC == Cell.HDR)
				&& (dstC == Cell.LAD || dstC == Cell.EMP || dstC == Cell.HDR || dstC == Cell.HOL))
			return true;
		else
			return false;
	}

	private boolean hasEdgeToUp(int srcX, int srcY, int dstX, int dstY) {
		Cell srcC = envi.cellNature(srcX, srcY);
		Cell dstC = envi.cellNature(dstX, dstY);

		if (srcC == Cell.LAD && (dstC == Cell.LAD || dstC == Cell.EMP))
			return true;
		else
			return false;
	}

	private boolean hasEdgeToUpRight(int srcX, int srcY, int dstX, int dstY) {
		if (srcX + 1 >= envi.getWidth())
			return false;

		Cell srcC = envi.cellNature(srcX, srcY);
		Cell rightC = envi.cellNature(srcX + 1, srcY);
		Cell dstC = envi.cellNature(dstX, dstY);

		if ( (srcC == Cell.HDR || srcC == Cell.HOL)
				&& (rightC == Cell.PLT || rightC == Cell.MTL)
				&& (dstC == Cell.EMP || dstC == Cell.LAD || dstC == Cell.HDR || dstC == Cell.HOL)) {
			return true;
		}
		else
			return false;
	}

	private boolean hasEdgeToUpLeft(int srcX, int srcY, int dstX, int dstY) {
		if (srcX - 1 < 0)
			return false;

		Cell srcC = envi.cellNature(srcX, srcY);
		Cell leftC = envi.cellNature(srcX - 1, srcY);
		Cell dstC = envi.cellNature(dstX, dstY);

		if ((srcC == Cell.HDR || srcC == Cell.HOL)
				&& (leftC == Cell.PLT || leftC == Cell.MTL)
				&& (dstC == Cell.EMP || dstC == Cell.LAD || dstC == Cell.HDR || dstC == Cell.HOL))
			return true;
		else
			return false;
	}

	private boolean hasEdgeToBottomRight(int srcX, int srcY, int dstX, int dstY) {
		Cell srcC = envi.cellNature(srcX, srcY);
		Cell dstC = envi.cellNature(dstX, dstY);

		if ((srcC != Cell.PLT && srcC != Cell.MTL && srcC != Cell.HDR && srcC != Cell.LAD && srcC != Cell.HOL)
				&& dstC == Cell.HDR)
			return true;
		else
			return false;
	}

	private boolean hasEdgeToBottomLeft(int srcX, int srcY, int dstX, int dstY) {
		Cell srcC = envi.cellNature(srcX, srcY);
		Cell dstC = envi.cellNature(dstX, dstY);
		
		if ((srcC != Cell.PLT && srcC != Cell.MTL && srcC != Cell.HDR && srcC != Cell.LAD && srcC != Cell.HOL)
				&& dstC == Cell.HDR) 
			return true;
		else
			return false;
	}

	private int[] computeShortestPath(int idSrc, int idDst) {
		int pred[] = new int[adjacencyList.length];
		for (int i = 0; i < pred.length; i++)
			pred[i] = -1;
		
		boolean seen[] = new boolean[adjacencyList.length];
		Queue<Integer> fifo = new LinkedList<>();
		fifo.add(idSrc);
		seen[idSrc] = true;
		while (!fifo.isEmpty()) {
			int u = fifo.remove();
			for(Integer v : adjacencyList[u]) {
				if (!seen[v]) {
					fifo.add(v);
					seen[v] = true;
					pred[v] = u;
				}
			}
		}
		// Si on a pas visité le noeud destination, alors il est injoignable depuis notre position
		if (!seen[idDst])
			return null;
		else
			return pred;
	}

	private List<Move> convertPredToBehaviours(int[] pred, int idSrc, int idDst) {
		List<Move> res = new ArrayList<>();
		int previousId = idDst;
		int currentId = pred[previousId];
		Node current = map.get(currentId);
		Node previous = map.get(previousId);
		while(currentId != idSrc) {
			if (previous.x < current.x)
				res.add(Move.LEFT);
			else if (previous.x > current.x)
				res.add(Move.RIGHT);
			else if (previous.y < current.y)
				res.add(Move.DOWN);
			else if (previous.y > current.y)
				res.add(Move.UP);
			
			previous = current;
			currentId = pred[currentId];
			current = map.get(currentId);
			if (currentId == -1) {
				current = map.get(pred[idSrc]);
				break;
			}
		}
		// Il faut encore ajouter la dernière étape
		if (previous.x < current.x)
			res.add(Move.LEFT);
		else if (previous.x > current.x)
			res.add(Move.RIGHT);
		else if (previous.y < current.y)
			res.add(Move.DOWN);
		else if (previous.y > current.y)
			res.add(Move.UP);
				
		return res;
	}

	@Override
	public boolean playerCanReachAllTreasuresAndPortal(List<Item> treasures, int portalX, int portalY) {
		int playerNode = getNodeFromPos(player.getWdt(), player.getHgt()).id;
		int portalNode = getNodeFromPos(portalX, portalY).id;
		
		if (computeShortestPath(playerNode, portalNode) == null)
			return false;
		
		for(Item t : treasures) {
			int treasureNode = getNodeFromPos(t.getWdt(), t.getHgt()).id;

			if (computeShortestPath(playerNode, treasureNode) == null)
				return false;
			else if (computeShortestPath(portalNode, treasureNode) == null)
				return false;
			else if (computeShortestPath(treasureNode, portalNode) == null)
				return false;
		}
		
		for(int i=0; i<treasures.size(); i++) {
			Item t = treasures.get(i);
			int srcNode = getNodeFromPos(t.getWdt(), t.getHgt()).id;
			for(int j = i+1; j<treasures.size(); j++) {
				Item t2 = treasures.get(j);
				int dstNode = getNodeFromPos(t2.getWdt(), t2.getHgt()).id;
			
				if (computeShortestPath(srcNode, dstNode) == null)
					return false;
				else if (computeShortestPath(dstNode, srcNode) == null)
					return false;
			}
		}
		
		return true;
	}
	
	@Override
	public boolean playerCanReachPos(int posX, int posY) {
		int idDst = getNodeFromPos(posX, posY).id;
		int idSrc = getNodeFromPos(player.getWdt(), player.getHgt()).id;
				
		return (computeShortestPath(idSrc, idDst) != null);
	}
	
	// Classe interne pour représenter les noeuds des graphes
	class Node {
		private int id, x, y;
		private Cell c;

		public Node(int id, int x, int y, Cell c) {
			this.id = id;
			this.x = x;
			this.y = y;
			this.c = c;
		}

		@Override
		public String toString() {
			return x + ":" + y;
		}
	}
}
