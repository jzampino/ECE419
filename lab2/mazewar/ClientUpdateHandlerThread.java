import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ClientUpdateHandlerThread extends Thread {

	private Socket socket = null;
	private Maze maze = null;
	private static ConcurrentSkipListMap<Integer, PlayerInfo> playerList = new ConcurrentSkipListMap<Integer, PlayerInfo>();

	public ClientUpdateHandlerThread(Socket socket, Maze maze) {
		super("ClientUpdateHandler");
		this.socket = socket;
		this.maze = maze;
	}

	public void run() {
		try {
			ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());

			PlayerPacket pPacket = new PlayerPacket();

			while ( (pPacket = (PlayerPacket) fromServer.readObject()) != null) {

				if(pPacket.type == PlayerPacket.PLAYER_REGISTER_REPLY) {
					PlayerInfo pInfo = new PlayerInfo();

					pInfo.hostName = pPacket.hostName;
					pInfo.playerName = pPacket.playerName;
					pInfo.uID = pPacket.uID;

					playerList.put(pInfo.uID, pInfo);

					System.out.println("Received Join from Player: " + pInfo.playerName);

					maze.addClient(new RemoteClient(pInfo.playerName));

					fromServer.close();
					socket.close();

					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
