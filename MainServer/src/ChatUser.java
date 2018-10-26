

import java.io.Serializable;

public class ChatUser implements Serializable {

	private static final long	serialVersionUID	= 1L;

	private String				username;
	private String				name;
	private String				address;
	private String				email;
	private String				phone;

	public static long getSerialversionuid() {

		return serialVersionUID;
	}

	public ChatUser() {

		this("", "", "", "", "");
	}

	/**
	 * @param name
	 */
	public ChatUser(String username) {

		super();
		this.username = username;
	}

	public ChatUser(String username, String name, String address, String email, String phone) {

		super();
		this.username = username;
		this.name = name;
		this.address = address;
		this.email = email;
		this.phone = phone;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj) { return true; }
		if(obj == null) { return false; }
		if(!(obj instanceof ChatUser)) { return false; }
		ChatUser other = (ChatUser)obj;
		if(address == null) {
			if(other.address != null) { return false; }
		} else if(!address.equals(other.address)) { return false; }
		if(email == null) {
			if(other.email != null) { return false; }
		} else if(!email.equals(other.email)) { return false; }
		if(name == null) {
			if(other.name != null) { return false; }
		} else if(!name.equals(other.name)) { return false; }
		if(phone == null) {
			if(other.phone != null) { return false; }
		} else if(!phone.equals(other.phone)) { return false; }
		if(username == null) {
			if(other.username != null) { return false; }
		} else if(!username.equals(other.username)) { return false; }
		return true;
	}

	public String getAddress() {

		return address;
	}

	public String getEmail() {

		return email;
	}

	public String getName() {

		return name;
	}

	public String getPhone() {

		return phone;
	}

	public String getUsername() {

		return username;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((address == null) ? 0 : address.hashCode());
		result = (prime * result) + ((email == null) ? 0 : email.hashCode());
		result = (prime * result) + ((name == null) ? 0 : name.hashCode());
		result = (prime * result) + ((phone == null) ? 0 : phone.hashCode());
		result = (prime * result) + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	public void setAddress(String address) {

		this.address = address;
	}

	public void setEmail(String email) {

		this.email = email;
	}

	public void setName(String name) {

		this.name = name;
	}

	public void setPhone(String phone) {

		this.phone = phone;
	}

	public void setUsername(String username) {

		this.username = username;
	}

	@Override
	public String toString() {

		return username;
	}

}
