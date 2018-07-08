import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import lenz.htw.zpifub.PowerupType;

public class PowerupThread implements Runnable {
    private final int x;
    private final int y;
    private final int playerID;
    private final PowerupType powerupType;
    private final BoardConfig boardConfig;
    private static final int A_STAR_LAYER = 4;

    public PowerupThread(BoardConfig boardConfig, int x, int y, PowerupType powerupType, int playerID) {
	this.x = x;
	this.y = y;
	this.powerupType = powerupType;
	this.boardConfig = boardConfig;
	this.playerID = playerID;
    }

    @Override
    public void run() {
	if (this.powerupType.name().equals("SLOW")) {
	    this.boardConfig.addSlowPowerup(x, y);
	    return;
	}
	int[][][] paths = new int[3][][];
	int playerIndex = this.playerID;
	List<Map.Entry<Integer, Float>> sortedTravelTimesMap = new ArrayList<Map.Entry<Integer, Float>>();
	for (int botIndex = 0; botIndex < 3; botIndex++) {
	    int[] startNew = new int[] { this.boardConfig.bots[playerIndex][botIndex][0],
		    this.boardConfig.bots[playerIndex][botIndex][1] };
	    startNew[0] = startNew[0] / (1 << A_STAR_LAYER);
	    startNew[1] = startNew[1] / (1 << A_STAR_LAYER);

	    int[] destNew = new int[] { x, y };
	    destNew[0] /= (1 << A_STAR_LAYER);
	    destNew[1] /= (1 << A_STAR_LAYER);

	    int[][] coords = AStar.search(startNew, destNew, this.boardConfig.layer[A_STAR_LAYER], Util.BOARD_SIZE >> 4,
		    null);

	    paths[botIndex] = coords;
	    float travelTime = Util.BOT_SPEEDS[botIndex] * paths[botIndex].length;
	    sortedTravelTimesMap.add(new AbstractMap.SimpleEntry<Integer, Float>(botIndex, travelTime));
	}
	Collections.sort(sortedTravelTimesMap, new Comparator<Map.Entry<Integer, Float>>() {
	    @Override
	    public int compare(Entry<Integer, Float> o1, Entry<Integer, Float> o2) {
		return o1.getValue().compareTo(o2.getValue());
	    }
	});
	for (Map.Entry<Integer, Float> entry : sortedTravelTimesMap) {
	    BotInterface bot = this.boardConfig.botInstances.get(entry.getKey());
	    boolean accept = bot.collectPowerUp(paths[entry.getKey()]);
	    if (accept) {
		return;
	    }
	}
	System.out.println("All bots were already busy collecting powerups!");
    }

}
