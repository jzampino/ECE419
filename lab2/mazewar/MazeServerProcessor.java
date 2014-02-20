import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.Map;

public class MazeServerProcessor extends Thread {

	public void run() {
		boolean queueProcessing = true;

		try {
	  		while(queueProcessing) {
				PlayerPacket toProcess = (PlayerPacket) MazeServer.requestLog.take();
				PlayerInfo pInfo = null;
				
				if (MazeServer.playerList.size() > 1) {
					Socket newPlayer = null;
					ObjectOutputStream toNewPlayer = null;
					
					newPlayer = new Socket(toProcess.hostName, toProcess.listenPort);
					toNewPlayer = new ObjectOutputStream(newPlayer.getOutputStream());

					for(Map.Entry<Integer, PlayerInfo> player : MazeServer.playerList.entrySet()) {
						pInfo = player.getValue();

						PlayerPacket updatePlayer = new PlayerPacket();
						
						updatePlayer.hostName = pInfo.hostName;
						updatePlayer.playerName = pInfo.playerName;
						updatePlayer.uID = pInfo.uID;

						if(pInfo.uID == toProcess.uID) {
							updatePlayer.type = PlayerPacket.PLAYER_REGISTER_REPLY;
						} else {	
							updatePlayer.type = PlayerPacket.PLAYER_REGISTER_UPDATE;
						}

						toNewPlayer.writeObject(updatePlayer);
					}

					toNewPlayer.close();
					newPlayer.close();

					for (Map.Entry<Integer, PlayerInfo> player : MazeServer.playerList.entrySet()) {
						pInfo = player.getValue();

						if(pInfo.uID != toProcess.uID) {
	
							Socket socket = new Socket(pInfo.hostName, pInfo.listenPort);

							ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());

							toClient.writeObject(toProcess);

							toClient.close();
							socket.close();
						}
					}
				} else {
					
					for (Map.Entry<Integer, PlayerInfo> player : MazeServer.playerList.entrySet()) {
						pInfo = player.getValue();

						Socket socket = new Socket(pInfo.hostName, pInfo.listenPort);

						ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());

						toClient.writeObject(toProcess);

						toClient.close();
						socket.close();
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
}
