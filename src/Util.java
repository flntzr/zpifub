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
