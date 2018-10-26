
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Config {

	private static Config instance = new Config();
	private static final String JAVA_BIN = System.getProperty("java.home") + File.separator + "bin" + File.separator
			+ "java";

	private Scanner sc;

	private Map<String, String> configMap;
	private int portMainServer;
	private int maxUserForRoom;
	private int numProcessInQueue;
	private String hostMainServer;
	private String hostRoomserver;
	private String mysqlUser;
	private String mysqlPassword;
	private String mysqlDatabase;
	private String pathMainServer;
	private String pathRoomServer;
	private String fileRoomServer;
	private String fileMainServer;
	private String fileRunMainServer;
	private String fileRunRoomServer;
	private String mainLogPath;
	private String roomLogPath;
	private String roomID;
	private int numThreadProcessing;
	

	public static Config getInstance() {

		return instance;
	}

	private Config() {

		super();
		readConfig();
	}

	public String getFileMainServer() {

		return fileMainServer;
	}

	public String getFileRoomServer() {

		return fileRoomServer;
	}

	public String getFileRunMainServer() {

		return fileRunMainServer;
	}

	public String getFileRunRoomServer() {

		return fileRunRoomServer;
	}

	public String getHostMainServer() {

		return hostMainServer;
	}

	public String getHostRoomserver() {

		return hostRoomserver;
	}

	public String getJavaBin() {

		return JAVA_BIN;
	}

	public String getMainLogPath() {

		return mainLogPath;
	}

	public int getMaxUserForRoom() {

		return maxUserForRoom;
	}

	public String getMysqlDatabase() {

		return mysqlDatabase;
	}

	public String getMysqlPassword() {

		return mysqlPassword;
	}

	public String getMysqlUser() {

		return mysqlUser;
	}

	public int getNumProcessInQueue() {

		return numProcessInQueue;
	}

	public int getNumThreadProcessing() {

		return numThreadProcessing;
	}

	public String getPathMainServer() {

		return pathMainServer;
	}

	public String getPathRoomServer() {

		return pathRoomServer;
	}

	public int getPortMainServer() {

		return portMainServer;
	}

	public String getRoomID() {

		return roomID;
	}

	public String getRoomLogPath() {

		return roomLogPath;
	}

	public Scanner getSc() {

		return sc;
	}

	public void readConfig() {

		try {
			FileInputStream fis = new FileInputStream("config/config.conf");
			sc = new Scanner(fis);
			configMap = new HashMap<>();

			String[] strings;
			String line;
			while (sc.hasNextLine()) {
				line = sc.nextLine();
				strings = line.split("=");
				if (strings.length != 2) {
					continue;
				}
				String key = strings[0].trim();
				String value = strings[1].trim();
				configMap.put(key, value);
			}

			sc.close();
			fis.close();

			maxUserForRoom = Integer.parseInt(configMap.get("maxuserforroom"));
			portMainServer = Integer.parseInt(configMap.get("portmainserver"));
			numProcessInQueue = Integer.parseInt(configMap.get("numprocessinqueue"));
			numThreadProcessing = Integer.parseInt(configMap.get("numthreadprocessing"));

			mysqlDatabase = configMap.get("mysqldatabase");
			mysqlPassword = configMap.get("mysqlpassword");
			mysqlUser = configMap.get("mysqluser");
			hostMainServer = configMap.get("hostmainserver");
			hostRoomserver = configMap.get("hostroomserver");
			pathMainServer = configMap.get("pathmainserver");
			pathRoomServer = configMap.get("pathroomserver");
			fileRoomServer = configMap.get("fileroomserver");
			fileMainServer = configMap.get("filemainserver");
			fileMainServer = configMap.get("filemainserver");
			fileRunMainServer = configMap.get("filerunmainserver");
			fileRunRoomServer = configMap.get("filerunroomserver");
			mainLogPath = configMap.get("mainlogpath");
			roomLogPath = configMap.get("roomlogpath");
			roomID = configMap.get("roomid");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
