
public class MapUpdateThread implements Runnable {
    private final int refreshIntervalInMillis = 500;
    private final int gameRuntime = 60;
    private final int playerID;
    private final StrategicMap map;
    private final BoardConfig config;

    public MapUpdateThread(int playerID, BoardConfig boardConfig, StrategicMap map) {
	this.playerID = playerID;
	this.config = boardConfig;
	this.map = map;
    }

    @Override
    public void run() {
	int runtime = 0;
	while (runtime < gameRuntime + 10) {
	    map.pullCompleteMap();
	    map.update(1, 1, 1024);
	    this.refreshScoreLayers();
	    map.render();
	    this.config.isScoreHeatmapInitialized = true;
	    try {
		Thread.sleep(refreshIntervalInMillis);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }

    private void refreshScoreLayers() {
	this.refreshTopScoreLayer();
	for (int l = 1; l < this.config.layer.length; l++) {
	    this.refreshScoreLayer(l);
	}
    }

    /**
     * Takes the layer above and uses that to refresh the score heatmap
     * 
     * @param layerN
     */
    private void refreshScoreLayer(int layerN) {
	for (int y = 0; y < this.config.layer[layerN].length; y++) {
	    for (int x = 0; x < this.config.layer[layerN].length; x++) {
		int score = this.config.scoreHeatmap[layerN - 1][x * 2][y * 2];
		score += this.config.scoreHeatmap[layerN - 1][x * 2 + 1][y * 2];
		score += this.config.scoreHeatmap[layerN - 1][x * 2][y * 2 + 1];
		score += this.config.scoreHeatmap[layerN - 1][x * 2 + 1][y * 2 + 1];
		score /= 4;
		this.config.scoreHeatmap[layerN][x][y] = score;
	    }
	}
    }

    private void refreshTopScoreLayer() {
	for (int y = 0; y < this.config.layer[0].length; y++) {
	    for (int x = 0; x < this.config.layer[0].length; x++) {
		this.config.scoreHeatmap[0][x][y] = Util.getColorScore(playerID, this.config.layer[0][x][y]);
	    }
	}
    }
}
