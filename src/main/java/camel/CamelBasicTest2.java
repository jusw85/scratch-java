package camel;

import io.netty.handler.codec.string.StringDecoder;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.Charset;

class CamelBasicTest2 {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) throws Exception {
        StringDecoder s = new StringDecoder(Charset.forName("UTF-8"));
        SimpleRegistry registry = new SimpleRegistry();
        registry.put("udp-stringdecoder", s);
        CamelContext context = new DefaultCamelContext(registry);
        context.setAllowUseOriginalMessage(false);
        RouteBuilder builder = new RouteBuilder() {
            public void configure() {
//                from("netty4:udp://0.0.0.0:6005?decoder=#udp-stringdecoder&disconnectOnNoReply=false&sync=false") //
//                from("netty4:udp://0.0.0.0:6005?allowDefaultCodec=false&sync=false") //
                from("netty4:udp://0.0.0.0:6005?allowDefaultCodec=false&sync=false") //
                        .process(exchange -> {
//                                byte[] body = exchange.getIn().getBody(byte[].class);
//                                exchange.getIn().setBody(body);
                            String s1 = exchange.getIn().getBody(String.class);
                            exchange.getIn().setBody(s1);
                        })
//                        .transform(body().prepend("")) //
                        .to("stream:out")
                ; //
            }
        };
        try {
            context.addRoutes(builder);
            context.start();

//            ProducerTemplate template = context.createProducerTemplate();
//            while (true) {
//                template.sendBody("direct:a", "asdf");
//                Thread.sleep(1000);
//            }
            Thread.sleep(10000000);
            context.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
