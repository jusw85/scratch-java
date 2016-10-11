package test.camel;

public class Main {

//    private static final Logger LOGGER = LogManager.getLogger();
//
//    public static void main(String[] args) {
//        SimpleRegistry registry = new SimpleRegistry();
//        registry.put("hw", new OrbcommTransformer());
//
//        CamelContext context = new DefaultCamelContext(registry);
//        context.setAllowUseOriginalMessage(false);
//
//        context.addComponent("orbcomm", new OrbcommComponent());
//
//        RouteBuilder builder = new RouteBuilder() {
//            public void configure() {
////                from("direct:a")
////                        .to("bean:hw?method=transform")
////                        .to("stream:out")
////                ;
//
////                from("orbcomm://globalais.orbcomm.net:9005?" +
////                        "username=Senseinfosys_LSS&" +
////                        "password=4tQf9umH&" +
////                        "testDisconnectInterval=30000&" +
////                        "isReconnectEnabled=true&" +
////                        "reconnectInterval=30000&" +
////                        "isLogEnabled=true&" +
////                        "logInterval=3600000")
////                        .transform(body().prepend(""))
////                        .to("stream:out")
//                ;
//
//                from("file://.?fileName=test&noop=true")
////                      @formatter:off
//                        .marshal().string("UTF-8")
//                        .split(body().tokenize("\n")).streaming()
//                        .to("bean:hw?method=transform")
//                        .choice()
//                            .when(body().isNull()).stop()
//                        .end()
////                      @formatter:on
//
//                        .to("netty4:udp://localhost:6006?sync=false&textline=true&autoAppendDelimiter=false")
//                ;
//            }
//        };
//
//
//        try {
//            context.addRoutes(builder);
//
//            context.start();
//            Thread.sleep(10000000L);
//            context.stop();
//        } catch (Exception e) {
//            LOGGER.warn(e);
//        }
//    }
}
