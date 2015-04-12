# Interaction Graph Filter

This set of Hadoop scripts allow us to process files with tweets and obtain a graph with all the mentions between users and a graph with all the retweets.

## Requirements

This project depends on the Hadoop 2.6.0 client library and it has been included in the Maven descriptor file (pom.xml) so, if you have Maven in your system, all you need to do is

```
mvn install
```

If you have no Maven you can downolad it from http://maven.apache.org/ and maybe take a look at this quick maven tutorial: http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html

## Basic Usage

On the jar folder you can find compiled jar files with all the classes required to run the jobs. To run them you just have to use the following commands:

- For extracting the text of all the mentions:

```
hadoop jar interaction-graph-filter-0.1.jar  interaction/jobs/MentionTextExtractor input output
```

- For extracting the mentions graph:

```
hadoop jar interaction-graph-filter-0.1.jar  interaction/jobs/MentionGraphExtractor input output
```

- For extracting the retweets graph:

```
hadoop jar interaction-graph-filter-0.1.jar  interaction/jobs/RetweetGraphExtractor input output
```

- For extracting the whole interaction graph:

```
hadoop jar interaction-graph-filter-0.1.jar  interaction/jobs/InteractionGraphExtractor input output
```

Being `input` and `output` the folders where the input files are and the output files will be placed.

Optionally you could download the source code and then compile the files with

```
mvn package
```

and do your own JAR file.

## Extended Usage

### Groupping interactions in time spans

By default, interactions are groupped in periods of 4 weeks. This way we will groups of similar length (28 days) of activity. But you can change the number of weeks the periods must take or even to group the interactions in more than group (e.g: group them on 4 weeks groups and 12 weeks groups).

To do so just set as third parameter the number of weeks for each group, separated by comma.

- Extracting the mentions graph in groups of 4 weeks:

```
hadoop jar interaction-graph-filter-0.1.jar  interaction/jobs/MentionGraphExtractor input output
```

Or

```
hadoop jar interaction-graph-filter-0.1.jar  interaction/jobs/MentionGraphExtractor input output 4
```

- Extracting the mentions graph in groups of 12 weeks:

```
hadoop jar interaction-graph-filter-0.1.jar  interaction/jobs/MentionGraphExtractor input output 12
```

- Extracting the mentions graph in groups of 4 weeks and 12 weeks:

```
hadoop jar interaction-graph-filter-0.1.jar  interaction/jobs/MentionGraphExtractor input output 4,12
```

Output files will be named after the grouping criteria, the year and the number of the group, as follows:

```
04weeks-2006-03-r-00000
04weeks-2006-04-r-00000
04weeks-2006-07-r-00000
04weeks-2006-08-r-00000
04weeks-2006-09-r-00000
04weeks-2006-10-r-00000
04weeks-2006-11-r-00000
04weeks-2006-12-r-00000
04weeks-2006-13-r-00000
04weeks-2007-00-r-00000
04weeks-2007-01-r-00000
04weeks-2007-02-r-00000
04weeks-2007-03-r-00000
04weeks-2007-12-r-00000
12weeks-2006-01-r-00000
12weeks-2006-02-r-00000
12weeks-2006-03-r-00000
12weeks-2006-04-r-00000
12weeks-2007-00-r-00000
12weeks-2007-01-r-00000
12weeks-2007-04-r-00000
```

where `04weeks-2006-09-r-00000` means "Grouping for each 4 weeks, this is the 9th group of 2006".

### Amazon Elastic Map Reduce

If you don't have a Hadoop cluster configured you may be interested in using this AWS feature that setups a cluster for you and allows you to run Hadoop jobs.

First, upload all your input files to an S3 bucket of your choice. For this example we will use `my-emr-bucket`. You can do this with the s3cmd tool available on every linux:

```
$ s3cmd mb s3://my-emr-bucket
$ s3cmd put my/local/files/* s3://my-emr-bucket/input/
```

Notice that we stored the files on `s3://my-emr-bucket/input/`.

Now, upload the corresponding JAR file from this repo.

```
$ s3cmd put interaction-graph-filter-X.Y.Z.jar s3://my-emr-bucket/
```

Now, in the EMR Management Console try yo create a new cluster.

