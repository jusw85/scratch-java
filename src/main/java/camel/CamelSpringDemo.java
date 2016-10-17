package camel;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;
import org.apache.camel.spring.Main;
import twitter4j.Status;

public class CamelSpringDemo {

    private static final MainListenerSupport COUNTER = new MainListenerSupport() {
        public void afterStart(MainSupport main) {
            CamelContext context = main.getCamelContexts().get(0);
            ProducerTemplate template = context.createProducerTemplate();
            for (int i = 0; i < 10; i++) {
                template.sendBody("direct:in", String.valueOf(i));
            }
        }
    };

    private int i = 0;

    public static void main(String[] args) throws Exception {
//        run("etc/camel/basic.xml", 3000L, COUNTER);

//        run("etc/camel/facebook.xml", 10000L);

//        run("etc/camel/twitter.xml", 10000L);

//        run("etc/camel/rss.xml", 10000L);
    }

    public static void run(String camelFile, long runtime) throws Exception {
        run(camelFile, runtime, null);
    }

    public static void run(String camelFile, long runtime, MainListenerSupport mainListenerSupport) throws Exception {
        final Main main = new Main();
        main.setFileApplicationContextUri(camelFile);
        if (mainListenerSupport != null) {
            main.addMainListener(mainListenerSupport);
        }
        Thread mainThread = new Thread(() -> {
            try {
                main.run();
            } catch (Exception e) {
            }
        });
        mainThread.start();
        Thread.sleep(runtime);
        main.stop();
        mainThread.join();
    }

    public void increment() {
        System.out.println("count: " + i++);
    }

    public static class TwitterProcessor {
        public String handleTweet(Status status) {
            return status.toString();
        }
    }

}
