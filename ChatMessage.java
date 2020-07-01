import java.io.*;
/*
 *  Since there are commands that are needed. It makes send to be able to distinguish between them
 *  By setting them up like this it makes implementing the commands a little bit easier to differentiate without
 *  having to compare and parse strings for every single time something is typed in. Got the idea to use this format from seeing other online examples implement this way. 
 *  It allows for a more modular approach for the commands and will make it possible for me to try to implement version 2. without messing up my version 1 implementation 
 */


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
public class ChatMessage implements Serializable {

	// The different types of commands or messages to be sent by the Client to the server
    // LOGIN - Allow users to login to the server
    // CREATE - Will allow user to make a new user
    // SEND - Just what it sounds like, send message to the chatroom
	// LOGOUT - to disconnect from the server
	static final int LOGIN = 0, CREATE = 1, SEND = 2, LOGOUT = 3;
	private int type;
	private String message;
	

	ChatMessage(int type, String message) {
		this.type = type;
		this.message = message;
	}
	
	int getType() {
		return type;
	}

	String getMessage() {
		return message;
	}
}