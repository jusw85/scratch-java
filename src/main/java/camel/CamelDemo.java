package camel;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;

import javax.jms.ConnectionFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class CamelDemo {

    public static void main(String[] args) throws Exception {
//        testBasicRoute();
//        testFtp();
//        testUdp();
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

    public static void testUdp() throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("netty4:udp://0.0.0.0:4998" +
                        "?allowDefaultCodec=false" +
                        "&sync=false")
                        .to("stream:out");
            }
        });
        context.start();
        Thread.sleep(Long.MAX_VALUE);
        context.stop();
    }

    /**
     * Convoluted example to demonstrate techniques
     */
    public static void testProcessor() throws Exception {
        SimpleRegistry registry = new SimpleRegistry();
        registry.put("myprocessor", new MyProcessor());

        CamelContext context = new DefaultCamelContext(registry);
        context.setAllowUseOriginalMessage(false);
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:in")
                        .unmarshal().string("UTF-8")
                        .split(body().tokenize("\n")).streaming()
                        .to("bean:myprocessor?method=process")
                        .filter(simple("${body} != null"))
                        .filter(body().isNotNull())
                        .choice().when(body().isNull()).stop().otherwise()
                        .process(exchange -> {
                            Message msg = exchange.getIn();
                            String payload = '-' + msg.getBody(String.class);
                            msg.setBody(payload.getBytes());
                        })
                        .marshal().string("UTF-8")
                        .to("stream:out");
            }
        });

        ProducerTemplate template = context.createProducerTemplate();
        context.start();
        for (int i = 'a'; i <= 'z'; i++) {
            String msg = String.valueOf((char) i) + "\n";
            template.sendBody("direct:in", msg);
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
