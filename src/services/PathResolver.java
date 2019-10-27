package services;

import java.util.List;

import data.Cell;
import data.Item;
import data.Move;

/**
 * inv : 
 * 		forall node_id [0..Array::Length(Graphe(P))]
 * 			∃ neighbour_id ∈ Graphe(P)[node_id]
 * 			=> (
 * 					// goLeft
 * 					(WdtOfNode(P, node_id) > 0
 * 					&& WdtOfNode(P, neighbour_id) == WdtOfNode(P, node_id) - 1
 * 					&& HgtOfNode(P, neighbour_id) == HgtOfNode(P, node_id)
 * 					&& NatureOfNode(P, node_id) ∉ {PLT,MTL}
 * 					&& NatureOfNode(P, neighbour_id) ∉ {PLT,MTL}
 * 					&& 	(
 * 							NatureOfNode(P, node_id) ∈ {LAD,HDR,HOL}
 * 						|| 	Screen::CellNature(Envi(P), WdtOfNode(P, node_id), HgtOfNode(P, node_id)-1) ∈ {PLT,MTL,LAD}
 * 						|| 	Environnement::CellContent(Envi(P), WdtOfNode(P, node_id), HgtOfNode(P, node_id)-1) == Character otherCaract
 * 						)
 * 					)
 * 				||
 * 					// goRight
 * 					(WdtOfNode(P, node_id) < Screen::Wdth(Envi(P)-1)
 * 					&& WdtOfNode(P, neighbour_id) == WdtOfNode(P, node_id) + 1
 * 					&& HgtOfNode(P, neighbour_id) == HgtOfNode(P, node_id)
 * 					&& NatureOfNode(P, node_id) ∉ {PLT,MTL}
 * 					&& NatureOfNode(P, neighbour_id) ∉ {PLT,MTL}
 * 					&& 	(
 * 							NatureOfNode(P, node_id) ∈ {LAD,HDR,HOL}
 * 						|| 	Screen::CellNature(Envi(P), WdtOfNode(P, node_id), HgtOfNode(P, node_id)-1) ∈ {PLT,MTL,LAD}
 * 						|| 	Environnement::CellContent(Envi(P), WdtOfNode(P, node_id), HgtOfNode(P, node_id)-1) == Character otherCaract
 * 						)
 * 					)
 * 				||
 * 					// goUp
 * 					(HgtOfNode(P, node_id) < Screen::Height(Envi(P)-1)
 * 					&& WdtOfNode(P, neighbour_id) == WdtOfNode(P, node_id)
 * 					&& HgtOfNode(P, neighbour_id) == HgtOfNode(P, node_id) + 1
 * 					&& NatureOfNode(P, node_id) == LAD
 * 					&& NatureOfNode(P, neighbour_id) ∈ {LAD,EMP}
 * 					)
 * 				||
 * 					// goDown
 * 					(HgtOfNode(P, node_id) > 0
 * 					&& WdtOfNode(P, neighbour_id) == WdtOfNode(P, node_id)
 * 					&& HgtOfNode(P, neighbour_id) == HgtOfNode(P, node_id) - 1
 * 					&& NatureOfNode(P, node_id) ∈ {LAD,EMP,HDR}
 * 					&& NatureOfNode(P, neighbour_id) ∈ {LAD,HDR,EMP,HOL}
 * 					)
 * 				||
 * 					// go to new HDR with goLeft
 *					(WdtOfNode(P, node_id) > 0
 *					&& HgtOfNode(P, neighbour_id) == HgtOfNode(P, node_id) - 1
 *					&& WdtOfNode(P, neighbour_id) == WdtOfNode(P, node_id) - 1
 * 					&& NatureOfNode(P, node_id) ∉ {PLT,MTL,HDR,LAD,HOL} 
 * 					&& NatureOfNode(P, neighbour_id) == HDR
 * 					)
 * 				||
 * 					// go to new HDR with goRight
 *					(WdtOfNode(P, node_id) < Screen::Wdth(Envi(P)-1)
 * 					&& HgtOfNode(P, neighbour_id) == HgtOfNode(P, node_id) - 1
 *					&& WdtOfNode(P, neighbour_id) == WdtOfNode(P, node_id) + 1
 * 					&& NatureOfNode(P, node_id) ∉ {PLT,MTL,HDR,LAD,HOL}
 * 					&& NatureOfNode(P, neighbour_id) == HDR
 * 					)
 *				|| 
 *					// go to platform from HDR/HOL with goLeft
 *					(WdtOfNode(P, node_id) > 0
 *					&& HgtOfNode(P, neighbour_id) == HgtOfNode(P, node_id) + 1
 *					&& WdtOfNode(P, neighbour_id) == WdtOfNode(P, node_id) - 1
 * 					&& NatureOfNode(P, node_id) ∈ {HDR,HOL}
 * 					&& Screen::CellNature(Envi(P), Wdt(P, node_id)-1, Hgt(P, node_id)) ∈ {PLT,MTL} 
 * 					&& NatureOfNode(P, neighbour_id) ∈ {EMP,LAD,HDR,HOL}
 * 					)
 * 				||
 * 					// go to platform from HDR/HOL with goRight
 *					(WdtOfNode(P, node_id) < Screen::Wdth(Envi(P)-1)
 *					&& HgtOfNode(P, neighbour_id) == HgtOfNode(P, node_id) + 1
 *					&& WdtOfNode(P, neighbour_id) == WdtOfNode(P, node_id) + 1
 * 					&& NatureOfNode(P, node_id) ∈ {HDR,HOL}
 * 					&& Screen::CellNature(Envi(P), Wdt(P, node_id)+1, Hgt(P, node_id)) ∈ {PLT,MTL} 
 * 					&& NatureOfNode(P, neighbour_id) ∈ {EMP,LAD,HDR,HOL}
 * 					)
 * 				)
 */
