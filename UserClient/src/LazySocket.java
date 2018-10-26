


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class LazySocket {

	private Socket				socket;
	private InputStream			is;
	private OutputStream		os;
	private ObjectInputStream	ois;
	private ObjectOutputStream	oos;

	public LazySocket(Socket socket) throws IOException {

		super();
		this.socket = socket;
		initialize();
	}

	public void cleanup() {

		if(oos != null) {
			try {
				oos.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

		if(ois != null) {
			try {
				ois.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

		if(socket != null) {
			try {
				socket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

	}

	public InputStream getIs() {

		return is;
	}

	public ObjectInputStream getOis() {

		return ois;
	}

	public ObjectOutputStream getOos() {

		return oos;
	}

	public OutputStream getOs() {

		return os;
	}

	public Socket getSocket() {

		return socket;
	}

	private void initialize() throws IOException {

		os = socket.getOutputStream();
		oos = new ObjectOutputStream(os);

		is = socket.getInputStream();
		ois = new ObjectInputStream(is);

	}
}
