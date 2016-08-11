Installations:


Start Kafka

Install zookeeper : brew install zookeeper

Install Kafka : brew install kafka

Install Spark Shell

Start zookeeper :  zkServer start (To start in background:  brew services start zookeeper)

Start Kafka: kafka-server-start /usr/local/etc/kafka/server.properties
Create a Kafka Topic : bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic <TOPIC_NAME>

You can start Kafka server on different ports and increase the replication-factor to the number of brokers


OpsDataProducer.java
OpsDataStreams.java
OpsResult.java
SparkLogisticRegression.java