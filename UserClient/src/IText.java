

import java.awt.Component;

import javax.swing.JOptionPane;

public interface IText {

	String	CAN_NOT_CONNECT			= "Can not connect to server!";
	String	KICKED_OUT				= "You were kicked out of room!";
	String	LOST_CONNECT			= "You lost the connection!";
	String	ROOM_FULL				= "Room was full!";
	String	ROOM_IS_NOT_AVAILABLE	= "Room is not available. Try again later!";

	public static int showConfirm(Object message, int opitonType) {

		return JOptionPane.showConfirmDialog(null, message, "Confirm", opitonType);

	}

	static String showInput(Component message, Object initialSelectionValue) {

		return JOptionPane.showInputDialog(message, initialSelectionValue);
	}

	static void showText(Object message, int messageType) {

		JOptionPane.showMessageDialog(null, message, "Notice", messageType);

	}

}
