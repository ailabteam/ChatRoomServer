


import java.io.Serializable;

public class CustomAddress implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;
	String						host;
	int							port;

	public CustomAddress(String host, int port) {

		super();
		this.host = host;
		this.port = port;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj) { return true; }
		if(obj == null) { return false; }
		if(!(obj instanceof CustomAddress)) { return false; }
		CustomAddress other = (CustomAddress)obj;
		if(host == null) {
			if(other.host != null) { return false; }
		} else if(!host.equals(other.host)) { return false; }
		if(port != other.port) { return false; }
		return true;
	}

	public String getHost() {

		return host;
	}

	public int getPort() {

		return port;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((host == null) ? 0 : host.hashCode());
		result = (prime * result) + port;
		return result;
	}

	public void setHost(String host) {

		this.host = host;
	}

	public void setPort(int port) {

		this.port = port;
	}

	@Override
	public String toString() {

		return "[host=" + host + "- port=" + port + "]";
	}

}
