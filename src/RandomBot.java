import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import lenz.htw.zpifub.Update;
import lenz.htw.zpifub.net.NetworkClient;

public class RandomBot implements Runnable {
	private String hostname;
	private String name;
	private String winMessage;
	private int playerNumber;
	private BoardConfig boardConfig;
	StrategicMap map;
	public RandomBot(String hostname, String name, String winMessage) {
		this.hostname = hostname;
		this.name = name;
		this.winMessage = winMessage;
		
	}

	@Override
	public void run() {
		NetworkClient client = new NetworkClient(this.hostname, this.name, this.winMessage);
		if(name=="random1") {
			map = new StrategicMap(client);
		}
		this.playerNumber = client.getMyPlayerNumber();
		this.boardConfig = Util.getInitialBoard(client);
		Update update;
		client.setMoveDirection(0, 1, 0); // bot 0 go right
		client.setMoveDirection(1, -1, 0); // bot 1 go left
		client.setMoveDirection(2, 0, 1); // bot 2 go up
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (true) {
			
			if(name=="random1"){
				map.render();
			}

			if ((update = client.pullNextUpdate()) == null) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			if (update.type == null) {
//				System.out.println(this.playerNumber + ": " + "Bot " + update.bot + " of player " + update.player
//						+ " at " + update.x + ", " + update.y);
				this.boardConfig.moveBot(update.player, update.bot, update.x, update.y);
			} else if (update.player == -1) {
				// update spawned, type, position
//				System.out.println(this.playerNumber + ": " + "Powerup at " + update.x + ", " + update.y);
			} else {
				// update collected
//				System.out.println("update!");
			}
			for (int i = 0; i < this.boardConfig.bots[this.playerNumber].length;i++) {
				int[] bot = this.boardConfig.bots[this.playerNumber][i];
				if (bot[0] == 0 && bot[1] == 0) {
					// Skip this special case where the bot position has not been set yet.
					continue;
				}
				int[] direction = this.pickMove(bot[0], bot[1]);
				client.setMoveDirection(i, direction[0], direction[1]);
			}
		}
	}

	private int[] pickMove(int x, int y) {
		List<int[]> possibleMoves = new ArrayList<>();
		if (boardConfig.isWalkable(x + 1, y)) {
			int[] move = new int[2];
			move[0] = x + 1;
			move[1] = y;
			possibleMoves.add(move);
		}
		if (boardConfig.isWalkable(x - 1, y)) {
			int[] move = new int[2];
			move[0] = x - 1;
			move[1] = y;
			possibleMoves.add(move);
		}
		if (boardConfig.isWalkable(x, y + 1)) {
			int[] move = new int[2];
			move[0] = x;
			move[1] = y + 1;
			possibleMoves.add(move);
		}
		if (boardConfig.isWalkable(x, y - 1)) {
			int[] move = new int[2];
			move[0] = x;
			move[1] = y - 1;
			possibleMoves.add(move);
		}
		
		if (possibleMoves.size() == 0) {
			int[] move = new int[2];
			move[0] = 1;
			move[1] = -1;
			return move;
		}
		
		int randomNum = possibleMoves.size() == 0 ? 0 : ThreadLocalRandom.current().nextInt(0, possibleMoves.size());
		int[] pickedMove = possibleMoves.get(randomNum);
		pickedMove[0] -= x;
		pickedMove[1] -= y;
		return pickedMove;
	}
}
