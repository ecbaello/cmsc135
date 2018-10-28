import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class client {

	private JFrame frame;
	private JTextField textField;
	private JButton btn;
	private Game game;
	public String name;
    private List<PlayerMP> players = new ArrayList<PlayerMP>();
	
	private String host = "7NVd";
	private int openS = 1234;
	private ObjectOutputStream out;
	private JTextField debugN;
	private JTextField debugL;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					client window = new client();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public client() {
		String s = (String)JOptionPane.showInputDialog(
                null,
                "Enter host address",
                "Customized Dialog",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "");
		System.out.println(s);
		if(s!=null && s.compareTo("")!=0){
			host=s;
		}
		initialize();
		createSocket();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setTitle("Game");
		//frame.setBounds(300, 100, 450, 300);
		frame.setSize(800, 715);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0,0));
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		
		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg) {
				if(arg.getKeyCode()==10){
					String text = textField.getText();
					text = text.trim();
					/*if(!text.startsWith("/c ") && text.contains(" ")){
						text = text.replaceAll("[ \t\n\r]+", " ");
						System.out.println(text);
						String arr[] = text.split(" ");
						for(int i=0;i<arr.length;i++){
							if(isCommand(arr[i].toString()))
								send(arr[i].toString());
							try {
								Thread.sleep(300);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}else{*/
						if(isCommand(text))
							send();
						else
							System.out.println("Command Not Recognized");
					//}
					textField.setText("");
				}
			}
		});
		panel.add(textField);
		textField.setColumns(30);
		
		btn = new JButton("Send");
		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if(isCommand(textField.getText()))
					send();
				else
					System.out.println("Command Not Recognized");
				textField.setText("");
			}
		});
		panel.add(btn);
		
		debugN = new JTextField();
		panel.add(debugN);
		debugN.setColumns(10);
		
		debugL = new JTextField();
		panel.add(debugL);
		debugL.setColumns(10);
		
		game = new Game();
		game.setMinimumSize(Game.DIMENSIONS);
        game.setMaximumSize(Game.DIMENSIONS);
        game.setPreferredSize(Game.DIMENSIONS);
		frame.getContentPane().add(game, BorderLayout.CENTER);
		
		game.start(this);
	}
	
	public void createSocket(){
		System.out.println("Start");
		
		try {
			Socket socket = new Socket(host,openS);
			out = new ObjectOutputStream(socket.getOutputStream()); 
			
			listener ll = new listener(socket);
			ll.start();
		} catch (IOException e) {
			System.out.println("Can't find I/O");
			System.exit(-1);
			//e.printStackTrace();
		}
	}
	
	public class listener extends Thread{
		Socket socket;
		listener(Socket arg){
			socket = arg;
		}
		public void run(){
			try {
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				while(true){
					try {
						Object liney = in.readObject();
						String[] str = (String[]) liney;
						debug(str[0], str[1], str[2]);
						if(str[0].equals(name)) continue;
						int index = game.level.getPlayerMPIndex(str[0]);
						if(index == -1){
							System.out.println("New Player!");
							PlayerMP pp = new PlayerMP(game.level, Integer.parseInt(str[1]), Integer.parseInt(str[2]), str[0]);
							players.add(pp);
							game.level.addEntity(pp);
							index = game.level.getPlayerMPIndex(str[0]);
						}
						if(index != -1){
							game.level.movePlayer(index,Integer.parseInt(str[1]),Integer.parseInt(str[2]),str[3].charAt(0),Integer.parseInt(str[4]));
						}
					} catch (IOException e) {
						System.out.println("Server died");
						System.exit(-1);
					} catch (ClassNotFoundException e) {
						System.out.println("Ayan");
					}
				}
			} catch (IOException e) {
				System.out.println("Server died");
				System.exit(-1);
			}
		}
	}
	
	private void send(){
		System.out.println("in");
		String text = textField.getText();
		if(isCommand(text.toLowerCase())){
			game.player.com = text;
			game.player.wasPressed = true;
			//callToSend();
		}else{
			System.out.println("Command not recognized");
		}
	}
	
	public void callToSend(){
		try {
			/**	0: username; 1: x; 2: y; 3: direction; 4: hp	**/
			String[] str = new String[5];
			str[0] = game.player.getUsername();
			str[1] = String.valueOf(game.player.x);
			str[2] = String.valueOf(game.player.y);
			str[3] = String.valueOf(game.player.direction);
			str[4] = String.valueOf(game.player.hp);
			
			out.writeObject(str);
			System.out.println("Sent");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean isCommand(String arg){
		if(arg.compareTo("w")==0 || arg.compareTo("a")==0 || arg.compareTo("d")==0 || arg.compareTo("back")==0)
			return true;
		
		if(arg.compareTo("q")==0 || arg.compareTo("e")==0 || arg.compareTo("pew")==0 || arg.startsWith("/c "))
			return true;
		
		return false;
	}
	
	private void debug(String str1, String str2, String str3){
		debugN.setText(str1);
		debugL.setText(str2 + "; "+ str3);
	}
}
