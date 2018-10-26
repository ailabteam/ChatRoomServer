


import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RoomServer extends BaseRoom implements IRoom {

	private static final Config		CONFIG	= Config.getInstance();
	private boolean					isStarting;
	private BlockingQueue<Socket>	queue;

	private final class RunnableListener implements Runnable {

		@Override
		public void run() {

			while(isStarting) {
				try {
					Socket socket = serverSocket.accept();
					queue.put(socket);
					System.out.println("in queue: " + socket);
				} catch(IOException e) {
					e.printStackTrace();
				} catch(InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private final class RunnableProcessing implements Runnable {

		@Override
		public void run() {

			while(isStarting) {
				try {
					LazySocket lazySocket;
					ObjectInputStream ois;
					String query;
					try {
						Socket socket = queue.take();
						System.out.println("take(): " + socket);
						lazySocket = new LazySocket(socket);
						ois = lazySocket.getOis();
						query = ois.readUTF();
					} catch(IOException e) {
						System.out.println("Boss checked!");
						continue;
					}

					if(query.equalsIgnoreCase("gotoRoom")) {
						ChatUser us = reiceiveChatUser(lazySocket);
						if(us != null) {
							ObjectOutputStream oos = lazySocket.getOos();
							if(!addUser(us, lazySocket)) {
								oos.writeUTF("full");
								oos.flush();
							} else {
								oos.writeUTF("success");
								oos.flush();
								// TODO
								loadHistoryFromServerDB(us, 50);
								processingInRoom(lazySocket);
							}
						}
					} else {
						lazySocket.cleanup();
						lazySocket = null;
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public RoomServer(String id, int initialCapacity) {

		super(id, initialCapacity);
		isStarting = false;
		queue = new LinkedBlockingQueue<>(CONFIG.getNumProcessInQueue());

		try {
			resgistryRoomToMainServer(CONFIG.getHostMainServer(), CONFIG.getPortMainServer());
			System.out.println("Resgistry Sucess!");
		} catch(IOException e) {
			System.err.println("Can't Connect To Server: " + e.getMessage());
			shutdownRoom();
			System.exit(1);
		}
		processing();
	}

	@Override
	public boolean addUser(ChatUser chatUser, LazySocket lazySocket) {

		try {
			mapUser.put(chatUser, lazySocket);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public CustomAddress getAddressRoom() {

		CustomAddress address = null;
		try {
			String host = InetAddress.getLocalHost().getHostAddress();
			host = CONFIG.getHostRoomserver();//"192.168.1.21";
			address = new CustomAddress(host, serverSocket.getLocalPort());
		} catch(UnknownHostException e) {
			e.printStackTrace();
		}
		return address;
	}

	@Override
	public String getIDRoom() {

		return id;
	}

	public RoomServer getRoomServer() {

		return this;
	}

	@Override
	public ArrayList<Message> loadHistoryFromServerDB(ChatUser u, int numMsg) throws SQLException {

		System.out.println("RoomServer.loadHistoryFromServerDB()");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized void processingInRoom(LazySocket lazySocket) {

		System.out.println("RoomServer.receiveMessageFromClient()");

		Thread thread = new Thread(() -> {

			try {
				while(true) {

					ObjectInputStream ois = lazySocket.getOis();
					String key = ois.readUTF();

					if("chat".equalsIgnoreCase(key)) {
						Message message = readMessage(lazySocket);
						saveMessageToDatabase(message);
						sendMessageToAllClient(message);
					} else if("sendfile".equalsIgnoreCase(key)) {
						receiveFileFromClient(lazySocket);

					} else {
						System.out.println("Other request: \"" + key + "\" Warning! Warning!");
					}
				}
			} catch(EOFException e) {
				System.out.println("Disconnected Client!");
			} catch(IOException e) {
				if(e.getMessage().equalsIgnoreCase("Connection reset")) {
					System.out.println("Disconnected Client!");
				} else {
					e.printStackTrace();
				}
			} catch(SQLException e) {
				e.printStackTrace();
			} finally {
				lazySocket.cleanup();
			}
		});
		thread.start();
	}

	@Override
	public void receiveFileFromClient(LazySocket lazySocket) throws IOException {

		ObjectInputStream ois = lazySocket.getOis();
		String fileName = ois.readUTF();

		String rootPath = "E:\\";
		File file = new File(rootPath + fileName);
		int c = 0;
		while(file.exists()) {
			c++;
			fileName = fileName + c;
			file = new File(rootPath + fileName);
		}

		System.out.println("received file: " + fileName + "from client, path in server room: " + file.getAbsolutePath());
		Files.copy(ois, file.toPath());
		// TODO hard code, hard edit!...

	}

	@Override
	public void removeUser(ChatUser userName) {

		System.out.println("RoomServer.removeUser()");
		mapUser.remove(userName);

	}

	@Override
	public void resgistryRoomToMainServer(String mainServerHost, int mainServerPort) throws IOException {

		System.out.println("RoomServer.resgistryRoomToMainServer()");
		System.out.println(mainServerHost + "->" + mainServerPort);
		
		Socket socket = new Socket(mainServerHost, mainServerPort);
		LazySocket lazySocket = new LazySocket(socket);

		ObjectOutputStream oos = lazySocket.getOos();
		oos.writeUTF("room");
		oos.writeUTF(id);
		oos.flush();
		oos.writeObject(getAddressRoom());
		oos.flush();

		ObjectInputStream ois = lazySocket.getOis();
		String result = ois.readUTF();

		if(!result.equalsIgnoreCase("success")) { throw new IOException("Resgistry failed!"); }

		lazySocket.cleanup();
		lazySocket = null;
	}

	@Override
	public void saveMessageToDatabase(Message m) throws SQLException {

		System.out.println("RoomServer.saveMessageToDatabase()");
		// TODO Auto-generated method stub

	}

	@Override
	public void sendMessageToAllClient(Message m) throws IOException {

		Queue<ChatUser> listMove = new LinkedList<>();

		for(Entry<ChatUser, LazySocket> entry : mapUser.entrySet()) {
			LazySocket lazySocket = entry.getValue();
			if(lazySocket == null) {
				listMove.add(entry.getKey());
				continue;
			}
			Socket socket = lazySocket.getSocket();
			if(socket.isInputShutdown() || socket.isOutputShutdown() || socket.isClosed()) {
				listMove.add(entry.getKey());
				continue;
			}
			ObjectOutputStream oos = lazySocket.getOos();
			oos.writeUTF("chat");
			oos.flush();
			oos.writeObject(m);
			oos.flush();
		}
		while(!listMove.isEmpty()) {
			ChatUser key = listMove.remove();
			removeUser(key);
		}
	}

	@Override
	public void shutdownRoom() {

		isStarting = false;
		for(LazySocket c : mapUser.values()) {
			c.cleanup();
		}
		System.out.println("Shutdown Room Server!");
	}

	@Override
	protected void processing() {

		isStarting = true;

		Runnable runnableListener = new RunnableListener();
		Runnable runnableProcessing = new RunnableProcessing();

		Thread t1 = new Thread(runnableListener);
		Thread t2 = new Thread(runnableProcessing);

		t1.start();
		t2.start();

	}

	private Message readMessage(LazySocket lazySocket) {

		try {
			return (Message)lazySocket.getOis().readObject();
		} catch(ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ChatUser reiceiveChatUser(LazySocket lazySocket) {

		try {
			ObjectInputStream ois = lazySocket.getOis();
			Object obj = ois.readObject();
			return (ChatUser)obj;
		} catch(ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
