package interaction.mappers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public abstract class TweetMapper<KeyOut, ValueOut> extends
		Mapper<Object, Text, KeyOut, ValueOut> {

	private JsonParser parser = new JsonParser();
	final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
	private SimpleDateFormat dateInputFormat = new SimpleDateFormat(TWITTER_DATE_FORMAT, Locale.ENGLISH);
	protected JsonObject tweet;
	protected Date tweetDate;
	
	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		// We get the file line which is "tweet_id \t tweet_json" and then
		// retrieve the json tweet
		String[] tweetLine = value.toString().split("\t", 2);
		
		this.tweet = parser.parse(tweetLine[1]).getAsJsonObject();

		// And now we parse the tweet date
		this.tweetDate = new Date();
		try {
			tweetDate = dateInputFormat.parse(tweet.get("created_at").getAsString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		this.mapTweet(key, value, context);
	}

	protected abstract void mapTweet(Object key, Text value, Context context) throws IOException, InterruptedException;
}
