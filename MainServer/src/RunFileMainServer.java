


import java.io.File;
import java.io.IOException;

public class RunFileMainServer {

	private static final Config CONFIG = Config.getInstance();

	public static void main(String[] args) throws IOException {

		String mainPath = CONFIG.getPathMainServer();
		String fileRunMainServer = CONFIG.getFileRunMainServer();

		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "start ", fileRunMainServer);
		builder.directory(new File(mainPath));
		builder.start();

	}

}
