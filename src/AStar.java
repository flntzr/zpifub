import java.util.ArrayList;
import java.util.List;

public class AStar {
	
	
	public static int[] AStar(int[] start, int[] dest, int[][] board,int boardWidth) {
		int[] pathCoords;
		List<AStarNode> openList = new ArrayList<AStarNode>();
		List<AStarNode> closedList = new ArrayList<AStarNode>();

		AStarNode startNode = new AStarNode(start,boardWidth,null);
		AStarNode destNode = new AStarNode(dest,boardWidth,null);
		openList.add(startNode);
		
		for(int j = 0; j < 120; j++) {		

			if(openList.size() == 0){
				System.out.println("Es führt kein Weg zum Ziel");
				break;
			} else if(getNodeById(destNode,openList)!=null){
				System.out.println("Pfad gefunden");
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
			AStar.getNeighbors(openList,closedList,cheapest,destNode,board,board[0].length,board.length);
			
		}
		return null;
	}
	
	public static void getNeighbors(List<AStarNode> openList,List<AStarNode> closedList, AStarNode node,AStarNode dest,int[][]board, int boardWidth, int boardHeight) {
		for(int x = node.coords[0]-1; x<=node.coords[0]+1; x++){
			for(int y = node.coords[1]-1; y<=node.coords[1]+1; y++){
				if(x<0 || y < 0 || x >= boardWidth || y >= boardHeight) continue; //Feld ist außerhalb des Boards
				

				if(board[y][x]!=0) continue; //Feld ist nicht begehbar
				AStarNode n = new AStarNode(new int[]{x,y},boardWidth,node); 
				if(getNodeById(n,closedList)!=null) continue;

				

				//G-Cost
				n.gCost = (float)Math.sqrt((node.coords[0]-x)*(node.coords[0]-x) + (node.coords[1]-y)*(node.coords[1]-y));						
				//H-Cost
				n.hCost = (float)Math.sqrt((dest.coords[0]-node.coords[0])*(dest.coords[0]-node.coords[0]) + (dest.coords[1]-node.coords[1])*(dest.coords[1]-node.coords[1]));
				n.fCost = n.gCost + n.hCost;
				
				AStarNode existingNode = getNodeById(n,openList);
				if(existingNode!=null && existingNode.gCost>n.gCost ){
					existingNode.gCost = n.gCost;
					//GCost updaten wenn geringer
				}
				else openList.add(n);
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
