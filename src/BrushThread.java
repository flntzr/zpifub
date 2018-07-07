import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BrushThread implements Runnable {
    private final int botID = 0;
    private final int heatmapLayer = 6; // in this layer each tile is 64px long, which approx. matches the 80px diameter
					// of the brush
    private final int aStarLayer = 4;
    private final int playerID;
    private final BoardConfig boardConfig;

    public BrushThread(BoardConfig boardConfig, int playerID) {
	this.boardConfig = boardConfig;
	this.playerID = playerID;
    }

    @Override
    public void run() {
	// Requires the heatmap to be filled initially, otherwise all scores are equal.
	// TODO find a better solution than waiting a bit.
	try {
	    Thread.sleep(500);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}

	// find the highest scored tile to go to
	this.findTargetPosition();
    }

    private int[] findTargetPosition() {
	int heatmapSize = Util.BOARD_SIZE >> this.heatmapLayer;
	List<int[]> scores = new ArrayList<>(); // [x, y, score]
	for (int y = 0; y < heatmapSize; y++) {
	    for (int x = 0; x < heatmapSize; x++) {
		int[] score = new int[3];
		score[0] = x;
		score[1] = y;
		score[2] = this.boardConfig.scoreHeatmap[this.heatmapLayer][x][y];
		scores.add(score);
	    }
	}
	Collections.sort(scores, new Comparator<int[]>() {
	    @Override
	    public int compare(int[] o1, int[] o2) {
		return -Integer.compare(o1[2], o2[2]);
	    }
	});

	// TODO also take the distance into account to find the best target
	int[] pickedScore = scores.get(0);
	int[] position = new int[2];
	int centerOffset = this.heatmapLayer == 0 ? 0 : 1 << (this.heatmapLayer - 1);
	position[0] = (pickedScore[0] << this.heatmapLayer) + centerOffset;
	position[1] = (pickedScore[1] << this.heatmapLayer) + centerOffset;
	return position;
    }

}
