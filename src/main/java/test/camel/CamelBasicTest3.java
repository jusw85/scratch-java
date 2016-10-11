package test.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class CamelBasicTest3 {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) throws Exception {
        CamelContext ctx = new DefaultCamelContext();
//        ctx.addRoutes(new RouteBuilder() {
//            @Override
//            public void configure() throws Exception {
//                from("file:dpr?noop=true")
//                        .to("file:dpr2");
//            }
//        });
        ctx.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("ftp:kgwadmin@192.168.1.151/test" +
                        "?password=RAW(p@ssw0rd)" +
                        "&binary=true" +
                        "&noop=true" +
                        "&idempotentKey=${file:name}-${file:modified}")
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                System.out.println(exchange);
                            }
                        })
//                        .to("file:dpr2")
                        .to("stream:out");
            }
        });

        ctx.start();
        Thread.sleep(100000);
        ctx.stop();
    }
}
