import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedWriter;
import java.io.FileWriter;


/*
    CSS4850 Networks Project -Version 1, maybe parts of 2..
    Written by Byron Smith
    pawprint: bjha43
    Student# 08306469

*/
/*
	 *  To run as a console application
	 * > Navigate to folder that files are in - must have jdk and possible jre added to computer's path variable if in windows. 
	 * > Type "javac Server.Java" to compile  
	 * > Then type "java Server" to run application
     * > Of course do not include the quotes when entering those commands. 
     * > Put this lower in the document, but as I "moved" the main method to the bottom just for organizational puroposes, felt I needed to have the instructions up here as well. 
*/ 
public class Server {
	
	private static int uniqueId; // for use of multiple clients later on
    private String serverMessage = " ***** "; // Saw this one another example and thought it looked good. Basically a way to frame server messages. Makes it feel more professional to me. 
    /*
    Using an ArrayList to keep track of a each Client, which will be different from the User. By using a threadable ClientList 
    I might be able to implement the graduate level tests. Whereas if I used something else it would be difficult to proceed. 
    Figured the best bet was to try to get version 1 working before I tried to implement version 2, but if I have time why not set myself
    up to be able to move into version 2 without having to completely re-write the whole thing?
    */

    private ArrayList<ClientThread> clientList;
    private ArrayList<User> clients; 
	private int port;
	private boolean endure;
	
	
	public Server(int port) {
		this.port = port;		
        clientList = new ArrayList<ClientThread>();
        clients = new ArrayList<User>();
	}
    
    public User getUserCredentials(String input){

        String pattern = "([A-Za-z]+),\\s*([A-Za-z0-9]+)";
        Pattern pat = Pattern.compile(pattern);
        Matcher m = pat.matcher(input);
        User user = new User("","");
        if( m.find()){
            user.setUsername(m.group(1));
            user.setPassword(m.group(2));
            //user = new User(m.group(1), m.group(2));
        }
        return user;

    }

    public boolean validateUser(String credentials){
       
        String pattern = "([A-Za-z]+),\\s*([A-Za-z0-9]+)";
        Pattern pat = Pattern.compile(pattern);
        Matcher m = pat.matcher(credentials);
        if( m.find())
        {
           // System.out.println("Found");
            User user = new User(m.group(1), m.group(2));
            for(int i = 0; i < clients.size(); i++){
                User tempUser = clients.get(i);
                if(tempUser.getUsername().equals(user.getUsername())){
                    if(tempUser.getPassword().equals(user.getPassword())){
                        return true;
                    }
                }
            }
        }    
        return false;
    }

    public boolean validateUsername(String credentials){

        String pattern = "([A-Za-z]+),\\s*([A-Za-z0-9]+)";
        Pattern pat = Pattern.compile(pattern);
        Matcher m = pat.matcher(credentials);
        if( m.find())
        {
            User user = new User(m.group(1), m.group(2));
            for(int i = 0; i < clients.size(); i++){
                User tempUser = clients.get(i);
                if(tempUser.getUsername().equals(user.getUsername())){
                        return true;
                }
            }
        }    
        return false;


    }

    
	public void start() {
        endure = true; // flag to keep server going. 
        
        String pattern = "([A-Za-z]+),\\s*([A-Za-z0-9]+)";
        Pattern pat = Pattern.compile(pattern);
        String st;  // Placeholder for reading in a line from the file so that can apply the pattern.
        // This just happens to be the file path of my local machine. Would need to be changed for other implementations
       try{

        File file = new File("C:\\\\Users\\\\themi\\\\IdeaProjects\\\\ChatAssignment\\\\src\\\\com\\\\company\\\\users.txt");
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine())
        {
            st = scanner.nextLine();
            Matcher m = pat.matcher(st);
            if( m.find())
            {
                User user = new User(m.group(1), m.group(2));
                clients.add(user);

            }
        }

        }
        catch(Exception e){
            System.out.println("Error loading client list.\n");
        }


