import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MentionMapper extends Mapper<Object, Text, Text, Interaction> {

	JsonParser parser = new JsonParser();
	final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
	SimpleDateFormat dateInputFormat = new SimpleDateFormat(TWITTER_DATE_FORMAT, Locale.ENGLISH);
	SimpleDateFormat dateOutputFormat = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);

	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		// We get the file line which is "tweet_id \t tweet_json" and then
		// retrieve the json tweet
		String[] tweetLine = value.toString().split("\t", 2);
		@SuppressWarnings("unchecked")
		JsonObject tweet = parser.parse(tweetLine[1]).getAsJsonObject();

		String userId = tweet.get("user").getAsJsonObject().get("id_str")
				.getAsString();

		Date tweetDate = new Date();
		try {
			tweetDate = dateInputFormat.parse(tweet.get("created_at").getAsString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String outputDate = dateOutputFormat.format(tweetDate);
		
		@SuppressWarnings("unchecked")
		JsonObject entities = tweet.get("entities").getAsJsonObject();

		for (JsonElement mentionedUser : entities.get("user_mentions")
				.getAsJsonArray()) {
			Interaction mention = new Interaction(userId, mentionedUser
					.getAsJsonObject().get("id_str").toString());
			context.write(new Text(outputDate), mention);
		}
		;

	}

}