In the configuration screen configure your log folder location: `s3://my-emr-bucket/log/`.

In software configuration you can remove all the "Applications to be installed" as we are not using Pig, Hive or Hue.

Here I have selected the 3.6.0 AMI Version with support for Hadoop 2.4.0

In steps, add a custom JAR step.

Name it as you like and in JAR Location write the location where you uploaded before: `s3://my-emr-bucket/interaction-graph-filter-X.Y.Z.jar`.

In arguments place the argument we already saw for the regular execution but using the s3 routes as input and output folders:

```
interaction/jobs/MentionGraphExtractor s3://my-emr-bucket/input/ s3://my-emr-bucket/output/
```

This will run the Mention Graph Extractor task taking s3://my-emr-bucket/input/ as input and leaving the output files at s3://my-emr-bucket/output/

Check auto-terminate if you want the cluster to be terminated after the job is done.

## Input formats

The RecordReader is compatible with two formats of files.

The first one is a simple format with the content of one single tweet per line:

```
83      {"created_at":"Wed Mar 22 00:33:45 +0000 2006","id":83,"id_str":"83","text":"@Errand","source":"<a href=\"http:\/\/twitter.com\" rel=\"nofollow\">Twitter Web Client<\/a>","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":21,"id_str":"21"},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":215,"favorite_count":47,"entities":{"hashtags":[],"symbols":[],"urls":[],"user_mentions":[{"screen_name":"errand","name":"Mike Lloyd-Hayes","id":9117212,"id_str":"9117212","indices":[0,7]}]},"favorited":false,"retweeted":false,"lang":"und"}
327     {"created_at":"Sat Mar 25 20:41:25 +0000 2006","id":327,"id_str":"327","text":"breakfst burrito @ herbivore. Mm!","source":"<a href=\"http:\/\/twitter.com\" rel=\"nofollow\">Twitter Web Client<\/a>","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":20,"id_str":"20"},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":10,"favorite_count":9,"entities":{"hashtags":[],"symbols":[],"urls":[],"user_mentions":[]},"favorited":false,"retweeted":false,"lang":"en"}
399     {"created_at":"Sun Mar 26 19:33:08 +0000 2006","id":399,"id_str":"399","text":"walking to polk street to get mouse for sara's hair and lox for bagels (listening to ze frank @ poptech)","source":"<a href=\"http:\/\/twitter.com\" rel=\"nofollow\">Twitter Web Client<\/a>","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":20,"id_str":"20"},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":8,"favorite_count":5,"entities":{"hashtags":[],"symbols":[],"urls":[],"user_mentions":[]},"favorited":false,"retweeted":false,"lang":"en"}
524     {"created_at":"Tue Mar 28 17:26:56 +0000 2006","id":524,"id_str":"524","text":" @work eating fruit","source":"<a href=\"http:\/\/twitter.com\" rel=\"nofollow\">Twitter Web Client<\/a>","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":23,"id_str":"23"},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":3,"favorite_count":2,"entities":{"hashtags":[],"symbols":[],"urls":[],"user_mentions":[{"screen_name":"work","name":"Work","id":51333,"id_str":"51333","indices":[1,6]}]},"favorited":false,"retweeted":false,"lang":"en"}
937     {"created_at":"Sun Apr 02 02:57:41 +0000 2006","id":937,"id_str":"937","text":"about to eat a whole sushi @ tsunami, then see kid koala @ the independent","source":"<a href=\"http:\/\/twitter.com\" rel=\"nofollow\">Twitter Web Client<\/a>","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":21,"id_str":"21"},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":2,"favorite_count":2,"entities":{"hashtags":[],"symbols":[],"urls":[],"user_mentions":[]},"favorited":false,"retweeted":false,"lang":"en"}
964     {"created_at":"Sun Apr 02 07:58:34 +0000 2006","id":964,"id_str":"964","text":"Party got shut down. Bar @ 9th and brannen","source":"<a href=\"http:\/\/twitter.com\" rel=\"nofollow\">Twitter Web Client<\/a>","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":61,"id_str":"61"},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":9,"favorite_count":5,"entities":{"hashtags":[],"symbols":[],"urls":[],"user_mentions":[]},"favorited":false,"retweeted":false,"lang":"en"}
1027    {"created_at":"Mon Apr 03 01:31:52 +0000 2006","id":1027,"id_str":"1027","text":"@ herbivore","source":"<a href=\"http:\/\/twitter.com\" rel=\"nofollow\">Twitter Web Client<\/a>","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":23,"id_str":"23"},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":25,"favorite_count":4,"entities":{"hashtags":[],"symbols":[],"urls":[],"user_mentions":[]},"favorited":false,"retweeted":false,"lang":"hr"}
1130    {"created_at":"Mon Apr 03 22:57:15 +0000 2006","id":1130,"id_str":"1130","text":"going to eat with sandy\/dan mom @ pestalozzi place great eats","source":"<a href=\"http:\/\/twitter.com\" rel=\"nofollow\">Twitter Web Client<\/a>","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":59,"id_str":"59"},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":1,"favorite_count":1,"entities":{"hashtags":[],"symbols":[],"urls":[],"user_mentions":[]},"favorited":false,"retweeted":false,"lang":"en"}
1654    {"created_at":"Sun Apr 09 08:47:19 +0000 2006","id":1654,"id_str":"1654","text":"burlesque @ 2am","source":"<a href=\"http:\/\/twitter.com\" rel=\"nofollow\">Twitter Web Client<\/a>","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":23,"id_str":"23"},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":5,"favorite_count":1,"entities":{"hashtags":[],"symbols":[],"urls":[],"user_mentions":[]},"favorited":false,"retweeted":false,"lang":"es"}
2341    {"created_at":"Mon Apr 17 16:06:44 +0000 2006","id":2341,"id_str":"2341","text":"happy for the sunshine. big plant on bus is hard to manage.@","source":"<a href=\"http:\/\/twitter.com\" rel=\"nofollow\">Twitter Web Client<\/a>","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":15,"id_str":"15"},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":3,"favorite_count":1,"entities":{"hashtags":[],"symbols":[],"urls":[],"user_mentions":[]},"favorited":false,"retweeted":false,"lang":"en"}
```

