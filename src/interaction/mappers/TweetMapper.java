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
		this.tweet = new Tweet(value.toString());
		this.mapTweet(key, value, context);
	}

	protected abstract void mapTweet(Object key, Text value, Context context) throws IOException, InterruptedException;
}
