package demo;

import org.apache.activemq.ActiveMQConnectionFactory;
import wrapper.MqWrapper;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class MQWrapperDemo {

    public static void main(String[] args) throws Exception {
//        testMQWrapper();
//        testMQWrapperReceiver();
//        testMQWrapperSender();
//        testMQStandaloneReceiver();
    }

    public static void testMQWrapper() throws Exception {
        String mqUrl = "vm://localhost?broker.persistent=false";
        String mqDestination = "MY.QUEUE";
        boolean isTopic = false;

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(mqUrl);
        factory.setUseAsyncSend(true);
        factory.setWatchTopicAdvisories(false);
        try (MqWrapper receiver = new MqWrapper(factory, "MyReceiver", mqDestination, isTopic);
             MqWrapper sender = new MqWrapper(factory, "MySender", mqDestination, isTopic)) {

            Thread thread = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = receiver.receiveTextMessage()) != null) {
                        System.out.println(msg);
                    }
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();

            Thread.sleep(1000);
            sender.sendTextMessage("Hello World!");
            Thread.sleep(1000);
        }
    }

    public static void testMQWrapperReceiver() throws Exception {
        String mqUrl = "failover:(tcp://127.0.0.1:61616)";
        String mqClientId = "MQReceiver";
        String mqDestination = "MY.TOPIC";
        boolean isTopic = true;

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(mqUrl);
        factory.setUseAsyncSend(true);
        factory.setWatchTopicAdvisories(false);
        try (MqWrapper mqWrapper = new MqWrapper(factory, mqClientId, mqDestination, isTopic)) {
            String msg;
            while ((msg = mqWrapper.receiveTextMessage()) != null) {
                System.out.println(msg);
            }
        }
    }

    public static void testMQWrapperSender() throws Exception {
        String mqUrl = "failover:(tcp://127.0.0.1:61616)";
        String mqClientId = "MQSender";
        String mqDestination = "MY.TOPIC";
        boolean isTopic = true;

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(mqUrl);
        factory.setUseAsyncSend(true);
        factory.setWatchTopicAdvisories(false);
        try (MqWrapper mqWrapper = new MqWrapper(factory, mqClientId, mqDestination, isTopic)) {
            mqWrapper.sendTextMessage("Hello World!");
        }
    }

    public static void testMQStandaloneReceiver() throws Exception {
        String mqUrl = "failover:(tcp://127.0.0.1:61616)";
        String mqClientId = "MQReceiver";
        String mqDestination = "MY.TOPIC";

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(mqUrl);
        factory.setUseAsyncSend(true);
        factory.setWatchTopicAdvisories(false);

        Connection connection = factory.createConnection();
        connection.setClientID(mqClientId);
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createTopic(mqDestination);
        MessageConsumer consumer = session.createConsumer(destination);

        connection.start();
        Message msg;
        while ((msg = consumer.receive()) != null) {
            String str = ((TextMessage) msg).getText();
            System.out.println(str);
        }

        if (consumer != null)
            consumer.close();
        if (session != null)
            session.close();
        if (connection != null) {
            connection.stop();
            connection.close();
        }
    }

}
