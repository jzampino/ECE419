import java.net.*;
import java.io.*;
import java.util.*;

public class MazeServerRequestHandler extends Thread {
	
	private Socket socket = null;
	private int pCount = 0;
	public static HashMap<Integer, String> playerList = new HashMap<Integer, String>();

	public MazeServerRequestHandler(Socket socket) {
		super("MazeServerRequestHandler");
		this.socket = socket;
	}

	public void run() {
		boolean gotQuit = false;

		try {
			ObjectInputStream fromPlayer = new ObjectInputStream(socket.getInputStream());
			PlayerPacket pPacket;

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

					System.out.println("Registered user: " + pPacket.playerName + ", from: " + pPacket.hostName);

					// Add request to FIFO, should cause handler thread to wake up

					MazeServerProcessor.requestLog.put(cPacket);

					break;
				}
			}

			fromPlayer.close();
			socket.close();
		} catch (IOException e) {
			if(!gotQuit)
				e.printStackTrace();
		} catch (ClassNotFoundException e) {
			if(!gotQuit)
				e.printStackTrace();
		} catch (InterruptedException e) {
			if(!gotQuit)
				e.printStackTrace();
		}
	}
}
