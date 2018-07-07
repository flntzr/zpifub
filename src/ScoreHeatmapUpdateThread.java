
public class ScoreHeatmapUpdateThread implements Runnable {
    private final int refreshInterval = 2;
    private final int gameRuntime = 60;
    private final int playerID;
    private final BoardConfig config;

    public ScoreHeatmapUpdateThread(int playerID, BoardConfig boardConfig) {
	this.playerID = playerID;
	this.config = boardConfig;
    }

    @Override
    public void run() {
	int runtime = 0;
	while (runtime < gameRuntime + 10) {
	    this.refreshScoreLayers();
	    this.config.isScoreHeatmapInitialized = true;
	    try {
		Thread.sleep(refreshInterval * 1000);
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
