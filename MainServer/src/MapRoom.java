

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapRoom implements IMapRoom {

	private static IMapRoom				instance	= new MapRoom();

	private Map<String, CustomAddress>	map;

	public static IMapRoom getInstance() {

		return instance;
	}

	private MapRoom() {

		super();
		map = new ConcurrentHashMap<>();
	}

	@Override
	public synchronized CustomAddress getAddressRoom(String id) {

		return map.getOrDefault(id, null);
	}

	@Override
	public void registryRoom(String key, CustomAddress address) throws IOException {

		map.put(key, address);
		System.out.println("put: " + key + " - " + address);
	}

}
