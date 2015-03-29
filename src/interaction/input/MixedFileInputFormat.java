package interaction.input;
import interaction.readers.MixedRecordReader;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;



public class MixedFileInputFormat extends TextInputFormat {

	@Override
	public RecordReader<LongWritable, Text> createRecordReader(
			InputSplit split, TaskAttemptContext context)  {
		try {
			return new MixedRecordReader(split, context);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


}
