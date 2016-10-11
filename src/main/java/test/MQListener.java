package test;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class MQListener implements MessageListener {
	public static void main(String[] args) throws Exception {
//      ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
//		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://192.168.1.79:61616");
//		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://192.168.1.153:62626");
//      ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://54.254.142.65:61616");
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://192.168.1.151:61616");
		
		factory.setUseAsyncSend(true);
		Connection connection = factory.createConnection();

//		MQListener listener = new MQListener();
		
		Session session = connection.createSession(false,
				Session.AUTO_ACKNOWLEDGE);
//		Destination destination = session.createTopic("METISA.RDF.Topic");
//		METISA.ER.KB.RESULT.Topic

        Destination destination = session.createTopic("KLAVER.DATA.Topic");
//        Destination destination = session.createTopic("METISA.ER.KB.RESULT.Topic");
//        Destination destination = session.createTopic("METISA.RDF.Topic");

//		Destination destination = session.createQueue("KLAVER.ER.PG.RESULT.Queue");
		MessageConsumer consumer = session.createConsumer(destination);
//		consumer.setMessageListener(listener);

		connection.start();
		Message msg = null;
		System.out.println("STARTING!");
		while ((msg = consumer.receive()) != null) {
			String str = ((TextMessage) msg).getText();
//			JSONObject obj = new JSONObject(str);
//			if (obj.getJSONObject("data").getString("type").equals("5")) {
//			    System.out.println(obj);
//			}
			System.out.println(str);
		}
		
		MessageProducer producer = null;
//		producer = session.createProducer(destination);
//        TextMessage msg2 = session.createTextMessage();
//        msg2.setText("sup");
//        producer.send(msg2);
        

        try {
            if (consumer != null)
                consumer.close();
            if (producer != null)
                producer.close();
            if (session != null)
                session.close();
            if (connection != null) {
                connection.stop();
                connection.close();
            }
        } catch (JMSException e) {
//            LOGGER.warn(e);
            e.printStackTrace();
        }
	}

	@Override
	public void onMessage(Message arg0) {
		
	}
}
