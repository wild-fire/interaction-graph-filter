import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


public class InteractionReducer extends
		Reducer<Text, Interaction, Text, Text> {

	private IntWritable result = new IntWritable();
	
	public void reduce(Text key, Iterable<Interaction> values,
			Context context) throws IOException, InterruptedException {
		int length = 0;
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
			length += 1;
		}
		
		for (String interactingUser : interactions.keySet()) {
			Map<String, Integer> userInteractions = interactions.get(interactingUser);			
			for(String interactedUser : userInteractions.keySet()) {
				context.write(key, new Text(interactingUser + "\t" + interactedUser + "\t" + userInteractions.get(interactedUser)));
			}
		}
		
	}
}
