
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.YES_OPTION;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ManageClient implements IManageClient {

	private static final Calendar CALENDAR_INSTANCE = Calendar.getInstance();
	private static final ConfigClient CONFIG_INSTANCE = ConfigClient.getInstance();

	private ClientUI clientUI;
	private LazySocket lazySocketRoom;
	private String hostMainServer;
	private int portMainServer;

	private boolean isInnerRoom;
	private ChatUser user;
	private JButton btnJoin;
	private JButton btnLeave;
	private JButton btnSendChat;
	private JTextArea txtContentChat;
	private JTextField txtRoomId;
	private JTextField txtSendChat;

	private JButton btnSendFile;

	private JFileChooser chooser;

	public ManageClient(ChatUser user) {

		super();
		this.user = user;
		try {
			initialize();
			addEvents();
			setFlagButton(false);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public boolean gotoRoom(CustomAddress addressRoomServer) throws IOException, ClassNotFoundException {

		System.out.println("ManageClient.gotoRoom()");

		String hostRoom = addressRoomServer.getHost();
		int portRoom = addressRoomServer.getPort();

		/* Debug */
		System.out.println(hostRoom + "->" + portRoom);

		Socket socket = new Socket(hostRoom, portRoom);
		lazySocketRoom = new LazySocket(socket);

		ObjectOutputStream oos = lazySocketRoom.getOos();
		oos.writeUTF("gotoRoom");
		oos.flush();

		System.out.println("user: " + user);
		oos.writeObject(user);
		oos.flush();

		ObjectInputStream ois = lazySocketRoom.getOis();
		String result = ois.readUTF();

		return result.equalsIgnoreCase("success");
	}

	@Override
	public void leaveRoom() throws IOException {

		System.out.println("ManageClient.leaveRoom()");
		ObjectOutputStream oos = lazySocketRoom.getOos();
		oos.writeUTF("leaveRoom");
		oos.flush();
		oos.writeObject(user);
		oos.flush();

		isInnerRoom = false;
		if (lazySocketRoom != null) {
			lazySocketRoom.cleanup();
			lazySocketRoom = null;
		}
		setFlagButton(false);
	}

	@Override
	public CustomAddress receiceAddressRoomFromMainServer(LazySocket lazySocketMainServer)
			throws IOException, ClassNotFoundException {
		System.out.println("lazySocketMainServer");
		System.out.println(lazySocketMainServer.getSocket().getRemoteSocketAddress());

		ObjectInputStream ois = lazySocketMainServer.getOis();
		CustomAddress address = (CustomAddress) ois.readObject();

		System.out.println("---");
		System.out.println(address);
		return address;
	}

	@Override
	public void reiceiveChatFromRoom() {

		System.out.println("ManageClient.reiceiveChatFromRoom()");

		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					while(isInnerRoom) {
						ObjectInputStream ois = lazySocketRoom.getOis();
						String key = ois.readUTF().toLowerCase();
						if("chat".equals(key)) {
							Message message = (Message)ois.readObject();
							txtContentChat.append(message.toString());
							txtContentChat.setCaretPosition(txtContentChat.getText().length());
						} else if("kick".equals(key)) {
							xuLyBiDuoiKhoiPhong();
						}
					}
				} catch(EOFException e) {
					xuLyThoatKhoiPhong();
					IText.showText(IText.LOST_CONNECT, ERROR_MESSAGE);
					setFlagButton(false);
				} catch(SocketException e) {
					if(e.getMessage().equalsIgnoreCase("Connection reset")) {
						IText.showText(IText.LOST_CONNECT, ERROR_MESSAGE);
					}
					xuLyThoatKhoiPhong();
	
				}
				catch(IOException e) {
					e.printStackTrace();
				} catch(ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	@Override
	public CustomAddress requestMainServer(String id) throws IOException, ClassNotFoundException, InterruptedException {

		System.out.println("ManageClient.requestMainServer()");
		Socket socket = new Socket(hostMainServer, portMainServer);

		System.out.println(hostMainServer + "->config client:" + portMainServer);

		LazySocket lazySocketMainServer = new LazySocket(socket);

		ObjectOutputStream oos = lazySocketMainServer.getOos();
		oos.writeUTF("user");
		oos.writeUTF(id);
		oos.flush();

		return receiceAddressRoomFromMainServer(lazySocketMainServer);

	}

	@Override
	public void sendChatToRoomServer(Message message) throws IOException {

		ObjectOutputStream oos = lazySocketRoom.getOos();
		oos.writeUTF("chat");
		oos.flush();
		oos.writeObject(message);
		oos.flush();
	}

	protected void xuLyBiDuoiKhoiPhong() throws IOException {

		IText.showText(IText.KICKED_OUT, JOptionPane.WARNING_MESSAGE);
		leaveRoom();
		setFlagButton(false);
	}

	protected void xuLyLeaveRoom() {

		System.out.println("ManageClient.xuLyLeaveRoom()");
		try {
			leaveRoom();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			setFlagButton(false);
		}
	}

	protected void xuLySendChat() {

		try {
			String msg = txtSendChat.getText();
			if (!checkString(msg)) {
				return;
			}

			Message message = new Message(user, msg, CALENDAR_INSTANCE.getTime());
			sendChatToRoomServer(message);
			txtSendChat.setText("");
			txtSendChat.requestFocus();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void xuLyThoatKhoiPhong() {

		isInnerRoom = false;
		setFlagButton(isInnerRoom);
		if (lazySocketRoom != null) {
			lazySocketRoom.cleanup();
		}
		lazySocketRoom = null;
	}

	protected void xuLyThoatUI() {

		int ret = IText.showConfirm("Exit ?", JOptionPane.YES_NO_OPTION);
		if (ret == YES_OPTION) {
			try {
				if (lazySocketRoom != null) {
					leaveRoom();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}

	}

	protected void xuLyVaoPhong() {

		System.out.println("ManageClient.xuLyVaoPhong()");
		String idRoom = txtRoomId.getText();
		if (!checkString(idRoom)) {
			return;
		}
		System.out.println("request: " + idRoom);
		CustomAddress addressRoomServer;
		try {
			addressRoomServer = requestMainServer(idRoom);
		} catch (Exception e) {
			IText.showText(IText.CAN_NOT_CONNECT, ERROR_MESSAGE);// Main server
			return;
		}

		if (addressRoomServer == null) {
			IText.showText(IText.ROOM_IS_NOT_AVAILABLE, WARNING_MESSAGE);// Room server
			return;
		}

		try {
			if (gotoRoom(addressRoomServer)) {
				setFlagButton(true);
				isInnerRoom = true;
				reiceiveChatFromRoom();
			} else {
				IText.showText(IText.ROOM_FULL, ERROR_MESSAGE);
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

	}

	private void addEvents() {

		btnJoin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				xuLyVaoPhong();
			}
		});
		btnLeave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				xuLyLeaveRoom();

			}
		});

		btnSendChat.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				xuLySendChat();

			}
		});
		clientUI.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {

				xuLyThoatUI();
			}
		});
		txtSendChat.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					btnSendChat.doClick();
				}
			}
		});
		txtRoomId.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					btnJoin.doClick();
				}
			}
		});
		btnSendFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					xuLySendFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

	}

	protected void xuLySendFile() throws IOException {

		System.out.println("ManageClient.xuLySendFile()");
		File file = null;
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
		}
		if (file == null || !file.exists()) {
			IText.showText("File or folder does not exist", ERROR_MESSAGE);
			return;
		}

		ObjectOutputStream oos = lazySocketRoom.getOos();
		oos.writeUTF("sendfile");
		oos.flush();
		oos.writeUTF(file.getName());
		oos.flush();

		// transfer here, one row, that is feature :)
		Files.copy(file.toPath(), oos);

		// always flush =))
		oos.flush();

	}

	private boolean checkString(String idRoom) {

		if ((idRoom == null) || idRoom.isEmpty()) {
			return false;
		}
		return true;
	}

	private void initialize() throws IOException {

		hostMainServer = CONFIG_INSTANCE.getHostMainServer();
		portMainServer = CONFIG_INSTANCE.getPortMainServer();

		clientUI = ClientUI.getInstance();

		btnJoin = clientUI.getBtnJoin();
		btnLeave = clientUI.getBtnLeave();
		btnSendChat = clientUI.getBtnSendChat();

		btnSendFile = clientUI.getBtnSendFile();

		txtContentChat = clientUI.getTxtContentChat();
		txtRoomId = clientUI.getTxtRoomId();
		txtSendChat = clientUI.getTxtSendChat();

		chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "gif", "bmp"));
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("File", "pdf", "doc", "docx", "xlsx"));
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("Zip", "zip", "rar5"));

	}

	private void setFlagButton(boolean b) {

		txtContentChat.setText("");
		txtRoomId.setEnabled(!b);
		btnJoin.setEnabled(!b);
		btnLeave.setEnabled(b);
		btnSendChat.setEnabled(b);
		btnSendFile.setEnabled(b);
	}

}
