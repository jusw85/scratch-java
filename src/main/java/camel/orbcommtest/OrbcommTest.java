package camel.orbcommtest;

import com.google.common.base.Strings;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;
import org.apache.camel.spring.Main;

public class OrbcommTest {

    public static void main(String[] args) throws Exception {
        String camelConfigFile = System.getProperty("camel.configurationFile");
        camelConfigFile = Strings.nullToEmpty(camelConfigFile).trim();
        if (camelConfigFile.isEmpty()) {
//            camelConfigFile = "./config/camel-spring2.xml";
            camelConfigFile = "./src/main/config/camel-spring2.xml";
        }

        Main main = new Main();
        main.setFileApplicationContextUri(camelConfigFile);
        main.addMainListener(new MainListenerSupport() {
                                 public void afterStart(MainSupport main) {
                                     CamelContext context = main.getCamelContexts().get(0);

                                     ProducerTemplate template = context.createProducerTemplate();
                                     for (int i = 0; i < 100; i++) {
                                         template.sendBody("direct:a", String.valueOf(i));
                                     }
                                 }
                             }
        );
        main.run();
    }

}
