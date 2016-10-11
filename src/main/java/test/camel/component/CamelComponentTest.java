package test.camel.component;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

class CamelComponentTest {

    public static void main(String[] args) {
        CamelContext context = new DefaultCamelContext();
        context.setAllowUseOriginalMessage(false);

        context.addComponent("hw", new HelloWorldComponent());

        RouteBuilder builder = new RouteBuilder() {
            public void configure() {
                from("hw:default")
                        .to("stream:out")
                ;
            }
        };


        try {
            context.addRoutes(builder);
            context.start();
            Thread.sleep(10000000);
            context.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