		try 
		{
			ServerSocket serverSocket = new ServerSocket(port); //ServerSocket client will connect to
         //   User user1 = new User("Byron", "bob11"); // was using this for testing - suprisingly have used it for multiple features, so leaving here for now
         //   clients.add(user1);
			while(endure) // basic "while true" loop.
			{
				display("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();
				// break if server stoped
				if(!endure){break;}
					
				ClientThread clientThread = new ClientThread(socket);
				clientList.add(clientThread);
				
				clientThread.start();
			}
			// try to stop the server
			try {
				serverSocket.close();
				for(int i = 0; i < clientList.size(); ++i) {
					ClientThread tempClient = clientList.get(i);
					try {
					
					tempClient.serverInput.close();
					tempClient.serverOutput.close();
					tempClient.socket.close();
					}
					catch(IOException ioE) {
					}
				}
			}
			catch(Exception e) {
				System.out.println("Exception closing the server " + e);
			}
		}
		catch (IOException e) {
            String msg = "ServerSocket Exception " + e + "\n";
			System.out.println(msg);
		}
	}
	

	protected void stop() {
		endure = false;
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
		}
	}
	
	// Simple helper method. Fewer keystrokes than typing out System.out.println every freakin time. (fewer keystrokes make me happy...) 
	private void display(String msg) {
	
		System.out.println(msg);
	}
	
	// Send messages to all clients threads. Hopefully this will work when dealing with more than one client. 
	private synchronized boolean broadcast(String message) {
		
			String messageLf = message + "\n";
			// display message
			System.out.print(messageLf);
			
			// we loop in reverse order in case we would have to remove a Client
			// because it has disconnected
			for(int i = clientList.size(); --i >= 0;) {
				ClientThread clientTemp = clientList.get(i);
				// try to write to the Client if it fails remove it from the list
				if(!clientTemp.writeMsg(messageLf)) {
					clientList.remove(i);
					display("Disconnected Client " + clientTemp.username + " removed from list.");
				}
			}
		
		return true;
		
		
	}

	// if client sent LOGOUT message to exit
	synchronized void remove(int id) {
		
		String disconnectedClient = new String(); // Somehow forgot I had to initialize a string -meh
		// scan the array list until we found the Id
		for(int i = 0; i < clientList.size(); ++i) {
			ClientThread tempClient = clientList.get(i);
			// if found remove it
			if(tempClient.id == id) {
				disconnectedClient = tempClient.getUsername();
				clientList.remove(i);
				break;
			}
		}
		broadcast(serverMessage + disconnectedClient + " has left the chat room." + serverMessage);
	}
	
	/*
	 *  To run as a console application
	 * > Navigate to folder that files are in - must have jdk and possible jre added to computer's path variable if in windows. 
	 * > Type "javac Server.Java" to compile  
	 * > Then type "java Server" to run application
     * > Of course do not include the quotes when entering those commands. 
	 */ 
	public static void main(String[] args) {
		// start server on port 16469 - convention provided by assignment. 1 + last 4 of my student ID number which is 6469 
		int portNumber = 16469;
        Server server = new Server(portNumber);
		server.start();
	}

	// 
	class ClientThread extends Thread {
        
        int id; // id for the thread
        String username; // username to be displayed
        String password; // password for the user
		Socket socket; // the socket
		ObjectInputStream serverInput; // Input stream for getting messages
		ObjectOutputStream serverOutput; // Output stream for sending messages
        ChatMessage chatMessage; // Since each message will be an object - make an object....
		
		
		ClientThread(Socket socket) {
			id = ++uniqueId;
			this.socket = socket;
			try
			{
				serverOutput = new ObjectOutputStream(socket.getOutputStream());
				serverInput  = new ObjectInputStream(socket.getInputStream());
            // Must figure out how to connect the threads with the User Class usernames.....since I am using the User class for persistance and the 
            // ClientThread for actual clients this could get dicey
                
				username = new String(); //(String) serverInput.readObject();
				//broadcast(serverMessage + username + " has joined the chat room." + serverMessage);
			}
			catch (IOException e) {
				display("Failure creating streams " + e);
				return;
			}
			// catch (ClassNotFoundException e) {
			// }
		}
		
		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
        }
        
        public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public void run() {
			// to loop until LOGOUT
            boolean endure = true;
            boolean loggedIn = false;
			while(endure) {
				// read a String (which is an object)
				try {
					chatMessage = (ChatMessage) serverInput.readObject();
				}
				catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;				
                }
                // Have to use ClassNotFoundException since I am not making this as a bundled project and just running from the command line. 
                // There might be a time when it does not seem to find the other class files in the folder. These are really long files as it is with multple 
                // classes in one file. Really hope I don't have to use a project with build tools if I can avoid it. Going for minimalistic format.
				catch(ClassNotFoundException e2) {
					break;
				}
				
                String message = chatMessage.getMessage();
                


				// different actions based on type message
				switch(chatMessage.getType()) {

                case ChatMessage.SEND:
                    if(!loggedIn)
                    {
                        broadcast(serverMessage + "You must be logged in to chat." + serverMessage);
                        broadcast(serverMessage + "Please Log In or Create an Account." + serverMessage);
                      //  loggedIn = true;
                        break;
                    }else
                    {
                        boolean confirmation =  broadcast(username + ": " + message);
					    if(confirmation==false){
						String msg = serverMessage + "Sorry. No such user exists." + serverMessage;
						writeMsg(msg);
					}
					break;

                    }
					
                case ChatMessage.LOGOUT:
                    
					display(username + " disconnected with a LOGOUT message.");
                    endure = false;
                    loggedIn = false;
					break;
                case ChatMessage.CREATE:
                    if(loggedIn){
                        broadcast(serverMessage + "You are already logged in as " + getUsername() + " " + serverMessage);
                    }else{
                        if(validateUsername(message)){
                            broadcast(serverMessage + "User already exists" + getUsername() + " " + serverMessage);
                        }else {
                            User newUser = getUserCredentials(message);
                            clients.add(newUser);

                            try{
                                
                                File file = new File("C:\\\\Users\\\\themi\\\\IdeaProjects\\\\ChatAssignment\\\\src\\\\com\\\\company\\\\users.txt");
                                FileWriter fileWriter = new FileWriter(file, true);
                                BufferedWriter writer = new BufferedWriter(fileWriter);
                                writer.write("\n" + message);
                                writer.close();
                                loggedIn = true;
                                setUsername(newUser.getUsername());
                                setPassword(newUser.getPassword());

                            }catch(IOException e){
                                System.out.println("Error writing file.");
                            }
                          
                        }
                        
                        


                    }
                    User newUser = getUserCredentials(message);

                
                    break;
                case ChatMessage.LOGIN:
                        if(loggedIn){
                            broadcast(serverMessage + "You are already logged in as " + getUsername() + " " + serverMessage);
                        }
                       
                        if(validateUser(message)){
                            System.out.println("Validated!!");
                            loggedIn = true;
                            User use = getUserCredentials(message);
                            setUsername(use.getUsername());
                            setPassword(use.getPassword());
                            broadcast(serverMessage + username + " has joined the chat room." + serverMessage);
                        }
                        else{
                            broadcast(serverMessage + "Incorrect username or password" + serverMessage); // not a valid acct send this message.
                        }
                        
				}
			}
			// if out of the loop then disconnected and remove from client list
			remove(id);
			close();
		}
		
		// close everything
		private void close() {
			try {
				if(serverOutput != null) serverOutput.close();
			}
			catch(Exception e) {}
			try {
				if(serverInput != null) serverInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}

		// write a String to the Client output stream
		private boolean writeMsg(String msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				serverOutput.writeObject(msg);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display(serverMessage + "Error sending message to " + username + serverMessage);
				display(e.toString());
			}
			return true;
		}
	}
}
