import java.io.Serializable;

public class PlayerPacket implements Serializable {

	// Packet types
	public static final int PLAYER_QUIT = 0;
	public static final int PLAYER_REGISTER_REPLY = 200;
	public static final int PLAYER_REGISTER_UPDATE = 300;
	public static final int PLAYER_REGISTER = 100;
	public static final int PLAYER_FORWARD = 101;
	public static final int PLAYER_BACKUP = 102;
	public static final int PLAYER_LEFT= 103;
	public static final int PLAYER_RIGHT = 104;
	public static final int PLAYER_FIRE = 105;

	public String hostName;
	public String playerName;
	
	// Unique ID of this player
	public int uID;
	
	// Packet type
	public int type;

	// Port that the client will listen on for broadcasts from the server
	public int listenPort;
}
