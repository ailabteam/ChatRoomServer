


import java.awt.EventQueue;
import java.util.Random;

public class TestClientUI {

	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {

				ClientUI ui = ClientUI.getInstance();
				if(args.length > 0) {
					new ManageClient(new ChatUser(args[0]));
				} else {
					new ManageClient(new ChatUser(new Random().nextDouble() + ""));
				}

				ui.showWindow();
			}
		});

	}
}
