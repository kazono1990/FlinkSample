import java.util.Properties;
import java.io.File;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.configuration.PropertiesConfiguration;
import java.util.Properties;

/**
 * Created by ponteru07 on 2017/07/21.
 */
public class KafkaConsumer {

    public static String BOOTSTRAP_SERVER = "localhost:9092";
    public static String TOPIC = "study-kafka";
    public static String GROUP_ID = "my-group";

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", BOOTSTRAP_SERVER);
        props.setProperty("group.id", GROUP_ID);

        FlinkKafkaConsumer010<String> consumer =
                new FlinkKafkaConsumer010<>(TOPIC, new SimpleStringSchema(), props);
        consumer.setStartFromEarliest();

        env.addSource(consumer).print();
        env.execute();
    }
}
