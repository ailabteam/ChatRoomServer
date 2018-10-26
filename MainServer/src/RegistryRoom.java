


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RegistryRoom extends ManageChild implements IRegistryRoom {

	public RegistryRoom(IMapRoom map) {

		super(map);

	}

	@Override
	public void processing(LazySocket lazySocket) {

		System.out.println("RegistryRoom.processing()");

		try {
			reiceiveRegistryForRoom(lazySocket);
			lazySocket.cleanup();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void reiceiveRegistryForRoom(LazySocket lazySocket) throws Exception {

		ObjectInputStream ois = lazySocket.getOis();
		String id = ois.readUTF();

		ObjectOutputStream oos = lazySocket.getOos();

		CustomAddress addressOld = map.getAddressRoom(id);
		if((addressOld != null) && isAliveRoom(addressOld.getHost(), addressOld.getPort())) {

			System.err.println("Room " + id + " is exists and alive, registry failed!");
			oos.writeUTF("fail");
			oos.flush();
			return;
		}

		CustomAddress addressNew = (CustomAddress)ois.readObject();
		map.registryRoom(id, addressNew);
		oos.writeUTF("success");
		oos.flush();

	}

}
