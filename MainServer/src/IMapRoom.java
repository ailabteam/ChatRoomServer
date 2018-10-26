

import java.io.IOException;


public interface IMapRoom {

	CustomAddress getAddressRoom(String id);

	void registryRoom(String id, CustomAddress address) throws IOException;

}
