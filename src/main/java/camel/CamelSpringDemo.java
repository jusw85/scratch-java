package camel;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;
import org.apache.camel.spring.Main;

public class CamelSpringDemo {

    public static void main(String[] args) throws Exception {
        final Main main = new Main();
        main.setFileApplicationContextUri("etc/camel/basic.xml");
        main.addMainListener(
                new MainListenerSupport() {
                    public void afterStart(MainSupport main) {
                        CamelContext context = main.getCamelContexts().get(0);
                        ProducerTemplate template = context.createProducerTemplate();
                        for (int i = 0; i < 10; i++) {
                            template.sendBody("direct:in", String.valueOf(i));
                        }
                    }
                }
        );
        Thread mainThread = new Thread(() -> {
            try {
                main.run();
            } catch (Exception e) {
            }
        });
        mainThread.start();
        Thread.sleep(3000);
        main.stop();
        mainThread.join();
    }

}
