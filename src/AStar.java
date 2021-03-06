import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AStar {
	
	
	public static int[][] search(int[] start, int[] dest, int[][] board,int boardWidth,int[][] weights) {
		List<AStarNode> openList = new ArrayList<AStarNode>();
		List<AStarNode> closedList = new ArrayList<AStarNode>();

		AStarNode startNode = new AStarNode(start,boardWidth,null);
		AStarNode destNode = new AStarNode(dest,boardWidth,null);
		openList.add(startNode);
		AStarNode finishNode = null;
		while(true){
			//System.out.println("Suche...."+openList.size()+"..."+closedList.size());
			finishNode = getNodeById(destNode,openList);
			if(openList.size() == 0){
				//System.out.println("Es f�hrt kein Weg zum Ziel");
				return new int[][]{start};
			} else if(finishNode!=null){				
				//System.out.println("Pfad gefunden");
				break;
			}
			
			AStarNode cheapest = openList.get(0);
			for(int i = 1; i < openList.size(); i++) {
				AStarNode n = openList.get(i);
				if(n.fCost<cheapest.fCost){
					cheapest = n;
				}
			}
			openList.remove(cheapest);
			closedList.add(cheapest);
			AStar.getNeighbors(openList,closedList,cheapest,destNode,board,weights,board[0].length,board.length);
			
		}
		
		//Pfad bauen
		List<int[]> revertPath = new ArrayList<>();
		AStarNode last = finishNode;
		if(last == null) return new int[][]{start};
		while(last.parent!=null){
			
			revertPath.add(last.coords);			
			last = last.parent;
			
		}
		Collections.reverse(revertPath);
		return revertPath.toArray(new int[revertPath.size()][2]);
	}
	
	public static void getNeighbors(List<AStarNode> openList,List<AStarNode> closedList, AStarNode node,AStarNode dest,int[][]board,int[][]weights, int boardWidth, int boardHeight) {
		for(int x = node.coords[0]-1; x<=node.coords[0]+1; x++){
			for(int y = node.coords[1]-1; y<=node.coords[1]+1; y++){
				if(x<0 || y < 0 || x >= boardWidth || y >= boardHeight) continue; //Feld ist au�erhalb des Boards
				

				if(board[y][x]==0) continue; //Feld ist nicht begehbar
				AStarNode n = new AStarNode(new int[]{x,y},boardWidth,node); 
				if(getNodeById(n,closedList)!=null) continue;

				//G-Cost
				n.gCost = (float)Math.sqrt((node.coords[0]-x)*(node.coords[0]-x) + (node.coords[1]-y)*(node.coords[1]-y));
				if(weights!=null) n.gCost *= -weights[y][x];
				//H-Cost
				n.hCost = (float)Math.sqrt((dest.coords[0]-n.coords[0])*(dest.coords[0]-n.coords[0]) + (dest.coords[1]-n.coords[1])*(dest.coords[1]-n.coords[1]));
				n.fCost = n.gCost + n.hCost;
				
				AStarNode existingNode = getNodeById(n,openList);
				if(existingNode!=null && existingNode.gCost>n.gCost ){
					existingNode.gCost = n.gCost;
					//GCost updaten wenn geringer
				}
				else if(existingNode==null) openList.add(n);
			}
		}
	}
	
	
	public static AStarNode getNodeById(AStarNode n ,List<AStarNode> list){
		for(int i = 0; i< list.size(); i++) {
			if(list.get(i).id == n.id) return list.get(i);
		}
		return null;
	}
	
	
	public static int coordsToIndex(int[] coords, int boardSize){
		return coords[1]*boardSize+coords[0];
	}
	
	public static int[] indexToCoords(int index, int boardSize){
		return new int[]{index%boardSize,index/boardSize};
	}	
}
