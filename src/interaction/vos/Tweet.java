package interaction.vos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Tweet {
	
	private JsonObject jsonTweet;
	private JsonParser parser = new JsonParser();
	private String tweetText;

	final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
	private SimpleDateFormat dateInputFormat = new SimpleDateFormat(TWITTER_DATE_FORMAT, Locale.ENGLISH);
	protected Date tweetDate;
	
	public Tweet(String json) {		
		this.jsonTweet  = parser.parse(json).getAsJsonObject();		
	}
	
	public Date getTweetDate() {
		if (this.tweetDate == null) {
			// And now we parse the tweet date
			this.tweetDate = new Date();
			try {
				this.tweetDate = dateInputFormat.parse(this.jsonTweet.get("created_at").getAsString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return this.tweetDate;
	}
	
	public String getText(){
		if (this.tweetText == null) {
			this.tweetText = this.jsonTweet.get("text").getAsString();
		}
		return this.tweetText;
	}
	
	public JsonElement get(String key) {
		return this.jsonTweet.get(key);
	}
	
	public boolean isRetweeting(String user) {
		return this.getText().toLowerCase().contains(" via @" + user.toLowerCase());
	}

}
