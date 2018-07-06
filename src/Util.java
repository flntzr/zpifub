import lenz.htw.zpifub.net.NetworkClient;

public class Util {
	public static final int BOARD_SIZE = 1024;

	public static BoardConfig getInitialBoard(NetworkClient client) {
		int[] influenceRadii = new int[3];
		influenceRadii[0] = client.getInfluenceRadiusForBot(0);
		influenceRadii[1] = client.getInfluenceRadiusForBot(1);
		influenceRadii[2] = client.getInfluenceRadiusForBot(2);
		return new BoardConfig(influenceRadii);
	}

	/**
	 * Scores color from the viewpoint of the given player.
	 * @param playerID
	 * @param color
	 * @return Score in range [0, 255]
	 */
	public static int getColorScore(int playerID, int color) {
		if (color == 0) {
			return 0;
		}
		int ownColorDistanceTo255 = 255 - ((color >> ((2 - playerID) * 8)) & 0xFF); // range: [0, 255]
		int otherColorsDistanceTo0 = ((color >> (((1 - playerID) % 3) * 8)) & 0xFF) + ((color >> (((-playerID) % 3) * 8)) & 0xFF); // range : [0, 510]
		return (ownColorDistanceTo255 + otherColorsDistanceTo0) / 3;
	}

	public static int[] getNeighbors(int index, int boardSize) {
		int x = index % boardSize;
		int y = index / boardSize;
		int xStart = Util.clamp(x - 1, 0, boardSize - 1);
		int xEnd = Util.clamp(x + 1, 0, boardSize - 1);
		int yStart = Util.clamp(y - 1, 0, boardSize - 1);
		int yEnd = Util.clamp(y + 1, 0, boardSize - 1);
		int[] neighbours = new int[(xEnd - xStart + 1) * (yEnd - yStart + 1) - 1];
		int i = 0;
		for (int iY = yStart; iY <= yEnd; iY++) {
			for (int iX = xStart; iX <= xEnd; iX++) {
				if (iX == x && iY == y) {
					continue;
				}
				neighbours[i++] = iY * boardSize + iX;
			}
		}
		return neighbours;
	}

	private static int clamp(int val, int min, int max) {
		return Math.max(min, Math.min(max, val));
	}
}
