package camel;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.ConnectionFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class CamelDemo {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) throws Exception {
//        testBasicRoute();
//        testFtp();
//        testProcessor();
    }

    public static void testBasicRoute() throws Exception {
        final Path outPath = Paths.get("etc/camel-outfile");
        Files.deleteIfExists(outPath);

        CamelContext context = new DefaultCamelContext();
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        context.addComponent("mychannel", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("mychannel:queue:myqueue")
                        .transform(body().append("\n"))
                        .to("file:.?fileName=" + outPath + "&fileExist=Append")
                        .to("stream:out");
            }
        });
        ProducerTemplate template = context.createProducerTemplate();
        context.start();
        for (int i = 0; i < 10; i++) {
            template.sendBody("mychannel:queue:myqueue", "Test Message: " + i);
        }
        Thread.sleep(2000);
        context.stop();
    }

    public static void testFtp() throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        ctx.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("ftp:anonymous@test.talia.net" +
                        "?password=RAW(p@ssw0rd)" +
                        "&binary=true" +
                        "&noop=true" +
                        "&idempotentKey=${file:name}-${file:modified}")
                        .to("file:etc/outdir");
            }
        });

        ctx.start();
        Thread.sleep(Long.MAX_VALUE);
        ctx.stop();
    }

    public static void testProcessor() throws Exception {
        SimpleRegistry registry = new SimpleRegistry();
        registry.put("myprocessor", new MyProcessor());

        CamelContext context = new DefaultCamelContext(registry);
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:in")
                        .to("bean:myprocessor?method=process")
                        .filter(body().isNotNull())
                        .process(exchange -> {
                            Message msg = exchange.getIn();
                            String payload = '-' + msg.getBody(String.class);
                            msg.setBody(payload.getBytes());
                        })
                        .to("stream:out");
            }
        });

        ProducerTemplate template = context.createProducerTemplate();
        context.start();
        for (int i = 'a'; i <= 'z'; i++) {
            template.sendBody("direct:in", (char) i);
        }
        Thread.sleep(2000);
        context.stop();
    }

    public static class MyProcessor {
        private StringBuilder sb = new StringBuilder();

        public String process(String msg) {
            sb.append(msg.toUpperCase());
            if (sb.length() >= 2) {
                String result = sb.reverse().append(' ').toString();
                sb.setLength(0);
                return result;
            }
            return null;
        }
    }

}
