import lenz.htw.zpifub.Update;
import lenz.htw.zpifub.net.NetworkClient;

public class Example {
	public static void test() {
		NetworkClient client = new NetworkClient(null, "drecksmaler", "I won!");

		client.getMyPlayerNumber(); // 0, 1, 2 entspricht r,g,b
		client.getScore(0);
		client.getInfluenceRadiusForBot(0); // 0: Sprühdose, 1: schmaler Pinsel, 2: breiter Pinsel
		int x = 1;
		int y = 1;

		client.isWalkable(x, y); // ist das Gleiche wie client.getBoard(x,y) != 0
		client.getBoard(x, y); // ARGB an Koordinate -> Bitshift!
		client.setMoveDirection(0, 1, 0); // nach rechts gehen, wird normiert d.h. (0,0.5,0) wäre identisch

		client.isAlive(); // Serververbindung steht?

		Update update; // gibt Veränderungen am Spielbrett. So muss nicht ständig alles gepullt und
						// pixelweise verglichen werden.
		// update enthält Bewegung des Spielers, Spawnen von Powerups und Einsammeln von
		// Powerups
		while ((update = client.pullNextUpdate()) != null) {
			// verarbeite Update
		}
	}
}
