import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class BoardConfig {
	public int[][][] layer;
	public int[][][] bots; // [player number][bot number][pixelArray index]
	public final int[] influenceRadii;

	public BoardConfig(int[] influenceRadii) {
		// this.pixelArray = new int[Util.BOARD_SIZE * Util.BOARD_SIZE];
		// System.arraycopy(pixelArray, 0, this.pixelArray, 0, Util.BOARD_SIZE *
		// Util.BOARD_SIZE);
		this.bots = new int[3][3][2]; // 3 players, 3 bots, 2 coordinates
		this.influenceRadii = influenceRadii;
	}

	public void moveBot(int playerID, int botID, int x, int y) {
		this.bots[playerID][botID][0] = x;
		this.bots[playerID][botID][1] = y;
	}

	public int getColor(int layer, int x, int y) {
		return this.layer[layer][x][y];
	}

	public int getColor(int layer, int index) {
		int layerSize = Util.BOARD_SIZE >> layer;
		return this.getColor(layer, index % layerSize, index / layerSize);
	}

	public boolean isWalkable(int layer, int x, int y) {
		return this.getColor(layer, x, y) != 0;
	}

	public boolean isWalkable(int layer, int index) {
		return this.getColor(layer, index) != 0;
	}

	public List<Integer> aStar(int start, int goal, int layer) {
		int boardSize = Util.BOARD_SIZE >> layer;
		Set<Integer> closedSet = new HashSet<>();
		Map<Integer, Integer> cameFrom = new HashMap<>(); // most efficient previous step
		Map<Integer, Double> gScore = new HashMap<>(); // each node with cost it takes to reach from previous node
		Map<Integer, Double> fScore = new HashMap<>(); // each node with cost it takes to reach from the start
		List<Integer> openSet = new ArrayList<>();
		// TreeSet<Integer> openSet = new TreeSet<Integer>(new Comparator<Integer>() {
		// @Override
		// public int compare(Integer o1, Integer o2) {
		// return -fScore.getOrDefault(o1,
		// Double.MAX_VALUE).compareTo(fScore.getOrDefault(o2, Double.MAX_VALUE));
		// }
		// });
		openSet.add(start);
		for (int i = 0; i < (boardSize * boardSize); i++) {
			gScore.put(i, Double.MAX_VALUE);
			fScore.put(i, Double.MAX_VALUE);
		}
		gScore.put(start, 0d); // cost to start = 0
		fScore.put(start, this.estimateCost(start, goal, layer));
		while (!openSet.isEmpty()) {
			int current = openSet.get(0); // openSet is sorted ascending by fScore -> always get lowest
			if (current == goal) {
				return reconstructPath(cameFrom, current);
			}
			openSet.remove(0);
			closedSet.add(current);

			for (int neighbor : this.getWalkableNeighbors(layer, current, boardSize)) {
				if (closedSet.contains(neighbor)) {
					continue;
				}
				if (!openSet.contains(neighbor)) {
					openSet.add(neighbor);
				}
				// distance from start to neighbor
				double tentativeGScore = gScore.get(current) + this.estimateCost(current, neighbor, layer);
				if (tentativeGScore >= gScore.get(neighbor)) {
					continue; // This path isn't better
				}
				// This path is better -> record it
				cameFrom.put(neighbor, current);
				gScore.put(neighbor, tentativeGScore);
				fScore.put(neighbor, gScore.get(neighbor) + this.estimateCost(neighbor, goal, layer));
				openSet.sort(new Comparator<Integer>() {
					@Override
					public int compare(Integer o1, Integer o2) {
						return fScore.getOrDefault(o1, Double.MAX_VALUE).compareTo(fScore.getOrDefault(o2, Double.MAX_VALUE));
					}
				});
			}
		}
		return new ArrayList<>(); // goal is not reachable
	}

	private double estimateCost(int start, int goal, int layer) {
		int boardSize = Util.BOARD_SIZE >> layer;
		int startX = start % boardSize;
		int startY = start / boardSize;
		int goalX = goal % boardSize;
		int goalY = goal / boardSize;
		int distanceX = startX - goalX;
		int distanceY = startY - goalY;
		return Math.sqrt(distanceX * distanceX + distanceY * distanceY); // euclidian distance
	}

	private List<Integer> reconstructPath(Map<Integer, Integer> cameFrom, int current) {
		List<Integer> path = new ArrayList<>();
		while (cameFrom.containsKey(current)) {
			current = cameFrom.get(current);
			path.add(current);
		}
		return path;
	}

	private int[] getWalkableNeighbors(int layer, int index, int boardSize) {
		int[] tempNeighbors = Util.getNeighbors(index, boardSize);
		int neighborCount = tempNeighbors.length;
		for (int i = 0; i < tempNeighbors.length; i++) {
			if (!this.isWalkable(layer, tempNeighbors[i])) {
				tempNeighbors[i] = -1;
				neighborCount--;
			}
		}
		int[] neighbors = new int[neighborCount];
		int neighborIdx = 0;
		for (int neighbor : tempNeighbors) {
			if (neighbor != -1) {
				neighbors[neighborIdx] = neighbor;
				neighborIdx++;
			}
		}
		return neighbors;
	}
}
