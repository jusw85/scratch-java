package test.camel;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

class CamelBasicTest {

    public static void main(String[] args) {
        CamelContext context = new DefaultCamelContext();
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                "vm://localhost?broker.persistent=false");
        context.addComponent("test-jms",
                JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        try {
            context.addRoutes(new RouteBuilder() {
                public void configure() {
                    from("test-jms:queue:test.queue")
                            .transform(body().append("\n"))
                            .to("file:.?fileName=out22&fileExist=Append");
//                          .to("stream:out");
                }
            });
            ProducerTemplate template = context.createProducerTemplate();
            context.start();
            for (int i = 0; i < 10; i++) {
                template.sendBody("test-jms:queue:test.queue",
                        "Test Message: " + i);
            }
            Thread.sleep(1000);
            context.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