The second one is a format with an array of tweets per line:

```
{"id":{"504713592":{"created_at":"Sun Dec 16 00:32:04 +0000 2007","id":504713592,"id_str":"504713592","text":"\u8d77\u304d\u305f\u3002\u5de6\u306e\u304f\u308b\u3076\u3057\u306e\u4e0a\u3042\u305f\u308a\u304c\u816b\u308c\u3066\u75db\u3044\u3002\u306a\u3093\u305e\u3053\u308c\u3002\u866b\u3055\u3055\u308c? \u4f4e\u6e29\u3084\u3051\u3069?","source":"\u003ca href=\"http:\/\/twitter.com\" rel=\"nofollow\"\u003eTwitter Web Client\u003c\/a\u003e","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":6376122,"id_str":"6376122"},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":0,"favorite_count":0,"entities":{"hashtags":[],"symbols":[],"user_mentions":[],"urls":[]},"favorited":false,"retweeted":false,"lang":"ja"},...... }
```
## Output format

### Graph Jobs

Graph Jobs return files containing connections between users with the following TSV format, being the columns: the user interacting (e.g: mentioning), the user receiving the interaction (e.g: being mentioned) and the number of ocurrences (the strength of the relationship).

```
33483\t4170491\t1
33483\t14052066\t3
33483\t335523\t7
33483\t7693\t4
```

This files will be named after the format explained on the "Groupping interactions in time spans" section (e.g: 04weeks-2006-09-r-00000).

However, the InteractionGraphExtractor will add a label to the output files to split mentions and retweets. This means that, instead of creating a single `04weeks-2006-09-r-00000` file, it will create two files `mentions-04weeks-2006-09-r-00000` and `retweets-04weeks-2006-09-r-00000`.

## Known Issues

* I have worked with datasets consisting on old tweets, before retweet button even existed (activation post was published on November 2009: https://blog.twitter.com/2009/retweet-limited-rollout) and so none of the tweets have retweet metada and retweets are detected with keywords (e.g: 'via @user'). Including metada support would be a great improvement and a great pull request if anybodi is interested :)

## Semantic Versioning

This project tries to follow the Semantic Versioning ideas: http://semver.org/
