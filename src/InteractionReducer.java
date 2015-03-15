import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;


public class InteractionReducer extends
		Reducer<Text, Interaction, Text, IntWritable> {

	private IntWritable result = new IntWritable();
	
	public void reduce(Text key, Iterable<Interaction> values,
			Context context) throws IOException, InterruptedException {
		int length = 0;
		for (Interaction i : values) {
			length += 1;
		}
		result.set(length);
		context.write(key, result);
	}
}
