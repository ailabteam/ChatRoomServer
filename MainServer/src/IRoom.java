
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * synchroninzed
 */
public interface IRoom {

	boolean addUser(ChatUser chatUser, LazySocket lazySocket);

	CustomAddress getAddressRoom();

	String getIDRoom();

	ArrayList<Message> loadHistoryFromServerDB(ChatUser u, int numMsg) throws SQLException;

	void processingInRoom(LazySocket lazySocket);// new thread{ while(true)}

	void receiveFileFromClient(LazySocket lazySocket) throws IOException;

	void removeUser(ChatUser userName);

	void resgistryRoomToMainServer(String host, int port) throws IOException;

	void saveMessageToDatabase(Message message) throws SQLException;

	void sendMessageToAllClient(Message m) throws IOException;

	void shutdownRoom();

}
