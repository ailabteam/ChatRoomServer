

import java.io.IOException;

public interface IManagePortRoomForUser {

	void callNewRoom(String id) throws IOException;

	CustomAddress getAddressByRoomId(String id);

	String reiceiveRequestRoom(LazySocket lazySocket) throws IOException;

	void responseAddressRoomToClient(CustomAddress address, LazySocket lazySocket);

}
