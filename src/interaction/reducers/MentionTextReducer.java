package interaction.reducers;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;


public class MentionTextReducer extends Reducer<Text, Text, Text, Text> {

	private MultipleOutputs<Text, Text> multipleOutputs;
	
	public void setup(Context context) {
	  this.multipleOutputs = new MultipleOutputs<Text, Text>(context);
	}
	
	public void cleanup(Context c) throws IOException, InterruptedException {
		this.multipleOutputs.close();
	}
	
	public void reduce(Text key, Iterable<Text> tweet_texts,
			Context context) throws IOException, InterruptedException {		
		
		
	    Pattern pattern = Pattern.compile("\\w+ @");

	    
		for (Text tweet_text : tweet_texts) {
		    Matcher matcher = pattern.matcher(tweet_text.toString());
		    while (matcher.find()) {
		    	this.multipleOutputs.write( key, tweet_text, matcher.group());
		    }
		}
		
	}
	
}
