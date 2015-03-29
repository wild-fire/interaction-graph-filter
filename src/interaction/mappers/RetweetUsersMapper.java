package interaction.mappers;

import interaction.vos.Interaction;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class RetweetUsersMapper extends InteractionDateGroupedMapper {

	
	@Override
	protected void mapTweet(Object key, Text value,
			Mapper<Object, Text, Text, Interaction>.Context context)
			throws IOException, InterruptedException {
		
		JsonObject entities = this.tweet.get("entities").getAsJsonObject();
		
		if(entities.get("user_mentions").getAsJsonArray().size() > 0) {
			String userId = this.tweet.get("user").getAsJsonObject().get("id_str")
					.getAsString();
			
			for (JsonElement mentionedUser : entities.get("user_mentions")
					.getAsJsonArray()) {
				JsonObject mentionedUserObj = mentionedUser.getAsJsonObject(); 
				if(this.tweet.isRetweeting(mentionedUserObj.get("screen_name").getAsString())) {
					Interaction mention = new Interaction(userId, mentionedUserObj.get("id").toString());
					this.contextWriteToGroups(context, mention);
				}
			}
		}
		
	}

}
