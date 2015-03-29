package interaction.mappers;

import interaction.vos.Interaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.hadoop.io.Text;

public abstract class InteractionDateGroupedMapper extends
		TweetMapper<Text, Interaction> {

	private ArrayList<Integer> weekGroups;
	private Calendar calendar = Calendar.getInstance();
	private Text groupKey = new Text();
	

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		this.weekGroups = new ArrayList<Integer>();
		for (String weekNumber : context.getConfiguration()
				.get("weekOutputGroups").split(",")) {
			this.weekGroups.add(Integer.parseInt(weekNumber));
		}
		super.setup(context);
	}

	public void contextWriteToGroups(Context context, Interaction interaction) throws IOException, InterruptedException {
		// First we set the date to the calendar
		this.calendar.setTime(this.tweet.getTweetDate());
		// Then, for each group
		for(Integer weekGroup : this.weekGroups) {
			// First, we learn in which group we are			
			int weekNumber = this.calendar.get(Calendar.WEEK_OF_YEAR);
			int group = weekNumber / weekGroup;

			this.groupKey.set(String.format("%d-%02dweeks-%02d", this.calendar.get(Calendar.YEAR), weekGroup, group));
			
			// Then we send this interaction to the reducers with the group as the key
			context.write(this.groupKey, interaction);			
			
		}
		
	}

}
