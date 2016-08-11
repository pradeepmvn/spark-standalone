package org.optum.spark.stream.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.google.common.io.Resources;

//header info
//TIME,Classifier,CPUPercent,ActiveProcesses,RunQueue,PeakDiskUtilization,PhysicalDiskIO Rate,PhysicalDiskByteRate,MemoryPageoutRate,MemoryUtilization,UserMemoryUtilization,SwapSpaceUtilization,PeakFilesystemSpaceUtilization,NetworkPacketRate,NetworkInboundPacketRate,NetworkOutboundPacketRate,SystemModeCPUPercent,UserModeCPUPercent

/**
 * Data Producer to Kafka by reading file and putting each lines in a Kafka Topic
 * @author pmamill
 *
 */
public class OpsDataProducer {
	private static String fileName = "/Users/pmamill/D-Drive/opsdata/trainingData_predict.csv";

	public static void main(String[] args) {
		KafkaProducer<String, String> producer;
		try {
			// initialize Kafka
			InputStream props = Resources.getResource("producer.props").openStream();
			Properties properties = new Properties();
			properties.load(props);
			producer = new KafkaProducer<>(properties);

				try (Scanner scanner = new Scanner(new File(fileName))) {
				while (scanner.hasNext()) {
					// send to kafka
					//System.out.println("Sending Line No : " + i);
					producer.send(new ProducerRecord<String, String>("ops-data", scanner.nextLine()));
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}

	}
}
