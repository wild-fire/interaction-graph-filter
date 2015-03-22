package interaction.mappers;

import interaction.vos.Interaction;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class RetweetUsersMapper extends TweetMapper<Text, Interaction> {


	private SimpleDateFormat dateOutputFormat = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
	
	@Override
	protected void mapTweet(Object key, Text value,
			Mapper<Object, Text, Text, Interaction>.Context context)
			throws IOException, InterruptedException {
		
		JsonObject entities = this.tweet.get("entities").getAsJsonObject();
		
		if(entities.get("user_mentions").getAsJsonArray().size() > 0) {
			String formattedOutputDate = dateOutputFormat.format(this.tweet.getTweetDate());
			String userId = this.tweet.get("user").getAsJsonObject().get("id_str")
					.getAsString();
			
			for (JsonElement mentionedUser : entities.get("user_mentions")
					.getAsJsonArray()) {
				JsonObject mentionedUserObj = mentionedUser.getAsJsonObject(); 
				if(this.tweet.isRetweeting(mentionedUserObj.get("screen_name").getAsString())) {
					Interaction mention = new Interaction(userId, mentionedUserObj.get("id").toString());
					context.write(new Text(formattedOutputDate), mention);
				}
			}
		}
		
	}

}
