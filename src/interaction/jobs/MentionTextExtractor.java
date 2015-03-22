package interaction.jobs;
import interaction.mappers.MentionTextMapper;
import interaction.reducers.TweetTextReducer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * This Hadoop Job just extracts all mentions into a file
 * @author David J. Brenes
 *
 */
public class MentionTextExtractor {

	public static void main(String[] args) throws Exception  {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(MentionGraphExtractor.class);
		job.setMapperClass(MentionTextMapper.class);
		job.setReducerClass(TweetTextReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

}
