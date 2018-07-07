import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BrushThread implements Runnable {
    private static final int BOT_ID = 0;
    private static final int HEATMAP_LAYER = 6; // in this layer each tile is 64px long, which approx. matches the 80px diameter
					// of the brush
    private static final int A_STAR_LAYER = 4;
    private final int playerID;
    private final BoardConfig boardConfig;

    public BrushThread(BoardConfig boardConfig, int playerID) {
	this.boardConfig = boardConfig;
	this.playerID = playerID;
    }

    @Override
    public void run() {
	// find the highest scored tile to go to
	int[] startPos = this.boardConfig.bots[this.playerID][BOT_ID];
	int[] goalPos = this.findTargetPosition();
	int[] translatedStartPos =  new int[2];
	translatedStartPos[0] = startPos[0] >> A_STAR_LAYER;
	translatedStartPos[1] = startPos[1] >> A_STAR_LAYER;
	int[] translatedGoalPos =  new int[2];
	translatedGoalPos[0] = goalPos[0] >> A_STAR_LAYER;
	translatedGoalPos[1] = goalPos[1] >> A_STAR_LAYER;
	this.boardConfig.aStar(translatedStartPos, translatedGoalPos, A_STAR_LAYER);
    }

    private int[] findTargetPosition() {
	// Wait for the heatmap to be filled initially, otherwise all scores are 0.
	while (!this.boardConfig.isScoreHeatmapInitialized) {
		try {
		    Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	}
	int heatmapSize = Util.BOARD_SIZE >> HEATMAP_LAYER;
	List<int[]> scores = new ArrayList<>(); // [x, y, score]
	for (int y = 0; y < heatmapSize; y++) {
	    for (int x = 0; x < heatmapSize; x++) {
		int[] score = new int[3];
		score[0] = x;
		score[1] = y;
		score[2] = this.boardConfig.scoreHeatmap[HEATMAP_LAYER][x][y];
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
	int centerOffset = HEATMAP_LAYER == 0 ? 0 : 1 << (HEATMAP_LAYER - 1);
	position[0] = (pickedScore[0] << HEATMAP_LAYER) + centerOffset;
	position[1] = (pickedScore[1] << HEATMAP_LAYER) + centerOffset;
	return position;
    }

}
