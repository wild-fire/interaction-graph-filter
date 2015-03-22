package interaction.mappers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.hadoop.io.Text;

import com.google.gson.JsonObject;

public class MentionTextMapper extends TweetMapper<Text, Text> {
	

	private SimpleDateFormat dateOutputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
	
	public void mapTweet(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		JsonObject entities = this.tweet.get("entities").getAsJsonObject();
		
		if(entities.get("user_mentions").getAsJsonArray().size() > 0) {
			String formattedOutputDate = dateOutputFormat.format(tweetDate);
			context.write(new Text(formattedOutputDate), new Text(this.tweet.get("text").getAsString()) );
		}

	}

}