public interface PathResolver {
	///////////////////////////////
	///////// OBSERVATORS /////////
	///////////////////////////////
	// CONST
	public Player getPlayer();
	
	// CONST
	public Environnement getEnvi();
	
	/**
	 * pre: getNodeIdOfPosition(P,x,y) require 0 <= y < Screen::height(Envi(P)) and 0 <= x < Screen::Width(Envi(P))
	 */
	public int getNodeIdOfPosition(int x, int y);
	
	public int getWdtOfNode(int nodeId);
	
	public int getHgtOfNode(int nodeId);
	
	public Cell getNatureOfNode(int nodeId);
	
	public List<Integer>[] getGraphe();
	
	/**
	 * inv de minimisation: 
	 * 		Soit playerNode = NodeIdOfPosition(P, Character::Wdt(Player(P)), Character::Hgt(Player(P)))
	 * 		Soit dstNode = NodeIdOfPosition(P, posX, posY)
	 * 		playerCanReachPos <=> ∃ ShortestPath(playerNode, dstNode) ∈ Graphe(P)
	 */
	public boolean playerCanReachPos(int posX, int posY);
	
	/**
	 * inv de minimisation: 
	 * 		playerCanReachAllTreasuresAndPortal(treasures, portalX, portalY) <=>
	 * 			Soit playerNode = NodeIdOfPosition(P, Character::Wdt(Player(P)), Character::Hgt(Player(P)))
	 * 			Soit portalNode = NodeIdOfPosition(P, portalX, portalY)
	 * 			
	 * 			∃ ShortestPath(playerNode, portalNode) ∈ Graphe(P)
	 * 			
	 * 			&& forall t in treasures
	 * 				Soit treasureNode = NodeIdOfPosition(P, GameObject::Wdt(t), GameObject::Hgt(t))
	 * 				∃ ShortestPath(playerNode, treasureNode) ∈ Graphe(P)
	 * 				&& ∃ ShortestPath(portalNode, treasureNode) ∈ Graphe(P)
	 * 				&& ∃ ShortestPath(treasureNode, portalNode) ∈ Graphe(P) 
	 *
	 * 			&& forall t in treasures
	 * 					Soit srcNode = NodeIdOfPosition(P, GameObject::Wdt(t), GameObject::Hgt(t))
	 * 					forall t2 in treasures
	 * 						Soit dstNode = NodeIdOfPosition(P, GameObject::Wdt(t2), GameObject::Hgt(t2))
	 * 						∃ ShortestPath(srcNode, dstNode) ∈ Graphe(P)
	 */
	public boolean playerCanReachAllTreasuresAndPortal(List<Item> treasures, int portalX, int portalY);
	
	/**
	 * inv de minimisation:
	 * 		Soit playerNode = NodeIdOfPosition(P, Character::Wdt(Player(P)), Character::Hgt(Player(P)))
	 * 		Soit guardNode  = NodeIdOfPosition(P, Character::Wdt(g), Character::Hgt(g))
	 * 		∃ ShortestPath(guardNode, playerNode) ∈ Graphe(P) <=>
	 * 			Soit currentNode = playerNode
	 * 			tant que currentNode != guardNode
	 * 				currentNode <= ShortestPathToPlayer(P, g)[currentNode]
	 * 		
	 * 		Retourne le plus court chemin dans Graphe(P) depuis le garde donné en paramètre jusqu'au joueur.
	 * 		Le plus court chemin est une suite d'ID de noeud à parcourir pour attendre le joueur.
	 * 		Le plus calcul se fait dans un graphe orienté non-pondéré
	 */
	public int[] getShortestPathToPlayer(Guard g);
	
