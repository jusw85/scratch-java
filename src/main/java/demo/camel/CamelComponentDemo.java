package demo.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

class CamelComponentDemo {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.addComponent("helloworld", new HelloWorldComponent());

        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("helloworld:in?consumerType=polling")
                        .to("helloworld:out");
            }
        });

        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("helloworld:in?consumerType=thread")
                        .to("helloworld:out");
            }
        });

        context.start();
        Thread.sleep(Long.MAX_VALUE);
        context.stop();
    }

}
