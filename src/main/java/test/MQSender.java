//package test;
//
//import com.sis.klaver.wrapper.MqWrapper;
//import org.apache.activemq.ActiveMQConnectionFactory;
//import org.apache.commons.io.FileUtils;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.io.File;
//import java.nio.charset.StandardCharsets;
//
//public class MQSender {
//
//    private static final Logger LOGGER = LogManager.getLogger();
//
//    public static void main(String[] args) {
//        String mqUrl = "failover:(tcp://192.168.1.79:61616)";
//        String mqClientId = "MQSender";
//        String mqDestination = "KLAVER.DATA2.Topic";
//        boolean isTopic = true;
//
//        File inDir = new File("./out");
//
//        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(
//                mqUrl);
//        factory.setUseAsyncSend(true);
//        factory.setWatchTopicAdvisories(false);
//        try (MqWrapper mqWrapper = new MqWrapper(factory, mqClientId,
//                mqDestination, isTopic);) {
//            for (File inFile : inDir.listFiles()) {
//                String msg = FileUtils.readFileToString(inFile, StandardCharsets.UTF_8);
//                mqWrapper.sendMessage(msg);
//            }
//        } catch (Exception e) {
//            LOGGER.error(e);
//        }
//    }
//}
