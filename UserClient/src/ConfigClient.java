


import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConfigClient {

	private static ConfigClient	instance	= new ConfigClient();
	private Scanner				sc;
	private Map<String, String>	configMap;

	private int					portMainServer;
	private String				hostMainServer;

	public static ConfigClient getInstance() {

		return instance;
	}

	private ConfigClient() {

		super();
		readConfig();
	}

	public String getHostMainServer() {

		return hostMainServer;
	}

	public int getPortMainServer() {

		return portMainServer;
	}

	public synchronized void readConfig() {

		try {
			FileInputStream fis = new FileInputStream("config/config.client");
			sc = new Scanner(fis);
			configMap = new HashMap<>();

			String[] strings;
			String line;
			while(sc.hasNextLine()) {
				line = sc.nextLine();
				strings = line.split("=");
				if(strings.length != 2) {
					continue;
				}
				String key = strings[0].trim().toLowerCase();
				String value = strings[1].trim().toLowerCase();
				configMap.put(key, value);
//				System.out.println(key + "=" + value);
			}

			sc.close();
			fis.close();

			portMainServer = Integer.parseInt(configMap.get("portmainserver"));
			hostMainServer = configMap.get("hostmainserver");

		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
