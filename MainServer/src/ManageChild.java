

import java.io.IOException;
import java.net.Socket;

public abstract class ManageChild {

	protected IMapRoom map;

	public ManageChild(IMapRoom map) {

		super();
		this.map = map;
	}

	public abstract void processing(LazySocket lazySocket);

	protected boolean isAliveRoom(String host, int port) {
		try {
			new Socket(host, port).close();
			return true;
		} catch(IOException e) {
			return false;
		}
	}

}
