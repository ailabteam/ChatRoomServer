

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {

	/**
	 *
	 */
	private static final long		serialVersionUID	= 1L;
	private static SimpleDateFormat	sdf					= new SimpleDateFormat("[dd/MM/yyyy hh:mm:ss] ");
	private static StringBuilder	stringBuilder		= new StringBuilder();
	private ChatUser				owner;
	private String					content;
	private Date					dateSend;

	/**
	 * @param owner
	 * @param content
	 * @param dateSend
	 */
	public Message(ChatUser owner, String content, Date dateSend) {

		super();
		this.owner = owner;
		this.content = content;
		this.dateSend = dateSend;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj) { return true; }
		if(obj == null) { return false; }
		if(!(obj instanceof Message)) { return false; }
		Message other = (Message)obj;
		if(content == null) {
			if(other.content != null) { return false; }
		} else if(!content.equals(other.content)) { return false; }
		if(dateSend == null) {
			if(other.dateSend != null) { return false; }
		} else if(!dateSend.equals(other.dateSend)) { return false; }
		if(owner == null) {
			if(other.owner != null) { return false; }
		} else if(!owner.equals(other.owner)) { return false; }
		return true;
	}

	public String getContent() {

		return content;
	}

	public Date getDateSend() {

		return dateSend;
	}

	public ChatUser getOwner() {

		return owner;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((content == null) ? 0 : content.hashCode());
		result = (prime * result) + ((dateSend == null) ? 0 : dateSend.hashCode());
		result = (prime * result) + ((owner == null) ? 0 : owner.hashCode());
		return result;
	}

	public void setContent(String content) {

		this.content = content;
	}

	public void setDateSend(Date dateSend) {

		this.dateSend = dateSend;
	}

	public void setOwner(ChatUser owner) {

		this.owner = owner;
	}

	@Override
	public String toString() {

		stringBuilder.setLength(0);
		stringBuilder.append(sdf.format(dateSend));
		stringBuilder.append(owner);
		stringBuilder.append(":");
		stringBuilder.append(content);
		stringBuilder.append("\n");
		return stringBuilder.toString();
	}

}
