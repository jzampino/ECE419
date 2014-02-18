import java.net.*;
import java.io.*;
import java.util.HashMap;

public class MazeServerHandler extends Thread {
	
	private Socket socket = null;
	private int pCount = 0;
	private static HashMap<Integer, String> playerList = new HashMap<Integer, String>();

	public MazeServerHandler(Socket socket) {
		super("MazeServerHandler");
		this.socket = null;
		System.out.println("Maze Server up and running");
	}

	public void run() {
		boolean gotQuit = false;

		try {
			ObjectInputStream fromPlayer = new ObjectInputStream(socket.getInputStream());
			PlayerPacket pPacket;

			ObjectOutputStream toPlayer = new ObjectOutputStream(socket.getOutputStream());

			System.out.println("Waiting for players...");

			while( (pPacket = (PlayerPacket) fromPlayer.readObject()) != null) {

				PlayerPacket cPacket = new PlayerPacket();
				String playerKey = pPacket.playerName + "." + pPacket.hostName;

				if(pPacket.type == PlayerPacket.PLAYER_REGISTER) {
					if(pPacket.uID == -1)
						pCount++;
					else {
						System.err.println("ERROR: Duplicate Register Request! User with pID " + pPacket.uID + " already registered!");
						System.exit(-1);
					}
						
					playerList.put(pCount, playerKey);

					cPacket = pPacket;
					cPacket.type = PlayerPacket.PLAYER_REGISTER_REPLY;
					cPacket.uID = pCount;
				}

				System.out.println("Registered user: " + pPacket.playerName + ", from: " + pPacket.hostName);

				toPlayer.writeObject(cPacket);

				continue;
			}

			fromPlayer.close();
			toPlayer.close();
			socket.close();
		} catch (IOException e) {
			if(!gotQuit)
				e.printStackTrace();
		} catch (ClassNotFoundException e) {
			if(!gotQuit)
				e.printStackTrace();
		}
	}
}
