package org.optum.spark.stream.core;

import java.io.IOException;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

/**
 * Logistic regression trainer.
 * Run this class to train dataset  from a CVS file which has classifiers and write model to a target.
 * @author pmamill
 *
 */
public class SparkLogisticRegression {

	private static  String path = "/Users/pmamill/D-Drive/opsdata/training_data_df.csv";

	public static void main(String[] args) {
		SparkConf sparkConf = new SparkConf().setAppName("SparkLogisticRegression").setMaster("local[2]");
		SparkContext sc = new SparkContext(sparkConf);
		SQLContext sqlContext = new SQLContext(sc);
		
		JavaRDD<String> data = sc.textFile(path, 1).toJavaRDD();
		JavaRDD<LabeledPoint> parsedData = data.map(line -> {
			String[] parts = line.split(",");
			double[] features = new double[parts.length - 1];
			for (int i = 1, j = 0; i < parts.length; i++, j++) {
				features[j] = Double.parseDouble(parts[i]);
			}
			return new LabeledPoint(Double.parseDouble(parts[0]), Vectors.dense(features));
		});
		DataFrame df = sqlContext.createDataFrame(parsedData, LabeledPoint.class);

		LogisticRegression lr = new LogisticRegression().setMaxIter(1000);
		// .setRegParam(0.3).setElasticNetParam(0.8);

		LogisticRegressionModel lrModel = lr.fit(df);

		System.out.println("Coefficients: " + lrModel.coefficients() + " Intercept: " + lrModel.intercept());
		// save the model to File System. In hadoop, this will be HDFS
		try {
			lrModel.save("target/tmp/opsDataLogisticRegression");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println(lrModel.predict(Vectors.dense(6.08,24,2.08,1.14,11.7,153.5,0,81.39,75.74,0,76.95,95,50.5,45,0,1.16)));
		// sc.stop();
	}

}
