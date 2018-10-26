


import java.io.File;
import java.io.IOException;

public class RunFileRoomServer {

	private static final Config CONFIG = Config.getInstance();

	public static void main(String[] args) throws IOException {

		String roomPath = CONFIG.getPathRoomServer();
//		String roomLogPath = CONFIG.getRoomLogPath();
		String fileRunRoomServer = CONFIG.getFileRunRoomServer();

		for(int i = 1; i <= 10; i++) {
			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "start ", fileRunRoomServer, "Room0" + i);
			builder.directory(new File(roomPath));
			builder.start();
		}

	}

}
