import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class server {

	private JFrame frame;
	private JTextField txtServer;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	
	private int openS = 1234;
	private int openO = 4321;
	private JTextField contAdd;
	private JTextField contName;
	private outputStream[] playerList = new outputStream[10];

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
	
	private void createSocket(){
		System.out.println("Start"); uiOut("Start");
		try {
			ServerSocket socketStr = new ServerSocket(openS);
			ServerSocket socketObj = new ServerSocket(openO);
			System.out.println("Server open");
			uiOut("Server open");
			looper ll = new looper(socketStr,socketObj);
			ll.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Cound not Listen!");
			uiOut("Cound not Listen!");
			System.exit(-1);
			e.printStackTrace();
		}
		
		for(int i=0;i<playerList.length;i++){
			playerList[i]=null;
		}
	}
	
	public class looper extends Thread{
		ServerSocket socketS;
		ServerSocket socketO;
		
		looper(ServerSocket str, ServerSocket obj){
			socketS = str;
			socketO = obj;
		}
		
		public void run(){
			System.out.println("Client Waiter OPEN");
			uiOut("Client Waiter OPEN");
			while(true){
				try {
					clientWaiter cc = new clientWaiter(socketS.accept(),socketO.accept());
					cc.start();
				} catch (IOException e) {
					System.out.println("Accept failed");
					e.printStackTrace();
				}
			}
		}
	}
	
	private class clientWaiter extends Thread{
		private Socket clientS;
		private Socket clientO;
		private int playerNum;
		
		clientWaiter(Socket sStr, Socket sObj){
			clientS = sStr;
			clientO = sObj;
			for(int i=0;i<playerList.length;i++){
				if(playerList[i]==null){
					try {
						playerList[i] = new outputStream(new PrintWriter(clientS.getOutputStream(),true),new ObjectOutputStream(clientO.getOutputStream())); 
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
				waitForObj obj = new waitForObj(new ObjectInputStream(clientO.getInputStream()),playerNum);
				obj.start();
				waitForStr str = new waitForStr(new BufferedReader(new InputStreamReader(clientS.getInputStream())),playerNum);
				str.start();
				System.out.println("Listening"); uiOut("Listening");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class waitForObj extends Thread{
		private ObjectInputStream in;
		private int playerNum;
		
		public waitForObj(ObjectInputStream in, int playerNum){
			this.in = in;
			this.playerNum = playerNum;
		}
		
		public void run(){
			while(true){
				try {
					Object liney = in.readObject();
					System.out.println("May na-receive from player "+(playerNum+1));
					uiOut("May na-receive from player "+(playerNum+1));
					update(liney);
				} catch (IOException e) {
					playerList[playerNum] = null;
					System.out.println("A client has disconnected");
					uiOut("A client has Disconnected");
					update();
					break;
				} catch (ClassNotFoundException e) {
					System.out.println("Ayan");
				}
			}
		}
	}
	
	private class waitForStr extends Thread{
		private BufferedReader in;
		private int playerNum;
		
		waitForStr(BufferedReader in, int playerNum){
			this.in= in;
			this.playerNum = playerNum;
		}
		
		public void run(){
			while(true){
				try {
					String line = in.readLine();
					System.out.println("May na-receive from player "+(playerNum+1));
					uiOut("May na-receive from player "+(playerNum+1));
					update(line);
				} catch (IOException e) {
					playerList[playerNum] = null;
					System.out.println("A client has disconnected");
					uiOut("A client has Disconnected");
					update();
					break;
				}
			}
		}
	}
	
	private void update(){
		//System.out.println(playerList.size());
		//char [][] test = mp.getMap();
		/*Character[][] eto = new Character[test.length][test.length];
		for(int i=0;i<test.length;i++){
			for(int j=0;j<test[0].length;j++){
				eto[i][j] = Character.valueOf(test[i][j]);
			}
		}*/
		/**	0: index; 1: xPos; 2: yPos; 3: dir; 4: hp		**/
		//String[][] eto = new String[mp.getPlayerSize()][6];
		/*int k=0;
		for(int i=0;i<mp.getPlayerSize();i++){
			while(playerList==null) k++;
			eto[i] = mp.getPlayerData(k);
			k++;
		}*/
		
		/*for(int i=0;i<playerList.length;i++){
			if(playerList[i]!=null){
				try {
					//playerList[i].out.writeObject(eto);
					/*String str="";
					if(mp.playerList[i].hp != 0){
						str = "health: "+mp.playerList[i].hp;
					}else{
						str = "health: "+mp.playerList[i].hp+" is dead";
					}
					playerList[i].out.writeObject(str);*/
				/*} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}*/
	}

	private void update(Object obj){
		for(int i=0;i<playerList.length;i++){
			if(playerList[i]!=null){
				System.out.println(i);
				uiOut(String.valueOf(i));
				try {
					playerList[i].getOOS().writeObject(obj);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void update(String str){
		System.out.println(str);
		for(int i=0;i<playerList.length;i++){
			if(playerList[i]!=null){
				System.out.println(i);
				uiOut(String.valueOf(i));
				playerList[i].getPW().println(str);
			}
		}
	}
	
	private void uiOut(String str){
		textArea.setText(textArea.getText()+"\n"+str);
	}

	private class outputStream{
		private ObjectOutputStream obj;
		private PrintWriter str;
		
		public outputStream(PrintWriter str, ObjectOutputStream obj){
			this.str = str;
			this.obj = obj;
		}
		
		public ObjectOutputStream getOOS(){
			return obj;
		}
		
		public PrintWriter getPW(){
			return str;
		}
	}
}
