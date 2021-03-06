package interaction.readers;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class MixedRecordReader extends RecordReader<LongWritable, Text> {

	private LineRecordReader lineReader;
	private LongWritable tweetKey;
	private Text tweetValue;
	private float previousProgress;
	private float lineProgress;
	private int tweetsInLine;
	private LinkedList<String> tweetsToProcess;
	private JsonParser parser = new JsonParser();
	

	public MixedRecordReader(InputSplit split, TaskAttemptContext context) throws IOException {
		this.initialize(split, context);
	}

	@Override
	public void close() throws IOException {
		this.lineReader.close();
	}

	@Override
	public LongWritable getCurrentKey() throws IOException,
			InterruptedException {
		this.tweetKey.set((this.lineReader.getCurrentKey().get() * this.tweetsInLine) -this.tweetsToProcess.size());
		return this.tweetKey;
	}

	@Override
	public Text getCurrentValue() throws IOException, InterruptedException {
		this.tweetValue.set(this.tweetsToProcess.getFirst());
		return this.tweetValue;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		return this.previousProgress + ((this.lineReader.getProgress() - this.previousProgress)*this.lineProgress);
	}

	@Override
	public void initialize(InputSplit split, TaskAttemptContext context) throws IOException {

		this.lineReader = new LineRecordReader();
		this.lineReader.initialize(split, context);
		this.tweetKey = new LongWritable(0);
		this.tweetValue = new Text("");
		this.previousProgress = 0;
		this.tweetsToProcess = new LinkedList<String>();
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		// We get the next tweet on the pile
		if(!this.tweetsToProcess.isEmpty()) {
			this.tweetsToProcess.pop();
		}
		
		// If there are more, then we're done
		// WE store the progress and return
		if(this.tweetsToProcess.size() > 0) {
			this.lineProgress = 1 - ((float)this.tweetsToProcess.size() / this.tweetsInLine);
			return true;
		} else {
			//If there are no more tweets we need to read the next line
			return this.nextLine();
		}
			
		
	}
	
	public boolean nextLine() throws IOException, InterruptedException {
		// We store the progress of the LineReader before it reads another line
		this.previousProgress = this.lineReader.getProgress();
		// We get the next line.
		// If there's no line then we return
		if(!this.lineReader.nextKeyValue()) {
			return false;
		}
		
		// If there was a line then we check the format: Is it a TSV line with two columns? Or is it a JSON Object?
		String[] splittedLine = this.lineReader.getCurrentValue().toString().split("\t");
		if(splittedLine.length == 2) {
			// The TSV line. We just store the json and return
			JsonElement jsonLine = parser.parse(splittedLine[1]);
			// Only if the JSON is an object then we push it
			if(jsonLine.isJsonObject()) {
				this.tweetsToProcess.push(jsonLine.toString());
			}
		} else {
			try {			
				// The JSON line. We have to parse the whole line and then store all the JSONs
				JsonElement jsonLine = parser.parse(this.lineReader.getCurrentValue().toString());
				// Only if the JSON is an object then we try to get the tweets. Otherwise, we ignore it as it's not a proper line
				if(jsonLine.isJsonObject()) {
					if(jsonLine.getAsJsonObject().has("id")) {
						JsonElement tweets = jsonLine.getAsJsonObject().get("id");						
						for (Entry<String, JsonElement> entry : tweets.getAsJsonObject().entrySet()) {
							// On the JSON files some tweets came as null. We don't send them to the mappers
							if (!entry.getValue().isJsonNull()) {
								this.tweetsToProcess.push(entry.getValue().toString());
							}
						}
					}
				}
			} catch (JsonSyntaxException e) {
				
			}
			
		}

		// If there are no tweets in this line (e.g. all the tweets came as null) then we skip to the next line
		if(this.tweetsToProcess.isEmpty()) {
			return this.nextLine();
		}
		
		// Now we store the tweets in this line (so we can track progress on this line)
		this.tweetsInLine = this.tweetsToProcess.size();
		
		return true;
	}

}
