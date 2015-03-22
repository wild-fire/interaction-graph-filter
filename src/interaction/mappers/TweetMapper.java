package interaction.mappers;

import interaction.vos.Tweet;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public abstract class TweetMapper<KeyOut, ValueOut> extends
		Mapper<Object, Text, KeyOut, ValueOut> {

	protected Tweet tweet;
	
	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		// We get the file line which is "tweet_id \t tweet_json" and then
		// retrieve the json tweet
		String[] tweetLine = value.toString().split("\t", 2);
		
		this.tweet = new Tweet(tweetLine[1]);
		
		
		this.mapTweet(key, value, context);
	}

	protected abstract void mapTweet(Object key, Text value, Context context) throws IOException, InterruptedException;
}
