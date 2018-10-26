


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;

public class ManagePortRoomForUser extends ManageChild implements IManagePortRoomForUser {

	private static final Config CONFIG = Config.getInstance();

	public ManagePortRoomForUser(IMapRoom map) {

		super(map);
	}

	@Override
	public synchronized void callNewRoom(String idRoom) throws IOException {
		String roomPath = CONFIG.getPathRoomServer();
		String fileRunRoomServer = CONFIG.getFileRunRoomServer();
		
		ProcessBuilder pb = new ProcessBuilder(roomPath + fileRunRoomServer, idRoom);
		Process p = pb.start();
		
		
	}

	@Override
	public CustomAddress getAddressByRoomId(String id) {

		CustomAddress addressRoom = map.getAddressRoom(id);
		return addressRoom;
	}

	@Override
	public void processing(LazySocket lazySocket) {

		System.out.println("ManagePortRoomForUser.processing()");
		try {
			String id = reiceiveRequestRoom(lazySocket);
			CustomAddress address = getAddressByRoomId(id);
			if(address == null) {
				responseAddressRoomToClient(address, lazySocket);
				return;
			}

			synchronized(address) {
				int port = address.getPort();
				
				
				while(!isAliveRoom(address.getHost(), address.getPort())) {
					System.out.println("portport: " + port);
					System.out.println(address.getHost() + "->" + address.getPort());
					
					System.out.println("\n");
					
					
					int c = 0;
					synchronized(id) {
						address = getAddressByRoomId(id);
						if(!isAliveRoom(address.getHost(), address.getPort())) {
							System.out.println(c + "--> " + address.getHost() + "->" + address.getPort());
							callNewRoom(id);
							System.out.println("end call new room");
						}
					}
					do {
						address = getAddressByRoomId(id);
						Thread.sleep(100);
					} while((address.getPort() == port) && ((++c) < 30));
					
					System.out.println("!isAliveRoom(address.getHost(), address.getPort())");
					System.out.println(!isAliveRoom(address.getHost(), address.getPort()));
				}
			}
			System.out.println("response to client: " + address);
			responseAddressRoomToClient(address, lazySocket);

		} catch(IOException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String reiceiveRequestRoom(LazySocket lazySocket) throws IOException {

		return lazySocket.getOis().readUTF();
	}

	@Override
	public void responseAddressRoomToClient(CustomAddress address, LazySocket lazySocket) {

		try {
			ObjectOutputStream oos = lazySocket.getOos();
			oos.writeObject(address);
			oos.flush();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
