import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JLabel;

public class client {

	private JFrame frame;
	public static JTextField textField;
	private JButton btn;
	private Game game;
	public String name;
    private List<PlayerMP> players = new ArrayList<PlayerMP>();
	
	private String host = "7NVd";
	private int openS = 1234;
	private int openO = 4321;
	private ObjectOutputStream obj;
	private PrintWriter text;
	private JScrollPane scrollPane;
	private JTextArea chatBox;
	public JTextField hpField;
	private JLabel lblHp;
	private JLabel lblCommand;
	public JTextField gunField;
	private JLabel lblGun;
	private int[] damage = {500, 1000, 2000};

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
		frame.setSize(1100, 715);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0,0));
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		
		textField = new JTextField();
		textField.setFont(new Font("Tahoma", Font.PLAIN, 16));
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg) {
				if(arg.getKeyCode()==10){
					signal();
				}
			}
		});
		
		lblHp = new JLabel("HP:");
		lblHp.setFont(new Font("Tahoma", Font.BOLD, 17));
		panel.add(lblHp);
		
		hpField = new JTextField();
		hpField.setEditable(false);
		hpField.setFont(new Font("Tahoma", Font.PLAIN, 15));
		panel.add(hpField);
		hpField.setColumns(5);
		
		lblGun = new JLabel("Gun:");
		lblGun.setFont(new Font("Tahoma", Font.BOLD, 17));
		panel.add(lblGun);
		
		gunField = new JTextField();
		gunField.setFont(new Font("Tahoma", Font.PLAIN, 15));
		gunField.setEditable(false);
		panel.add(gunField);
		gunField.setColumns(10);
		
		lblCommand = new JLabel("Command:");
		lblCommand.setFont(new Font("Tahoma", Font.BOLD, 14));
		panel.add(lblCommand);
		panel.add(textField);
		textField.setColumns(30);
		
		
		btn = new JButton("Send");
		btn.setFont(new Font("Tahoma", Font.BOLD, 16));
		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				signal();
				textField.requestFocus();
			}
		});
		panel.add(btn);
		
		game = new Game();
		game.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				textField.requestFocus();
			}
		});
		game.setMinimumSize(Game.DIMENSIONS);
        game.setMaximumSize(Game.DIMENSIONS);
        game.setPreferredSize(Game.DIMENSIONS);
		frame.getContentPane().add(game, BorderLayout.CENTER);
		
		scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.EAST);
		
		chatBox = new JTextArea();
		chatBox.setEditable(false);
		chatBox.setColumns(20);
		scrollPane.setViewportView(chatBox);
		chatBox.setText("Chat Box\n Movements:\n w - forward a - rotate left\n d - rotate right back - retreat\n q or e - change weapon pew - fire\n /c - chat");
		
		game.start(this);
	}
	
	public void createSocket(){
		System.out.println("Start");
		
		try {
			Socket socketS = new Socket(host,openS);
			Socket socketO = new Socket(host,openO);
			obj = new ObjectOutputStream(socketO.getOutputStream());
			text = new PrintWriter(socketS.getOutputStream(),true);
			
			listener ll = new listener(socketS,socketO);
			ll.start();
		} catch (IOException e) {
			System.out.println("Can't find I/O");
			System.exit(-1);
			//e.printStackTrace();
		}
	}
	
	public class listener extends Thread{
		Socket socketS;
		Socket socketO;
		
		listener(Socket str, Socket obj){
			socketS = str;
			socketO = obj;
		}
		
		public void run(){
			try {
				waitForObj obj = new waitForObj(new ObjectInputStream(socketO.getInputStream()));
				obj.start();
				waitForStr str = new waitForStr(new BufferedReader(new InputStreamReader(socketS.getInputStream())));
				str.start();
			} catch (IOException e) {
				System.out.println("Server died");
				System.exit(-1);
			}
		}
	}
	
	private class waitForObj extends Thread{
		ObjectInputStream in;
		
		public waitForObj(ObjectInputStream in){
			this.in = in;
		}
		
		public void run(){
			while(true){
				try {
					Object liney = in.readObject();
					String[] str = (String[]) liney;
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
						game.level.movePlayer(index,Integer.parseInt(str[1]),Integer.parseInt(str[2]),str[3].charAt(0),Integer.parseInt(str[4]),Integer.parseInt(str[5]));
					}
				} catch (IOException e) {
					System.out.println("Server died");
					System.exit(-1);
				} catch (ClassNotFoundException e) {
					System.out.println("Ayan");
				}
			}
		}
	}
	
	private class waitForStr extends Thread{
		private BufferedReader in;
		
		public waitForStr(BufferedReader in){
			this.in = in;
		}
		
		public void run(){
			while(true){
				try {
					String line = in.readLine();
					line = line.trim();
					if(line.startsWith("<")){
						chatBox.append("\n"+line);
					}else if(line.startsWith(name)){
						String[] event = line.split("-_-");
						int weap = Integer.parseInt(event[1]);
						System.out.println(weap);
						game.player.hp -= damage[weap-1];
						if(game.player.hp<0){
							game.player.hp = 0;
						}
						game.player.printHP();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void signal(){
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
				send(text);
			else
				System.out.println("Command Not Recognized");
		//}
		textField.setText("");
	}
	
	private void send(String txt){
		if(txt.startsWith("/c")){
			String mess = txt.replaceFirst("/c ", "");
			mess = "<"+name+">:  "+mess;
			//chatBox.append(mess);
			text.println(mess);
		}else{
			game.player.com = txt;
			game.player.wasPressed = true;
		}
	}
	
	public void callDmg(String str){
		text.println(str);
	}
	
	public void callToSend(){
		try {
			/**	0: username; 1: x; 2: y; 3: direction; 4: hp	**/
			String[] str = new String[6];
			str[0] = game.player.getUsername();
			str[1] = String.valueOf(game.player.x);
			str[2] = String.valueOf(game.player.y);
			str[3] = String.valueOf(game.player.direction);
			str[4] = String.valueOf(game.player.hp);
			str[5] = String.valueOf(game.player.weapon);
			
			obj.writeObject(str);
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
}
