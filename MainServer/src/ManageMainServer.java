


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ManageMainServer implements IManageMainServer {

	private static final Config				CONFIG			= Config.getInstance();
	private static final SimpleDateFormat	sdf				= new SimpleDateFormat("dd/MM/yyyy hh:mmaaa");	// ex: 22/12/2018 06:08PM
	private static final StringBuilder		sb				= new StringBuilder();

	private int								numUserRequest	= 0;
	private int								numRoomRequest	= 0;
	private int								numOtherRequest	= 0;

	private ServerSocket					serverSocket;
	private IMapRoom						map;
	private ManageChild						manageForRoom;
	private ManageChild						manageForUser;

	private BlockingQueue<Socket>			mainQueue;
	//

	private BlockingQueue<LazySocket>		registryQueue;
	private BlockingQueue<LazySocket>		processingQueue;

	private final class ListenerRunnable implements Runnable {

		@Override
		public void run() {

			while(true) {
				try {
					Socket socket = serverSocket.accept();
					mainQueue.put(socket);
				} catch(IOException e) {
					e.printStackTrace();
				} catch(InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	private final class ProcessingRunnable implements Runnable {

		@Override
		public void run() {

			while(true) {
				try {
					sb.setLength(0);

					Socket socket = mainQueue.take();
					LazySocket lazySocket = new LazySocket(socket);

					String type = getType(lazySocket);

					if(type.equalsIgnoreCase("user")) {

						processingQueue.put(lazySocket);
						System.out.println(sb.append("User request: ").append(++numUserRequest).append(", Wait For Next User!.. ").append(", Total request: ").append(numUserRequest + numRoomRequest + numOtherRequest).toString());

					} else if(type.equalsIgnoreCase("room")) {
						registryQueue.put(lazySocket);

						System.out.println(sb.append("Room request: ").append(++numRoomRequest).append(", Wait For Next Room!.. ").append(", Total request: ").append(numUserRequest + numRoomRequest + numOtherRequest).toString());

					} else {

						System.out.println("Other request???? warning! warning!");
						System.out.println(sb.append("Other request: ").append(++numOtherRequest).append(", Total request: ").append(numUserRequest + numRoomRequest + numOtherRequest).toString());
						lazySocket.cleanup();
						socket = null;
					}

				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private final class processingUserRunnable implements Runnable {

		@Override
		public void run() {

			while(true) {
				try {
					System.out.println("ManageMainServer.processingUserRunnable.run()");
					manageForUser.processing(processingQueue.take());
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private final class registryRoomRunnable implements Runnable {

		@Override
		public void run() {

			while(true) {
				try {
					System.out.println("ManageMainServer.registryRoomRunnable.run()");
					manageForRoom.processing(registryQueue.take());
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public ManageMainServer(int port) throws IOException {

		super();
		serverSocket = new ServerSocket(port);
		inititalize();
	}

	public ManageMainServer(ServerSocket serverSocket) {

		super();
		this.serverSocket = serverSocket;
		inititalize();
	}

	@Override
	public void processing() {

		System.out.println(sb.append(sdf.format(Calendar.getInstance().getTime())).append("\nServer is starting..."));

		Runnable runnableListener = new ListenerRunnable();
		ProcessingRunnable processingRunnable = new ProcessingRunnable();

		Thread t1 = new Thread(runnableListener);
		Thread t2 = new Thread(processingRunnable);

		t1.start();
		t2.start();

	}

	private String getType(LazySocket lazySocket) throws IOException {

		return lazySocket.getOis().readUTF();

	}

	private void inititalize() {

		map = MapRoom.getInstance();

		manageForRoom = new RegistryRoom(map);
		manageForUser = new ManagePortRoomForUser(map);

		int numProcessInQueue = CONFIG.getNumProcessInQueue();
		mainQueue = new ArrayBlockingQueue<>(numProcessInQueue * 2);
		processingQueue = new ArrayBlockingQueue<>(numProcessInQueue);
		registryQueue = new ArrayBlockingQueue<>(numProcessInQueue);

		int numThreadProcessing = CONFIG.getNumThreadProcessing();

		for(int i = 0; i < numThreadProcessing; i++) {
			System.out.println("Thread " + i + ":");
			new Thread(new processingUserRunnable()).start();
			new Thread(new registryRoomRunnable()).start();
		}

	}

}
