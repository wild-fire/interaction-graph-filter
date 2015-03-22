package interaction.jobs;

import interaction.mappers.RetweetUsersMapper;
import interaction.reducers.InteractionReducer;
import interaction.vos.Interaction;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class RetweetGraphExtractor {

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "extract retweet graph");
		job.setJarByClass(RetweetGraphExtractor.class);
		job.setMapperClass(RetweetUsersMapper.class);
		job.setReducerClass(InteractionReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Interaction.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}