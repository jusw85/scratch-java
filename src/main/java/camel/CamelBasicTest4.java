package camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.Calendar;

class CamelBasicTest4 {

    public static void main(String[] args) {
        CamelContext context = new DefaultCamelContext();
        context.setAllowUseOriginalMessage(false);
        RouteBuilder builder = new RouteBuilder() {
            public void configure() {
                from("netty4:tcp://globalais.orbcomm.net:9005?" +
                        "allowDefaultCodec=false&" +
                        "clientMode=true&" +
                        "sync=true") //
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                exchange.getOut().setBody(getAuthString());
//                                String s = exchange.getIn().getBody(String.class);
//                                exchange.getIn().setBody(s);
                            }
                        })
                        .to("stream:out")
                ; //
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

    private static String getAuthString() {
        String username = "Senseinfosys_LSS";
        String password = "4tQf9umH";
        String datetime = String.valueOf(Calendar.getInstance().getTimeInMillis());
        String msg = "PMWLSS," + datetime + ",4," + username + "," + password + ",1";
        int checksum = 0;
        for (int i = 0; i < msg.length(); i++) {
            checksum ^= (int) msg.charAt(i);
        }
        String authString = "$" + msg + "*" + Integer.toHexString(checksum)
                + "\r\n";
        return authString;
    }
}
