package org.optum.spark.stream.core;

/**
 * Main class to start the streaming of OpsData from Kafka Topic, applying ML and writing results to Rethink DB 
 * 
 * @pmamill
 */
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.spark.SparkConf;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

import kafka.serializer.StringDecoder;

public class OpsDataStreams {
	private static final Connection conn = RethinkDB.r.connection().hostname("localhost").port(28015).connect();
	
	public static void main(String[] args) {
		
		//Spark local Configuration
		SparkConf sparkConf = new SparkConf().setAppName("KafkaInbound").setMaster("local[4]");
		//Java Streaming Context
		JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.milliseconds(1000));
		//sqlConetxt not needed here
		//SQLContext sqlContext = new SQLContext(jssc.sparkContext().sc());
		// Load Model
		LogisticRegressionModel opsDatamodel = LogisticRegressionModel.load("target/tmp/opsDataLogisticRegression");		
		//Kafka Configurations/Params
		Set<String> topicsSet = new HashSet<>(Arrays.asList("ops-data"));
		Map<String, String> kafkaParams = new HashMap<>();
		kafkaParams.put("metadata.broker.list", "localhost:9092");

		// Create direct kafka stream with brokers and topics
		JavaPairInputDStream<String, String> messages = KafkaUtils.createDirectStream(jssc, String.class, String.class,
				StringDecoder.class, StringDecoder.class, kafkaParams, topicsSet);
		
		// Get the lines from message
		JavaDStream<String> lines = messages.map(tuple2 -> tuple2._2());
		
		JavaDStream<OpsResult> predictedLines =lines.map(line -> {
			System.out.println("Got Line :" +line);
			String[] featuresString = line.split(",");
			double[] featuresDouble = new double[featuresString.length];
			for (int i = 0; i < featuresString.length; i++) {
				featuresDouble[i] = Double.parseDouble(featuresString[i]);
			}
			Double predicedValue = opsDatamodel.predict(Vectors.dense(featuresDouble));
			System.out.println("Predicted Output : "+predicedValue );
			return new OpsResult(predicedValue, line);
		});

		predictedLines.foreachRDD(rdd -> {
//			DataFrame df = sqlContext.createDataFrame(rdd, OpsResult.class);
//			df.registerTempTable("OperationalResults");
			rdd.foreach(opsResult -> {
//				RethinkDB.r.db("test").tableCreate("ops_data").run(conn);
				RethinkDB.r.db("ServerOperations").table("ops_data").insert(opsResult).run(conn);
			});
		});
		
		predictedLines.print();
		jssc.start();
		jssc.awaitTermination();
	}
}
