


import java.io.IOException;

public class TestMainServer {

	private static final Config CONFIG = Config.getInstance();

	public static void main(String[] args) throws IOException {

		IManageMainServer mainServer = new ManageMainServer(CONFIG.getPortMainServer());
		mainServer.processing();
	}
}
