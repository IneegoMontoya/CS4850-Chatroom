import java.net.*;
import java.io.*;
import java.util.*;


/*
    CSS4850 Networks Project -Version 1, maybe parts of 2..
    Written by Byron Smith
    pawprint: bjha43
    Student# 08306469

*/
/*      PUTTING THIS IN ALL DOCUMENTS TO BE SENT
	 *  To run as a console application
	 * > Navigate to folder that files are in - must have jdk and possible jre added to computer's path variable if in windows. 
	 * > Type "javac Server.Java" to compile  
	 * > Then type "java Server" to run application
     * > Of course do not include the quotes when entering those commands. 
     * > Put this lower in the document, but as I "moved" the main method to the bottom just for organizational puroposes, felt I needed to have the instructions up here as well. 
*/ 


public class Client  {
	
	// notification
    private String serverMessage = " ***** ";
	private ObjectInputStream sInput;		// to read from the socket
	private ObjectOutputStream sOutput;		// to write on the socket
	private Socket socket;					// socket object
	
    private String server;
    private String username;
    private String password;
	private int port = 16469;		// port number to be used per assignment description

    // getter and setter methods
	public String getUsername() {
		return username;
    }
    
    public String getPassword() {
		return password;
    }
    
    public void setPassword(String password){
        this.password = password;
    }

	public void setUsername(String username) {
		this.username = username;
	}

    //client constructor - have it setup to where I can possibly add to it later for implementing version 2
	
	Client(String server, int port, String username) {
		this.server = server;
		this.port = port;
		this.username = username;
	}
	

	public boolean start() {
		
		try {
			socket = new Socket(server, port);
		}catch(Exception ec) {
			display("Failed to connect to server: " + ec);
			return false;
		}
		
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);
	
		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException e) {
			return false;
		}
 
		new ListenFromServer().start(); // start up the listener. Yeah threads!
		return true;
	}

	/*
	 * To send a message to the console - fewer keystrokes makes me happy. Shorter than System.out.println....every time....
	 */
	private void display(String msg) {

		System.out.println(msg);
		
	}
	
	/*
	 * To send a message to the server including which "Type" of message that it is. 
	 */
	void sendMessage(ChatMessage msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}


	private void disconnect() {
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {}
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {}
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {}
			
	}
	
	public static void main(String[] args) {
	
        int portNumber = 16469;
        // Setting Address to localhost for class demo, would like to leave options open for possible later use in as side practice to have actual server address. 
		String serverAddress = "localhost"; 
        String userName = "";
        String passWord = "";
		Scanner scan = new Scanner(System.in);
		
		Client client = new Client(serverAddress, portNumber, userName);
		// try to connect to the server and return if not connected
		if(!client.start())
			return;
		
		System.out.println("\nHello.! Welcome to the chat.");
		System.out.println("Instructions:");
		System.out.println("1. Simply type the message to send broadcast to all active clients");
		System.out.println("2. Type 'LOGIN' without quotes to login and enter the chat");
		System.out.println("3. Type 'CREATE' without quotes to create a new user");
		System.out.println("4. Type 'LOGOUT' without quotes to logoff from server");
		
		
		while(true) {
            // Give something other than standard prompt - this way the users know it is still going on.
			System.out.print("> ");
            String msg = scan.nextLine();
            /*
                check the user inputs for the given commands. And route them accordingly to the format they need to have to be recognized by the
                server. 

            */
			
			if(msg.equalsIgnoreCase("LOGOUT")) {
				client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
				break;
			}
			else if(msg.equalsIgnoreCase("CREATE")) {
                System.out.println("Select your username: ");
                userName = scan.nextLine();
                client.setUsername(userName);
                System.out.println("Enter a password between 4 and 8 charachters in length: ");
                passWord = scan.nextLine();
                
                while(passWord.length() < 4 || passWord.length() > 8){
                    System.out.println("Enter a password between 4 and 8 charachters in length: ");
                    passWord = scan.nextLine();

                }
                
                client.setPassword(passWord);
                client.sendMessage(new ChatMessage(ChatMessage.CREATE, "(" + userName + ", " + passWord + ")"));

                // still need to add input validation - but I think that is all that is left to do to finish version 1. 
                // WIll see if I can play around and work on version 2. Research leads me to believe I can do version 2 with this setup...I hope...
            }
            else if(msg.equalsIgnoreCase("LOGIN")){
                System.out.println("Enter your username: ");
                userName = scan.nextLine();
                client.setUsername(userName);
                System.out.println("Enter your password: ");
                passWord = scan.nextLine();
                client.setPassword(passWord);
                client.sendMessage(new ChatMessage(ChatMessage.LOGIN, "(" + userName + ", " + passWord + ")"));
                

            }
            // Using this as a default for the listed "SEND" command. can change later to SEND for if I can implement version 2.
            // But for right now, only having one person in the room, it seems silly to have to type "send" before every message.
			else {
				client.sendMessage(new ChatMessage(ChatMessage.SEND, msg));
			}
			

		}
        scan.close();
		client.disconnect();	
	}

	/*
	 * a class that waits for the message from the server uses threads and like many other things with the project does alot of while true looping
	 */
	class ListenFromServer extends Thread {

		public void run() {
			while(true) {
				try {
					String msg = (String) sInput.readObject();
					System.out.println(msg);
					System.out.print("> ");
				}
				catch(IOException e) {
					display(serverMessage + "Not connected to any server" + e + serverMessage);
					break;
				}
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}
}
