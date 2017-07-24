import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.util.Collector;

import java.util.Properties;

/**
 * Created by ponteru07 on 2017/07/23.
 */
public class WordCountStreamSQL {

    public static String BOOTSTRAP_SERVER = "localhost:9092";
    public static String TOPIC = "study-kafka";
    public static String GROUP_ID = "my-group";

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", BOOTSTRAP_SERVER);
        props.setProperty("group.id", GROUP_ID);

        FlinkKafkaConsumer010<String> consumer =
                new FlinkKafkaConsumer010<>(TOPIC, new SimpleStringSchema(), props);
        consumer.setStartFromEarliest();

        DataStream<String> messageStream = env.addSource(consumer);

        DataStream<Tuple2<String, Integer>> count = messageStream
                .map(sentence -> sentence.toLowerCase())
                .flatMap(new Splitter());

        StreamTableEnvironment tEnv = TableEnvironment.getTableEnvironment(env);
        tEnv.registerDataStream("myTable", count, "word, frequency");

        Table tb = tEnv.sql("SELECT word, SUM(frequency) as frequency FROM myTable GROUP BY word");
        DataStream<Tuple2<Boolean, WC>> stream = tEnv.toRetractStream(tb, WC.class);

        stream.print();
        env.execute();

    }

    public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) throws Exception {
            for (String word: sentence.split(" ")) {
                out.collect(new Tuple2<String, Integer>(word, 1));
            }
        }
    }

    public static class WC {

        public String word;
        public int frequency;

        public WC() {}

        public WC(String word, int frequency) {
            this.word = word;
            this.frequency = frequency;
        }

        @Override
        public String toString() {
            return "WC " + word + " " + frequency;
        }
    }
}
