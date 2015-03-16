import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;


public class InteractionReducer extends
		Reducer<Text, Interaction, Text, Text> {

	private MultipleOutputs<Text, Text> multipleOutputs;
	
	public void setup(Context context) {
	  this.multipleOutputs = new MultipleOutputs<Text, Text>(context);
	}
	
	public void cleanup(Context c) throws IOException, InterruptedException {
		this.multipleOutputs.close();
	}
	
	public void reduce(Text key, Iterable<Interaction> values,
			Context context) throws IOException, InterruptedException {
		Map<String, Map<String, Integer>> interactions = new HashMap<String, Map<String, Integer>>();
		
		for (Interaction i : values) {
			Map<String, Integer> userInteractions = interactions.get(i.getInteractingUser());
			if(userInteractions == null) {
				userInteractions = new HashMap<String, Integer>();
			}
			Integer totalInteractions = userInteractions.get(i.getInteractedUser());
			if(totalInteractions == null) {
				userInteractions.put(i.getInteractedUser(), 1);
			} else {
				userInteractions.put(i.getInteractedUser(), totalInteractions + 1);
			}
			interactions.put(i.getInteractingUser(), userInteractions);
		}
		
		for (String interactingUser : interactions.keySet()) {
			Map<String, Integer> userInteractions = interactions.get(interactingUser);			
			for(String interactedUser : userInteractions.keySet()) {
				this.multipleOutputs.write( new Text(interactingUser), new Text(interactedUser + "\t" + userInteractions.get(interactedUser)), key.toString());
			}
		}
		
	}
}
