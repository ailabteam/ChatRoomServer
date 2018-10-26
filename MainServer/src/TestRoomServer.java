public class TestRoomServer {

	private static final Config CONFIG = Config.getInstance();

	public static void main(String[] args) {

		if(args.length != 0) {
			new RoomServer(args[0], CONFIG.getMaxUserForRoom());
		} else {
			new RoomServer("Room05", CONFIG.getMaxUserForRoom());
		}

	}
}
