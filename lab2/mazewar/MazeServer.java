import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MazeServer {

	public static ConcurrentSkipListMap<Integer, PlayerInfo> playerList = new ConcurrentSkipListMap<Integer, PlayerInfo>();
	public static LinkedBlockingQueue<PlayerPacket> requestLog = new LinkedBlockingQueue<PlayerPacket>();
	public static int pCount = 0;
	public static int numPlayers = 0;

	public static void main(String[] args) throws IOException {

		ServerSocket serverSocket = null;
		boolean listening = true;

		try {
			if (args.length == 2) {
				serverSocket = new ServerSocket(Integer.parseInt(args[0]));
				numPlayers = Integer.parseInt(args[1]);
				new MazeServerProcessor().start();
			} else {
				System.err.println("ERROR: Invalid number of arguments passed in!");
				System.out.println("Usage: java MazeServer <port_num>");
				System.exit(-1);
			}
		}
		catch (IOException e) {
			System.err.println("ERROR: Could not listen on port!");
			System.exit(-1);
		}

		while (listening) {
			new MazeServerRequestHandler(serverSocket.accept()).start();
		}

		serverSocket.close();
	}
}