	/**
	 * inv de minimisation:
	 * 		NextMoveToReachPlayer(P, g) == LEFT <=>
	 * 		(	
	 * 			(
	 * 				(
	 * 					(
	 * 						NatureOfNode(P, NodeIdOfPosition(P, Character::Wdt(g), Character::Hgt(g))) ∉ {PLT,MTL,HDR,LAD,HOL}
	 * 						&& NatureOfNode(P, NodeIdOfPosition(P, Character::Wdt(g)-1, Character::Hgt(g)-1)) == HDR
	 * 					)
	 * 					=>
	 * 					ShortestPathToPlayer(P, g)[NodeIdOfPosition(P, Character::Wdt(g)-1, Character::Hgt(g)-1)] == NodeIdOfPosition(P, Character::Wdt(g), Character::Hgt(g)))
	 * 				)
	 * 				&&
	 * 				(
	 * 					(
	 * 						NatureOfNode(P, NodeIdOfPosition(P, Character::Wdt(g), Character::Hgt(g))) ∈ {HDR,HOL}
	 * 						&& NatureOfNode(P, NodeIdOfPosition(P, Character::Wdt(g)-1, Character::Hgt(g))) ∈ {PLT,MTL}
	 * 						&& NatureOfNode(P, NodeIdOfPosition(P, Character::Wdt(g)-1, Character::Hgt(g)+1)) ∈ {EMP,LAD,HDR,HOL}
	 * 					)
	 * 					=>
	 * 					ShortestPathToPlayer(P, g)[NodeIdOfPosition(P, Character::Wdt(g)-1, Character::Hgt(g)+1)] == NodeIdOfPosition(P, Character::Wdt(g), Character::Hgt(g)))
	 * 				)
	 * 			)
	 * 			||
	 * 			ShortestPathToPlayer(P, g)[NodeIdOfPosition(P, Character::Wdt(g)-1, Character::Hgt(g))] == NodeIdOfPosition(P, Character::Wdt(g), Character::Hgt(g)))
	 * 		)
	 * 
	 * 		NextMoveToReachPlayer(P, g) == RIGHT <=>
	 * 		(	
	 * 			(
	 * 				(
	 * 					(
	 * 						NatureOfNode(P, NodeIdOfPosition(P, Character::Wdt(g), Character::Hgt(g))) ∉ {PLT,MTL,HDR,LAD,HOL}
	 * 						&& NatureOfNode(P, NodeIdOfPosition(P, Character::Wdt(g)+1, Character::Hgt(g)-1)) == HDR
	 * 					)
	 * 					=>
	 * 					ShortestPathToPlayer(P, g)[NodeIdOfPosition(P, Character::Wdt(g)+1, Character::Hgt(g)-1)] == NodeIdOfPosition(P, Character::Wdt(g), Character::Hgt(g)))
	 * 				)
	 * 				&&
	 * 				(
	 * 					(
	 * 						NatureOfNode(P, NodeIdOfPosition(P, Character::Wdt(g), Character::Hgt(g))) ∈ {HDR,HOL}
	 * 						&& NatureOfNode(P, NodeIdOfPosition(P, Character::Wdt(g)+1, Character::Hgt(g))) ∈ {PLT,MTL}
	 * 						&& NatureOfNode(P, NodeIdOfPosition(P, Character::Wdt(g)+1, Character::Hgt(g)+1)) ∈ {EMP,LAD,HDR,HOL}
	 * 					)
	 * 					=>
	 * 					ShortestPathToPlayer(P, g)[NodeIdOfPosition(P, Character::Wdt(g)+1, Character::Hgt(g)+1)] == NodeIdOfPosition(P, Character::Wdt(g), Character::Hgt(g)))
	 * 				)
	 * 			)
	 * 			||
	 * 			ShortestPathToPlayer(P, g)[NodeIdOfPosition(P, Character::Wdt(g)+1, Character::Hgt(g))] == NodeIdOfPosition(P, Character::Wdt(g), Character::Hgt(g)))
	 * 		)
	 *		NextMoveToReachPlayer(P, g) == DOWN <=>
	 * 		ShortestPathToPlayer(P, g)[NodeIdOfPosition(P, Character::Wdt(g), Character::Hgt(g)-1)] == NodeIdOfPosition(P, Character::Wdt(g), Character::Hgt(g)))
	 * 
	 * 		NextMoveToReachPlayer(P, g) == UP <=>
	 * 		ShortestPathToPlayer(P, g)[NodeIdOfPosition(P, Character::Wdt(g), Character::Hgt(g)+1)] == NodeIdOfPosition(P, Character::Wdt(g), Character::Hgt(g)))
	 * 		
	 */
	public Move getNextMoveToReachPlayer(Guard g);
	
	
	///////////////////////////////
	//////// CONSTRUCTORS /////////
	///////////////////////////////
    /**
	 * post : Envi(P) == envi
 	 * 
	 * post : Player(P) == player
	 */
	public void init(Environnement envi, Player player);
	
	
	///////////////////////////////
	////////// OPERATORS //////////
	///////////////////////////////
	public void recomputeGraph();
}
