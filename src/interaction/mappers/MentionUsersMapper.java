package interaction.mappers;

import interaction.vos.Interaction;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.hadoop.io.Text;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MentionUsersMapper extends TweetMapper<Text, Interaction> {

	private SimpleDateFormat dateOutputFormat = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
	
	@Override
	protected void mapTweet(Object key, Text value, Context context)
			throws IOException, InterruptedException {

		JsonObject entities = this.tweet.get("entities").getAsJsonObject();

		if(entities.get("user_mentions").getAsJsonArray().size() > 0) {
			String formattedOutputDate = dateOutputFormat.format(this.tweet.getTweetDate());
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
