import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;

import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by ponteru07 on 2017/07/22.
 */
public class KafkaProducer {

    public static String BOOTSTRAP_SERVER = "localhost:9092";
    public static String TOPIC = "study-kafka";

    public static void main(String[] args) throws Exception {

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", BOOTSTRAP_SERVER);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        String[] sentences = new String[] {
                "the cow jumped over the moon",
                "an apple a day keeps doctor away",
                "four score and seven years ago",
                "snow white and seven dwarfs"};

        DataStream<String> stream = env.fromElements(sentences);

        FlinkKafkaProducer010.FlinkKafkaProducer010Configuration myProducerConfig = FlinkKafkaProducer010
                .writeToKafkaWithTimestamps(
                        stream, TOPIC, new SimpleStringSchema(), props);

        myProducerConfig.setLogFailuresOnly(false);
        myProducerConfig.setFlushOnCheckpoint(true);

        env.execute();
    }
}
