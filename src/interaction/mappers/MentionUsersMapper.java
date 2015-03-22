package interaction.mappers;

import interaction.vos.Interaction;

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

public class MentionUsersMapper extends TweetMapper<Text, Interaction> {

	private SimpleDateFormat dateOutputFormat = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
	
	@Override
	protected void mapTweet(Object key, Text value, Context context)
			throws IOException, InterruptedException {

		JsonObject entities = this.tweet.get("entities").getAsJsonObject();

		if(entities.get("user_mentions").getAsJsonArray().size() > 0) {
			String formattedOutputDate = dateOutputFormat.format(tweetDate);
			String userId = this.tweet.get("user").getAsJsonObject().get("id_str")
					.getAsString();
			
			for (JsonElement mentionedUser : entities.get("user_mentions")
					.getAsJsonArray()) {
				Interaction mention = new Interaction(userId, mentionedUser
						.getAsJsonObject().get("id_str").toString());
		
				context.write(new Text(formattedOutputDate), mention);
			}
		}

	}

}
