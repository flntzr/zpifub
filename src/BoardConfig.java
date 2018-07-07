import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BoardConfig {

    public int[][][] walklayer;
    public int[][][] debuglayer;

    /** [layer][x][y] */
    public int[][][] layer;
    /** [layer][x][y] */
    public int[][][] scoreHeatmap;
    /** If the score heatmap has been initialized yet. */
    public boolean isScoreHeatmapInitialized = false;
    /** [player number][bot number][x, y] */
    public int[][][] bots;
    public final int[] influenceRadii;
    public List<BotInterface> botInstances;
    public List<Thread> botThreads;

    public BoardConfig(int[] influenceRadii) {
	this.bots = new int[3][3][2]; // 3 players, 3 bots, 2 coordinates
	this.influenceRadii = influenceRadii;
	this.botInstances = new ArrayList<>();
	this.botThreads = new ArrayList<>();
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

    public int[] aStar(int x, int y, int destX, int destY, int layer) {
		int layerSize = Util.BOARD_SIZE >> layer;
//		int start = (y >> layer) * (Util.BOARD_SIZE >> 4) + (x >> layer);
//		int dest = (destY >> layer) * (Util.BOARD_SIZE >> 4) + (destX >> layer);
		int start = (y / layerSize) * (1 << layer) + (x / layerSize);
		int dest = (destY / layerSize) * (1 << layer) + (destX / layerSize);
		
		List<Integer> path = aStar(start, dest, layer);
		int[] result = new int[path.size()];
		for (int i = 0; i < result.length; i++) {
		    int step = path.get(i);
		    int stepLayerX = step % Util.BOARD_SIZE;
		    int stepLayerY = step / Util.BOARD_SIZE;
		    result[i] = (stepLayerY << layer) * Util.BOARD_SIZE + (stepLayerX << layer);
		}
		return result;
    }

    /**
     * Accepts start and goal position in the context of the specified layer, e.g.
     * in layer 4 x and y must be < 64.
     * 
     * @param startIndex
     * @param goalIndex
     * @param layerN
     * @return the positions specific to that layer.
     */
    private List<Integer> aStar(int startIndex, int goalIndex, int layerN) {
	int boardSize = Util.BOARD_SIZE >> layerN;
	Set<Integer> closedSet = new HashSet<>();
	Map<Integer, Integer> cameFrom = new HashMap<>(); // most efficient previous step
	Map<Integer, Double> gScore = new HashMap<>(); // each node with cost it takes to reach from previous node
	Map<Integer, Double> fScore = new HashMap<>(); // each node with cost it takes to reach from the start
	List<Integer> openSet = new ArrayList<>();
	openSet.add(startIndex);
	for (int i = 0; i < (boardSize * boardSize); i++) {
	    gScore.put(i, Double.MAX_VALUE);
	    fScore.put(i, Double.MAX_VALUE);
	}
	gScore.put(startIndex, 0d); // cost to start = 0
	fScore.put(startIndex, this.estimateCost(startIndex, goalIndex, layerN));
	while (!openSet.isEmpty()) {
	    int current = openSet.get(0); // openSet is sorted ascending by fScore -> always get lowest
	    if (current == goalIndex) {
		return reconstructPath(cameFrom, current);
	    }
	    openSet.remove(0);
	    closedSet.add(current);

	    for (int neighbor : this.getWalkableNeighbors(layerN, current, boardSize)) {
		if (closedSet.contains(neighbor)) {
		    continue;
		}
		if (!openSet.contains(neighbor)) {
		    openSet.add(neighbor);
		}
		// distance from start to neighbor
		double tentativeGScore = gScore.get(current) + this.estimateCost(current, neighbor, layerN);
		if (tentativeGScore >= gScore.get(neighbor)) {
		    continue; // This path isn't better
		}
		// This path is better -> record it
		cameFrom.put(neighbor, current);
		gScore.put(neighbor, tentativeGScore);
		fScore.put(neighbor, gScore.get(neighbor) + this.estimateCost(neighbor, goalIndex, layerN));
		openSet.sort(new Comparator<Integer>() {
		    @Override
		    public int compare(Integer o1, Integer o2) {
			return fScore.getOrDefault(o1, Double.MAX_VALUE)
				.compareTo(fScore.getOrDefault(o2, Double.MAX_VALUE));
		    }
		});
	    }
	}
	return new ArrayList<>(); // goal is not reachable
    }

    private double estimateCost(int start, int goal, int layer) {
	int boardSize = Util.BOARD_SIZE >> layer;
	int distanceX = (start % boardSize) - (goal % boardSize);
	int distanceY = (start / boardSize) - (goal / boardSize);
	// System.out.println("x,y: " + distanceX+","+distanceY);
	return Math.sqrt(distanceX * distanceX + distanceY * distanceY); // euclidean distance
    }

    private List<Integer> reconstructPath(Map<Integer, Integer> cameFrom, int current) {
	List<Integer> path = new ArrayList<>();
	while (cameFrom.containsKey(current)) {
	    current = cameFrom.get(current);
	    path.add(current);
	}
	Collections.reverse(path);
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

    public int getColorScore(int x, int y, int size, int layer, int playerID) {
	int score = 0;
	for (int posY = 0; posY < size; posY++) {
	    for (int posX = 0; posX < size; posX++) {
		score += Util.getColorScore(playerID, this.layer[layer][x + posX][y + posY]);
	    }
	}
	return score;
    }
}
