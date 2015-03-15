import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

import com.codesnippets4all.json.parsers.JSONParser;
import com.codesnippets4all.json.parsers.JsonParserFactory;


public class MentionMapper extends Mapper<Object, Text, Text, Interaction> {
	

	 JSONParser parser = JsonParserFactory.getInstance().newJsonParser();
	 DateFormat dateFormat = DateFormat.getDateInstance();
	 
	public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		// We get the file line which is "tweet_id \t tweet_json" and then retrieve the json tweet
		String[] tweetLine = value.toString().split("\t", 2);
		@SuppressWarnings("unchecked")
		Map<String, Object> tweet = parser.parseJson(tweetLine[1]);
		
		Map<String, Object> user = (Map<String, Object>) tweet.get("user");
		String userId = user.get("id_str").toString();
		
		Date tweetDate = new Date();
		try {
			tweetDate = dateFormat.parse(tweet.get("created_at").toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		@SuppressWarnings("unchecked")
		Map<String, Map<String, Object>[]> entities = (Map<String, Map<String, Object>[]>) tweet.get("entities");
		
		for(Map<String, Object> mentionedUser: entities.get("user_mentions")){
			Interaction mention = new Interaction(userId, mentionedUser.get("id_str").toString() );
			context.write(new Text(tweetDate.toString()), mention);
		};
		

	}

}
