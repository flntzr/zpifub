import lenz.htw.zpifub.Server;

public class ServerThread implements Runnable {
	private String[] args;
	
	public ServerThread(String[] args) {
		this.args = args;
	}

	@Override
	public void run() {
		Server.main(this.args);
	}

}
