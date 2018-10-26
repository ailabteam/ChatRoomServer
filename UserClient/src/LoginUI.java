


import static javax.swing.JOptionPane.YES_OPTION;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginUI extends JFrame {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;
	private JPasswordField		txtPassWord;
	private JButton				btnLogin;
	private JButton				btnExit;
	private JTextField			txtUserName;

	public LoginUI(String title) throws HeadlessException {

		super(title);
		addControls();
		addEvents();
	}

	public void showWindow() {

		pack();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	protected void xuLyLogin() {

		// TODO Auto-generated method stub
		System.out.println("LoginUI.xuLyLogin()");

		ClientUI ui = ClientUI.getInstance();
		ui.showWindow();
		dispose();

	}

	protected void xuLyThoat() {

		int ret = IText.showConfirm("Exit ?", JOptionPane.YES_NO_OPTION);
		if(ret == YES_OPTION) {
			// TODO Auto-generated method stub
//			System.exit(0);
			dispose();
		}

	}

	private void addControls() {

		int col = 20;
		Container con = getContentPane();
		con.setLayout(new BorderLayout());

		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());
		con.add(pnMain, BorderLayout.CENTER);

		JPanel pnMid = new JPanel();
		pnMid.setLayout(new BoxLayout(pnMid, BoxLayout.Y_AXIS));
		pnMain.add(pnMid, BorderLayout.CENTER);

		JPanel pnTitle = new JPanel();
		JLabel lblTitle = new JLabel("�?ăng Nhập");
		lblTitle.setFont(new Font("", Font.BOLD, 30));
		lblTitle.setForeground(Color.RED);
		pnTitle.add(lblTitle);
		pnMid.add(pnTitle);

		JPanel pnUserName = new JPanel();
		pnMid.add(pnUserName);
		JLabel lblUserName = new JLabel("UserName:");
		pnUserName.add(lblUserName);
		txtUserName = new JTextField(col);
		pnUserName.add(txtUserName);

		JPanel pnPassWord = new JPanel();
		pnMid.add(pnPassWord);
		JLabel lblPassWord = new JLabel("PassWord:");
		pnPassWord.add(lblPassWord);
		txtPassWord = new JPasswordField(col);
		pnPassWord.add(txtPassWord);

		JPanel pnBtn = new JPanel();
		pnMid.add(pnBtn);
		btnLogin = new JButton("Login");
		pnBtn.add(btnLogin);
		btnExit = new JButton("Exit");
		pnBtn.add(btnExit);

	}

	private void addEvents() {

		// TODO Auto-generated method stub
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {

				xuLyThoat();
			}
		});
		btnExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				xuLyThoat();
			}
		});
		btnLogin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				xuLyLogin();
			}
		});

	}

}
