

class AStarNode {
		public final AStarNode parent;
		public final int id;
		public final int[] coords;
		public float fCost;
		public float gCost;
		public float hCost;
		public AStarNode(int[] coords,int boardSize, AStarNode parent) {
			id = AStar.coordsToIndex(coords, boardSize);
			this.parent = parent;
			this.coords = coords;
		}
	}
