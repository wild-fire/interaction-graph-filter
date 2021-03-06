package interaction.vos;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;


/**
 * @author David J. Brenes
 * 
 * This class stores an interaction between two users 
 */
public class Interaction implements Writable {

	
	/**
	 * The user that performs the interaction (e.g. in a mention, the user who writes the mention)
	 */
	private String interactingUser;
	/**
	 * The user that receives the interaction (e.g. in a mentions, the user who is mentioned)
	 */
	private String interactedUser;
	
	public Interaction() {
    	super();	
	}
	
	public String getInteractingUser() {
		return interactingUser;
	}

	public void setInteractingUser(String interactingUser) {
		this.interactingUser = interactingUser;
	}

	public String getInteractedUser() {
		return interactedUser;
	}

	public void setInteractedUser(String interactedUser) {
		this.interactedUser = interactedUser;
	}

	public Interaction(String interactiongUser, String interactedUser) {
		super();
		this.interactingUser = interactiongUser;
		this.interactedUser = interactedUser;
	}

	public void readFields(DataInput input) throws IOException {
		this.interactingUser = input.readUTF();
		this.interactedUser = input.readUTF();		
	}

	public void write(DataOutput output) throws IOException {
		output.writeUTF(this.interactingUser);
		output.writeUTF(this.interactedUser);
	}

}
