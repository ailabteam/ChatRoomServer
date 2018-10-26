

import java.io.IOException;

public interface IManageClient {

	boolean gotoRoom(CustomAddress addressRoomServer) throws IOException, ClassNotFoundException;

	void leaveRoom() throws IOException;

	CustomAddress receiceAddressRoomFromMainServer(LazySocket lazySocketMainServer) throws IOException, ClassNotFoundException;

	void reiceiveChatFromRoom();// while(true)

	CustomAddress requestMainServer(String s) throws Exception;

	void sendChatToRoomServer(Message message) throws IOException;

}
