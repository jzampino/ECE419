import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.Map;

public class MazeServerProcessor extends Thread {

	public void run() {
		boolean queueProcessing = true;

		try {
	  		while(queueProcessing) {
				// This thread basically peeks the head of the FIFO and checks to see if
				// there are any pending requests, if not, it will keep looping.
				PlayerPacket toProcess = (PlayerPacket) MazeServer.requestLog.peek();
				
				if(toProcess == null)
					continue;

				if(toProcess.type == PlayerPacket.PLAYER_REGISTER_REPLY) {
					toProcess = (PlayerPacket) MazeServer.requestLog.take();

					// Small check to see if anyone else is already registered
					if (MazeServer.playerList.size() > 1) {
						updateNewPlayer(toProcess);
						
						broadCastAction(toProcess, 1);
					} else {
						broadCastAction(toProcess, 0);
					}

				} else {
					// Check to see if the playerList has numPlayers in it
					if(MazeServer.playerList.size() == MazeServer.numPlayers || toProcess.type == PlayerPacket.PLAYER_QUIT) {
						toProcess = (PlayerPacket) MazeServer.requestLog.take();
						
						broadCastAction(toProcess, 0);

						if(MazeServer.playerList.size() == 0) {
							System.out.println("All players quit, next join will start a new game");
						}
					} else {
						toProcess = (PlayerPacket) MazeServer.requestLog.take();
						System.out.println("Waiting for " + (MazeServer.numPlayers - MazeServer.playerList.size()) + " more players, discarding request");
					}
				}
			}
		} catch (IOException e) {
			if(!queueProcessing)
				e.printStackTrace();
		} catch (InterruptedException e) {
			if(!queueProcessing)
				e.printStackTrace();
		}
	}

	// Either broadcast to everyone (including myself, mode 0), or to everyone except myself (mode 1)
	private void broadCastAction(PlayerPacket pAction, int mode) throws IOException {
		PlayerInfo pInfo = null;

		if (mode == 0) {
			for (Map.Entry<Integer, PlayerInfo> player : MazeServer.playerList.entrySet()) {
				pInfo = player.getValue();

				Socket socket = new Socket(pInfo.hostName, pInfo.listenPort);

				ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());

				toClient.writeObject(pAction);

				toClient.close();
				socket.close();
			}
		} else if (mode == 1) {
			for (Map.Entry<Integer, PlayerInfo> player : MazeServer.playerList.entrySet()) {
				pInfo = player.getValue();

				if(pInfo.uID != pAction.uID) {
	
					Socket socket = new Socket(pInfo.hostName, pInfo.listenPort);

					ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());

					toClient.writeObject(pAction);

					toClient.close();
					socket.close();
				}
			}
		}
	}

	// Send update packets to the new player so they have an up-to-date map and place
	// themselves in the correct spot
	private void updateNewPlayer(PlayerPacket pAction) throws IOException {
		PlayerInfo pInfo = null;
		Socket newPlayer = null;
		ObjectOutputStream toNewPlayer = null;
					
		newPlayer = new Socket(pAction.hostName, pAction.listenPort);
		toNewPlayer = new ObjectOutputStream(newPlayer.getOutputStream());

		for(Map.Entry<Integer, PlayerInfo> player : MazeServer.playerList.entrySet()) {
			pInfo = player.getValue();

			PlayerPacket updatePlayer = new PlayerPacket();
						
			updatePlayer.hostName = pInfo.hostName;
			updatePlayer.playerName = pInfo.playerName;
			updatePlayer.uID = pInfo.uID;

			if(pInfo.uID == pAction.uID) {
				updatePlayer.type = PlayerPacket.PLAYER_REGISTER_REPLY;
			} else {	
				updatePlayer.type = PlayerPacket.PLAYER_REGISTER_UPDATE;
			}

			toNewPlayer.writeObject(updatePlayer);
		}

		toNewPlayer.close();
		newPlayer.close();
	}
}
