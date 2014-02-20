import java.io.Serializable;

public class PlayerPacket implements Serializable {

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
	public int uID;
	public int type;
	public int listenPort;
}
