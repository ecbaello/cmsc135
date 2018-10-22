import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class client {

	private JFrame frmClient;
	private JTextField textField;
	private JButton btnNewButton;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	
	private String host = "7NVd";
	private int openS = 1234;
	private ObjectOutputStream out;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					client window = new client();
					window.frmClient.setVisible(true);
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
		while(true){
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
				break;
			}
		}
		initialize();
		createSocket();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmClient = new JFrame();
		frmClient.setResizable(false);
		frmClient.setTitle("CLIENT");
		frmClient.setBounds(300, 15, 800, 715);
		frmClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmClient.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		frmClient.getContentPane().add(panel, BorderLayout.SOUTH);
		
		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg) {
				if(arg.getKeyCode()==10){
					String text = textField.getText();
					text = text.trim();
					if(!text.startsWith("/c ") && text.contains(" ")){
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
					}else{
						if(isCommand(text))
							send();
						else
							System.out.println("Command Not Recognized");
					}
				}
			}
		});
		panel.add(textField);
		textField.setColumns(30);
		textField.requestFocusInWindow();
		
		btnNewButton = new JButton("Send");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				send();
			}
		});
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		panel.add(btnNewButton);
		
		scrollPane = new JScrollPane();
		frmClient.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setBackground(Color.BLACK);
		textArea.setForeground(Color.WHITE);
		textArea.setFont(new Font("Monospaced", Font.BOLD, 9));
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
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
						Character[][] test = (Character[][]) liney;
						String line="";
						for(int i=0;i<test.length;i++){
							for(int j=0;j<test[0].length;j++){
								line = line.concat(" " + String.valueOf(test[i][j]) + " ");
							}
							line = line.concat("\n");
						}
						String hp = (String) in.readObject();
						line = line.concat(hp);
						textArea.setText(line);
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
		try {
			System.out.println("in");
			String text = textField.getText();
			if(isCommand(text.toLowerCase())){
				out.writeObject(text);
				System.out.println("Sent");
			}else{
				System.out.println("Command not recognized");
			}
			textField.setText("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void send(String str){
		try {
			if(isCommand(str.toLowerCase())){
				out.writeObject(str);
				System.out.println("Sent");
			}else{
				System.out.println("Command not recognized");
			}
			textField.setText("");
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
