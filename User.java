
import java.io.*;

/*
    CSS4850 Networks Project -Version 1, maybe parts of 2..
    Written by Byron Smith
    pawprint: bjha43
    Student# 08306469

    This class it used to the persistance of the user accounts. Since I used threads to act as the clients. I needed some other data structure to house
    the persistant data. So just a basic Java Class with constructor, getter and setter methods will allow this to work. 

    Also, in using two seperate data structures I should be able to see who is online by checking the active threads against the list of users that I have in persistance. 
    Wish I could have just used one, but couldn't seem to get the bugs out. 


*/
/*
	 *  To run as a console application
	 * > Navigate to folder that files are in - must have jdk and possible jre added to computer's path variable if in windows. 
	 * > Type "javac Server.Java" to compile  
	 * > Then type "java Server" to run application
     * > Of course do not include the quotes when entering those commands. 
     * > Put this lower in the document, but as I "moved" the main method to the bottom just for organizational puroposes, felt I needed to have the instructions up here as well. 
*/ 

public class User {

    private String username;
    private String password;

    //Constructor
   public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    //Get and Set methods for field values
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}