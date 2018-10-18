import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetAddress;

import java.awt.BorderLayout;
import javax.swing.JSplitPane;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class server {

	private JFrame frame;
	private JTextField txtServer;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	
	private player[] playerList = new player[10];
	private int openS = 1234;
	private map mp;
	private JTextField contAdd;
	private JTextField contName;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					server window = new server();
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
	public server() {
		initialize();
		try {
			InetAddress myaddress = InetAddress.getLocalHost();
			contAdd.setText(" IP Address: "+myaddress.getHostAddress());
			contName.setText(" Host Name: "+myaddress.getHostName());
			//System.out.println("IP Address: "+myaddress.getHostAddress());
			//System.out.println("Host Name: "+myaddress.getHostName());
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		frame.getContentPane().add(splitPane, BorderLayout.NORTH);
		
		JButton btnStartServer = new JButton("Start Server");
		btnStartServer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				createSocket();
				btnStartServer.setEnabled(false);
			}
		});
		splitPane.setRightComponent(btnStartServer);
		
		txtServer = new JTextField();
		txtServer.setFont(new Font("Tahoma", Font.BOLD, 15));
		txtServer.setEditable(false);
		txtServer.setText("Server");
		splitPane.setLeftComponent(txtServer);
		txtServer.setColumns(10);
		
		JSplitPane splitPaneBott = new JSplitPane();
		frame.getContentPane().add(splitPaneBott, BorderLayout.SOUTH);
		
		contAdd = new JTextField();
		splitPaneBott.setLeftComponent(contAdd);
		contAdd.setColumns(10);
		
		contName = new JTextField();
		splitPaneBott.setRightComponent(contName);
		contName.setColumns(10);
		splitPaneBott.setDividerLocation(225);
		
		scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
	}
	
	private void update(){
		char [][] test = mp.getMap();
		Character[][] eto = new Character[test.length][test.length];
		String cont="";
		for(int i=0;i<test.length;i++){
			for(int j=0;j<test[0].length;j++){
				eto[i][j] = Character.valueOf(test[i][j]);
			}
		}
		for(int i=0;i<playerList.length;i++){
			if(playerList[i]!=null){
				try {
					playerList[i].out.writeObject(eto);
					String str="";
					if(mp.playerList[i].hp != 0){
						str = "health: "+mp.playerList[i].hp;
					}else{
						str = "health: "+mp.playerList[i].hp+" is dead";
					}
					playerList[i].out.writeObject(str);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void createSocket(){
		System.out.println("Start");
		try {
			ServerSocket socket = new ServerSocket(openS);
			System.out.println("Server open");
			uiOut("Server open");
			looper ll = new looper(socket);
			ll.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Cound not Listen!");
			uiOut("Cound not Listen!");
			System.exit(-1);
			e.printStackTrace();
		}
		
		mp = new map(50);
		for(int i=0;i<playerList.length;i++){
			playerList[i]=null;
		}
	}
	
	public class looper extends Thread{
		ServerSocket socket;
		looper(ServerSocket arg){
			socket = arg;
		}
		public void run(){
			System.out.println("Client Waiter OPEN");
			uiOut("Client Waiter OPEN");
			while(true){
				try {
					clientWaiter cc = new clientWaiter(socket.accept());
					cc.start();
				} catch (IOException e) {
					System.out.println("Accept failed");
					e.printStackTrace();
				}
			}
		}
	}
	
	private class clientWaiter extends Thread{
		private Socket client;
		private int playerNum;
		
		clientWaiter(Socket toAccept){
			client = toAccept;
			for(int i=0;i<playerList.length;i++){
				if(playerList[i]==null){
					playerList[i] = new player(client,i);
					playerNum=i;
					update();
					System.out.println("Connected "+playerList.length);
					uiOut("Connected "+playerList.length);
					break;
				}
				if(i==playerList.length-1){
					System.out.println("Cannot add new player");
					uiOut("Cannot add new player");
				}
			}
		}
		
		public void run(){
			try {
				ObjectInputStream in = new ObjectInputStream(client.getInputStream());
				System.out.println("Ready to listen"); uiOut("Ready to listen");
				
				System.out.println("Listening");
				while(true){
					try {
						if(mp.playerList[playerNum].hp <= 0){
							mp.playerList[playerNum].sprite = '#';
							System.out.println("Player "+playerNum+" is dead");
							return;
						}
						Object liney = in.readObject();
						String line = liney.toString();
						System.out.println("May na-receive from player "+(playerNum+1));
						uiOut("May na-receive from player "+(playerNum+1));
						System.out.println(line+"\n");
						line.trim();
						if(line.compareToIgnoreCase("w")==0){
							mp.playerList[playerNum].move();
						}else if(line.compareToIgnoreCase("a")==0){
							mp.playerList[playerNum].rotL();
						}else if(line.compareToIgnoreCase("d")==0){
							mp.playerList[playerNum].rotR();
						}else if(line.compareToIgnoreCase("back")==0){
							mp.playerList[playerNum].back();
						}else if(line.compareToIgnoreCase("pew")==0){
							mp.playerList[playerNum].attack();
						}else{
							System.out.println(line);
							uiOut(line);
						}
						update();
					} catch (IOException e) {
						playerList[playerNum] = null;
						mp.remPlayer(playerNum);
						System.out.println("A client has disconnected");
						uiOut("A client has Disconnected");
						update();
						break;
					} catch (ClassNotFoundException e) {
						System.out.println("Ayan");
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void uiOut(String str){
		textArea.setText(textArea.getText()+"\n"+str);
	}
	
	private class player{
		protected Socket client;
		protected ObjectOutputStream out;
		
		player(Socket arg, int num){
			client = arg;
			try {
				out = new ObjectOutputStream(arg.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mp.newPlayer(num);
		}
	}

}
