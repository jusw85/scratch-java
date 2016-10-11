package test.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;


/**
 * Created by user on 12/17/15.
 */
public class HelloWorld implements Processor {
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public void getMessage() {
        System.out.println("Your Message : " + message);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String s = exchange.getIn().getBody(String.class);
        exchange.getIn().setBody(s);
    }
}
